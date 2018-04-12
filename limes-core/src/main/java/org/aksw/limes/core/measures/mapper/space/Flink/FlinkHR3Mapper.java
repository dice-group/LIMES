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
import org.apache.flink.api.common.accumulators.IntCounter;
import org.apache.flink.api.common.functions.FlatJoinFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichFlatJoinFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

public class FlinkHR3Mapper {

	/*
	 * params
	 */
	public static int granularity = 4;
	public int dim;
	public static ArrayList<Double> thresholds = new ArrayList<Double>();
	public ArrayList<String> properties = new ArrayList<String>();

	public FlinkHR3Mapper(){
		
	}
	
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

		System.out.println("sourcecount:" + source.count());
		System.out.println("propertieslist:" + properties);
		DataSet<Tuple2<Tuple, Instance>> sourceBid = source
				.flatMap(new GetSourceInstanceBlocksToCompare(properties, dim, getBlockInstanceMapperTypeInfo(), granularity, thresholds));
		DataSet<Tuple2<Tuple, Instance>> targetBid = target
				.flatMap(new GetTargetBlocks(properties, dim, getBlockInstanceMapperTypeInfo(), granularity, thresholds));

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
	private TypeInformation getBlockInstanceMapperTypeInfo() {
		TypeInformation<Integer> tinteger = TypeInformation.of(new TypeHint<Integer>() { });
		TypeInformation<Instance> tinstance = TypeInformation.of(new TypeHint<Instance>() { });
		TypeInformation[] dimTypes = new TypeInformation[dim];
		for (int i = 0; i < dim; i++) {
			dimTypes[i] = tinteger;
		}
		return new TupleTypeInfo<>(new TupleTypeInfo<>(dimTypes), tinstance);
	}


