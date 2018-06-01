package org.hobbit.sdk.examples.examplebenchmark.benchmark;

import org.hobbit.core.components.AbstractDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DataGenerator extends AbstractDataGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(DataGenerator.class);
	
	@Override
	public void init() throws Exception {
		// Always init the super class first!
		super.init();
		logger.debug("Init()");
		// Your initialization code comes here...
	}
	
	@Override
	protected void generateData() throws Exception {
		// Create your data inside this method. You might want to use the
		// id of this data generator and the number of all data generators
		// running in parallel.
		int dataGeneratorId = getGeneratorId();
		int numberOfGenerators = getNumberOfGenerators();
		
		logger.debug("generateData()");
		String data;
		int i = 0;
		while (i < 1) {
			i++;
			// Create your data here
			data = new String("data_" + String.valueOf(i));
			
			// the data can be sent to the task generator(s) ...
			logger.trace("sendDataToTaskGenerator()->{}", data);
			sendDataToTaskGenerator(data.getBytes());
			// an to system adapter
			//logger.debug("sendDataToSystemAdapter()->{}",data);
			//sendDataToSystemAdapter(data.getBytes());
		}
	}
	
	@Override
	public void close() throws IOException {
		// Free the resources you requested here
		logger.debug("close()");
		// Always close the super class after yours!
		super.close();
	}
	
}