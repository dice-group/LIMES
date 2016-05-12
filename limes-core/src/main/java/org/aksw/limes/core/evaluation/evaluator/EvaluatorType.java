package org.aksw.limes.core.evaluation.evaluator;

/**
 * This enumeration represents the qualitative measures list used in the framework.
 * They represent an input to the measure factory to retrieve a measure object to
 * evaluate a generated mapping
 * 
 * @author mofeed
 * @version 1.0
 */
public enum EvaluatorType {
	PRECISION, RECALL, F_MEASURE, PF_MEASURE, P_PRECISION, P_RECALL, ACCURACY, AUC, ALL
}
