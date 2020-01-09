package org.aksw.limes.core.controller;

import org.aksw.limes.core.io.cache.Instance;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Message Classes for JSON Parsing
 */
class ServerMessage {

    protected boolean success = true;

    static class ErrorMessage extends ServerMessage {

        private Error error;

        private static class Error {
            private int code;
            private String message;
        }

        ErrorMessage(Throwable e) {
            this(-1, e.getMessage());
        }

        ErrorMessage(int code, String message) {
            this.success = false;
            this.error = new Error();
            this.error.code = code;
            this.error.message = message;
        }

    }

    static class StatusMessage extends ServerMessage {

        private Status status;

        private static class Status {
            int code;
            String description;
        }

        StatusMessage(int status, String description) {
            this.status = new Status();
            this.status.code = status;
            this.status.description = description;
        }
    }

    static class ResultsMessage extends ServerMessage {

        private List<String> availableFiles;

        ResultsMessage(List<String> availableFiles) {
            this.availableFiles = availableFiles;
        }
    }

    static class MeasuresMessage extends ServerMessage {

        private List<String> availableMeasures;

        MeasuresMessage(List<String> availableMeasures) {
            this.availableMeasures = availableMeasures;
        }
    }

    static class OperatorsMessage extends ServerMessage {

        private List<String> availableOperators;

        OperatorsMessage(List<String> availableOperators) {
            this.availableOperators = availableOperators;
        }
    }

    static class PreprocessingsMessage extends ServerMessage {

        static class PPInfo {

            private String name;
            private int minArgs;
            private int maxArgs;
            private boolean isComplex;

            PPInfo(String name, int minArgs, int maxArgs, boolean isComplex) {
                this.name = name;
                this.minArgs = minArgs;
                this.maxArgs = maxArgs;
                this.isComplex = isComplex;
            }

            public boolean isComplex() {
                return isComplex;
            }
        }

        private List<PPInfo> availablePreprocessings;

        PreprocessingsMessage(List<PPInfo> availablePreprocessings) {
            this.availablePreprocessings = availablePreprocessings;
        }
    }

    static class SubmitMessage extends ServerMessage {

        private String requestId;

        SubmitMessage(String requestId) {
            this.requestId = requestId;
        }
    }

    static class ActiveLearningMessage extends SubmitMessage {

        private int iteration;
        private List<ExampleInfo> examples = new ArrayList<>();

        ActiveLearningMessage(String requestId, AsynchronousServerOracle oracle) {
            super(requestId);
            this.iteration = oracle.getIteration();
            if (!oracle.isStopped()) {
                for (ActiveLearningExamples.Example ex : oracle.getExamples()) {
                    examples.add(new ExampleInfo(ex.getSource(), ex.getTarget()));
                }
            }
        }

        static class ExampleInfo {

            private String source;
            private String target;
            private List<ExampleInfoContext> sourceContext = new LinkedList<>();
            private List<ExampleInfoContext> targetContext = new LinkedList<>();

            ExampleInfo(Instance source, Instance target) {
                this.source = source.getUri();
                this.target = target.getUri();
                for (String predicate : source.getAllProperties()) {
                    for (String object : source.getProperty(predicate)) {
                        sourceContext.add(new ExampleInfoContext(predicate, object));
                    }
                }
                for (String predicate : target.getAllProperties()) {
                    for (String object : target.getProperty(predicate)) {
                        targetContext.add(new ExampleInfoContext(predicate, object));
                    }
                }
            }
        }

        static class ExampleInfoContext {

            private String predicate;
            private String object;

            private ExampleInfoContext(String predicate, String object) {
                this.predicate = predicate;
                this.object = object;
            }
        }
    }

    static class ScoresMessage {
        private List<Double> exampleScores;

        ScoresMessage(List<Double> exampleScores) {
            this.exampleScores = exampleScores;
        }

        public List<Double> getExampleScores() {
            return exampleScores;
        }

    }

    static class UploadMessage extends ServerMessage {

        private List<UploadInfo> uploads = new ArrayList<>();

        static class UploadInfo {

            private String partName;
            private String uploadId;

            UploadInfo(String partName, String uploadId) {
                this.partName = partName;
                this.uploadId = uploadId;
            }
        }

        UploadMessage(Map<String, String> partUploads) {
            for (Map.Entry<String, String> upload : partUploads.entrySet()) {
                this.uploads.add(new UploadInfo(upload.getKey(), upload.getValue()));
            }
        }
    }

}
