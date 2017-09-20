package org.aksw.limes.core.measures.mapper.space.Flink;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.space.blocking.HR3Blocker;
import org.aksw.limes.core.measures.measure.space.ISpaceMeasure;
import org.aksw.limes.core.measures.measure.space.SpaceMeasureFactory;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple0;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.util.Collector;

public class FlinkH3Mapper {
	/*
	 * Used for debugging
	 */
	public static int comparisons = 0;
	public static int granularity = 4;
	public static int dim;
	public static ArrayList<Double> thresholds = new ArrayList<Double>();
	public static ArrayList<String> properties = new ArrayList<String>();


	public AMapping getMapping(DataSet<Instance> source, DataSet<Instance> target, String sourceVar, String targetVar,
			String expression, double threshold) throws Exception {
		AMapping mapping = MappingFactory.createDefaultMapping();

		// 0. get properties
		String property1, property2;
		// get property labels
		Parser p = new Parser(expression, threshold);
		// get first property label
		String term1 = p.getLeftTerm();
		if (term1.contains(".")) {
			String split[] = term1.split("\\.");
			property1 = split[1];
			if (split.length >= 2)
				for (int part = 2; part < split.length; part++)
					property1 += "." + split[part];
		} else {
			property1 = term1;
		}

		// get second property label
		String term2 = p.getRightTerm();
		if (term2.contains(".")) {
			String split[] = term2.split("\\.");
			property2 = split[1];
			if (split.length >= 2)
				for (int part = 2; part < split.length; part++)
					property2 += "." + split[part];
		} else {
			property2 = term2;
		}
		// get number of dimensions we are dealing with
		String[] split = property2.split("\\|");
		dim = split.length;

		// initialize the measure for similarity computation
		ISpaceMeasure measure = SpaceMeasureFactory.getMeasure(p.getOperator(), dim);
		for (int i = 0; i < dim; i++) {
			thresholds.add(measure.getThreshold(i, threshold));
			properties.add(split[i]);
		}

		// set appropriate blocks
		source = source.map(new SetAllBlockIds());
		source = source.map(new SetAllBlocksToCompare());
		target = target.map(new SetAllBlockIds());

		// Map blockids to instances
		DataSet<Tuple2<Tuple, Instance>> sourceBid = source.flatMap(new BlocksToCompareIdToInstanceMapper());
		DataSet<Tuple2<Tuple, Instance>> targetBid = target.flatMap(new BlockIdToInstanceMapper());

		// comparison
		DataSet<MappingObject> result =
				// join compareto blocks from source and blocks from target
				sourceBid.join(targetBid)
						 .where("f0")
						 .equalTo("f0")
						// compare the instances
						.with(new Joiner(measure, property1, property2, threshold))
						.returns(new TypeHint<MappingObject>() {}.getTypeInfo());

		List<MappingObject> tmpRes = result.collect();
		for (MappingObject m : tmpRes) {
			mapping.add(m.sid, m.tid, m.sim);
		}
		return mapping;
	}

	/**
	 * @return TypeInfo depending on the number of {@link #dim}
	 */
	private static TypeInformation getBlockInstanceMapperTypeInfo() {
		TypeInformation<Integer> tinteger = TypeInformation.of(new TypeHint<Integer>() { });
		TypeInformation<Instance> tinstance = TypeInformation.of(new TypeHint<Instance>() { });
		TypeInformation[] dimTypes = new TypeInformation[dim];
		for (int i = 0; i < dim; i++) {
			dimTypes[i] = tinteger;
		}
		return new TupleTypeInfo<>(new TupleTypeInfo<>(dimTypes), tinstance);
	}

	/**
	 * Used to map the {@link Instance#getBlocksToCompare()} to {@link Instance} 
	 * @author Daniel Obraczka
	 *
	 */
	private static class BlocksToCompareIdToInstanceMapper implements FlatMapFunction<Instance, Tuple2<Tuple, Instance>>,
			ResultTypeQueryable<Tuple2<Tuple, Instance>> {

		private transient TypeInformation typeInformation;

		public BlocksToCompareIdToInstanceMapper() {
			typeInformation = getBlockInstanceMapperTypeInfo();
		}

		@Override
		public void flatMap(Instance i, Collector<Tuple2<Tuple, Instance>> out) throws Exception {
			for (Tuple bid : i.getBlocksToCompare().ids) {
				out.collect(new Tuple2<Tuple, Instance>(bid, i));
			}
		}

		@Override
		public TypeInformation<Tuple2<Tuple, Instance>> getProducedType() {
			return typeInformation;
		}
	}

	/**
	 * Used to map the {@link Instance#getBlockIds()} to {@link Instance} 
	 * @author Daniel Obraczka
	 *
	 */
	private static class BlockIdToInstanceMapper implements FlatMapFunction<Instance, Tuple2<Tuple, Instance>>,
			ResultTypeQueryable<Tuple2<Tuple, Instance>> {

		private transient TypeInformation typeInformation;

		public BlockIdToInstanceMapper() {
			typeInformation = getBlockInstanceMapperTypeInfo();
		}

		@Override
		public void flatMap(Instance i, Collector<Tuple2<Tuple, Instance>> out) throws Exception {
			for (Tuple bid : i.getBlockIds().ids) {
				out.collect(new Tuple2<Tuple, Instance>(bid, i));
			}
		}

		@Override
		public TypeInformation<Tuple2<Tuple, Instance>> getProducedType() {
			return typeInformation;
		}
	}

