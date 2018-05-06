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
import scala.actors.Eval;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimilarityTest {

    @Test
    public void testSimilarity(){
        String[] datasets = {"PERSON1","PERSON2" , "RESTAURANTS","OAEI2014BOOKS","DBLPACM","ABTBUY","DBLPSCHOLAR","AMAZONGOOGLEPRODUCTS","DBPLINKEDMDB","DRUGS","PERSON2_CSV","PERSON2_CSV","PERSON1_CSV","RESTAURANTS_CSV"};

        EvaluationData dataset = DataSetChooser.getData(datasets[0]);

        WLSimilarityMapper mapper = new WLSimilarityMapper();
        AMapping mapping = mapper.getMapping(dataset.getSourceCache(), dataset.getTargetCache(), null, null,
                                             "graph_wls(x,y)", 0.5);

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

        for(Map.Entry<EvaluatorType, Double> e: quality.entrySet()){
            System.out.println(String.format("%s: %f", e.getKey().name(), e.getValue()));
        }



    }
}
