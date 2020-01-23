package org.aksw.limes.core.controller;

import org.aksw.limes.core.io.mapping.AMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 */
public class ConsoleOracle implements ActiveLearningOracle {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleOracle.class);

    private boolean stopped = false;

    private final int maxIterations;

    private int i = 0;

    public ConsoleOracle(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Override
    public AMapping classify(ActiveLearningExamples examples) {
        Scanner scan = new Scanner(System.in);
        double rating;
        String reply, evaluationMsg;
        if (++i >= maxIterations) {
            stopped = true;
        }
        logger.info("To rate the " + i + ". set of examples, write 'r' and press enter.\n" +
                "To quit learning at this point and write out the mapping, write 'q' and press enter.\n" +
                "For rating examples, use numbers in [-1,+1].\n" +
                "\t(-1 := strong negative example, +1 := strong positive example)");
        reply = scan.next();
        if (reply.trim().equals("q")) {
            stopped = true;
        } else {
            int j = 0;
            for (ActiveLearningExamples.Example ex : examples) {
                boolean rated = false;
                j++;
                do {
                    evaluationMsg = "Example #" + i + "." + j + ": (" + ex.getSource().getUri() + ", " + ex.getTarget().getUri() + ")";
                    try {
                        logger.info(evaluationMsg);
                        rating = scan.nextDouble();
                        if (rating >= -1.0d && rating <= 1.0d) {
                            ex.classify(rating);
                            rated = true;
                        } else {
                            logger.error("Input number out of range [-1,+1], please try again...");
                        }
                    } catch (NoSuchElementException e) {
                        logger.error("Input did not match floating point number, please try again...");
                        scan.next();
                    }
                } while (!rated);
            }
        }
        return examples.getExampleMap();
    }

    public boolean isStopped() {
        return stopped;
    }

    @Override
    public void stop() {
        this.stopped = true;
    }

    @Override
    public int getIteration() {
        return i;
    }
}
