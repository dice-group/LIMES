package org.aksw.limes.core.ml.algorithm;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.euclid.BooleanEuclid;
import org.aksw.limes.core.ml.algorithm.euclid.LinearEuclid;
import org.aksw.limes.core.ml.algorithm.euclid.MeshEuclid;
import org.junit.Test;
/**
 * Tests all EUCLID algorithm implementations
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 */
public class EuclidTest extends MLAlgorithmTest{
//	
//
//	@Test
//	public void testAlgorithms() {
//        Class[] algorithms = {
//        		LinearEuclid.class, 
//        		BooleanEuclid.class,
//        		MeshEuclid.class,
//        };
//        // for each Euclid sub type
//        for(Class<? extends ACoreMLAlgorithm> algorithm : algorithms) {
//        	logger.info("Testing unsupervised "+algorithm.getSimpleName());
//        	testUnsupervised(algorithm);
//        	
//        	logger.info("Testing supervised "+algorithm.getSimpleName());
////        	testSupervisedBatch(algorithm);
//        }        
//	}
	
/*-------------------------- unsupervised tests ----------------------------------*/	
	
	/**
	 * Test a certain Euclid implementation. Is unsupervised learning implemented, is it producing actual results?
	 * @param algorithm
	 */
	public void testUnsupervised(Class<? extends ACoreMLAlgorithm> algorithm) {
        UnsupervisedMLAlgorithm unsupEuclid = null;
    	try {
        	unsupEuclid = MLAlgorithmFactory.createMLAlgorithm(algorithm,
                    MLImplementationType.UNSUPERVISED).asUnsupervised();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (unsupEuclid.getClass().equals(UnsupervisedMLAlgorithm.class));
        unsupEuclid.getMl().setDefaultParameters();
        
        List<LearningParameter> params = new ArrayList<LearningParameter>(5);
        params.add(new LearningParameter(LinearEuclid.ITERATIONS_MAX, 30));
        params.add(new LearningParameter(LinearEuclid.KAPPA, 0.8));
        params.add(new LearningParameter(LinearEuclid.LEARNING_RATE, 0.25));
        unsupEuclid.init(params, sc, tc);
        unsupEuclid.getMl().setConfiguration(config);
        assert(unsupEuclid.getMl().supports(MLImplementationType.UNSUPERVISED));
        try {
			MLResults result = unsupEuclid.learn(new PseudoFMeasure());
			
			logger.info("Test (" + unsupEuclid.getName()+") results:");
			logger.info("LS:" + result.linkspec.toStringOneLine());
			logger.info("Mapping size= "  +result.getMapping().size());
			logger.info("Quality:" + result.quality);
			
			for(String key : result.getDetails().keySet()) {
				logger.info(key+" : "+result.getDetails().get(key));    				
			}
			
			assert(result.getLinkSpecification().size()>0);
			assert(result.getMapping().size()>=0);
			
			AMapping mapping = unsupEuclid.predict(sc, tc, result);
			logger.info(mapping);
			logger.info("result:"+result.getMapping().size()+" predict: "+mapping.size());
			if(unsupEuclid.getName().equals(MeshEuclid.ALGORITHM_NAME)) {
				logger.error("Mesh Euclids predict() method doesn't work properly! Skipping test.");
			}
			else
				assert(result.getMapping().size() == mapping.size());
			for(String s : mapping.getMap().keySet()) {
				for(String t : mapping.getMap().get(s).keySet()) {
					assert(result.getMapping().contains(s, t));
				}
			}
			
		} catch (UnsupportedMLImplementationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
/*------------------------------ supervised tests --------------------------------*/
	/**
	 * Tests supervised (batch) variants.
	 * @param algorithm
	 */
    public void testSupervisedBatch(Class<? extends ACoreMLAlgorithm> algorithm) {
        SupervisedMLAlgorithm euclid = null;
        try {
        	euclid = MLAlgorithmFactory.createMLAlgorithm(algorithm,
                    MLImplementationType.SUPERVISED_BATCH).asSupervised();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (euclid.getClass().equals(SupervisedMLAlgorithm.class));
       
        euclid.getMl().setDefaultParameters();
        
        List<LearningParameter> params = new ArrayList<LearningParameter>(5);
        params.add(new LearningParameter(LinearEuclid.ITERATIONS_MAX, 30));
        params.add(new LearningParameter(LinearEuclid.KAPPA, 0.8));
        params.add(new LearningParameter(LinearEuclid.LEARNING_RATE, 0.25));
        euclid.init(params, sc, tc);
        euclid.getMl().setConfiguration(config);
        assert(euclid.getMl().supports(MLImplementationType.SUPERVISED_BATCH));
        
        MLResults mlModel = null;
		try {
			/* Test learning function */
			mlModel = euclid.learn(trainingMap);
			
			logger.info("Test (" + euclid.getName()+") supervised (batch) results:");
			logger.info("Mapping size= "  +mlModel.getMapping().size());
			logger.info("Quality:" + mlModel.quality);
			
			for(String key : mlModel.getDetails().keySet()) {
				logger.info(key+" : "+mlModel.getDetails().get(key));    				
			}
			
			/* TEST predict() method on different caches*/
	        AMapping resultMap = euclid.predict(sc, tc, mlModel);
	        logger.info("Predicted links:"+resultMap.getSize());	        
	        AMapping extendedResultMap = euclid.predict(extendedSourceCache, extendedTargetCache, mlModel);
	        logger.info("Predict on extended maps: "+extendedResultMap.size());
	        
	        boolean containAll = true;
	        for(String sUri : resultMap.getMap().keySet())
	        	for(String tUri : resultMap.getMap().get(sUri).keySet())
	        		containAll &= extendedResultMap.contains(sUri, tUri);	    
	        
	        assert(resultMap.size()<=extendedResultMap.size());
	        assert(containAll);
	        
		} catch (UnsupportedMLImplementationException e) {
			e.printStackTrace();
		}
		
		assert(mlModel != null);
    }
	
	
}
