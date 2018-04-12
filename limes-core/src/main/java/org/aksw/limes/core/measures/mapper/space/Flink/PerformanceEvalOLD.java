package org.aksw.limes.core.measures.mapper.space.Flink;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.writer.CSVMappingWriter;
import org.aksw.limes.core.measures.mapper.space.HR3Mapper;

public class PerformanceEvalOLD {

	public static ACache source;
	public static ACache target;
	public static String measureExpr = "";
	public static double threshold;

	private static void prepareDataOLD(String configPath) {
		RDFConfigurationReader reader = new RDFConfigurationReader(configPath);
		Configuration c = reader.read();
		source = HybridCache.getData(c.getSourceInfo());
		target = HybridCache.getData(c.getTargetInfo());
		measureExpr = c.getMetricExpression();
		threshold = c.getAcceptanceThreshold();
	}

	public static void main(String[] args) throws Exception {
		prepareDataOLD(args[0]);
		PrintWriter resWriter = new PrintWriter(new FileOutputStream("NormalHR3Eval.csv"));
		resWriter.write("Iteration\tNormal\n");
		resWriter.close();
//
		long start, finish;
		HR3Mapper hr3m = new HR3Mapper();
		for (int i = 0; i < 10; i++) {
			HR3Mapper.comparisons = 0;
			resWriter = new PrintWriter(new FileOutputStream("NormalHR3Eval.csv", true));
			start = System.currentTimeMillis();
			AMapping links = hr3m.getMapping(source, target, "?x", "?y", measureExpr, threshold);
			System.out.println("link size: " + links.size());
			finish = System.currentTimeMillis();
			long hr3res = finish - start;
			System.out.println("Normal: " + hr3res);
			System.out.println("====Comparisons: " + HR3Mapper.comparisons);
			resWriter.write(i + "\t" + hr3res + "\n");
			resWriter.close();
                CSVMappingWriter linkWriter = new CSVMappingWriter();
                linkWriter.write(links, "NormalHR3links.csv");
		}
	}
}
