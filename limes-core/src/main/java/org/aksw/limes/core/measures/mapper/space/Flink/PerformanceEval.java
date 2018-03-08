package org.aksw.limes.core.measures.mapper.space.Flink;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.writer.CSVMappingWriter;
import org.aksw.limes.core.measures.mapper.space.HR3Mapper;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceEval {

    public static ACache source;
    public static ACache target;
    public static DataSet<Instance> sourceDS;
    public static DataSet<Instance> targetDS;
    public static String measureExpr = "";
    public static double threshold;
	final static ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
    public static Logger logger = LoggerFactory.getLogger(PerformanceEval.class);
	
	private static void prepareDataFLINK(String configPath) throws Exception{
		RDFConfigurationReader reader = new RDFConfigurationReader(configPath);
		Configuration c = reader.read();
    	sourceDS = readInstancesFromCSV(c.getSourceInfo().getEndpoint()); 
    	targetDS = readInstancesFromCSV(c.getTargetInfo().getEndpoint()); 
    	measureExpr = c.getMetricExpression();
    	threshold = c.getAcceptanceThreshold();
	}

	private static void prepareDataOLD(String configPath){
		RDFConfigurationReader reader = new RDFConfigurationReader(configPath);
		Configuration c = reader.read();
		source = HybridCache.getData(c.getSourceInfo());
		target = HybridCache.getData(c.getTargetInfo());
    	measureExpr = c.getMetricExpression();
    	threshold = c.getAcceptanceThreshold();
	}
	
	private static DataSet<Instance> readInstancesFromCSV(String path){
		System.out.println(path);
		DataSet<Tuple3<String,Double,Double>> lines = (DataSet<Tuple3<String, Double, Double>>) env.readCsvFile(path).ignoreFirstLine().types(String.class, Double.class, Double.class);
		return lines.map(line -> {
			Instance i = new Instance(line.f0);
			i.addProperty("lat",line.f1.toString());
			i.addProperty("long",line.f2.toString());
			return i;
		}).returns(new TypeHint<Instance>() {});
	}
	
	public static void main(String[] args) throws Exception{
		ParameterTool parameter = ParameterTool.fromArgs(args);
		if(parameter.getRequired("mode").equals("normal")){
			prepareDataOLD(parameter.getRequired("input"));
            PrintWriter resWriter = new PrintWriter(new FileOutputStream("NormalHR3Eval.csv"));
            resWriter.write("Iteration\tNormal\n");
            resWriter.close();

            long start, finish;
            HR3Mapper hr3m = new HR3Mapper();
            for(int i = 0; i < 10; i++){
                resWriter = new PrintWriter(new FileOutputStream("NormalHR3Eval.csv", true));
                start = System.currentTimeMillis();
                AMapping links = hr3m.getMapping(source, target, "?x", "?y", measureExpr, threshold);
                logger.error("link size: " + links.size());
                finish = System.currentTimeMillis();
                long hr3res = finish - start;
                System.out.println("Normal: " + hr3res);
//                resWriter.write(i + "\t" + hr3res + "\n");
                resWriter.close();
                CSVMappingWriter linkWriter = new CSVMappingWriter();
                linkWriter.write(links, "NormalHR3links.csv");
            }
		}else{
			prepareDataFLINK(parameter.getRequired("input"));
            PrintWriter resWriter = new PrintWriter(new FileOutputStream("FlinkHR3Eval.csv"));
            resWriter.write("Iteration\tFlink\n");
            resWriter.close();
            FlinkHR3Mapper flinkhr3m = new FlinkHR3Mapper();
//            for(int i = 0; i < 10; i++){
                resWriter = new PrintWriter(new FileOutputStream("FlinkHR3Eval.csv", true));
                long start = System.currentTimeMillis();
                AMapping links = flinkhr3m.getMapping(sourceDS, targetDS, "?x", "?y", measureExpr, 0.9);
                long finish = System.currentTimeMillis();
                long flinkhr3res = finish - start;
                logger.error("link size: " + links.size());
//                resWriter.write(i + "\t" + flinkhr3res + "\n");
                resWriter.close();
                CSVMappingWriter linkWriter = new CSVMappingWriter();
                linkWriter.write(links, "FlinkHR3links.csv");
                logger.info("\n\n ====Comparisons: " + FlinkHR3Mapper.comparisons + "\n\n");
//            }
        }
	}
}
