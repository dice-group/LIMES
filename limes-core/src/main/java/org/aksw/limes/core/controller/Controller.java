package org.aksw.limes.core.controller;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.RED;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.planning.planner.ExecutionPlannerFactory;
import org.aksw.limes.core.execution.rewriter.RewriterFactory;
import org.aksw.limes.core.gui.LimesGUI;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.AConfigurationReader;
import org.aksw.limes.core.io.config.reader.rdf.RDFConfigurationReader;
import org.aksw.limes.core.io.config.reader.xml.XMLConfigurationReader;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.serializer.ISerializer;
import org.aksw.limes.core.io.serializer.SerializerFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the default LIMES Controller used to run the software as CLI.
 *
 * @author Kevin Dreßler
 */
public class Controller {

    public static final String DEFAULT_LOGGING_PATH = "limes.log";
    private static final int MAX_ITERATIONS_NUMBER = 10;
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    private static int serverPort = 8080;
    private static Options options = getOptions();

    /**
     * Take configuration file as argument and run the specified linking task.
     *
     * @param args
     *            Command line arguments
     */
    public static void main(String[] args) {
        // I. Configure Logger
        CommandLine cmd = parseCommandLine(args);
        System.setProperty("logFilename", cmd.hasOption('o') ? cmd.getOptionValue("o") : DEFAULT_LOGGING_PATH);
        ((org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false)).reconfigure();
        // II. Digest Options
        if (cmd.hasOption('h')) {
            printHelp();
            System.exit(0);
        } else if (cmd.hasOption('g')){
            LimesGUI.startGUI(new String[0]);
            System.exit(0);
        } else if (cmd.hasOption('s')){
            int port = serverPort;
            if (cmd.hasOption('p')) port = Integer.parseInt(cmd.getOptionValue('p'));
            SimpleServer.startServer(port);
        } else {
            // III. Has Arguments?
            if (cmd.getArgs().length < 1) {
                logger.error("Error:\n\t Please specify a configuration file to use!");
                printHelp();
                System.exit(1);
            }

            Configuration config = getConfig(cmd);
            ResultMappings mappings = getMapping(config);
            writeResults(mappings, config);
        }
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
        // 1. Determine appropriate ConfigurationReader
        String format = "xml";
        String fileNameOrUri = cmd.getArgs()[0];
        if (cmd.hasOption('f')) {
            format = cmd.getOptionValue("f").toLowerCase();
        } else if (fileNameOrUri.endsWith(".nt")
                    || fileNameOrUri.endsWith(".ttl")
                    || fileNameOrUri.endsWith(".n3")
                    || fileNameOrUri.endsWith(".rdf")) {
            format = "rdf";
        }

        AConfigurationReader reader = null;
        switch (format) {
            case "xml":
                reader = new XMLConfigurationReader(fileNameOrUri);
                break;
            case "rdf":
                reader = new RDFConfigurationReader(fileNameOrUri);
                break;
            default:
                logger.error("Error:\n\t Not a valid format: \"" + format + "\"!");
                printHelp();
                System.exit(1);
        }

        // 2. Read configuration

        return reader.read();
    }

    /**
     * Execute LIMES
     *
     * @param config
     *            LIMES configuration object
     *
     * @return Instance of ResultMapping
     *
     */
    public static ResultMappings getMapping(Configuration config) {
        AMapping results = null;

        // 3. Fill Caches
        HybridCache sourceCache = HybridCache.getData(config.getSourceInfo());
        HybridCache targetCache = HybridCache.getData(config.getTargetInfo());

        // 4. Machine Learning or Planning
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean isAlgorithm = !config.getMlAlgorithmName().equals("");
        if (isAlgorithm) {
            try {
                results = MLPipeline.execute(sourceCache, targetCache, config, config.getMlAlgorithmName(),
                        config.getMlImplementationType(), config.getMlAlgorithmParameters(),
                        config.getTrainingDataFile(), config.getMlPseudoFMeasure(), MAX_ITERATIONS_NUMBER);
            } catch (UnsupportedMLImplementationException e) {
                e.printStackTrace();
            }
        } else {
            results = LSPipeline.execute(sourceCache, targetCache, config.getMetricExpression(),
                    config.getVerificationThreshold(), config.getSourceInfo().getVar(), config.getTargetInfo().getVar(),
                    RewriterFactory.getRewriterType(config.getExecutionRewriter()),
                    ExecutionPlannerFactory.getExecutionPlannerType(config.getExecutionPlanner()),
                    ExecutionEngineFactory.getExecutionEngineType(config.getExecutionEngine()));
        }
        logger.info("Mapping task finished in " + stopWatch.getTime() + " ms");
        assert results != null;
        AMapping acceptanceMapping = results.getSubMap(config.getAcceptanceThreshold());
        AMapping verificationMapping = MappingOperations.difference(results, acceptanceMapping);
        logger.info("Mapping size: " + acceptanceMapping.size() + " (accepted) + " + verificationMapping.size()
                + " (need verification) = " + results.size() + " (total)");
        return new ResultMappings(verificationMapping, acceptanceMapping);
    }

    private static void writeResults(ResultMappings mappings, Configuration config) {
        String outputFormat = config.getOutputFormat();
        ISerializer output = SerializerFactory.createSerializer(outputFormat);
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
        options.addOption("g", false, "Run LIMES GUI");
        options.addOption("s", false, "Run LIMES Server");
        options.addOption("h", false, "Show this help");
        options.addOption("o", true, "Set path of log file. Default is 'limes.log'");
        options.addOption("f", true, "Optionally configure format of <config_file_or_uri>, either \"xml\" (default) or " +
                "\"rdf\". If not specified, LIMES tries to infer the format from file ending.");
        options.addOption("p", true, "Optionally configure HTTP server port. Only effective if -s is specified. Default port is 8080.");
        // options.addOption("s", false, "Silent run");
        // options.addOption("v", false, "Verbose run");
        return options;
    }

}
