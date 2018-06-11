package org.aksw.limes.core.measures.measure.graphs;

import com.github.andrewoma.dexx.collection.Sets;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.evaluator.Evaluator;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.graphs.WLSimilarityMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.actors.Eval;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimilarityTest {

    static Logger logger = LoggerFactory.getLogger(SimilarityTest.class);

    @Test
    public void testSimilarity() throws FileNotFoundException {
        String[] datasets = {"PERSON1","PERSON2" , "RESTAURANTS","OAEI2014BOOKS","DBLPACM","ABTBUY","DBLPSCHOLAR","AMAZONGOOGLEPRODUCTS","DBPLINKEDMDB","DRUGS","PERSON2_CSV","PERSON2_CSV","PERSON1_CSV","RESTAURANTS_CSV"};

        File f = new File("result.txt");
        PrintWriter writer = new PrintWriter(f);
        try {
            for (String d : datasets) {
                EvaluationData dataset = DataSetChooser.getData(d);
                logger.info(String.format("Evaluate dataset %s.", dataset.getName()));

                WLSimilarityMapper mapper = new WLSimilarityMapper();
                AMapping mapping = mapper.getMapping(dataset.getSourceCache(), dataset.getTargetCache(), null, null,
                        "graph_wls(x,y)", 0.4);

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

                writer.println(d + ":");
                for (Map.Entry<EvaluatorType, Double> e : quality.entrySet()) {
                    writer.println(String.format("\t%s: %f", e.getKey().name(), e.getValue()));
                }
            }
        }finally{
            writer.close();
        }



    }
}
