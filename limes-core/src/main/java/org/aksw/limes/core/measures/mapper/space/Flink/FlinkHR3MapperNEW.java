package org.aksw.limes.core.measures.mapper.space.Flink;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.space.blocking.BlockingFactory;
import org.aksw.limes.core.measures.mapper.space.blocking.IBlockingModule;
import org.aksw.limes.core.measures.measure.space.ISpaceMeasure;
import org.aksw.limes.core.measures.measure.space.SpaceMeasureFactory;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.util.Collector;

import com.google.common.base.Joiner;

public class FlinkHR3MapperNEW {

	/*
	 * params
	 */
	public static int granularity = 4;
	public int dim;
	public static ArrayList<Double> thresholds = new ArrayList<Double>();
	public ArrayList<String> properties = new ArrayList<String>();

	public FlinkHR3MapperNEW(){
		
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

		IBlockingModule gen = BlockingFactory.getBlockingModule(property2, p.getOperator(), threshold, granularity);
		final String finalProperty1 = property1;
		final String finalProperty2 = property2;
		DataSet<Tuple2<HR3Block, Instance>> sourceBlocks = source.flatMap(new GetSourceBlocks(gen, finalProperty1));
		DataSet<Tuple2<HR3Block, Instance>> targetBlocks = target.flatMap(new GetTargetBlocks(gen));
		sourceBlocks.join(targetBlocks).where("f0").equalTo("f0").map(tuple -> {
			final Instance s = tuple.f0.f1;
			final Instance t = tuple.f1.f1;
			final double sim = measure.getSimilarity(s, t, finalProperty1, finalProperty2);
			return new Tuple3<String, String, Double>(s.getUri(), t.getUri(), sim);
		}).returns(new TupleTypeInfo<>(TypeInformation.of(new TypeHint<String>() {
		}), TypeInformation.of(new TypeHint<String>() {
		}), TypeInformation.of(new TypeHint<Double>() {
		}))).collect().forEach(t -> {
			if (t.f2 >= threshold)
				mapping.add(t.f0, t.f1, t.f2);
		});
		return mapping;
	}

	public static class GetSourceBlocks implements FlatMapFunction<Instance, Tuple2<HR3Block, Instance>>,
			ResultTypeQueryable<Tuple2<HR3Block, Instance>> {
		private IBlockingModule gen;
		private String finalProperty1;

		public GetSourceBlocks(IBlockingModule gen, String finalProperty1) {
			super();
			this.gen = gen;
			this.finalProperty1 = finalProperty1;
		}

		@Override
		public void flatMap(Instance i, Collector<Tuple2<HR3Block, Instance>> out) throws Exception {
			for (ArrayList<Integer> sourceId : gen.getAllSourceIds(i, finalProperty1)) {
				for (ArrayList<Integer> blockToCompare : gen.getBlocksToCompare(sourceId)) {
					out.collect(HR3Block.createTuple(blockToCompare, i));
				}
			}
		}

		@Override
		public TypeInformation<Tuple2<HR3Block, Instance>> getProducedType() {
			return new TupleTypeInfo<>(TypeInformation.of(new TypeHint<HR3Block>() {
			}), TypeInformation.of(new TypeHint<Instance>() {
			}));
		}
	}

	public static class GetTargetBlocks implements FlatMapFunction<Instance, Tuple2<HR3Block, Instance>>,
			ResultTypeQueryable<Tuple2<HR3Block, Instance>> {
		private IBlockingModule gen;

		public GetTargetBlocks(IBlockingModule gen) {
			super();
			this.gen = gen;
		}

		@Override
		public void flatMap(Instance i, Collector<Tuple2<HR3Block, Instance>> out) throws Exception {
			for (ArrayList<Integer> block : gen.getAllBlockIds(i)) {
				out.collect(HR3Block.createTuple(block, i));
			}
		}

		@Override
		public TypeInformation<Tuple2<HR3Block, Instance>> getProducedType() {
			return new TupleTypeInfo<>(TypeInformation.of(new TypeHint<HR3Block>() {
			}), TypeInformation.of(new TypeHint<Instance>() {
			}));
		}
	}

	public static class HR3Block {

//		public List<Integer> id;
		public String id;

		public static Tuple2<HR3Block, Instance> createTuple(List<Integer> blockId, Instance instance) {
			HR3Block hr3Block = new HR3Block();
			hr3Block.id = Joiner.on("").join(blockId);
			return new Tuple2<>(hr3Block, instance);
		}
	}

}