	/**
	 * Used to "join" (actually compare) Instances where a compareToBlock of a source Instance and the 
	 * block of a target Instance are equal.
	 * The instances are compare with the given similarity measure and a MappingObject is returned if the similarity is over the threshold
	 * @author Daniel Obraczka
	 *
	 */
	private static class Joiner extends RichFlatJoinFunction<Tuple2<Tuple, Instance>, Tuple2<Tuple, Instance>, MappingObject> implements
			ResultTypeQueryable<MappingObject> {
		public ISpaceMeasure measure;
		public String property1;
		public String property2;
		public double threshold;
		private transient TypeInformation<MappingObject> typeInformation;

		private IntCounter comparisons = new IntCounter();

		public Joiner(ISpaceMeasure measure, String property1, String property2, double threshold) {
			this.measure = measure;
			this.property1 = property1;
			this.property2 = property2;
			this.threshold = threshold;
			typeInformation = TypeInformation.of(new TypeHint<MappingObject>() {
			});
		}

		@Override
		public void open(Configuration parameters) throws Exception {
			super.open(parameters);
			getRuntimeContext().addAccumulator("comparisons", this.comparisons);
		}

		@Override
		public void join(Tuple2<Tuple, Instance> first, Tuple2<Tuple, Instance> second, Collector<MappingObject> out)
				throws Exception {
			comparisons.add(1);
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
	
	/**
	 * Calculates the blocks for an instance as TupleN where N = number of dimensions.
	 * Returns Tuple2 of block and this instance
	 * @author Daniel Obraczka
	 *
	 */
	private static class GetTargetBlocks implements FlatMapFunction<Instance, Tuple2<Tuple,Instance>> ,ResultTypeQueryable<Tuple2<Tuple, Instance>> {
		private final ArrayList<String> properties;
		private final int dim;
		private transient TypeInformation typeInformation;
		private final int granularity;
		private final ArrayList<Double> thresholds;

		public GetTargetBlocks(ArrayList<String> properties, int dim, TypeInformation typeInformation,
							   int granularity, ArrayList<Double> thresholds) {
			this.properties = properties;
			this.dim = dim;
			this.typeInformation = typeInformation;
			this.granularity = granularity;
			this.thresholds = thresholds;
		}

		@Override
		public void flatMap(Instance a, Collector<Tuple2<Tuple, Instance>> out) throws Exception {
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
				out.collect(new Tuple2<Tuple,Instance>(block,a));
			}
		}

		@Override
		public TypeInformation<Tuple2<Tuple, Instance>> getProducedType() {
			return typeInformation;
		}
	}

	/**
	 * Calculates the blocks for an instance as TupleN where N = number of dimensions.
	 * Uses these blocks to calculate the blocks that have to be compared for this block.
	 * Returns Tuple2 of block to compare and this instance
	 * @author Daniel Obraczka
	 *
	 */
	private static class GetSourceInstanceBlocksToCompare
			implements FlatMapFunction<Instance, Tuple2<Tuple, Instance>>,
			ResultTypeQueryable<Tuple2<Tuple, Instance>> {

		private final ArrayList<String> properties;
		private final int dim;
		private transient TypeInformation typeInformation;
		private final int granularity;
		private final ArrayList<Double> thresholds;

		public GetSourceInstanceBlocksToCompare(ArrayList<String> properties, int dim, TypeInformation typeInformation,
												int granularity, ArrayList<Double> thresholds) {
			this.properties = properties;
			this.dim = dim;
			this.typeInformation = typeInformation;
			this.granularity = granularity;
			this.thresholds = thresholds;
			PerformanceEval.logger.info("GetSourceInstanceBlocksToCompare constructor");
		}

		@Override
		public void flatMap(Instance a, Collector<Tuple2<Tuple, Instance>> out) throws Exception {
			int blockId;
			PerformanceEval.logger.info("GetSourceInstanceBlocksToCompare flatmap");
			ArrayList<ArrayList<Double>> combinations = new ArrayList<ArrayList<Double>>();
			// get all property combinations
			for (int i = 0; i < dim; i++) {
				combinations = HR3Blocker.addIdsToList(combinations, a.getProperty(properties.get(i)));
			}

			//GET BLOCKS
			for (int i = 0; i < combinations.size(); i++) {
				ArrayList<Double> combination = combinations.get(i);
				Tuple block = ListToTupleConverter.createEmptyNTuple(dim);
				for (int j = 0; j < combination.size(); j++) {
					blockId = (int) java.lang.Math.floor((granularity * combination.get(j)) / thresholds.get(j));
					block.setField(blockId, j);
				}

				//==============================================
				//=== GET BLOCKS TO COMPARE ====================
				//==============================================
                ArrayList<Tuple> result = new ArrayList<Tuple>();
                result.add(block);

                ArrayList<Tuple> toAdd;
                Tuple id;

                for (int k = 0; k < dim; k++) {
                    for (int j = 0; j < Math.pow(2 * granularity + 1, k); j++) {
                        id = result.get(j);
                        toAdd = new ArrayList<Tuple>();
                        for (int m = 0; m < 2 * granularity; m++) {
                            toAdd.add(ListToTupleConverter.createEmptyNTuple(dim));
                        }
                        for (int m = 0; m < dim; m++) {
                            if (m != k) {
                                for (int l = 0; l < 2 * granularity; l++) {
                                    toAdd.get(l).setField(id.getField(m),m);
                                }
                            } else {
                                for (int l = 0; l < granularity; l++) {
                                    toAdd.get(l).setField((((Integer)id.getField(m)) - (l + 1)), m);
                                }
                                for (int l = 0; l < granularity; l++) {
                                    toAdd.get(l + granularity).setField(((Integer)id.getField(m)) + l + 1,m);
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
                Tuple blockToCompare;
                int hr3Index;
                int index;
                for (int l = 0; l < result.size(); l++) {
                    hr3Index = 0;
                    blockToCompare = result.get(l);
                    for (int j = 0; j < dim; j++) {
                        if (blockToCompare.getField(j) == block.getField(j)) {
                            hr3Index = 0;
                            break;
                        } else {
                            index = (Math.abs(((Integer)block.getField(j)) - ((Integer)blockToCompare.getField(j))) - 1);
                            hr3Index = hr3Index + (int) Math.pow(index, dim);
                        }
                    }
                    if (hr3Index < alphaPowered)
                    	out.collect(new Tuple2<Tuple,Instance>(blockToCompare,a));
                }
			}
		}

		@Override
		public TypeInformation<Tuple2<Tuple, Instance>> getProducedType() {
			return typeInformation;
		}
	}

}
