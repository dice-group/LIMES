package org.aksw.limes.core.measures.measure.graphs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import slib.sglib.algo.graph.utils.GAction;
import slib.sglib.algo.graph.utils.GActionType;
import slib.sglib.io.conf.GDataConf;
import slib.sglib.io.conf.GraphConf;
import slib.sglib.io.loader.GraphLoaderGeneric;
import slib.sglib.io.loader.annot.GraphLoader_TSVannot;
import slib.sglib.io.util.GFormat;
import slib.sglib.model.graph.G;
import slib.sglib.model.graph.utils.Direction;
import slib.sglib.model.impl.repo.URIFactoryMemory;
import slib.sglib.model.repo.URIFactory;

public class BenchmarkBuilder {

    public static void main(String[] args) {

        int[] size_benchmarks = {1000, 10000};//, 1000000, 100000000}; // size of the benchmark series
        int nbFiles = 3; // number of files to generate per series

        // the directory in which the data are and the results will be generated
        //String root_dir = "resources/data/test/go/";
        String root_dir = "C:\\Users\\onlym\\Documents\\GitHub\\LIMES\\limes-core\\src\\test\\resources\\";
        //root_dir = "./limes-core/src/test/resources/go";
        String onto_dir = root_dir + "onto/";
        String annot_dir = root_dir + "annot/";
        String benchmark_dir = root_dir + "benchmarks/";
        // prefix of the paths
        String file_prefix = benchmark_dir + "benchmark";


        try {
            // We generate the benchmark using the SML API

            String ns_go = "http://purl.org/obo/owl/GO#";
            String ns_gene = "http://gene/";

            URIFactory uriFactory = URIFactoryMemory.getSingleton();
            uriFactory.loadNamespacePrefix("GO", ns_go);


            GraphConf goConf = new GraphConf(uriFactory.createURI("http://gene_ontology"));
            goConf.addGDataConf(new GDataConf(GFormat.RDF_XML, onto_dir + "go_weekly-termdb.owl"));

            GDataConf annotConf = new GDataConf(GFormat.TSV_ANNOT, annot_dir + "dump_orgHsegGO_sml.tsv");
            annotConf.addParameter(GraphLoader_TSVannot.PARAM_PREFIX_SUBJECT, ns_gene);

            goConf.addGDataConf(annotConf);

            // We reduce the Graph in order to only consider the Biological Process aspect
            URI GOtermBP = uriFactory.createURI("http://purl.org/obo/owl/GO#GO_0008150");
            GAction rooting = new GAction(GActionType.VERTICES_REDUCTION);
            rooting.addParameter("root_uri", GOtermBP.stringValue());
            //goConf.addGAction(rooting);

            G go = GraphLoaderGeneric.load(goConf);

            System.out.println(go);

//            UtilDebug.exit();

            /*
             * At this stage the graph contains the taxonomic graph
             * corresponding to the BP aspect of the GO and the uri
             * of the genes which are annotated by BP terms + other URIs relative to OWL
             */
            List<URI> classes = new ArrayList<URI>();
            List<URI> genes = new ArrayList<URI>();

            for (URI u : go.getV()) {

                if (u.getNamespace().equals(ns_go)) {
                    classes.add(u);
                } else if (u.getNamespace().equals(ns_gene)) {
                    genes.add(u);


            }
            }
            System.out.println("BP terms: " + classes.size());
            System.out.println("Genes   : " + genes.size());

            System.out.println("Reducing Term (only consider those used in an annotion)");
            Iterator<URI> it = classes.iterator();
            while (it.hasNext()) {
                URI u = it.next();
                Set<URI> annotatedByU = go.getV(u, RDF.TYPE, Direction.IN);
                boolean valid = false;
                for (URI i : annotatedByU) {
                    if (i.getNamespace().equals(ns_gene)) {
                        valid = true;
                        break;
                    }
                }
                if(!valid) it.remove();
            }

            System.out.println("Valid BP terms: " + classes.size());

            Random r = new Random();
            URI a, b;
            File f;
            BufferedWriter bw;

            System.out.println("Create Term to Term benchmarks");

            for (int i = 0; i < size_benchmarks.length; i++) {

                for (int j = 0; j < nbFiles; j++) {

                    String fname = file_prefix + "_pariwise_" + size_benchmarks[i] + "_" + j + ".tsv";
                    f = new File(fname);

                    if (!f.exists()) {
                        f.createNewFile();
                    }

                    bw = new BufferedWriter(new FileWriter(f.getAbsoluteFile()));

                    for (int k = 0; k < size_benchmarks[i]; k++) {
                        a = classes.get(r.nextInt(classes.size()));
                        b = classes.get(r.nextInt(classes.size()));

                        bw.write("GO:" + matchStr(a.getLocalName()) + "\t" + "GO:" + matchStr(b.getLocalName()) + "\n");
                    }
                    bw.close();
                    System.out.println("Benchmark generated at " + fname);
                }
            }

            System.out.println("Create Gene to Gene benchmarks");

            for (int i = 0; i < size_benchmarks.length; i++) {

                for (int j = 0; j < nbFiles; j++) {

                    String fname = file_prefix + "_groupwise_" + size_benchmarks[i] + "_" + j + ".tsv";
                    f = new File(fname);

                    if (!f.exists()) {
                        f.createNewFile();
                    }

                    bw = new BufferedWriter(new FileWriter(f.getAbsoluteFile()));

                    for (int k = 0; k < size_benchmarks[i]; k++) {
                        a = genes.get(r.nextInt(genes.size()));
                        b = genes.get(r.nextInt(genes.size()));

                        bw.write(a.getLocalName()  + "\t" + b.getLocalName() + "\n");
                    }
                    bw.close();
                    System.out.println("Benchmark generated at " + fname);
                }
            }

        } catch (Exception e) {
            System.out.println("An error occured during the treatment... more information below");
            e.printStackTrace();
        }
        System.out.println("done.");
    }

    private static String matchStr(String s){
        if(s.contains("_")){
            return s.split("_")[1];
        }
        return s;
    }
}
