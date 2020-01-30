package org.aksw.limes.core.controller;

import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AsynchronousServerOracle implements ActiveLearningOracle {

    private int iteration = 1;
    private int maxIteration = 10;
    private boolean stopped = false;

    AsynchronousServerOracle() {}

    AsynchronousServerOracle(int maxIteration) {
        if (maxIteration < 1) {
            maxIteration = 1;
        }
        this.maxIteration = maxIteration;
    }

    private CompletableFuture<ActiveLearningExamples> activeLearningStarted = new CompletableFuture<>();
    private CompletableFuture<AMapping> activeLearningFinished = new CompletableFuture<>();

    @Override
    public AMapping classify(ActiveLearningExamples examples) {
        activeLearningStarted.complete(examples);
        activeLearningFinished = new CompletableFuture<>();
        activeLearningFinished.thenRun(() -> {
            activeLearningStarted = new CompletableFuture<>();
            if (++iteration > maxIteration) {
                stop();
            }
        });
        return activeLearningFinished.join();
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    @Override
    public int getIteration() {
        return iteration;
    }

    public void stop() {
        stopped = true;
        activeLearningStarted.complete(new ActiveLearningExamples(MappingFactory.createDefaultMapping(), new MemoryCache(), new MemoryCache()));
    }

    public ActiveLearningExamples getExamples() {
        return activeLearningStarted.join();
    }

    public void completeClassification(List<Double> scores) {
        ActiveLearningExamples examples = activeLearningStarted.join();
        Iterator<ActiveLearningExamples.Example> exIt = examples.iterator();
        for (Double score : scores) {
            if (exIt.hasNext()) {
                exIt.next().classify(score);
            }
        }
        activeLearningFinished.complete(examples.getExampleMap());
    }

}
