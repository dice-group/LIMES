package org.aksw.limes.core.ml.algorithm.eagle.util;

/**
 * To differentiate termination criteria for ML algorithms.
 *
 * @author Klaus Lyko
 * @version Feb 10, 2016
 */
public enum TerminationCriteria {
    iteration, // EAGLE generations others maxIteration
    duration, // using timeBased criteria, specified in millisecond: maxDuration
    quality, // a quality based approach
}
