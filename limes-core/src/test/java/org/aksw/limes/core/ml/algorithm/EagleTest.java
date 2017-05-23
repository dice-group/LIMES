package org.aksw.limes.core.ml.algorithm;

import static org.junit.Assert.fail;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.Eagle;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.SupervisedMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.UnsupervisedMLAlgorithm;
import org.junit.Test;


/**
 * @author Tommaso Soru (tsoru@informatik.uni-leipzig.de)
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 *
 */
public class EagleTest extends MLAlgorithmTest{

    @Test
    public void testUnsupervised() throws UnsupportedMLImplementationException {
        UnsupervisedMLAlgorithm eagleUnsup = null;
        try {
            eagleUnsup = MLAlgorithmFactory.createMLAlgorithm(Eagle.class,
                    MLImplementationType.UNSUPERVISED).asUnsupervised();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (eagleUnsup.getClass().equals(UnsupervisedMLAlgorithm.class));
        trainingMap = null;
        eagleUnsup.init(null, sc, tc);
        eagleUnsup.getMl().setConfiguration(config);
        eagleUnsup.setParameter(Eagle.PROPERTY_MAPPING, pm);

        MLResults mlModel = eagleUnsup.learn(new PseudoFMeasure());
        AMapping resultMap = eagleUnsup.predict(sc, tc, mlModel);

        assert (resultMap.getSize() > 0);     
    }
    
/* ------------------------------ supervised tests  -----------------------------*/    
    @Test
    public void testSupervisedBatch() throws UnsupportedMLImplementationException {
        SupervisedMLAlgorithm eagleSup = null;
        try {
            eagleSup = MLAlgorithmFactory.createMLAlgorithm(Eagle.class,
                    MLImplementationType.SUPERVISED_BATCH).asSupervised();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (eagleSup.getClass().equals(SupervisedMLAlgorithm.class));
        eagleSup.init(null, sc, tc);
        eagleSup.getMl().setConfiguration(config);
        eagleSup.setParameter(Eagle.INQUIRY_SIZE, 2);
        eagleSup.setParameter(Eagle.PROPERTY_MAPPING, pm);
        /* ------ test predict ------ */
        MLResults mlModel = eagleSup.learn(trainingMap);
        AMapping resultMap = eagleSup.predict(sc, tc, mlModel);
        logger.info("Predicted links:"+resultMap.size());
        assert (resultMap.getSize() >= 0);  
        
        AMapping extendedResultMap = eagleSup.predict(extendedSourceCache, extendedTargetCache, mlModel);
        logger.info("Predicted extended links:"+extendedResultMap.size());
        
        assert(extendedResultMap.size()>=resultMap.size());
        boolean containAll = true;
        for(String sUri : resultMap.getMap().keySet())
        	for(String tUri : resultMap.getMap().get(sUri).keySet())
        		containAll &= extendedResultMap.contains(sUri, tUri);	        
        assert(resultMap.size()<extendedResultMap.size());
        assert(containAll);
        
    }
    
    @Test
    public void testSupervisedActive() throws UnsupportedMLImplementationException {
    	ActiveMLAlgorithm eagleSup = null;
        try {
            eagleSup = MLAlgorithmFactory.createMLAlgorithm(Eagle.class,
                    MLImplementationType.SUPERVISED_ACTIVE).asActive();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (eagleSup.getClass().equals(ActiveMLAlgorithm.class));
        eagleSup.init(null, sc, tc);
        eagleSup.getMl().setConfiguration(config);
        eagleSup.setParameter(Eagle.INQUIRY_SIZE, 2);
        eagleSup.setParameter(Eagle.PROPERTY_MAPPING, pm);
        eagleSup.activeLearn(trainingMap);
       
        AMapping toAnnotate = eagleSup.getNextExamples(2);
        AMapping newOracle = MappingFactory.createDefaultMapping();
        for(String sKey : toAnnotate.getMap().keySet())
        	for(String tKey : toAnnotate.getMap().get(sKey).keySet()) {
        		logger.info("Asking Oracle about "+sKey+" - "+tKey+" ("+toAnnotate.getConfidence(sKey, tKey)+")");
        		if(trainingMap.contains(sKey, tKey) || extendedTrainingMap.contains(sKey, tKey)) {
        			newOracle.add(sKey, tKey, 1d);
        		} else {
        			newOracle.add(sKey, tKey, -1d);
        		}
        	}
        logger.info("new Oracle: "+newOracle.size()+": "+newOracle);
        MLResults resultMap = eagleSup.activeLearn(newOracle);
        logger.info("new resultMap: "+resultMap);
        assert (resultMap.getMapping() != null);  
    }
    
    
    

}
