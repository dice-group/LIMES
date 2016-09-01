package org.aksw.limes.core.ml;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTreeLearning;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.junit.Before;
import org.junit.Test;

//Temporary version
//TODO finish
public class DecisionTreeLearningTest {

    ACache sc = new MemoryCache();
    ACache tc = new MemoryCache();
    Configuration config = new Configuration();

    AMapping trainingMap, refMap;
    PropertyMapping pm;

    @Before
    public void init() {
        List<String> props = new LinkedList<String>();
        props.add("name");
        props.add("surname");

        Instance i1 = new Instance("ex:i1");
        i1.addProperty("name", "Klaus");
        i1.addProperty("surname", "Lyko");
        Instance i2 = new Instance("ex:i2");
        i2.addProperty("name", "John");
        i2.addProperty("surname", "Doe");
        Instance i3 = new Instance("ex:i3");
        i3.addProperty("name", "Claus");
        i3.addProperty("surname", "Stadler");
        Instance i4 = new Instance("ex:i4");
        i4.addProperty("name", "Hans");
        i4.addProperty("surname", "Peter");
        Instance i5 = new Instance("ex:i5");
        i5.addProperty("name", "Maria");
        i5.addProperty("surname", "Mustermann");

        sc.addInstance(i1);
        sc.addInstance(i3);
        sc.addInstance(i4);

        tc.addInstance(i1);
        tc.addInstance(i2);
        tc.addInstance(i3);
        tc.addInstance(i5);

        KBInfo si = new KBInfo();
        si.setVar("?x");
        si.setProperties(props);

        KBInfo ti = new KBInfo();
        ti.setVar("?y");
        ti.setProperties(props);

        config.setSourceInfo(si);
        config.setTargetInfo(ti);

        pm = new PropertyMapping();
        pm.addStringPropertyMatch("name", "name");
        pm.addStringPropertyMatch("surname", "surname");

        trainingMap = MappingFactory.createDefaultMapping();
        trainingMap.add("ex:i1", "ex:i1", 1d);

        refMap = MappingFactory.createDefaultMapping();
        refMap.add("ex:i1", "ex:i1", 1d);
        refMap.add("ex:i3", "ex:i3", 1d);
    }

    @Test
    public void testActive() throws UnsupportedMLImplementationException {
        ActiveMLAlgorithm dtl = null;
        try {
            dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
                    MLImplementationType.SUPERVISED_ACTIVE).asActive();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (dtl.getClass().equals(ActiveMLAlgorithm.class));
        dtl.init(setParameters(), sc, tc);
        dtl.getMl().setConfiguration(config);
        dtl.activeLearn();
        Field deltaLSField = null;
	try {
	    deltaLSField = DecisionTreeLearning.class.getDeclaredField("deltaLS");
	    deltaLSField.setAccessible(true);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} 
	assertNotNull(deltaLSField);
        AMapping nextExamples = dtl.getNextExamples(3);
        AMapping oracleFeedback = oracleFeedback(nextExamples,trainingMap);
        MLResults mlModel = dtl.activeLearn(oracleFeedback);
        AMapping resultMap = dtl.predict(sc, tc, mlModel);
        assert (resultMap.equals(refMap));
    }
    
    private AMapping oracleFeedback(AMapping predictionMapping, AMapping referenceMapping) {
        AMapping result = MappingFactory.createDefaultMapping();

        for(String s : predictionMapping.getMap().keySet()){
            for(String t : predictionMapping.getMap().get(s).keySet()){
                if(referenceMapping.contains(s, t)){
                    result.add(s, t, 1.0);
                }else{
                    result.add(s, t, 0.0);
                }
            }
        }
        return result;
    }

