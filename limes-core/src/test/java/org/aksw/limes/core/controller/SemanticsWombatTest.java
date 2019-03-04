package org.aksw.limes.core.controller;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.dragon.Dragon;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

public class SemanticsWombatTest {
    private static final Logger logger = Logger.getLogger(SemanticsWombatTest.class);

    @Test
    public void testDragon() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsAbtBuy");
        String folder = "src/main/resources/datasets/Abt-Buy/Mappings/";
        FileUtils.deleteDirectory(new File(folder));
        String[] arguments = { "Abt-Buy", "3", "dragon", "init" };
        SemanticsWombat.main(arguments);
        //////////////////////////////////////////////////////////////////////
        String[] arguments2 = { "Abt-Buy", "3", "dragon", "debug" };
        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments2);

        Set<String> measures = controller.getMeasures();
        SupervisedMLAlgorithm ml = MLAlgorithmFactory.createMLAlgorithm(MLAlgorithmFactory.getAlgorithmType(controller.mlAlgorithm),
                        MLImplementationType.SUPERVISED_BATCH).asSupervised();
        List<LearningParameter> lp = controller.setLearningParameters(measures);
        ml.init(lp, null, null);
        
        Set<String> dragonMeasures = Dragon.defaultMeasures;
        logger.info(""+dragonMeasures.toString());
        
        Set<String> conMeasures = controller.getMeasures();
        
        assertTrue(dragonMeasures.equals(conMeasures));

    }

    @Test
    public void reducedCachesAbtBuy() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsAbtBuy");
        String folder = "src/main/resources/datasets/Abt-Buy/Mappings/";
        FileUtils.deleteDirectory(new File(folder));
        String[] arguments = { "Abt-Buy", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "Abt-Buy", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        ACache fullSourceCache = controllerW.data.getSourceCache();
        ACache fullTargetCache = controllerW.data.getTargetCache();
        for (int i = 0; i < loadedMappingsWombat.size(); i++) {
            AMapping subMapping = loadedMappingsWombat.get(i);
            // HashMap<String, HashMap<String, Double>> loadedMappingWombat =
            // subMapping.getMap();
            List<ACache> reducedCaches = controllerW.reduceCaches(subMapping, fullSourceCache, fullTargetCache);
            ACache subSource = reducedCaches.get(0);
            ACache subTarget = reducedCaches.get(1);
            for (Instance sourceInstance : subSource.getAllInstances()) {
                assertTrue(fullSourceCache.containsInstance(sourceInstance));
            }
            for (Instance targetInstance : subTarget.getAllInstances()) {
                assertTrue(fullTargetCache.containsInstance(targetInstance));
            }
            assertTrue(subSource.size() <= fullSourceCache.size());
            assertTrue(subTarget.size() <= fullTargetCache.size());

        }
    }

    @Test
    public void reducedCachesAmazon() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsAmazon-GoogleProducts");
        String folder = "src/main/resources/datasets/Amazon-GoogleProducts/Mappings/";
        FileUtils.deleteDirectory(new File(folder));
        String[] arguments = { "Amazon-GoogleProducts", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "Amazon-GoogleProducts", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        ACache fullSourceCache = controllerW.data.getSourceCache();
        ACache fullTargetCache = controllerW.data.getTargetCache();
        for (int i = 0; i < loadedMappingsWombat.size(); i++) {
            AMapping subMapping = loadedMappingsWombat.get(i);
            // HashMap<String, HashMap<String, Double>> loadedMappingWombat =
            // subMapping.getMap();
            List<ACache> reducedCaches = controllerW.reduceCaches(subMapping, fullSourceCache, fullTargetCache);
            ACache subSource = reducedCaches.get(0);
            ACache subTarget = reducedCaches.get(1);
            for (Instance sourceInstance : subSource.getAllInstances()) {
                assertTrue(fullSourceCache.containsInstance(sourceInstance));
            }
            for (Instance targetInstance : subTarget.getAllInstances()) {
                assertTrue(fullTargetCache.containsInstance(targetInstance));
            }
            assertTrue(subSource.size() <= fullSourceCache.size());
            assertTrue(subTarget.size() <= fullTargetCache.size());

        }
    }

    @Test
    public void reducedCachesDDI() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsdrugs");
        String folder = "src/main/resources/datasets/dailymed-drugbank-ingredients/Mappings/";
        FileUtils.deleteDirectory(new File(folder));
        String[] arguments = { "drugs", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "drugs", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        ACache fullSourceCache = controllerW.data.getSourceCache();
        ACache fullTargetCache = controllerW.data.getTargetCache();
        for (int i = 0; i < loadedMappingsWombat.size(); i++) {
            AMapping subMapping = loadedMappingsWombat.get(i);
            // HashMap<String, HashMap<String, Double>> loadedMappingWombat =
            // subMapping.getMap();
            List<ACache> reducedCaches = controllerW.reduceCaches(subMapping, fullSourceCache, fullTargetCache);
            ACache subSource = reducedCaches.get(0);
            ACache subTarget = reducedCaches.get(1);
            for (Instance sourceInstance : subSource.getAllInstances()) {
                assertTrue(fullSourceCache.containsInstance(sourceInstance));
            }
            for (Instance targetInstance : subTarget.getAllInstances()) {
                assertTrue(fullTargetCache.containsInstance(targetInstance));
            }
            assertTrue(subSource.size() <= fullSourceCache.size());
            assertTrue(subTarget.size() <= fullTargetCache.size());

        }
    }

    @Test
    public void reducedCachesACM() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsACM");
        String folder = "src/main/resources/datasets/DBLP-ACM/Mappings/";
        FileUtils.deleteDirectory(new File(folder));
        String[] arguments = { "DBLP-ACM", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "DBLP-ACM", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        ACache fullSourceCache = controllerW.data.getSourceCache();
        ACache fullTargetCache = controllerW.data.getTargetCache();
        for (int i = 0; i < loadedMappingsWombat.size(); i++) {
            AMapping subMapping = loadedMappingsWombat.get(i);
            // HashMap<String, HashMap<String, Double>> loadedMappingWombat =
            // subMapping.getMap();
            List<ACache> reducedCaches = controllerW.reduceCaches(subMapping, fullSourceCache, fullTargetCache);
            ACache subSource = reducedCaches.get(0);
            ACache subTarget = reducedCaches.get(1);
            for (Instance sourceInstance : subSource.getAllInstances()) {
                assertTrue(fullSourceCache.containsInstance(sourceInstance));
            }
            for (Instance targetInstance : subTarget.getAllInstances()) {
                assertTrue(fullTargetCache.containsInstance(targetInstance));
            }
            assertTrue(subSource.size() <= fullSourceCache.size());
            assertTrue(subTarget.size() <= fullTargetCache.size());

        }
    }

    @Test
    public void reducedCachesScholar() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsScholar");
        String folder = "src/main/resources/datasets/DBLP-Scholar/Mappings/";
        FileUtils.deleteDirectory(new File(folder));
        String[] arguments = { "DBLP-Scholar", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "DBLP-Scholar", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        ACache fullSourceCache = controllerW.data.getSourceCache();
        ACache fullTargetCache = controllerW.data.getTargetCache();
        for (int i = 0; i < loadedMappingsWombat.size(); i++) {
            AMapping subMapping = loadedMappingsWombat.get(i);
            // HashMap<String, HashMap<String, Double>> loadedMappingWombat =
            // subMapping.getMap();
            List<ACache> reducedCaches = controllerW.reduceCaches(subMapping, fullSourceCache, fullTargetCache);
            ACache subSource = reducedCaches.get(0);
            ACache subTarget = reducedCaches.get(1);
            for (Instance sourceInstance : subSource.getAllInstances()) {
                assertTrue(fullSourceCache.containsInstance(sourceInstance));
            }
            for (Instance targetInstance : subTarget.getAllInstances()) {
                assertTrue(fullTargetCache.containsInstance(targetInstance));
            }
            assertTrue(subSource.size() <= fullSourceCache.size());
            assertTrue(subTarget.size() <= fullTargetCache.size());

        }
    }

    @Test
    public void reducedCachesMovies() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsMovies");
        String folder = "src/main/resources/datasets/dbpedia-linkedmdb/Mappings/";
        FileUtils.deleteDirectory(new File(folder));
        String[] arguments = { "DBPLINKEDMDB", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "DBPLINKEDMDB", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        ACache fullSourceCache = controllerW.data.getSourceCache();
        ACache fullTargetCache = controllerW.data.getTargetCache();
        for (int i = 0; i < loadedMappingsWombat.size(); i++) {
            AMapping subMapping = loadedMappingsWombat.get(i);
            // HashMap<String, HashMap<String, Double>> loadedMappingWombat =
            // subMapping.getMap();
            List<ACache> reducedCaches = controllerW.reduceCaches(subMapping, fullSourceCache, fullTargetCache);
            ACache subSource = reducedCaches.get(0);
            ACache subTarget = reducedCaches.get(1);
            for (Instance sourceInstance : subSource.getAllInstances()) {
                assertTrue(fullSourceCache.containsInstance(sourceInstance));
            }
            for (Instance targetInstance : subTarget.getAllInstances()) {
                assertTrue(fullTargetCache.containsInstance(targetInstance));
            }
            assertTrue(subSource.size() <= fullSourceCache.size());
            assertTrue(subTarget.size() <= fullTargetCache.size());

        }
    }

    @Test
    public void usingSameMappingsAbtBuy() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsAbtBuy");
        String folder = "src/main/resources/datasets/Abt-Buy/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        String[] arguments = { "Abt-Buy", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "Abt-Buy", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        assertTrue(loadedMappingsWombat.size() == 10);
        assertTrue(controllerW.mlAlgorithm.equals("wombat simple"));
        /////////////////////////////////////////////////////////////////////////////////
        String[] argumentsDragon = { "Abt-Buy", "1", "dragon", "debug" };
        SemanticsWombat controllerD = new SemanticsWombat();
        controllerD.run(argumentsDragon);
        List<AMapping> loadedMappingsDragon = controllerD.debugMappings;
        assertTrue(loadedMappingsDragon.size() == 10);
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
        assertTrue(recreatedGoldenStandardWombat.size() == goldenStandardWombat.size());
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
        assertTrue(posNegExamples.size() == 2 * golderStandardDragon.size());
        assertTrue(counterPos == counterNeg);
        assertTrue(recreatedGoldenStandardDragon.size() == golderStandardDragon.size());

        assertTrue(recreatedGoldenStandardDragon.equals(recreatedGoldenStandardWombat));
    }

    @Test
    public void saveLoadMappingsWombatAbtBuy() throws UnsupportedMLImplementationException, IOException {
        logger.info("Abt-Buy");
        String[] arguments = { "Abt-Buy", "1", "wombat simple", "init" };
        String folder = "src/main/resources/datasets/Abt-Buy/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping recreatedGoldenStandard = MappingFactory.createDefaultMapping();
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard
                    assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    // source,target must not be included in more than one sub
                    // mappings
                    assertFalse(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                    recreatedGoldenStandard.add(entry.getKey(), entry2.getKey(), entry2.getValue());
                    assertTrue(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                }

            }
        }

        assertTrue(recreatedGoldenStandard.size() == goldenStandard.size());

    }

    @Test
    public void saveLoadMappingsDragonAbtBuy() throws UnsupportedMLImplementationException, IOException {
        logger.info("Abt-Buy-Dragon");
        String[] arguments = { "Abt-Buy", "1", "dragon", "init" };
        String folder = "src/main/resources/datasets/Abt-Buy/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping posNegExamples = MappingFactory.createDefaultMapping();
        int counterPos = 0;
        int counterNeg = 0;
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard, if
                    // their sim is 1
                    if (entry2.getValue() == 1.0d)
                        assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    else
                        assertFalse(goldenStandard.contains(entry.getKey(), entry2.getKey()));

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
        logger.info("goldenStandard size:" + goldenStandard.size());
        assertTrue(posNegExamples.size() == 2 * goldenStandard.size());
        assertTrue(counterPos == counterNeg);
    }

    @Test
    public void saveLoadMappingsWombatAmazon() throws UnsupportedMLImplementationException, IOException {
        logger.info("Amazon-Google");

        String[] arguments = { "Amazon-GoogleProducts", "1", "wombat simple", "init" };
        String folder = "src/main/resources/datasets/Amazon-GoogleProducts/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping recreatedGoldenStandard = MappingFactory.createDefaultMapping();
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard
                    assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    // source,target must not be included in more than one sub
                    // mappings
                    assertFalse(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                    recreatedGoldenStandard.add(entry.getKey(), entry2.getKey(), entry2.getValue());
                    assertTrue(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                }

            }
        }

        assertTrue(recreatedGoldenStandard.size() == goldenStandard.size());

    }

    @Test
    public void saveLoadMappingsDragonAmazonGoogle() throws UnsupportedMLImplementationException, IOException {
        logger.info("Amazon-Google-Dragon");
        String[] arguments = { "Amazon-GoogleProducts", "1", "dragon", "init" };
        String folder = "src/main/resources/datasets/Amazon-GoogleProducts/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping posNegExamples = MappingFactory.createDefaultMapping();
        int counterPos = 0;
        int counterNeg = 0;
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard, if
                    // their sim is 1
                    if (entry2.getValue() == 1.0d)
                        assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    else
                        assertFalse(goldenStandard.contains(entry.getKey(), entry2.getKey()));

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
        logger.info("goldenStandard size:" + goldenStandard.size());
        assertTrue(posNegExamples.size() == 2 * goldenStandard.size());
        assertTrue(counterPos == counterNeg);
    }

    @Test
    public void usingSameMappingsAmazonGoogle() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsAmazon-GoogleProducts");
        String folder = "src/main/resources/datasets/Amazon-GoogleProducts/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        String[] arguments = { "Amazon-GoogleProducts", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "Amazon-GoogleProducts", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        assertTrue(loadedMappingsWombat.size() == 10);
        assertTrue(controllerW.mlAlgorithm.equals("wombat simple"));
        /////////////////////////////////////////////////////////////////////////////////
        String[] argumentsDragon = { "Amazon-GoogleProducts", "1", "dragon", "debug" };
        SemanticsWombat controllerD = new SemanticsWombat();
        controllerD.run(argumentsDragon);
        List<AMapping> loadedMappingsDragon = controllerD.debugMappings;
        assertTrue(loadedMappingsDragon.size() == 10);
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
        assertTrue(recreatedGoldenStandardWombat.size() == goldenStandardWombat.size());
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
        assertTrue(posNegExamples.size() == 2 * golderStandardDragon.size());
        assertTrue(counterPos == counterNeg);
        assertTrue(recreatedGoldenStandardDragon.size() == golderStandardDragon.size());

        assertTrue(recreatedGoldenStandardDragon.equals(recreatedGoldenStandardWombat));
    }

    @Test
    public void saveLoadMappingsWombatDDI() throws UnsupportedMLImplementationException, IOException {
        logger.info("DDI");
        String[] arguments = { "drugs", "1", "wombat simple", "init" };
        String folder = "src/main/resources/datasets/dailymed-drugbank-ingredients/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping recreatedGoldenStandard = MappingFactory.createDefaultMapping();
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard
                    assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    // source,target must not be included in more than one sub
                    // mappings
                    assertFalse(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                    recreatedGoldenStandard.add(entry.getKey(), entry2.getKey(), entry2.getValue());
                    assertTrue(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                }

            }
        }

        assertTrue(recreatedGoldenStandard.size() == goldenStandard.size());

    }

    @Test
    public void saveLoadMappingsDragonDDI() throws UnsupportedMLImplementationException, IOException {
        logger.info("DDI-Dragon");
        String[] arguments = { "drugs", "1", "dragon", "init" };
        String folder = "src/main/resources/datasets/dailymed-drugbank-ingredients/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping posNegExamples = MappingFactory.createDefaultMapping();
        int counterPos = 0;
        int counterNeg = 0;
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard, if
                    // their sim is 1
                    if (entry2.getValue() == 1.0d)
                        assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    else
                        assertFalse(goldenStandard.contains(entry.getKey(), entry2.getKey()));

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
        logger.info("goldenStandard size:" + goldenStandard.size());
        assertTrue(posNegExamples.size() == 2 * goldenStandard.size());
        assertTrue(counterPos == counterNeg);
    }

    @Test
    public void usingSameMappingsDDI() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsdrugs");
        String folder = "src/main/resources/datasets/dailymed-drugbank-ingredients/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        String[] arguments = { "drugs", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "drugs", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        assertTrue(loadedMappingsWombat.size() == 10);
        assertTrue(controllerW.mlAlgorithm.equals("wombat simple"));
        /////////////////////////////////////////////////////////////////////////////////
        String[] argumentsDragon = { "drugs", "1", "dragon", "debug" };
        SemanticsWombat controllerD = new SemanticsWombat();
        controllerD.run(argumentsDragon);
        List<AMapping> loadedMappingsDragon = controllerD.debugMappings;
        assertTrue(loadedMappingsDragon.size() == 10);
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
        assertTrue(recreatedGoldenStandardWombat.size() == goldenStandardWombat.size());
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
        assertTrue(posNegExamples.size() == 2 * golderStandardDragon.size());
        assertTrue(counterPos == counterNeg);
        assertTrue(recreatedGoldenStandardDragon.size() == golderStandardDragon.size());

        assertTrue(recreatedGoldenStandardDragon.equals(recreatedGoldenStandardWombat));
    }

    @Test
    public void saveLoadMappingsWombatACM() throws UnsupportedMLImplementationException, IOException {
        logger.info("DBLP-ACM");

        String[] arguments = { "DBLP-ACM", "1", "wombat simple", "init" };
        String folder = "src/main/resources/datasets/DBLP-ACM/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping recreatedGoldenStandard = MappingFactory.createDefaultMapping();
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard
                    assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    // source,target must not be included in more than one sub
                    // mappings
                    assertFalse(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                    recreatedGoldenStandard.add(entry.getKey(), entry2.getKey(), entry2.getValue());
                    assertTrue(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                }

            }
        }

        assertTrue(recreatedGoldenStandard.size() == goldenStandard.size());

    }

    @Test
    public void saveLoadMappingsDragonACM() throws UnsupportedMLImplementationException, IOException {
        logger.info("ACM-Dragon");
        String[] arguments = { "DBLP-ACM", "1", "dragon", "init" };
        String folder = "src/main/resources/datasets/DBLP-ACM/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping posNegExamples = MappingFactory.createDefaultMapping();
        int counterPos = 0;
        int counterNeg = 0;
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard, if
                    // their sim is 1
                    if (entry2.getValue() == 1.0d)
                        assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    else
                        assertFalse(goldenStandard.contains(entry.getKey(), entry2.getKey()));

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
        logger.info("goldenStandard size:" + goldenStandard.size());
        assertTrue(posNegExamples.size() == 2 * goldenStandard.size());
        assertTrue(counterPos == counterNeg);
    }

    @Test
    public void usingSameMappingsACM() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsACM");
        String folder = "src/main/resources/datasets/DBLP-ACM/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        String[] arguments = { "DBLP-ACM", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "DBLP-ACM", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        assertTrue(loadedMappingsWombat.size() == 10);
        assertTrue(controllerW.mlAlgorithm.equals("wombat simple"));
        /////////////////////////////////////////////////////////////////////////////////
        String[] argumentsDragon = { "DBLP-ACM", "1", "dragon", "debug" };
        SemanticsWombat controllerD = new SemanticsWombat();
        controllerD.run(argumentsDragon);
        List<AMapping> loadedMappingsDragon = controllerD.debugMappings;
        assertTrue(loadedMappingsDragon.size() == 10);
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
        assertTrue(recreatedGoldenStandardWombat.size() == goldenStandardWombat.size());
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
        assertTrue(posNegExamples.size() == 2 * golderStandardDragon.size());
        assertTrue(counterPos == counterNeg);
        assertTrue(recreatedGoldenStandardDragon.size() == golderStandardDragon.size());

        assertTrue(recreatedGoldenStandardDragon.equals(recreatedGoldenStandardWombat));
    }

    @Test
    public void saveLoadMappingsWombatScholar() throws UnsupportedMLImplementationException, IOException {
        logger.info("DBLP-Scholar");

        String[] arguments = { "DBLP-Scholar", "1", "wombat simple", "init" };
        String folder = "src/main/resources/datasets/DBLP-Scholar/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping recreatedGoldenStandard = MappingFactory.createDefaultMapping();
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard
                    assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    // source,target must not be included in more than one sub
                    // mappings
                    assertFalse(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                    recreatedGoldenStandard.add(entry.getKey(), entry2.getKey(), entry2.getValue());
                    assertTrue(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                }

            }
        }

        assertTrue(recreatedGoldenStandard.size() == goldenStandard.size());

    }

    @Test
    public void saveLoadMappingsDragonScholar() throws UnsupportedMLImplementationException, IOException {
        logger.info("Scholar-Dragon");
        String[] arguments = { "DBLP-Scholar", "1", "dragon", "init" };
        String folder = "src/main/resources/datasets/DBLP-Scholar/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping posNegExamples = MappingFactory.createDefaultMapping();
        int counterPos = 0;
        int counterNeg = 0;
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard, if
                    // their sim is 1
                    if (entry2.getValue() == 1.0d)
                        assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    else
                        assertFalse(goldenStandard.contains(entry.getKey(), entry2.getKey()));

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
        logger.info("goldenStandard size:" + goldenStandard.size());

        assertTrue(posNegExamples.size() == 2 * goldenStandard.size());
        assertTrue(counterPos == counterNeg);
    }

    @Test
    public void usingSameMappingsScholar() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsScholar");
        String folder = "src/main/resources/datasets/DBLP-Scholar/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        String[] arguments = { "DBLP-Scholar", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "DBLP-Scholar", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        assertTrue(loadedMappingsWombat.size() == 10);
        assertTrue(controllerW.mlAlgorithm.equals("wombat simple"));
        /////////////////////////////////////////////////////////////////////////////////
        String[] argumentsDragon = { "DBLP-Scholar", "1", "dragon", "debug" };
        SemanticsWombat controllerD = new SemanticsWombat();
        controllerD.run(argumentsDragon);
        List<AMapping> loadedMappingsDragon = controllerD.debugMappings;
        assertTrue(loadedMappingsDragon.size() == 10);
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
        assertTrue(recreatedGoldenStandardWombat.size() == goldenStandardWombat.size());
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
        assertTrue(posNegExamples.size() == 2 * golderStandardDragon.size());
        assertTrue(counterPos == counterNeg);
        assertTrue(recreatedGoldenStandardDragon.size() == golderStandardDragon.size());

        assertTrue(recreatedGoldenStandardDragon.equals(recreatedGoldenStandardWombat));
    }

    @Test
    public void saveLoadMappingsWombatDBMD() throws UnsupportedMLImplementationException, IOException {
        logger.info("DBPLINKEDMDB");

        String[] arguments = { "DBPLINKEDMDB", "1", "wombat simple", "init" };
        String folder = "src/main/resources/datasets/dbpedia-linkedmdb/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping recreatedGoldenStandard = MappingFactory.createDefaultMapping();
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard
                    assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    // source,target must not be included in more than one sub
                    // mappings
                    assertFalse(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                    recreatedGoldenStandard.add(entry.getKey(), entry2.getKey(), entry2.getValue());
                    assertTrue(recreatedGoldenStandard.contains(entry.getKey(), entry2.getKey()));
                }

            }
        }

        assertTrue(recreatedGoldenStandard.size() == goldenStandard.size());

    }

    @Test
    public void saveLoadMappingsDragonDBMD() throws UnsupportedMLImplementationException, IOException {
        logger.info("DBMD-Dragon");
        String[] arguments = { "DBPLINKEDMDB", "1", "dragon", "init" };
        String folder = "src/main/resources/datasets/dbpedia-linkedmdb/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        SemanticsWombat controller = new SemanticsWombat();
        controller.run(arguments);
        List<AMapping> loadedMappings = controller.loadMappings();
        assertTrue(loadedMappings.size() == 10);

        AMapping goldenStandard = controller.fullReferenceMapping;
        AMapping posNegExamples = MappingFactory.createDefaultMapping();
        int counterPos = 0;
        int counterNeg = 0;
        // re-create the golden standard from the separate files
        for (int i = 0; i < loadedMappings.size(); i++) {
            HashMap<String, HashMap<String, Double>> loadedMapping = loadedMappings.get(i).getMap();
            for (Entry<String, HashMap<String, Double>> entry : loadedMapping.entrySet()) {
                for (Entry<String, Double> entry2 : entry.getValue().entrySet()) {
                    // source,target must be inside the golden standard, if
                    // their sim is 1
                    if (entry2.getValue() == 1.0d)
                        assertTrue(goldenStandard.contains(entry.getKey(), entry2.getKey()));
                    else
                        assertFalse(goldenStandard.contains(entry.getKey(), entry2.getKey()));

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
        logger.info("goldenStandard size:" + goldenStandard.size());
        assertTrue(posNegExamples.size() == 2 * goldenStandard.size());
        assertTrue(counterPos == counterNeg);
    }

    @Test
    public void usingSameMappingsDBMD() throws IOException, UnsupportedMLImplementationException {
        logger.info("usingSameMappingsDBMD");
        String folder = "src/main/resources/datasets/dbpedia-linkedmdb/Mappings/";
        FileUtils.deleteDirectory(new File(folder));

        String[] arguments = { "DBPLINKEDMDB", "1", "wombat simple", "init" };
        SemanticsWombat.main(arguments);
        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        String[] argumentsWombat = { "DBPLINKEDMDB", "1", "wombat simple", "debug" };
        SemanticsWombat controllerW = new SemanticsWombat();
        controllerW.run(argumentsWombat);
        List<AMapping> loadedMappingsWombat = controllerW.debugMappings;
        assertTrue(loadedMappingsWombat.size() == 10);
        assertTrue(controllerW.mlAlgorithm.equals("wombat simple"));
        /////////////////////////////////////////////////////////////////////////////////
        String[] argumentsDragon = { "DBPLINKEDMDB", "1", "dragon", "debug" };
        SemanticsWombat controllerD = new SemanticsWombat();
        controllerD.run(argumentsDragon);
        List<AMapping> loadedMappingsDragon = controllerD.debugMappings;
        assertTrue(loadedMappingsDragon.size() == 10);
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
        assertTrue(recreatedGoldenStandardWombat.size() == goldenStandardWombat.size());
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
        assertTrue(posNegExamples.size() == 2 * golderStandardDragon.size());
        assertTrue(counterPos == counterNeg);
        assertTrue(recreatedGoldenStandardDragon.size() == golderStandardDragon.size());

        assertTrue(recreatedGoldenStandardDragon.equals(recreatedGoldenStandardWombat));
    }
}
