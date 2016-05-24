package org.aksw.limes.core.controller;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory.ExecutionPlannerType;
import org.aksw.limes.core.execution.planning.planner.IPlanner;
import org.aksw.limes.core.execution.planning.planner.Planner;
import org.aksw.limes.core.execution.rewriter.Rewriter;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.execution.rewriter.RewriterFactory.RewriterFactoryType;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.ConfigurationReader;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.serializer.ISerializer;
import org.aksw.limes.core.io.serializer.SerializerFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.setting.LearningParameters;
import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

/**
 * This is the default LIMES Controller used to run the software as CLI.
 *
 * @author Kevin Dreßler
 */
public class Controller {

    private static final Logger logger = Logger.getLogger(Controller.class.getName());
    private static Options options = getOptions();

    /**
     * Take configuration file as argument and run the specified linking task.
     *
     * @param args
     *            Command line arguments
     */
    public static void main(String[] args) {
        CommandLine cl = parseCommandLine(args);
        Configuration config = getConfig(cl);
        ResultMappings mappings = getMapping(config);
        writeResults(mappings, config);
    }

    public static CommandLine parseCommandLine(String[] args) {
        CommandLineParser parser = new BasicParser();
        CommandLine cl = null;
        try {
            cl = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(ansi().fg(RED).a("Parsing error:\n\t" + e.getMessage()).reset());
            printHelp();
            System.exit(-1);
        }
        return cl;
    }

    public static Configuration getConfig(CommandLine cmd) {
        if (cmd.hasOption('h')) {
            printHelp();
            System.exit(0);
        }
        // I. Has Argument?
        if (cmd.getArgs().length < 1) {
            System.out.println(ansi().fg(RED).a("Error:\n\t Please specify a configuration file to use!").reset());
            printHelp();
            System.exit(1);
        }
        // II. Configure Logger
        // @todo Make this work
        if (cmd.hasOption('s')) {
            logger.info("Disable logging...");
//            logger.setLevel(Level.OFF);
            logger.info("Disabled logging?");
        } else if (cmd.hasOption('v')) {
            logger.setLevel(Level.INFO);
        }
        // 1. Determine appropriate ConfigurationReader
        String format;
        if (cmd.hasOption('f')) {
            format = cmd.getOptionValue("f").toLowerCase();
        } else {
            // for now just assume XML
            // @todo implement full proof ConfigurationReaderFactory to handle
            // format detection.
            format = "xml";
        }

        ConfigurationReader reader = null;
        switch (format) {
            case "xml":
                reader = new XMLConfigurationReader();
                break;
            case "rdf":
                reader = new RDFConfigurationReader();
                break;
            default:
                System.out.println(ansi().fg(RED).a("Error:\n\t Not a valid format: \"" + format + "\"!").reset());
                printHelp();
                System.exit(1);
        }

        // 2. Read configuration
        String configFileOrUri = cmd.getArgs()[0];
        return reader.read(configFileOrUri);
    }

    /**
     * Execute LIMES
     *
     * @param config
     *            LIMES configuration object
     */
    public static ResultMappings getMapping(Configuration config) {
        Mapping results = null;

        // 3. Fill Caches
        HybridCache sourceCache = HybridCache.getData(config.getSourceInfo());
        HybridCache targetCache = HybridCache.getData(config.getTargetInfo());

        // 4. Machine Learning or Planning
        boolean isAlgorithm = !config.getMlAlgorithmName().equals("");
        if (isAlgorithm) {
            try {
                results = MLPipeline.execute(sourceCache, targetCache,
                        config.getMlAlgorithmName(), config.getMlImplementationType(),
                        (LearningParameters) config.getMlParameters(), config.getMlTrainingData(), config.getMlPseudoFMeasure());
            } catch (UnsupportedMLImplementationException e) {
                e.printStackTrace();
            }
        } else {
            results = LSPipeline.execute(sourceCache, targetCache,
                    config.getMetricExpression(), config.getVerificationThreshold(),
                    config.getSourceInfo().getVar(), config.getTargetInfo().getVar(),
                    RewriterFactory.getRewriterFactoryType("default"),
                    ExecutionPlannerFactory.getExecutionPlannerType(config.getExecutionPlan()),
                    ExecutionEngineFactory.getExecutionEngineType(config.getExecutionPlan()));
        }
        Mapping acceptanceMapping = results.getSubMap(config.getAcceptanceThreshold());
        Mapping verificationMapping = MappingOperations.difference(results, acceptanceMapping);
        return new ResultMappings(verificationMapping, acceptanceMapping);
    }

    private static void writeResults(ResultMappings mappings, Configuration config) {
        String outputFormat = config.getOutputFormat();
        ISerializer output = SerializerFactory.getSerializer(outputFormat);
        output.setPrefixes(config.getPrefixes());
        output.writeToFile(mappings.getVerificationMapping(), config.getVerificationRelation(),
                config.getVerificationFile());
        output.writeToFile(mappings.getAcceptanceMapping(), config.getAcceptanceRelation(), config.getAcceptanceFile());
    }

    /**
     * Print the usage text
     */
    private static void printHelp() {
        new HelpFormatter().printHelp("limes [OPTION]... <config_file_or_uri>", options);
    }

    /**
     * Get available options for CLI
     *
     * @return Options object containing all available command line options
     */
    private static Options getOptions() {
        Options options = new Options();
        options.addOption("f", true, "Format of <config_file_or_uri>, either \"xml\" (default) or \"rdf\"");
        options.addOption("h", false, "Help");
        options.addOption("s", false, "Silent run");
        options.addOption("v", false, "Verbose run");
        return options;
    }
}