    private List<LearningParameter> setParameters() {
	ArrayList<LearningParameter> parameters = new ArrayList<>();
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_TRAINING_DATA_SIZE, 3, Integer.class, 10d, 100000, 10d, DecisionTreeLearning.PARAMETER_TRAINING_DATA_SIZE));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_UNPRUNED_TREE, false, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_UNPRUNED_TREE));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_COLLAPSE_TREE, true, Boolean.class, 0, 1, 1, DecisionTreeLearning.PARAMETER_COLLAPSE_TREE));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_PRUNING_CONFIDENCE, 0.25, Double.class, 0d, 1d, 0.01d, DecisionTreeLearning.PARAMETER_PRUNING_CONFIDENCE));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_REDUCED_ERROR_PRUNING, false, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_REDUCED_ERROR_PRUNING));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_FOLD_NUMBER, 3, Integer.class, 0, 10, 1, DecisionTreeLearning.PARAMETER_FOLD_NUMBER));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_SUBTREE_RAISING, true, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_SUBTREE_RAISING));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_CLEAN_UP, true, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_CLEAN_UP));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_LAPLACE_SMOOTHING, false, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_LAPLACE_SMOOTHING));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_MDL_CORRECTION, true, Boolean.class, 0, 1, 0, DecisionTreeLearning.PARAMETER_MDL_CORRECTION));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_SEED, 1, Integer.class, 0, 100, 1, DecisionTreeLearning.PARAMETER_SEED));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_PROPERTY_MAPPING, pm, PropertyMapping.class, Double.NaN, Double.NaN, Double.NaN,
		DecisionTreeLearning.PARAMETER_PROPERTY_MAPPING));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_MAPPING, trainingMap, AMapping.class, Double.NaN, Double.NaN, Double.NaN, DecisionTreeLearning.PARAMETER_MAPPING));
	parameters.add(new LearningParameter(DecisionTreeLearning.PARAMETER_LINK_SPECIFICATION, null , LinkSpecification.class, Double.NaN, Double.NaN, Double.NaN,
		DecisionTreeLearning.PARAMETER_LINK_SPECIFICATION));
	return parameters;
    }
    
    @Test
    public void testDelta(){
	try {
	DecisionTreeLearning dtl = new DecisionTreeLearning();
	Class[] argClasses = new Class[1];
	argClasses[0] = LinkSpecification.class;
	Method subtractDeltaFromLS = dtl.getClass().getDeclaredMethod("subtractDeltaFromLS", argClasses);
	subtractDeltaFromLS.setAccessible(true);
	LinkSpecification lsComplex = new LinkSpecification(
                "OR(jaccard(x.surname,y.name)|0.5,OR(XOR(OR(XOR(trigrams(x.name,y.name)|0.7728,qgrams(x.surname,y.name)|0.6)|0.7,XOR(trigrams(x.name,y.name)|0.78,qgrams(x.surname,y.name)|0.6029)|0.7728)|0.5807,OR(XOR(trigrams(x.name,y.name)|0.7728,qgrams(x.surname,y.name)|0.6029)|0.7728,trigrams(x.surname,y.name)|0.5919)|0.5807)|0.7728,trigrams(x.name,y.name)|0.7728)|0.5807)",
                0.0);
	LinkSpecification lsComplexResult = (LinkSpecification) subtractDeltaFromLS.invoke(dtl, lsComplex);
	assertEquals("(null, 0.0, OR, null,){(jaccard(x.surname,y.name)|0.40, 0.4, null, null),(null, 0.5807, OR, null,){(null, 0.7728, XOR, null,){(null, 0.5807, OR, null,){(null, 0.7, XOR, null,){(trigrams(x.name,y.name)|0.67, 0.6728, null, null),(qgrams(x.surname,y.name)|0.50, 0.5, null, null),},(null, 0.7728, XOR, null,){(trigrams(x.name,y.name)|0.68, 0.68, null, null),(qgrams(x.surname,y.name)|0.50, 0.5029, null, null),},},(null, 0.5807, OR, null,){(null, 0.7728, XOR, null,){(trigrams(x.name,y.name)|0.67, 0.6728, null, null),(qgrams(x.surname,y.name)|0.50, 0.5029, null, null),},(trigrams(x.surname,y.name)|0.49, 0.4919, null, null),},},(trigrams(x.name,y.name)|0.67, 0.6728, null, null),},}", lsComplexResult.toStringOneLine());
	//check if the original ls stayed intact
	assertThat(lsComplex.toStringOneLine(), not(lsComplexResult.toStringOneLine()));
        LinkSpecification lsSimple = new LinkSpecification("cosine(x.name,y.name)|0.8", 0.8);
	LinkSpecification lsSimpleResult = (LinkSpecification) subtractDeltaFromLS.invoke(dtl, lsSimple);
	assertEquals("(cosine(x.name,y.name)|0.70, 0.7, null, null)", lsSimpleResult.toStringOneLine());

        LinkSpecification lsMinus = new LinkSpecification("MINUS(jaccard(x.surname,y.surname)|0.00,jaccard(x.surname,y.surname)|0.60)", 0.0);
	LinkSpecification lsMinusResult = (LinkSpecification) subtractDeltaFromLS.invoke(dtl, lsMinus);
	assertEquals("(null, 0.0, MINUS, null,){(jaccard(x.surname,y.surname)|0.00, 0.0, null, null),(jaccard(x.surname,y.surname)|0.70, 0.7, null, null),}", lsMinusResult.toStringOneLine());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} 
    }
}
