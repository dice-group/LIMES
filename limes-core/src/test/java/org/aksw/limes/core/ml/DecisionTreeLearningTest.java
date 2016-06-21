package org.aksw.limes.core.ml;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTreeLearning;
import org.aksw.limes.core.ml.oldalgorithm.MLModel;
import org.junit.Test;

public class DecisionTreeLearningTest {

    @Test
    public void DTLTest() {
	EvaluationData evalData = DataSetChooser.getData("ABTBUY");
	Configuration config = evalData.getConfigReader().read();
	DecisionTreeLearning dtl = new DecisionTreeLearning(config);
	dtl.setPropertyMapping(evalData.getPropertyMapping());

	MLModel model = null;
	try {
	    dtl.init(null, evalData.getSourceCache(), evalData.getTargetCache());
	    Method activeLearn = null;
	    Class[] parameterTypes = {};
	    activeLearn = dtl.getClass().getDeclaredMethod("activeLearn", parameterTypes);
	    activeLearn.setAccessible(true);
	    Object[] parameters = {};
	    model = (MLModel) activeLearn.invoke(dtl, parameters);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	assertEquals("(trigrams(x.name, y.name)|0.89, 0.888889, null, null)", model.getLinkSpecification().toStringOneLine());
	dtl.predict(evalData.getSourceCache(), evalData.getTargetCache(), model);
    }
}
