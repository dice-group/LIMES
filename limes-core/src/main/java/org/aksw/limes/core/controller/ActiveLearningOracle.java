package org.aksw.limes.core.controller;

import org.aksw.limes.core.io.mapping.AMapping;

/**
 *
 */
public interface ActiveLearningOracle {
    AMapping classify(ActiveLearningExamples examples);
    boolean isStopped();
    void stop();
    int getIteration();
}
