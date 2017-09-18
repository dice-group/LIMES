package org.aksw.limes.core.io.cache;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.space.Flink.FlinkH3Mapper;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;

public class Test {

    public static ACache source;
    public static  ACache target;
    
    public static DataSet<Instance> sourceDS;
    public static DataSet<Instance> targetDS;
	final static ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

	public static void main(String[] args) throws Exception {
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		// get input data
		// CsvReader reader =
		// env.readCsvFile("/home/ohdorno/git/LIMES-dev2/limes-core/src/main/resources/datasets/dailymed-drugbank-ingredients/source.csv")
		// .fieldDelimiter("\t");
		// DataSet<Tuple2<String,String>> text = reader.types(String.class,
		// String.class);
		// List<List<Integer>> bids =
		// Arrays.asList(Arrays.asList(2),Arrays.asList(10),Arrays.asList(5));
		// Instance i1 = new Instance("i1");
		// i1.setBlockIds(bids);
		// List<List<Integer>> bids2 =
		// Arrays.asList(Arrays.asList(2),Arrays.asList(1),Arrays.asList(5));
		// Instance i2 = new Instance("i2");
		// i2.setBlockIds(bids2);
		// DataSet<Instance> ds = env.fromElements(i1);
		// ds.groupBy("getBlockIds")
		// .reduceGroup(new GroupReduceFunction<Instance, Double>() {
		// @Override
		// public void reduce(Iterable<Instance> values, Collector<Double> out)
		// throws Exception {
		// out.collect(1.0);
		// }
		// });
		// env.execute();
		// List<Tuple> sbids = Arrays.asList(new Tuple2<Integer,
		// Integer>(1,2),new Tuple2<Integer, Integer>(2,3));
		// List<Tuple> sbids2 = Arrays.asList(new Tuple2<Integer,
		// Integer>(2,3));
		// List<Tuple> sbids3 = Arrays.asList(new Tuple2<Integer,
		// Integer>(1,2),new Tuple2<Integer, Integer>(5,4));
		// Instance si1 = new Instance("S1");
		// si1.setBlockIds(sbids);
		// Instance si2 = new Instance("S2");
		// si2.setBlockIds(sbids2);
		// Instance si3 = new Instance("S3");
		// si3.setBlockIds(sbids3);
		// DataSet<Instance> source = env.fromElements(si1,si2,si3);
		//
		// List<Tuple> tbids = Arrays.asList(new Tuple2<Integer,
		// Integer>(1,2),new Tuple2<Integer, Integer>(2,3));
		// List<Tuple> tbids2 = Arrays.asList(new Tuple2<Integer,
		// Integer>(2,3));
		// List<Tuple> tbids3 = Arrays.asList(new Tuple2<Integer,
		// Integer>(1,2),new Tuple2<Integer, Integer>(5,4));
		// Instance ti1 = new Instance("T1");
		// ti1.setBlockIds(tbids);
		// Instance ti2 = new Instance("T2");
		// ti2.setBlockIds(tbids2);
		// Instance ti3 = new Instance("T3");
		// ti3.setBlockIds(tbids3);
		// DataSet<Instance> target = env.fromElements(ti1,ti2,ti3);
		//
		// source.map(new MapFunction<Instance,Tuple2<Instance,Set<Tuple>>>(){
		// //BlocksToCompare
		// @Override
		// public Tuple2<Instance,Set<Tuple>> map(Instance i) throws Exception {
		// return new Tuple2<Instance,Set<Tuple>>(i,new
		// HashSet<Tuple>(i.getBlockIds()));
		// }
		//
		// }).join(target.map(new
		// MapFunction<Instance,Tuple2<Set<Tuple>,Instance>>(){
		//
		// @Override
		// public Tuple2<Set<Tuple>, Instance> map(Instance i) throws Exception
		// {
		// return new Tuple2<Set<Tuple>, Instance>(new
		// HashSet<Tuple>(i.getBlockIds()),i);
		// }
		//
		// }))
		// .where(new KeySelector<Tuple2<Instance,Set<Tuple>>,
		// Tuple2<Set<Tuple>,Instance>>() {
		//
		// @Override
		// public Tuple2<Set<Tuple>, Instance> getKey(Tuple2<Instance,
		// Set<Tuple>> value) throws Exception {
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// });
//		ArrayList<ArrayList<Integer>> l = new ArrayList<>();
//		l.add(new ArrayList<>());
//		l.get(0).add(0);
//		l.get(0).add(0);
//		l.add(new ArrayList<>());
//		l.get(1).add(0);
//		l.get(1).add(1);
//		l.add(new ArrayList<>());
//		l.get(2).add(1);
//		l.get(2).add(0);
//		l.add(new ArrayList<>());
//		l.get(3).add(1);
//		l.get(3).add(1);
//		System.out.println(l);
//		CustomInstance i1 = new CustomInstance("i1", new Blocks(new HashSet<>(Arrays.asList(new Tuple2<Integer, Integer>(1,2),new Tuple2<Integer, Integer>(2,3)))));
//		CustomInstance i4 = new CustomInstance("i4", new Blocks(new HashSet<>(Arrays.asList(new Tuple2<Integer, Integer>(3,5),new Tuple2<Integer, Integer>(2,3)))));
//		CustomInstance i2 = new CustomInstance("i2", new Blocks(new HashSet<>(Arrays.asList(new Tuple2<Integer, Integer>(1,2)))));
//		CustomInstance i3 = new CustomInstance("i3", new Blocks(new HashSet<>(Arrays.asList(new Tuple2<Integer, Integer>(3,5)))));
//		DataSet<CustomInstance> left = env.fromElements(i1,i4);
//		DataSet<CustomInstance> right = env.fromElements(i2,i3);
//		DataSet<String> output = left.join(right)
//		    .where(i -> i.blocks)
//            .equalTo(i -> i.blocks)
//		    .with(new JoinFunction<CustomInstance,CustomInstance,String>() {
//
//				@Override
//				public String join(CustomInstance first, CustomInstance second) throws Exception {
//					return first.name + "->" + second.name;
//				}
//			});
//		output.print();
    	source = HybridCache.loadFromFile(new File("/home/ohdorno/git/LIMES-dev2/limes-core/cache/-1605365096.ser"));
    	target = HybridCache.loadFromFile(new File("/home/ohdorno/git/LIMES-dev2/limes-core/cache/1412794763.ser"));
        sourceDS = cacheToDS(source); 
        targetDS = cacheToDS(target);

        Path path = Paths.get("/tmp/CrossTest.csv");
        Files.write(path, "Cross\n".getBytes());
    	String measureExpr = "euclidean(x.geo:lat|geo:long, y.geo:lat|geo:long)";
    	for(int i = 0; i < 10; i++){
            FlinkH3Mapper flinkhr3m = new FlinkH3Mapper();
            long flinkStart = System.currentTimeMillis();
            AMapping flinkM = flinkhr3m.getMapping(sourceDS, targetDS, "?x", "?y", measureExpr, 0.9);
            long flinkStop = System.currentTimeMillis();
            Files.write(path, ((flinkStop - flinkStart) + "\n").getBytes(), StandardOpenOption.APPEND);
    	}
    	

	}

