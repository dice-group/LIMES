package org.aksw.limes.core.measures.mapper.space.Flink;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class FlinkH3Mapper {
    public static int comparisons = 0;

    public static int granularity = 4;
    public static int dim;    
    public static ArrayList<Double> thresholds = new ArrayList<Double>();
    public static ArrayList<String> properties = new ArrayList<String>();

	public AMapping getMapping(DataSet<Instance> source, DataSet<Instance> target, String sourceVar, String targetVar, String expression,
			double threshold) throws Exception {
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
        
        //set appropriate blocks
        source = source.map(new SetAllBlockIds());
        source = source.map(new SetAllBlocksToCompare());
        target = target.map(new SetAllBlockIds());
        
        //comparison
//        DataSet<MappingObject> result = source.join(target)
//              .where(s -> s.getBlocksToCompare())
//              .equalTo(t -> t.getBlockIds())
//              .with(new CompareInstances(measure, property1, property2, threshold));

        DataSet<MappingObject> result = source
        		.cross(target)
        		.with((i1,i2) -> new Tuple2<Instance,Instance>(i1,i2))
        		.returns(new TypeHint<Tuple2<Instance,Instance>>(){}.getTypeInfo())
        		.flatMap(new CompareInstances(measure, property1, property2, threshold));

       List<MappingObject> tmpRes = result.collect(); 
       for(MappingObject m : tmpRes){
           mapping.add(m.sid, m.tid, m.sim);
       }
       return mapping;
	}
	
//	private static class CompareInstances implements JoinFunction<Instance, Instance, MappingObject>{
//	
//		public ISpaceMeasure measure;
//		public String property1;
//		public String property2;
//		public double threshold;
//
//
//		public CompareInstances(ISpaceMeasure measure, String property1, String property2, double threshold) {
//			this.measure = measure;
//			this.property1 = property1;
//			this.property2 = property2;
//			this.threshold = threshold;
//		}
//
//
//		@Override
//		public MappingObject join(Instance first, Instance second) throws Exception {
//            HR3FlinkTest.FlinkHR3Comparisons.add(first.getUri() + "->"+ second.getUri());
//			comparisons++;
////			System.out.println(first.getUri() + " - > " + second.getUri() + " # " + comparisons);
//            double sim = measure.getSimilarity(first, second, property1, property2);
//            if (sim >= threshold) {
//              	return new MappingObject(first.getUri(), second.getUri(),sim);
//            }
//            return null;
//		}
//	}
	private static class CompareInstances implements FlatMapFunction<Tuple2<Instance, Instance>, MappingObject>{
	
		public ISpaceMeasure measure;
		public String property1;
		public String property2;
		public double threshold;


		public CompareInstances(ISpaceMeasure measure, String property1, String property2, double threshold) {
			this.measure = measure;
			this.property1 = property1;
			this.property2 = property2;
			this.threshold = threshold;
		}


		@Override
		public void flatMap(Tuple2<Instance, Instance> st, Collector<MappingObject> out) throws Exception {
			if(st.f0.getBlocksToCompare().intersectNotEmpty(st.f1.getBlockIds())){
//                HR3FlinkTest.FlinkHR3Comparisons.add(st.f0.getUri() + "->"+ st.f1.getUri());
                Files.write(Paths.get("/tmp/Flink"),(st.f0.getUri() + "->"+ st.f1.getUri() + "\n").getBytes(), StandardOpenOption.APPEND);
                comparisons++;
    //			System.out.println(first.getUri() + " - > " + second.getUri() + " # " + comparisons);
                double sim = measure.getSimilarity(st.f0, st.f1, property1, property2);
                if (sim >= threshold) {
                      out.collect(new MappingObject(st.f0.getUri(), st.f1.getUri(),sim));
                }
			}
		}
	}
	
	
	private static class MappingObject{
		String sid;
		String tid;
		double sim;

		public MappingObject(String sid, String tid, double sim) {
			this.sid = sid;
			this.tid = tid;
			this.sim = sim;
		}
		
		
	}

	private static class SetAllBlocksToCompare implements MapFunction<Instance,Instance> {

		@Override
		public Instance map(Instance a) throws Exception {
			Set<Tuple> blocksToCompare = new HashSet<>();
			for(Tuple blockId : a.getBlockIds().ids){
				int arity = blockId.getArity();
				ArrayList<Integer> convertedBlockId = new ArrayList<>();
				for(int i = 0; i < arity; i++){
					convertedBlockId.add(blockId.getField(i));
				}
				ArrayList<ArrayList<Integer>> tmp = getBlocksToCompare(convertedBlockId);
				for(ArrayList<Integer> bid : tmp){
					blocksToCompare.add(ListToTupleConverter.convertToTuple(bid));
//                	HR3FlinkTest.FlinkHR3sourceToCompare.add(a.getUri() + " -> " + bid);
				}
			}
			a.setBlocksToCompare(blocksToCompare);
			return a;
		}
	}

    /**
     * Computes the blocks that are to be compared with a given block
     *
     * @param blockId
     *         ID of the block for which comparisons are needed
     * @return List of IDs that are to be compared
     */
    public static ArrayList<ArrayList<Integer>> getBlocksToCompare(ArrayList<Integer> blockId) {
        int dim = blockId.size();
        if (dim == 0) {
            return new ArrayList<ArrayList<Integer>>();
        }
//        if(cache.containsKey(blockId))
//        {
//            return cache.get(blockId);
//        }
        ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> hr3result = new ArrayList<ArrayList<Integer>>();
        result.add(blockId);

        ArrayList<ArrayList<Integer>> toAdd;
        ArrayList<Integer> id;

        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < Math.pow(2 * granularity + 1, i); j++) {
                //System.out.println("Result"+result);
                id = result.get(j);
                //System.out.println(j+" -> "+id);
                toAdd = new ArrayList<ArrayList<Integer>>();
                for (int k = 0; k < 2 * granularity; k++) {
                    toAdd.add(new ArrayList<Integer>());
                }
                //System.out.println(toAdd.size());
                for (int k = 0; k < dim; k++) {
                    if (k != i) {
                        for (int l = 0; l < 2 * granularity; l++) {
                            toAdd.get(l).add(id.get(k));
                        }
                    } else {
                        for (int l = 0; l < granularity; l++) {
                            toAdd.get(l).add(id.get(k) - (l + 1));
                        }
                        for (int l = 0; l < granularity; l++) {
                            toAdd.get(l + granularity).add(id.get(k) + l + 1);
                        }
                    }
                    //System.out.println(i+". "+(k+1)+". "+toAdd);
                }
                //Merge results
                for (int l = 0; l < 2 * granularity; l++) {
                    result.add(toAdd.get(l));
                }
            }
        }

        //now run hr3 check
        int alphaPowered = (int) Math.pow(granularity, dim);
        ArrayList<Integer> block;
        int hr3Index;
        int index;
        for (int i = 0; i < result.size(); i++) {
            hr3Index = 0;
            block = result.get(i);
            for (int j = 0; j < dim; j++) {
                if (block.get(j) == blockId.get(j)) {
                    hr3Index = 0;
                    break;
                } else {
                    index = (Math.abs(blockId.get(j) - block.get(j)) - 1);
                    hr3Index = hr3Index + (int) Math.pow(index, dim);
                }
            }
            if (hr3Index < alphaPowered) hr3result.add(block);
        }

        return hr3result;
    }
	
	private static class SetAllBlockIds implements MapFunction<Instance,Instance> {
        @Override
        public Instance map(Instance a) throws Exception {
            int blockId;
            Set<Tuple> blockIds = new HashSet<>();
            ArrayList<ArrayList<Double>> combinations = new ArrayList<ArrayList<Double>>();
            //get all property combinations
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
