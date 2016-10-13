package org.aksw.limes.core.evaluation;

import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.junit.Test;

public class EvaluatorsInitTest {

    @Test
    public void test() {
        initializeEvaluators();
    }
    
    public Set<EvaluatorType> initializeEvaluators() {
        Set<EvaluatorType> evaluators=null;
        try {
            evaluators=new TreeSet<EvaluatorType>();
            evaluators.add(EvaluatorType.PRECISION);
            evaluators.add(EvaluatorType.RECALL);
            evaluators.add(EvaluatorType.F_MEASURE);
            evaluators.add(EvaluatorType.P_PRECISION);
            evaluators.add(EvaluatorType.P_RECALL);
            evaluators.add(EvaluatorType.PF_MEASURE);
            evaluators.add(EvaluatorType.ACCURACY);
            return evaluators;
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(true);
        return evaluators;
    }

}