    private static DataSet<Instance> cacheToDS(ACache c){
    	return env.fromCollection(c.getAllInstances());
    }
//		public static class CustomInstance {
//			public String name;
//			public Blocks blocks;
//			
//
//			public CustomInstance(String name, Blocks blocks) {
//				this.name = name;
//				this.blocks = blocks;
//			}
//		}
//		private static class Blocks implements Comparable<Blocks>{
//				public Set<Tuple> ids;
//
//				Blocks(Set<Tuple> _ids) {
//					ids = _ids;
//				}
//
//				public <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
//					Set<T> tmp = new HashSet<T>();
//					for (T x : setA)
//						if (setB.contains(x))
//							tmp.add(x);
//					return tmp;
//				}
//
//				@Override
//				public boolean equals(Object obj) {
//					if (obj instanceof Blocks) {
//						if (intersection(((Blocks) obj).ids, this.ids).size() != 0) {
//							return true;
//						}
//					}
//					return false;
//				}
//				
//				@Override
//				public int hashCode() {
//				return 1;
//				}
//
//				@Override
//				public int compareTo(Blocks o) {
//					if (intersection(o.ids, this.ids).size() != 0) {
//							return 0;
//					}
//					if(o.ids.size() < this.ids.size()){
//						return 1;
//					}
//					return -1;
//				}
//
//
//			}
}
