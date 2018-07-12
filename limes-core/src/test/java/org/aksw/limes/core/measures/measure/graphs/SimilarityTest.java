package org.aksw.limes.core.measures.measure.graphs;

import com.google.common.base.Stopwatch;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.aksw.limes.core.measures.mapper.bags.jaccard.JaccardBagMapper;
import org.aksw.limes.core.measures.mapper.customGraphs.ConfigurableGraphMapper;
import org.aksw.limes.core.measures.mapper.customGraphs.TopologicalGraphMapper;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.cluster.SimilarityFilter;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.impl.APRelabel;
import org.aksw.limes.core.measures.measure.customGraphs.relabling.impl.ExactRelabel;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SimilarityTest {

    static Logger logger = LoggerFactory.getLogger(SimilarityTest.class);

    public IMapper createMapper(){
        List<SimilarityFilter> definitions = new ArrayList<>();
        definitions.add(new SimilarityFilter(MeasureType.LEVENSHTEIN, 0.6));
        definitions.add(new SimilarityFilter(MeasureType.TRIGRAM, 0.6));
        definitions.add(new SimilarityFilter(MeasureType.JAROWINKLER, 0.8));

        IMapper mapper = new ConfigurableGraphMapper(2, 1, new APRelabel(definitions), new JaccardBagMapper());

        mapper = new ConfigurableGraphMapper(2, 1, new ExactRelabel(), new JaccardBagMapper());
        mapper = new TopologicalGraphMapper(2, 1);
        return mapper;
    }

    @Test
    public void testSimilarity() throws FileNotFoundException {
        String[] datasets = {"PERSON1","PERSON2" , "RESTAURANTS","OAEI2014BOOKS","DBLPACM","ABTBUY","DBLPSCHOLAR","AMAZONGOOGLEPRODUCTS","DBPLINKEDMDB","DRUGS","PERSON2_CSV","PERSON1_CSV","RESTAURANTS_CSV"};

        datasets = new String[]{"PERSON1", "PERSON2", "RESTAURANTS", "DBLPACM", "ABTBUY", "DBLPSCHOLAR", "AMAZONGOOGLEPRODUCTS"};

        double[] thresholds = {0.243, 0.242, 0.267, 0.52, 0.267, 0.206, 0.3, 0.3, 0.3, 0.34, 0.202, 0.384, 0.146 };
        thresholds = new double[]{0.551, 0.253, 0.534, 0.3, 0.267, 0.206, 0.267, 0.299, 0.3, 0.34, 0.187, 0.384, 0.146};

        IMapper mapper = createMapper();

        File f = new File("result.txt");
        PrintWriter writer = new PrintWriter(f);
        int i = 0;
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        try {
            for (String d : datasets) {
                stopwatch.start();
                EvaluationData dataset = DataSetChooser.getData(d);
                double threshold = thresholds[i++];
                logger.info(String.format("Evaluate dataset %s.", dataset.getName()));

                AMapping mapping = mapper.getMapping(dataset.getSourceCache(), dataset.getTargetCache(), null, null,
                        "graph_wls(x,y)", threshold);



                //Configuration config = dataset.getConfigReader().getConfiguration();

                //AMapping mapping1 = LSPipeline.execute(dataset.getSourceCache(), dataset.getTargetCache(), config.getMetricExpression(),
                  //      config.getVerificationThreshold(), config.getSourceInfo().getVar(), config.getTargetInfo().getVar(),
                    //    RewriterFactory.getRewriterType(config.getExecutionRewriter()),
                      //  ExecutionPlannerFactory.getExecutionPlannerType(config.getExecutionPlanner()),
                        //ExecutionEngineFactory.getExecutionEngineType(config.getExecutionEngine()));

                //mapping = MappingOperations.union(mapping, mapping1);


                stopwatch.stop();

                GoldStandard standard = new GoldStandard(dataset.getReferenceMapping(),
                        dataset.getSourceCache().getAllUris(),
                        dataset.getTargetCache().getAllUris());

                QualitativeMeasuresEvaluator evaluator = new QualitativeMeasuresEvaluator();

                Set<EvaluatorType> evalTypes = new HashSet<>();
                evalTypes.add(EvaluatorType.ACCURACY);
                evalTypes.add(EvaluatorType.PRECISION);
                evalTypes.add(EvaluatorType.RECALL);
                evalTypes.add(EvaluatorType.F_MEASURE);

                Map<EvaluatorType, Double> quality = evaluator.evaluate(mapping, standard, evalTypes);

                System.out.println(d+"( "+stopwatch.elapsed(TimeUnit.MILLISECONDS)+" ms) :");
                writer.write(d+"( "+stopwatch.elapsed(TimeUnit.MILLISECONDS)+" ms) :\n");
                for (Map.Entry<EvaluatorType, Double> e : quality.entrySet()) {
                    writer.write(String.format("\t%s: %f\n", e.getKey().name(), e.getValue()));
                    System.out.println(String.format("\t%s: %f", e.getKey().name(), e.getValue()));
                }
                stopwatch.reset();
            }
        }finally{
            writer.close();
        }



    }
}
