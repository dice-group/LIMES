package org.aksw.limes.core.ml;

import java.util.ArrayList;
import java.util.Arrays;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.AMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.DecisionTreeLearning;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.junit.Before;
import org.junit.Test;

public class Mock {

    public ACache source = new MemoryCache();
    public ACache target = new MemoryCache();
    public AMapping trainingData = MappingFactory.createDefaultMapping();
	public Configuration config = new Configuration();
	public KBInfo sinfo = new KBInfo();
	public KBInfo tinfo = new KBInfo();
	public PropertyMapping propMap = new PropertyMapping();

    @Before
    public void setUp() {
        source = new MemoryCache();
        target = new MemoryCache();
        // create source cache
        source.addTriple("S1", "name", "Hans-Peter");
        source.addTriple("S1", "surname", "Zimmermann");
        source.addTriple("S1", "age", "26");

        source.addTriple("S2", "name", "Heiko Kurt");
        source.addTriple("S2", "surname", "Troelsen");
        source.addTriple("S2", "age", "13");

        source.addTriple("S3", "name", "Amata Joana");
        source.addTriple("S3", "surname", "Troelsen");
        source.addTriple("S3", "age", "52");

        source.addTriple("S4", "name", "Ariane");
        source.addTriple("S4", "surname", "Guerra");
        source.addTriple("S4", "age", "25");

        source.addTriple("S5", "name", "Sib");
        source.addTriple("S5", "surname", "Gonzales");
        source.addTriple("S5", "age", "56");

        target.addTriple("T1", "name", "Hans Peter");
        target.addTriple("T1", "surname", "zimmermann");
        target.addTriple("T1", "age", "26");

        target.addTriple("T2", "name", "Heiko");
        target.addTriple("T2", "surname", "Troelsen");
        target.addTriple("T2", "age", "13");

        target.addTriple("T3", "name", "Amata Joana K.");
        target.addTriple("T3", "surname", "Troelsen");
        target.addTriple("T3", "age", "52");

        target.addTriple("T4", "name", "Ariane");
        target.addTriple("T4", "surname", "Guerra");
        target.addTriple("T4", "age", "25");

        target.addTriple("T5", "name", "Ffion");
        target.addTriple("T5", "surname", "Einarsson");
        target.addTriple("T5", "age", "12");
        
        trainingData.add("S2", "T2", 1.0);
        trainingData.add("S2", "T3", 0.0);
        trainingData.add("S3", "T3", 1.0);
        trainingData.add("S4", "T4", 1.0);
        trainingData.add("S5", "T5", 0.0);
        
        propMap.stringPropPairs = new ArrayList<>(Arrays.asList(new PairSimilar<String>("name","name"),new PairSimilar<String>("surname","surname")));
        propMap.numberPropPairs = new ArrayList<>(Arrays.asList(new PairSimilar<String>("age","age")));

    }
    
    @Test
    public void test() throws UnsupportedMLImplementationException{

			AMLAlgorithm dtl = MLAlgorithmFactory.createMLAlgorithm(DecisionTreeLearning.class,
					MLImplementationType.SUPERVISED_BATCH);
			dtl.init(null, source, target);
			dtl.getMl().setConfiguration(config);
			((DecisionTreeLearning) dtl.getMl()).setPropertyMapping(propMap);
			dtl.asSupervised().learn(trainingData);
    }
}
