/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.io.serializer;

import org.aksw.limes.core.datastrutures.EvaluationRun;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class EvaluationSerializerTest {

    List<EvaluationRun> results = new ArrayList<EvaluationRun>();

    private void init() {

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
