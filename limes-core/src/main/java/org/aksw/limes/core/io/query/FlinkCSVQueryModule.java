package org.aksw.limes.core.io.query;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.preprocessing.Preprocessor;
import org.aksw.limes.core.util.DataCleaner;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlinkCSVQueryModule {

    Logger logger = LoggerFactory.getLogger(FlinkCSVQueryModule.class.getName());
    ExecutionEnvironment env;

    KBInfo kb;
    private String sep = ",";
    private List<String> properties;

    public FlinkCSVQueryModule(KBInfo kbinfo, ExecutionEnvironment env, String sep) {
        this.kb = kbinfo;
        this.env = env;
        this.sep = sep;
    }
    
    public FlinkCSVQueryModule(KBInfo kbinfo, ExecutionEnvironment env) {
        this.kb = kbinfo;
        this.env = env;
    }

    public void setSeparation(String s) {
        sep = s;
    }

    public DataSet<Instance> readFileToDataSet() throws IOException {
    	BufferedReader brTest = new BufferedReader(new FileReader(kb.getEndpoint()));
        String firstLine = brTest .readLine();
        properties = Arrays.asList(firstLine.split(sep));
        brTest.close();
    	return  env.readTextFile(kb.getEndpoint())
                .map(new LineSplitter(firstLine, sep, properties))
                .groupBy(array -> array[0])
                .reduceGroup(new InstanceCreator(properties, kb));
    }
    
    public final class LineSplitter implements MapFunction<String, String[]>{
    	public String firstLine;
    	public String sep;
    	public List<String> properties;
    	
    	public LineSplitter(String firstLine, String sep, List<String> properties) {
			this.firstLine = firstLine;
			this.sep = sep;
			this.properties = properties;
		}

		@Override
        public String[] map(String line){
//    		if(line.equals(firstLine)){
//    			return null;
//    		}

            return DataCleaner.separate(line, sep, properties.size());
//            //If it is tab-seperated it is easy
//            if(sep.equals("\t")){
//                return line.split("\t");
//            }
//            //Else use regex to to avoid splitting on commas inside quotes
//            String otherThanQuote = " [^\"] ";
//            String quotedString = String.format(" \" %s* \" ", otherThanQuote);
//            String regex = String.format("(?x) "+ // enable comments, ignore white spaces
//                    sep							+ // match seperator
//                    "(?=                       "+ // start positive look ahead
//                    "  (?:                     "+ //   start non-capturing group 1
//                    "    %s*                   "+ //     match 'otherThanQuote' zero or more times
//                    "    %s                    "+ //     match 'quotedString'
//                    "  )*                      "+ //   end group 1 and repeat it zero or more times
//                    "  %s*                     "+ //   match 'otherThanQuote'
//                    "  $                       "+ // match the end of the string
//                    ")                         ", // stop positive look ahead
//                    otherThanQuote, quotedString, otherThanQuote);
//
//            return line.split(regex, -1);
        }
    }
    
    public final class InstanceCreator implements GroupReduceFunction<String[], Instance>{
    	
    	List<String> properties;
    	KBInfo kb;
    	
		public InstanceCreator(List<String> properties, KBInfo kb) {
			this.properties = properties;
			this.kb = kb;
		}

		@Override
		public void reduce(Iterable<String[]> groupedLines, Collector<Instance> out) throws Exception {
			Instance i = null;
            String rawValue, value;
			for(String[] split : groupedLines){
				if(Arrays.asList(split).equals(properties)){
					break;
				}
				if(i == null){
					i = new Instance(split[0]);
				}
                for (String propertyLabel : kb.getProperties()) {
                    rawValue = split[properties.indexOf(propertyLabel)];
                        if (kb.getFunctions().containsKey(propertyLabel)) {
                            for (String propertyDub : kb.getFunctions().get(propertyLabel).keySet()) {
                                //functions.get(propertyLabel).get(propertyDub) gets the preprocessing chain that leads from 
                                //the propertyLabel to the propertyDub
                                value = Preprocessor.process(rawValue, kb.getFunctions().get(propertyLabel).get(propertyDub));
                                if (properties.indexOf(propertyLabel) >= 0) {
                                    i.addProperty(propertyDub, value);
                                }
                            }
                        } else {
                            i.addProperty(propertyLabel, rawValue.replaceAll(Pattern.quote("@en"), ""));
                        }
                }
			}
			if(i != null){
				out.collect(i);
			}
		}
    	
    }
}
