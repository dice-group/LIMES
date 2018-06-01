package org.hobbit.sdk.examples.examplebenchmark.system;

import org.hobbit.core.components.AbstractSystemAdapter;
import org.hobbit.sdk.JenaKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class SystemAdapter extends AbstractSystemAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(SystemAdapter.class);
	private static JenaKeyValue parameters;
	
	@Override
	public void init() throws Exception {
		super.init();
		logger.debug("Init()");
		// Your initialization code comes here...
		parameters = new JenaKeyValue.Builder().buildFrom(systemParamModel);
		logger.debug("SystemModel: " + parameters.encodeToString());
		// You can access the RDF model this.systemParamModel to retrieve meta data about this system adapter
	}
	
	@Override
	public void receiveGeneratedData(byte[] data) {
		// handle the incoming data as described in the benchmark description
		String dataStr = new String(data);
		logger.trace("receiveGeneratedData(" + new String(data) + "): " + dataStr);
		
		
	}
	
	@Override
	public void receiveGeneratedTask(String taskId, byte[] data) {
		// handle the incoming task and create a result
		String result = "result_" + taskId;
		logger.trace("receiveGeneratedTask({})->{}", taskId, new String(data));
		
		// Send the result to the evaluation storage
		try {
			logger.trace("sendResultToEvalStorage({})->{}", taskId, result);
			sendResultToEvalStorage(taskId, result.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
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

