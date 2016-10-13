package org.aksw.limes.core.evaluation.evaluator;

/**
 * This enumeration represents the qualitative measures list used in the framework.<br> They represent an input to the measure factory to retrieve a measure object to
 * evaluate a generated mapping
 *
 * @author Mofeed Hassan (mounir@informatik.uni-leipzig.de)
 * @version 1.0
 * @since 1.0
 */
public enum EvaluatorType {
    PRECISION, RECALL, F_MEASURE, P_PRECISION, P_RECALL, PF_MEASURE, ACCURACY, AUC, PR_PRECISION, PR_RECALL, PRF_MEASURE, ALL
}
