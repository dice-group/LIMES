package org.aksw.limes.core.measures.mapper.space.Flink;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.measures.mapper.space.HR3Mapper;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple3;

public class PerformanceEval {

    public static ACache source;
    public static ACache target;
    public static DataSet<Instance> sourceDS;
    public static DataSet<Instance> targetDS;
    public static String measureExpr = "";
	final static ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
	
	private static void prepareData(String configPath){
		RDFConfigurationReader reader = new RDFConfigurationReader(configPath);
		Configuration c = reader.read();
		source = HybridCache.getData(c.getSourceInfo());
		target = HybridCache.getData(c.getTargetInfo());
//    	sourceDS = env.fromCollection(source.getAllInstances());
//    	targetDS = env.fromCollection(target.getAllInstances());
    	sourceDS = readInstancesFromCSV(c.getSourceInfo().getEndpoint()); 
    	targetDS = readInstancesFromCSV(c.getTargetInfo().getEndpoint()); 
    	measureExpr = c.getMetricExpression();
	}
	
	private static DataSet<Instance> readInstancesFromCSV(String path){
		DataSet<Tuple3<String,Double,Double>> lines = (DataSet<Tuple3<String, Double, Double>>) env.readCsvFile(path).ignoreFirstLine().types(String.class, Double.class, Double.class);
		return lines.map(line -> {
			Instance i = new Instance(line.f0);
			i.addProperty("lat",line.f1.toString());
			i.addProperty("long",line.f2.toString());
			return i;
		}).returns(new TypeHint<Instance>() {});
	}
	
	public static void main(String[] args) throws Exception{
		prepareData(args[0]);
		PrintWriter resWriter = new PrintWriter(new FileOutputStream("NormalHR3Eval.csv"));
		resWriter.write("Iteration\tNormal\n");
		resWriter.close();
		long start, finish;
		
        HR3Mapper hr3m = new HR3Mapper();
		for(int i = 0; i < 10; i++){
    		resWriter = new PrintWriter(new FileOutputStream("NormalHR3Eval.csv", true));
            start = System.currentTimeMillis();
            hr3m.getMapping(source, target, "?x", "?y", measureExpr, 0.9);
            finish = System.currentTimeMillis();
            long hr3res = finish - start;
            System.out.println("Normal: " + hr3res);
            resWriter.write(i + "\t" + hr3res + "\n");
    		resWriter.close();
		}
		resWriter = new PrintWriter(new FileOutputStream("FlinkHR3Eval.csv"));
		resWriter.write("Iteration\tFlink\n");
		resWriter.close();
        FlinkHR3Mapper flinkhr3m = new FlinkHR3Mapper(args[1]);
		for(int i = 0; i < 10; i++){
			resWriter = new PrintWriter(new FileOutputStream("FlinkHR3Eval.csv", true));
            start = System.currentTimeMillis();
            flinkhr3m.getMapping(sourceDS, targetDS, "?x", "?y", measureExpr, 0.9);
            finish = System.currentTimeMillis();
            long flinkhr3res = finish - start;
            resWriter.write(i + "\t" + flinkhr3res + "\n");
    		resWriter.close();
		}
	}
}
