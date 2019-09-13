package org.aksw.limes.core.controller;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.apache.log4j.Logger;

public class MLSemanticsTest {
    private static final Logger logger = Logger.getLogger(MLSemanticsTest.class);

    // @Test
    public void usingSameMappings() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappings");
        String folder = "src/main/resources/datasets/Abt-Buy/Mappings7extra/";
        FileUtils.deleteDirectory(new File(folder));

        String[] arguments = { "Abt-Buy", "1", "wombat simple", "init" };
        MLSemantics.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "Abt-Buy", "1", "wombat simple", "debug" };
        MLSemantics controllerW = new MLSemantics();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        assertTrue(loadedMappingsWombat.size() == 7);
        assertTrue(controllerW.mlAlgorithm.equals("wombat simple"));
        /////////////////////////////////////////////////////////////////////////////////
        String[] argumentsDragon = { "Abt-Buy", "1", "dragon", "debug" };
        MLSemantics controllerD = new MLSemantics();
        controllerD.run(argumentsDragon);
        List<AMapping> loadedMappingsDragon = controllerD.debugMappings;
        assertTrue(loadedMappingsDragon.size() == 7);
        assertTrue(controllerD.mlAlgorithm.equals("dragon"));
        //////////////////////////////////////////////////////////////////

        for (int i = 0; i < loadedMappingsDragon.size(); i++) {

            HashMap<String, HashMap<String, Double>> loadedMappingDragon = loadedMappingsDragon.get(i).getMap();
            HashMap<String, HashMap<String, Double>> loadedMappingWombat = loadedMappingsWombat.get(i).getMap();
            assertFalse(loadedMappingDragon.equals(loadedMappingWombat));

            assertTrue(loadedMappingDragon.size() >= loadedMappingWombat.size());
            int counterW = 0;
            int counterD = 0;
            for (Entry<String, HashMap<String, Double>> entry : loadedMappingDragon.entrySet()) {
                counterD += entry.getValue().size();
            }
            for (Entry<String, HashMap<String, Double>> entry : loadedMappingWombat.entrySet()) {
                counterW += entry.getValue().size();
            }
            // dragon must have double the pairs compared to wombat
            assertTrue(counterD == 2 * counterW);
            // check that dragon positive training data is always the same as
            // wombat's
            HashMap<String, HashMap<String, Double>> testMapping = new HashMap<String, HashMap<String, Double>>();
            for (Entry<String, HashMap<String, Double>> entry : loadedMappingDragon.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    if (entry2.getValue() == 1.0d) {
                        if (!testMapping.containsKey(entry.getKey())) {
                            testMapping.put(entry.getKey(), new HashMap<String, Double>());
                        }
                        testMapping.get(entry.getKey()).put(entry2.getKey(), entry2.getValue());
                    }
                }
            }
            // logger.info(" "+i+" "+counter);
            assertTrue(loadedMappingWombat.equals(testMapping));
        }
        //////////////////////////////////////////////////////////////////////////////////////////////
        // re-create the golden standard for wombat
        AMapping goldenStandardWombat = controllerW.fullReferenceMapping;
        AMapping recreatedGoldenStandardWombat = MappingFactory.createDefaultMapping();
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappingsWombat.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappingsWombat.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard
                    assertTrue(goldenStandardWombat.contains(entry.getKey(), entry2.getKey()));
                    // source,target must not be included in more than one sub
                    // mappings
                    assertFalse(recreatedGoldenStandardWombat.contains(entry.getKey(), entry2.getKey()));
                    recreatedGoldenStandardWombat.add(entry.getKey(), entry2.getKey(), entry2.getValue());
                    assertTrue(recreatedGoldenStandardWombat.contains(entry.getKey(), entry2.getKey()));
                }

            }
        }
        assertTrue(recreatedGoldenStandardWombat.size() < goldenStandardWombat.size());
        /////////////////////////////////////////////////////////////////////////////
        // re-create the golden standard for dragon
        AMapping golderStandardDragon = controllerD.fullReferenceMapping;
        AMapping recreatedGoldenStandardDragon = MappingFactory.createDefaultMapping();
        AMapping posNegExamples = MappingFactory.createDefaultMapping();
        int counterPos = 0;
        int counterNeg = 0;
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappingsDragon.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappingsDragon.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard, if
                    // their sim is 1
                    if (entry2.getValue() == 1.0d) {
                        assertTrue(golderStandardDragon.contains(entry.getKey(), entry2.getKey()));
                        recreatedGoldenStandardDragon.add(entry.getKey(), entry2.getKey(), entry2.getValue());
                    } else
                        assertFalse(golderStandardDragon.contains(entry.getKey(), entry2.getKey()));

                    if (entry2.getValue() == 1.0d)
                        counterPos++;
                    else
                        counterNeg++;
                    // source,target must not be included in more than one sub
                    // mappings
                    assertFalse(posNegExamples.contains(entry.getKey(), entry2.getKey()));
                    posNegExamples.add(entry.getKey(), entry2.getKey(), entry2.getValue());
                    assertTrue(posNegExamples.contains(entry.getKey(), entry2.getKey()));
                }

            }
        }
        logger.info("posNegExamples size:" + posNegExamples.size());
        logger.info("goldenStandard size:" + golderStandardDragon.size());
        assertTrue(posNegExamples.size() < 2 * golderStandardDragon.size());
        assertTrue(counterPos == counterNeg);
        assertTrue(recreatedGoldenStandardDragon.size() < golderStandardDragon.size());

        assertTrue(recreatedGoldenStandardDragon.equals(recreatedGoldenStandardWombat));
    }

    @Test
    public void differentMappings() throws IOException, UnsupportedMLImplementationException {

        String folder = "src/main/resources/datasets/dbpedia-linkedmdb/Mappings7extra/";
        FileUtils.deleteDirectory(new File(folder));

        String[] arguments = { "DBPLINKEDMDB", "1", "wombat simple", "init" };
        MLSemantics.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "DBPLINKEDMDB", "1", "wombat simple", "debug" };
        MLSemantics controllerW = new MLSemantics();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;

        String[] argumentsDragon = { "DBPLINKEDMDB", "1", "dragon", "debug" };
        MLSemantics controllerD = new MLSemantics();
        controllerD.run(argumentsDragon);
        List<AMapping> loadedMappingsDragon = controllerD.debugMappings;

        for (int i = 0; i < 7; i++) {

            AMapping trainingSetW = loadedMappingsWombat.get(i);
            trainingSetW.getReversedMap();

            // testing on the remaining 98%
            AMapping testSetW = controllerW.getLearningPool(trainingSetW);
            testSetW.getReversedMap();
            logger.info("Training set size " + trainingSetW.size());
            logger.info("testSet set size " + testSetW.size());

            assertTrue(trainingSetW.size() <= testSetW.size());

            AMapping intersectionW = MappingOperations.intersection(testSetW, trainingSetW);
            assertTrue(intersectionW.size() == 0);

            AMapping differenceW = MappingOperations.difference(testSetW, trainingSetW);
            assertTrue(differenceW.equals(testSetW));

            /////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////

            AMapping trainingSetD = loadedMappingsDragon.get(i);
            trainingSetD.getReversedMap();

            // testing on the remaining 98%
            AMapping testSetD = controllerD.getLearningPool(trainingSetD);
            testSetD.getReversedMap();
            logger.info("Training set size " + trainingSetD.size());
            logger.info("testSet set size " + testSetD.size());

            assertTrue(trainingSetD.size() <= testSetD.size());

            AMapping intersectionD = MappingOperations.intersection(testSetD, trainingSetD);
            assertTrue(intersectionD.size() == 0);

            AMapping differenceD = MappingOperations.difference(testSetD, trainingSetD);
            assertTrue(differenceD.equals(testSetD));

            ////////////////////////////////////////////////////
            // check stuff between wombat and dragon
            assertTrue(!trainingSetW.equals(trainingSetD.size()));
            assertTrue(!testSetW.equals(testSetD.size()));

            assertTrue(trainingSetW.size() * 2 == trainingSetD.size());
            assertTrue(testSetW.size() * 2 == testSetD.size());

            /////////////////////////////////////////////////////////////////////////
            AMapping intersectionWDtraining = MappingOperations.intersection(trainingSetD, trainingSetW);
            assertTrue(intersectionWDtraining.equals(trainingSetW));

            AMapping intersectionWDTest = MappingOperations.intersection(testSetD, testSetW);
            assertTrue(intersectionWDTest.equals(testSetW));

            /////////////////////////////////////////////////////////////////////////
            AMapping differenceWDtraining = MappingOperations.difference(trainingSetD, trainingSetW);
            assertTrue(differenceWDtraining.size() == trainingSetW.size());

            AMapping differenceWDtest = MappingOperations.difference(testSetD, testSetW);
            assertTrue(differenceWDtest.size() == testSetW.size());
            /////////////////////////////////////////////////////////////////////////
            AMapping unionWDTraining = MappingOperations.union(trainingSetD, trainingSetW);
            assertTrue(unionWDTraining.equals(trainingSetD));

            AMapping unionWDTest = MappingOperations.union(testSetD, testSetW);
            assertTrue(unionWDTest.equals(testSetD));
            ////////////////////////////////////////////////////
            
        }

    }
}
