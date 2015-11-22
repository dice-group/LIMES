/**
 * 
 */
package org.aksw.limes.core.evaluation;

import static org.junit.Assert.*;

import java.util.TreeSet;

import org.junit.Test;

import java.util.Set;

import org.aksw.limes.core.evaluation.quality.FMeasure;
import org.aksw.limes.core.evaluation.quality.Precision;
import org.aksw.limes.core.evaluation.quality.Recall;
import org.aksw.limes.core.model.*;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * @author mofeed
 *
 */
public class QualitativeMeasuresTest {

	@Test
	public void test() {
		Model model = ModelFactory.createDefaultModel();
		Set<Link> m1 = new TreeSet<Link>();
		Set<Link> m2 = new TreeSet<Link>();
		Set<Link> ref = new TreeSet<Link>();

		Set<Link> goldStandard = initGoldStandardList();
		Set<Link> predictions = initPredictionsList();
		double precision = new Precision().calculate(predictions, goldStandard);
    	assertTrue(precision == 0.5);
    	
    	double recall = new Recall().calculate(predictions, goldStandard);
    	assertTrue(recall == 0.6);

    	double fmeasure = new FMeasure().calculate(predictions, goldStandard);
    	assertTrue(fmeasure > 1.8);

	}
	private Set<Link> initGoldStandardList()
	{
		Set<Link> goldStandard = new TreeSet<Link>();
		NodeFactory.createURI("");
		Node p = NodeFactory.createURI("http://www.w3.org/2002/07/owl#sameAs");
		Link l2 = new Link(NodeFactory.createURI("http://dbpedia.org/resource/Albania"), NodeFactory.createURI("http://dbpedia.org/resource/Albania"), p);
		Link l3 = new Link(NodeFactory.createURI("http://dbpedia.org/resource/Algeria"), NodeFactory.createURI("http://dbpedia.org/resource/Algeria"), p);
		Link l4 = new Link(NodeFactory.createURI("http://dbpedia.org/resource/Andorra"), NodeFactory.createURI("http://dbpedia.org/resource/Andorra"), p);
		Link l5 = new Link(NodeFactory.createURI("http://dbpedia.org/resource/Argentina"), NodeFactory.createURI("http://dbpedia.org/resource/Argentina"), p);
		Link l6 = new Link(NodeFactory.createURI("http://dbpedia.org/resource/Australia"), NodeFactory.createURI("http://dbpedia.org/resource/Australia"), p);
		goldStandard.add(l2);
		goldStandard.add(l3);
		goldStandard.add(l4);
		goldStandard.add(l5);
		goldStandard.add(l6);		
		return goldStandard;
		
	}
	private Set<Link> initPredictionsList()
	{
		Set<Link> predictions = new TreeSet<Link>();
		Node p = NodeFactory.createURI("http://www.w3.org/2002/07/owl#sameAs");
		Link l1 = new Link(NodeFactory.createURI("http://dbpedia.org/resource/Afghanistan"), NodeFactory.createURI("http://dbpedia.org/resource/Afghanistan"), p);
		Link l2 = new Link(NodeFactory.createURI("http://dbpedia.org/resource/Albania"), NodeFactory.createURI("http://dbpedia.org/resource/Albania"), p);
		Link l3 = new Link(NodeFactory.createURI("http://dbpedia.org/resource/Algeria"), NodeFactory.createURI("http://dbpedia.org/resource/Algeria"), p);
		Link l4 = new Link(NodeFactory.createURI("http://dbpedia.org/resource/Angola"), NodeFactory.createURI("http://dbpedia.org/resource/Andorra"), p);
		Link l5 = new Link(NodeFactory.createURI("http://dbpedia.org/resource/Argentina"), NodeFactory.createURI("http://dbpedia.org/resource/Argentina"), p);
		Link l6 = new Link(NodeFactory.createURI("http://dbpedia.org/resource/Austria"), NodeFactory.createURI("http://dbpedia.org/resource/Australia"), p);
		predictions.add(l1);
		predictions.add(l2);
		predictions.add(l3);
		predictions.add(l4);
		predictions.add(l5);
		predictions.add(l6);		
		return predictions;
		
	}

}