	/**
	 * Used to "join" (actually compare) Instances where a {@link Instance#getBlocksToCompare()} block of a source Instance and the {@link Instance#getBlockIds()}
	 * block of a target Instance are equal.
	 * The instances are compare with the given similarity measure and a MappingObject is returned if the similarity is over the threshold
	 * @author Daniel Obraczka
	 *
	 */
	private static class Joiner implements FlatJoinFunction<Tuple2<Tuple, Instance>, Tuple2<Tuple, Instance>, MappingObject>,
			ResultTypeQueryable<MappingObject> {
		public ISpaceMeasure measure;
		public String property1;
		public String property2;
		public double threshold;
		private transient TypeInformation<MappingObject> typeInformation;

		public Joiner(ISpaceMeasure measure, String property1, String property2, double threshold) {
			this.measure = measure;
			this.property1 = property1;
			this.property2 = property2;
			this.threshold = threshold;
			typeInformation = TypeInformation.of(new TypeHint<MappingObject>() {
			});
		}

		@Override
		public void join(Tuple2<Tuple, Instance> first, Tuple2<Tuple, Instance> second, Collector<MappingObject> out)
				throws Exception {
			comparisons++;
			double sim = measure.getSimilarity(first.f1, second.f1, property1, property2);
			if (sim >= threshold) {
				out.collect(new MappingObject(first.f1.getUri(), second.f1.getUri(), sim));
			}
		}

		@Override
		public TypeInformation<MappingObject> getProducedType() {
			return typeInformation;
		}
	}

	/**
	 * Helper class to store mappings
	 * @author Daniel Obraczka
	 *
	 */
	private static class MappingObject {
		String sid;
		String tid;
		double sim;

		public MappingObject(String sid, String tid, double sim) {
			this.sid = sid;
			this.tid = tid;
			this.sim = sim;
		}

	}

	private static class SetAllBlocksToCompare implements MapFunction<Instance, Instance> {

		@Override
		public Instance map(Instance a) throws Exception {
			Set<Tuple> blocksToCompare = new HashSet<>();
			for (Tuple blockId : a.getBlockIds().ids) {
				blocksToCompare.addAll(getBlocksToCompare(blockId));
			}
			a.setBlocksToCompare(blocksToCompare);
			return a;
		}
	}

	/**
	 * Computes the blocks that are to be compared with a given block
	 *
	 * @param blockId
	 *            ID of the block for which comparisons are needed
	 * @return List of IDs that are to be compared
	 */
	private static ArrayList<Tuple> getBlocksToCompare(Tuple blockId) {
		if (dim == 0) {
			return new ArrayList<Tuple>();
		}
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		ArrayList<Tuple> hr3result = new ArrayList<Tuple>();
		result.add(blockId);

		ArrayList<Tuple> toAdd;
		Tuple id;

		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < Math.pow(2 * granularity + 1, i); j++) {
				id = result.get(j);
				toAdd = new ArrayList<Tuple>();
				for (int k = 0; k < 2 * granularity; k++) {
					toAdd.add(ListToTupleConverter.createEmptyNTuple(dim));
				}
				for (int k = 0; k < dim; k++) {
					if (k != i) {
						for (int l = 0; l < 2 * granularity; l++) {
							toAdd.get(l).setField(id.getField(k),k);
//							assert(toAdd.get(l).get(k) == id.get(k));
						}
					} else {
						for (int l = 0; l < granularity; l++) {
							toAdd.get(l).setField((((Integer)id.getField(k)) - (l + 1)), k);
//							assert(toAdd.get(l).get(k) == (id.get(k) - (l+1)));
						}
						for (int l = 0; l < granularity; l++) {
							toAdd.get(l + granularity).setField(((Integer)id.getField(k)) + l + 1,k);
//							assert(toAdd.get(l + granularity).get(k) == (id.get(k) + l + 1));
						}
					}
				}
				// Merge results
				for (int l = 0; l < 2 * granularity; l++) {
					result.add(toAdd.get(l));
				}
			}
		}

		// now run hr3 check
		int alphaPowered = (int) Math.pow(granularity, dim);
		Tuple block;
		int hr3Index;
		int index;
		for (int i = 0; i < result.size(); i++) {
			hr3Index = 0;
			block = result.get(i);
			for (int j = 0; j < dim; j++) {
				if (block.getField(j) == blockId.getField(j)) {
					hr3Index = 0;
					break;
				} else {
					index = (Math.abs(((Integer)blockId.getField(j)) - ((Integer)block.getField(j))) - 1);
					hr3Index = hr3Index + (int) Math.pow(index, dim);
				}
			}
			if (hr3Index < alphaPowered)
				hr3result.add(block);
		}

		return hr3result;
	}

	private static class SetAllBlockIds implements MapFunction<Instance, Instance> {
		@Override
		public Instance map(Instance a) throws Exception {
			int blockId;
			Set<Tuple> blockIds = new HashSet<>();
			ArrayList<ArrayList<Double>> combinations = new ArrayList<ArrayList<Double>>();
			// get all property combinations
			for (int i = 0; i < dim; i++) {
				combinations = HR3Blocker.addIdsToList(combinations, a.getProperty(properties.get(i)));
			}
			for (int i = 0; i < combinations.size(); i++) {
				ArrayList<Double> combination = combinations.get(i);
				Tuple block = ListToTupleConverter.createEmptyNTuple(dim);
				for (int j = 0; j < combination.size(); j++) {
					blockId = (int) java.lang.Math.floor((granularity * combination.get(j)) / thresholds.get(j));
					block.setField(blockId, j);
				}
				blockIds.add(block);
			}
			a.setBlockIds(blockIds);
			return a;
		}
	}

}
