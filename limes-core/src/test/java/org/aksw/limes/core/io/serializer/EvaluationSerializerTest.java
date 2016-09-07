package org.aksw.limes.core.io.serializer;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.datastrutures.EvaluationRun;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.junit.Test;

public class EvaluationSerializerTest {

    List<EvaluationRun> results = new ArrayList<EvaluationRun>();
    private void init()
    {
        
        Map<EvaluatorType, Double> scores = new LinkedHashMap<EvaluatorType, Double>();
        scores.put(EvaluatorType.PRECISION, 0.8);
        scores.put(EvaluatorType.RECALL, 0.9);
        scores.put(EvaluatorType.F_MEASURE, 0.98);
        
        EvaluationRun er1 = new EvaluationRun("EAGLE","UNSUPERVISED", "PERSON1", scores);
        EvaluationRun er2 = new EvaluationRun("WOMBATSIMPLE", "SUPERVISED_BATCH","PERSON1", scores);
        EvaluationRun er3 = new EvaluationRun("WOMBATCOMPLETE","SUPERVISED_ACTIVE" ,"PERSON1", scores);
        
        results.add(er1);
        results.add(er2);
        results.add(er3);

    }
    @Test
    public void test() {
        init();
        
        try {
            EvaluationSerlializer evaSer = new EvaluationSerlializer();
            evaSer.setSeparator(",");
            evaSer.writeToFile(results, "resources/evauationResults.csv");
            evaSer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
        assertTrue(true);
    }
    


}
