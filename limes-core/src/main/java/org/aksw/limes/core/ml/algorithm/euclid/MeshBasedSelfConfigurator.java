package org.aksw.limes.core.ml.algorithm.euclid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.classifier.ComplexClassifier;
import org.aksw.limes.core.ml.algorithm.classifier.SimpleClassifier;
import org.apache.log4j.Logger;

/**
 * @author Kevin Dreßler
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 */
public class MeshBasedSelfConfigurator extends BooleanSelfConfigurator {

    static Logger logger = Logger.getLogger(MeshBasedSelfConfigurator.class);
    
    public static String MAX_STRATEGY = "max";
    public static String MIN_STRATEFY = "min";
    
    String strategy = MAX_STRATEGY;
    
    /**
     * Basic constructor uses default values for minCoverage and beta.
     * @param source
     * @param target
     */
    public MeshBasedSelfConfigurator(ACache source, ACache target) {
        super(source, target);
    }
    
    /**
     * Constructor
     *
     * @param source Source cache
     * @param target Target cache
     * @param minCoverage Minimal coverage for a property to be considered for
     * linking
     *
     */
    public MeshBasedSelfConfigurator(ACache source, ACache target, double minCoverage) {
        super(source, target, minCoverage, 1);
    }

    public static List<Double> copy(List<Double> l) {
        ArrayList<Double> copy = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            copy.add(l.get(i));
        }
        return copy;
    }

    /**
     * Generates a mesh over the space defined by the mins and max set. The size
     * of the mesh is set to ensure that each side of the mesh consists of
     * exactly n points
     *
     * @param min Coordinates on the bottom-left point (in 2D)
     * @param max Coordinates on the top-right point (in 2D)
     * @param n Size of the mesh
     * @return List of coordinates for the mesh points
     */
    public static List<List<Double>> generateCoordinates(List<Double> min, List<Double> max, int n) {
//    	logger.error( "generateCoordinates(List<Double> min, List<Double> max, int n)"+min+"\n"+max+"\n"+n);
        int dimensions = min.size();
        if (dimensions == 0) {
            return null;
        }
        List<List<Double>> result = new ArrayList<List<Double>>();
        double delta = (max.get(0) - min.get(0)) / (double) (n - 1);//0.25
        for (int i = 0; i < n; i++) {
            ArrayList<Double> entry = new ArrayList<Double>();
            entry.add(min.get(0) + i * delta);
            result.add(entry);
        }
//        int count = 0;
        List<List<Double>> buffer;
        List<List<Double>> clones;
        for (int dim = 1; dim < dimensions; dim++) {
            buffer = new ArrayList<List<Double>>();
            for (int i = 0; i < result.size(); i++) {
                clones = new ArrayList<List<Double>>();
                //first create n clones of each entry in result
                for (int j = 0; j < n; j++) {
                    clones.add(copy(result.get(i)));
                }

                //then add new coordinates
                delta = (max.get(dim) - min.get(dim)) / (double) (n - 1);
                for (int j = 0; j < n; j++) {
                    clones.get(j).add(min.get(dim) + j * delta);
//                    count++;
//                   logger.error("clones nr "+count+": "+clones.get(j));
                    buffer.add(clones.get(j));
                }
            }
            result = buffer;
        }
//        logger.error("Coordinates:  "+result);
        return result;
    }

    /**
     * Computes the mappings for a classifier across a dimension of the mesh
     *
     * @param min
     * @param max
     * @param n
     * @param cp
     * @return
     */
    public Map<Double, AMapping> getMappings(double min, double max, int n, SimpleClassifier cp) {
        Map<Double, AMapping> result = new HashMap<>();
        double delta = (max - min) / (double) (n - 1);
        double threshold;
        AMapping m;
        if (min == 0 && max == min) {
            return new HashMap<>();
        }
        if (min == 0 && max != min) {
            m = executeClassifier(cp, min + delta);
        } else {
            m = executeClassifier(cp, min);
        }
        for (int i = 0; i < n; i++) {
            threshold = min + i * delta;
            if (threshold > 0) {
                result.put(threshold, m.getSubMap(threshold));
            }
        }
        return result;
    }
    
    /**
     * Generates the grids iteratively to find the best possible solution
     *
     * @param gridPoints Number of points used for the grid. 5 leads to a
     * 4x4x..x4 grid
     * @param iterations Number of times that the zooming is applied
     * @param sc Initial classifier, defines the dimensions of the space
     * @return A complex classifier
     */
    public ComplexClassifier getZoomedHillTop(int gridPoints, int iterations, List<SimpleClassifier> sc) {
//        logger.info("Beginning self-configuration process ... ");
        //first iteration
        if (gridPoints < 5) {
            gridPoints = 5;
        }
        ComplexClassifier cc = getHillTop(gridPoints, sc);
        ComplexClassifier bestCc = cc;
        double delta = 1.0 / ((double) gridPoints - 1.0);
        for (int i = 1; i < iterations; i++) {
            if (cc.getfMeasure() == 1) {
                return cc;
            }
//            logger.info("Current F-score = " + cc.fMeasure);
//            logger.info("Current delta = " + delta);

            List<Double> min = new ArrayList<Double>();
            List<Double> max = new ArrayList<Double>();

            for (int j = 0; j < cc.getClassifiers().size(); j++) {
                //fill min
                if (cc.getClassifiers().get(j).getThreshold() >= delta) {
                    min.add(cc.getClassifiers().get(j).getThreshold() - delta);
                } else {
                    min.add(0.0);
                }
                //fill max
                if (cc.getClassifiers().get(j).getThreshold() + delta >= 1) {
                    max.add(1.0);
                } else {
                    max.add(cc.getClassifiers().get(j).getThreshold() + delta);
                }
            }
            //get best classifier from the grid
            cc = getHillTop(min, max, gridPoints, cc.getClassifiers());
            // remember the best overall classifier
            if (bestCc.getfMeasure() <= cc.getfMeasure()) {
                bestCc = cc;
            } else {
                cc = bestCc;
            }
            delta = 2 * delta / ((double) gridPoints - 1);
        }
//        logger.info("Final F-score = " + bestCc.fMeasure);
        return bestCc;
    }

    /** Alternative: Generates the grids iteratively to find the best possible solution, within a given maxmimal duration in seconds
     *
     * @param gridPoints Number of points used for the grid. 5 leads to a
     * @param duration maximal duration in seconds
     * @param sc Initial classifier, defines the dimensions of the space
     * @return A complex classifier
     */
    public ComplexClassifier getZoomedHillTop(int gridPoints, long duration, List<SimpleClassifier> sc) {
//        logger.info("Beginning self-configuration process ... ");
        //first iteration
        if (gridPoints < 5) {
            gridPoints = 5;
        }
        long start = System.currentTimeMillis();
        ComplexClassifier cc = getHillTop(gridPoints, sc);
        ComplexClassifier bestCc = cc;
        double delta = 1.0 / ((double) gridPoints - 1.0);
        for (int i = 1; i>0; i++) {
            if (cc.getfMeasure() == 1) {
                return cc;
            }
//            logger.info("Current F-score = " + cc.fMeasure);
//            logger.info("Current delta = " + delta);
            if((System.currentTimeMillis()-start)/1000 >= duration)
                break;

            List<Double> min = new ArrayList<Double>();
            List<Double> max = new ArrayList<Double>();

            for (int j = 0; j < cc.getClassifiers().size(); j++) {
                //fill min
                if (cc.getClassifiers().get(j).getThreshold() >= delta) {
                    min.add(cc.getClassifiers().get(j).getThreshold() - delta);
                } else {
                    min.add(0.0);
                }
                //fill max
                if (cc.getClassifiers().get(j).getThreshold() + delta >= 1) {
                    max.add(1.0);
                } else {
                    max.add(cc.getClassifiers().get(j).getThreshold() + delta);
                }
            }
            //get best classifier from the grid
            cc = getHillTop(min, max, gridPoints, cc.getClassifiers());
            // remember the best overall classifier
            if (bestCc.getfMeasure() <= cc.getfMeasure()) {
                bestCc = cc;
//                globalBestMapping = bestMapping;
            } else {
                cc = bestCc;
            }
            delta = 2 * delta / ((double) gridPoints - 1);
        }
//        logger.info("Final F-score = " + bestCc.fMeasure);
        return bestCc;
    }

    public ComplexClassifier getHillTop(int n, List<SimpleClassifier> sc) {
        List<Double> min = new ArrayList<Double>();
        List<Double> max = new ArrayList<Double>();
        for (int i = 0; i < sc.size(); i++) {
            min.add(0.0);
            max.add(1.0);
        }
        return getHillTop(min, max, n, sc);
    }

    /**    
     * 
     * @param min
     * @param max
     * @param n
     * @param sc
     * @return
     */
    public ComplexClassifier getHillTop(List<Double> min, List<Double> max, int n, List<SimpleClassifier> sc) {
//        logger.info("Getting hill top for dimensions described in " + sc);
        //first generate coordinates of points in the mesh
        //these basically give the thresholds for each of the classifiers
        List<List<Double>> coordinates = generateCoordinates(min, max, n);
//        logger.info("Generated " + coordinates.size() + " grid points ...");
        //then generate mappings for each of the classifiers
        // the access to the map is classifier_index -> threshold -> mapping
        Map<Double, Map<Double, AMapping>> mappings = new HashMap<Double, Map<Double, AMapping>>();
        for (int i = 0; i < sc.size(); i++) {
            mappings.put(new Double(i), getMappings(min.get(i), max.get(i), n, sc.get(i)));
        }
        //get list of best classifiers
        double bestF = -1;
        List<List<Double>> highestPoints = new ArrayList<List<Double>>();
        AMapping bestMapping = MappingFactory.createDefaultMapping();
        AMapping m = MappingFactory.createDefaultMapping();
        for (int i = 0; i < coordinates.size(); i++) {
            List<Double> meshPoint = coordinates.get(i);
            List<AMapping> currentMappings = new ArrayList<AMapping>();
            //get the mappings for the current meshpoint
            for (int j = 0; j < meshPoint.size(); j++) {
                // take the jth classifier and its threshold
                if (meshPoint.get(j) > 0) {
                    currentMappings.add(mappings.get(new Double(j)).get(meshPoint.get(j)));
                }
            }
            double f;
            if (currentMappings.isEmpty()) {
                f = 0.0;
            } else {
                m = getIntersection(currentMappings);

                f = computeQuality(m);
            }
            if (f > bestF) {
                highestPoints = new ArrayList<List<Double>>();
                highestPoints.add(meshPoint);
                bestF = f;
                bestMapping = m;
            } else if (f == bestF) {
                highestPoints.add(meshPoint);
            }
        }
        //we have found the best points. Return the point with the highest total coordinates
        double bestSum = -1;
        List<Double> bestPoint = null;
        //if solution unique return it
        if (highestPoints.size() == 1) {
            bestPoint = highestPoints.get(0);
        } //else return solution with highest sum of thresholds. Reason is simply
        //that we want to be biased towards precision
        else {
            if (strategy.toLowerCase().startsWith(MAX_STRATEGY)) {
                for (int i = 0; i < highestPoints.size(); i++) {
                    List<Double> point = highestPoints.get(i);
                    double sum = 0;
                    for (int j = 0; j < point.size(); j++) {
                        sum = sum + point.get(j);
                    }
                    if (sum > bestSum) {
                        bestPoint = point;
                        bestSum = sum;
                    }
                }
            } else {
                bestSum = sc.size();
                for (int i = 0; i < highestPoints.size(); i++) {
                    List<Double> point = highestPoints.get(i);
                    double sum = 0;
                    for (int j = 0; j < point.size(); j++) {
                        sum = sum + point.get(j);
                    }
                    if (sum < bestSum) {
                        bestPoint = point;
                        bestSum = sum;
                    }
                }
            }
        }
        List<SimpleClassifier> scList = new ArrayList<SimpleClassifier>();
        for (int i = 0; i < sc.size(); i++) {
            if (bestPoint.get(i) > 0) {
                scList.add(sc.get(i).clone());
                scList.get(scList.size() - 1).setThreshold(bestPoint.get(i));
            }
        }
        ComplexClassifier cc = new ComplexClassifier(scList, bestF);
        cc.setMapping(bestMapping);
//        System.out.println("Best Classifier: " + cc.getClassifiers());
//        System.out.println("Highest Point: " + bestPoint);
//        System.out.println("Best AMapping: " + cc.mapping);
//        System.out.println("FMeasure = " + bestF);
        return cc;
    }

    
    public void setStrategy(String strategy) {
    	this.strategy = strategy;
    }
}
