package org.hobbit.sdk.examples.examplebenchmark.benchmark;

import org.hobbit.core.components.AbstractEvaluationStorage;
import org.hobbit.core.data.ResultPair;
import org.hobbit.sdk.evalStorage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Simple in-memory implementation of an evaluation storage that can be used for
 * testing purposes.
 *
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 * https://github.com/hobbit-project/core/blob/master/src/main/java/org/hobbit/core/components/test/InMemoryEvaluationStore.java
 */

public class EvalStorage extends AbstractEvaluationStorage {
	
	private static final Logger logger = LoggerFactory.getLogger(EvalStorage.class);
	/**
	 * Map containing a mapping from task Ids to result pairs.
	 */
	private Map<String, ResultPair> results = Collections
		.synchronizedMap(new HashMap<String, ResultPair>());
	
	@Override
	public void init() throws Exception {
		super.init();
		logger.debug("Init()");
	}
	
	// the actual response gets transmitted here
	@Override
	public void receiveResponseData(String taskId, long timestamp, byte[] data) {
		logger.trace("receiveResponseData()->{}", new String(data));
		putResult(false, taskId, timestamp, data);
	}
	
	@Override
	public void receiveExpectedResponseData(String taskId, long timestamp, byte[] data) {
		logger.trace("receiveExpectedResponseData()->{}", new String(data));
		putResult(true, taskId, timestamp, data);
	}
	
	/**
	 * Adds the given result to the map of results.
	 *
	 * @param isExpectedResult true if the result has been received from a task generator,
	 * i.e., is the expected result for a task
	 * @param taskId id of the task
	 * @param timestamp time stamp for the task result
	 * @param data the result
	 */
	public synchronized void putResult(boolean isExpectedResult, String taskId, long timestamp,
		byte[] data) {
		ResultPairImpl pair;
		if (results.containsKey(taskId)) {
			pair = (ResultPairImpl) results.get(taskId);
		} else {
			pair = new ResultPairImpl();
			results.put(taskId, pair);
		}
		if (isExpectedResult) {
			pair.setExpected(new ResultImpl(timestamp, data));
		} else {
			pair.setActual(new ResultImpl(timestamp, data));
		}
	}
	
	@Override
	protected Iterator<ResultPair> createIterator() {
		logger.trace("createIterator()->{} items", results.size());
		return results.values().iterator();
	}
	
}

