package org.aksw.limes.core.measures.mapper.space.Flink;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.space.HR3Mapper;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;

public class PerformanceEval {

    public static ACache source;
    public static ACache target;
    public static DataSet<Instance> sourceDS;
    public static DataSet<Instance> targetDS;
    public static final String measureExpr = "euclidean(x.geo:lat|geo:long, y.geo:lat|geo:long)";
	final static ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
	
	private static void prepareData(String configPath){
		RDFConfigurationReader reader = new RDFConfigurationReader(configPath);
		Configuration c = reader.read();
		source = HybridCache.getData(c.getSourceInfo());
		target = HybridCache.getData(c.getTargetInfo());
    	sourceDS = env.fromCollection(source.getAllInstances());
    	targetDS = env.fromCollection(target.getAllInstances());
	}
	
	public static void main(String[] args) throws Exception{
		prepareData(args[0]);
		PrintWriter resWriter = new PrintWriter(new FileOutputStream("FlinkHR3Eval.csv"));
		resWriter.write("Iteration\tNormal\tFlink\n");
		
		for(int i = 0; i < 10; i++){
            HR3Mapper hr3m = new HR3Mapper();
            long start = System.currentTimeMillis();
            AMapping hm = hr3m.getMapping(source, target, "?x", "?y", measureExpr, 0.9);
            long finish = System.currentTimeMillis();
            long hr3res = finish - start;


            FlinkH3Mapper flinkhr3m = new FlinkH3Mapper();
            start = System.currentTimeMillis();
            AMapping fm = flinkhr3m.getMapping(sourceDS, targetDS, "?x", "?y", measureExpr, 0.9);
            finish = System.currentTimeMillis();
            long flinkhr3res = finish - start;
            System.out.println(hr3res + " " + flinkhr3res);
            resWriter.write(i + "\t" + hr3res + "\t" + flinkhr3res + "\n");
            assert(hm.equals(fm));
		}
		resWriter.close();
	}
}
