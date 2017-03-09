package org.aksw.limes.core.ml.algorithm;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.euclid.BooleanEuclid;
import org.aksw.limes.core.ml.algorithm.euclid.LinearEuclid;
import org.junit.Test;

public class EuclidTest extends MLAlgorithmTest{
	
	@Test
	public void testUnsupervised() {
        UnsupervisedMLAlgorithm unsupEuclid = null;
        Class[] algorithms = {
        		LinearEuclid.class, 
        		BooleanEuclid.class
        };
        
        for(Class<? extends ACoreMLAlgorithm> algorithm : algorithms) {
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
    			
    			logger.info("TEST (" + unsupEuclid.getName()+"):");
    			logger.info("LS:" + result.linkspec.toStringOneLine());
    			logger.info("Mapping size= "  +result.getMapping().size());
    			logger.info("Quality:" + result.quality);
    			
    			for(String key : result.getDetails().keySet()) {
    				logger.info(key+" : "+result.getDetails().get(key));    				
    			}
    			
    			assert(result.getLinkSpecification().size()>0);
    			assert(result.getMapping().size()>0);
    			
    			AMapping mapping = unsupEuclid.predict(sc, tc, result);
    			logger.info(mapping);
    			
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
        
        
	}
}
