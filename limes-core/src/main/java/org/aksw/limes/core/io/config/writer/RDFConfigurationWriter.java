package org.aksw.limes.core.io.config.writer;

import java.io.FileOutputStream;
import java.io.IOException;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.reader.rdf.LIMES;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Nov 12, 2015
 */
public class RDFConfigurationWriter implements IConfigurationWriter {
    private static final Logger logger = LoggerFactory.getLogger(RDFConfigurationWriter.class.getName());

    public static void writeModel(Model model, String format, String outputFile) throws IOException {
        logger.info("Saving dataset to " + outputFile + " ...");
        long starTime = System.currentTimeMillis();
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        model.write(fileOutputStream, format);
        fileOutputStream.close();
        logger.info("Saving file done in " + (System.currentTimeMillis() - starTime) + "ms.");
    }

    /* (non-Javadoc)
     * @see org.aksw.limes.core.io.config.writer.IConfigurationWriter#write(org.aksw.limes.core.io.config.Configuration, java.lang.String)
     */
    @Override
    public void write(Configuration configuration, String outputFile) throws IOException {
        String format = outputFile.substring(outputFile.indexOf(".") + 1).trim().toLowerCase();
        switch (format) {
            case "n3":
            case "nt":
                write(configuration, outputFile, "N-TRIPLE");
                break;
            case "ttl":
                write(configuration, outputFile, "TTL");
                break;
            case "rdf":
                write(configuration, outputFile, null);
                break;
            case "jsonld":
                write(configuration, outputFile, "JSON-LD");
                break;
            default:
                logger.error("Serialization " + format + " is not yet implemented, exit with error!");
                throw new IOException("Serialization " + format + " is not yet implemented!");
        }
    }

    /* (non-Javadoc)
     * @see org.aksw.limes.core.io.config.writer.IConfigurationWriter#write(org.aksw.limes.core.io.config.Configuration, java.lang.String, java.lang.String)
     */
    @Override
    public void write(Configuration configuration, String outputFile, String format) throws IOException {
        Model m = ModelFactory.createDefaultModel();
        String uri = LIMES.uri + System.currentTimeMillis();
        Resource s = ResourceFactory.createResource(uri);
        m.add(s, RDF.type, LIMES.LimesSpecs);

        // Prefixes
        m.setNsPrefixes(configuration.getPrefixes());
        m.setNsPrefix(LIMES.prefix, LIMES.uri);
        m.setNsPrefix("owl", OWL.NS);
        m.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");

        // 1. Source
        Resource source = ResourceFactory.createResource(uri + "_source");
        m.add(s, LIMES.hasSource, source);
        m.add(source, RDF.type, LIMES.SourceDataset);
        m.add(source, RDFS.label, configuration.getSourceInfo().getId());
        m.add(source, LIMES.endPoint, ResourceFactory.createResource(configuration.getSourceInfo().getEndpoint()));
        m.add(source, LIMES.type, String.valueOf(configuration.getSourceInfo().getType()));
        m.add(source, LIMES.variable, configuration.getSourceInfo().getVar());
        m.add(source, LIMES.pageSize, String.valueOf(configuration.getSourceInfo().getPageSize()));
        for (String r : configuration.getSourceInfo().getRestrictions()) {
            m.add(source, LIMES.restriction, r);
        }
        for (String p : configuration.getSourceInfo().getProperties()) {
            m.add(source, LIMES.property, p);
        }

        // 2. Target
        Resource target = ResourceFactory.createResource(uri + "_target");
        m.add(s, LIMES.hasTarget, target);
        m.add(target, RDF.type, LIMES.TargetDataset);
        m.add(target, RDFS.label, configuration.getTargetInfo().getId());
        m.add(target, LIMES.endPoint, ResourceFactory.createResource(configuration.getTargetInfo().getEndpoint()));
        m.add(target, LIMES.type, String.valueOf(configuration.getTargetInfo().getType()));
        m.add(target, LIMES.variable, configuration.getTargetInfo().getVar() + "");
        m.add(target, LIMES.pageSize, String.valueOf(configuration.getTargetInfo().getPageSize()));
        for (String r : configuration.getTargetInfo().getRestrictions()) {
            m.add(target, LIMES.restriction, r);
        }
        for (String p : configuration.getTargetInfo().getProperties()) {
            m.add(target, LIMES.property, p);
        }

        // 3. Metric
        Resource metric = ResourceFactory.createResource(uri + "_metric");
        m.add(s, LIMES.hasMetric, metric);
        m.add(metric, RDF.type, LIMES.Metric);
        m.add(metric, LIMES.expression, configuration.getMetricExpression());


        //4. ACCEPTANCE file and conditions
        Resource acceptance = ResourceFactory.createResource(uri + "_acceptance");
        m.add(s, LIMES.hasAcceptance, acceptance);
        m.add(acceptance, RDF.type, LIMES.Acceptance);
        m.add(acceptance, LIMES.threshold, String.valueOf(configuration.getAcceptanceThreshold()));
        m.add(acceptance, LIMES.file, ResourceFactory.createResource(configuration.getAcceptanceFile()));
        m.add(acceptance, LIMES.relation, createResource(m, configuration.getAcceptanceRelation()));

        //5. VERIFICATION file and conditions
        Resource review = ResourceFactory.createResource(uri + "_review");
        m.add(s, LIMES.hasReview, review);
        m.add(review, RDF.type, LIMES.Review); 
        m.add(review, LIMES.threshold, String.valueOf(configuration.getVerificationThreshold()));
        m.add(review, LIMES.file, ResourceFactory.createResource(configuration.getVerificationFile()));
        m.add(review, LIMES.relation, createResource(m, configuration.getVerificationRelation()));

        //6. EXECUTION
        m.add(s, LIMES.executionPlanner, configuration.getExecutionPlanner());
        m.add(s, LIMES.executionRewriter, configuration.getExecutionRewriter());
        m.add(s, LIMES.executionEngine, configuration.getExecutionEngine());

        //7. TILING if necessary
        m.add(s, LIMES.granularity, String.valueOf(configuration.getGranularity()));

        //8. OUTPUT format
        if (configuration.getOutputFormat() != null) {
            m.add(s, LIMES.outputFormat, configuration.getOutputFormat());
        }
        writeModel(m, format, outputFile);
    }

    private Resource createResource(Model m, String p) {
        if (p.contains(":")) {
            String pPrefix = p.substring(0, p.indexOf(":"));
            if (!m.getNsPrefixMap().containsKey(pPrefix)) {
                logger.error("Undefined prefix " + pPrefix);
            }
            String pPrefixUri = m.getNsPrefixMap().get(pPrefix);
            p = p.replace(":", "").replace(pPrefix, pPrefixUri);
        }
        return ResourceFactory.createResource(p);
    }

}
