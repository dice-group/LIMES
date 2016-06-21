package org.aksw.limes.core.ml;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.ml.algorithm.decisionTreeLearning.TreeParser;
import org.junit.Test;

@SuppressWarnings("rawtypes")
public class TreeParserTest {

	@Test
	public void addRootLSToChildTest() throws Exception {
		TreeParser tp = new TreeParser();
		Method addRootLSToChild = null;    
		Class[] parameterTypes = {LinkSpecification.class, LinkSpecification.class};
		addRootLSToChild = tp.getClass().getDeclaredMethod("addRootLSToChild", parameterTypes);
		addRootLSToChild.setAccessible(true);
		LinkSpecification ls1 = new LinkSpecification("AND(trigrams(x.name, y.name)|0.5,jaccard(x.title,y.title)|0.4)", 0.7);
		LinkSpecification ls2 = new LinkSpecification("cosine(x.name, y.name)", 0.7);
		Object[] parameters = {ls1, ls2};
		LinkSpecification ls3 = (LinkSpecification) addRootLSToChild.invoke(tp, parameters);
		assertEquals("(null, 0.0, AND, null,){(null, 0.7, AND, null,){(trigrams(x.name,y.name)|0.50, 0.5, null, null),(jaccard(x.title,y.title)|0.40, 0.4, null, null),},(cosine(x.name,y.name)|0.70, 0.7, null, null),}", ls3.toStringOneLine());
	}
	
	@Test
	public void parseAtomicTreeTest() throws Exception{
		TreeParser tp = new TreeParser();
		Method parseAtomicTree = null;    
		Class[] parameterTypes = {String.class};
		parseAtomicTree = tp.getClass().getDeclaredMethod("parseAtomicTree", parameterTypes);
		parseAtomicTree.setAccessible(true);
		String tree = "jaccard#title|name: <= 0.857143,\n > 0.857143[positive (1528.0/8.0)][negative (108.0)]";
		Object[] parameters = {tree};
		LinkSpecification ls3 = (LinkSpecification) parseAtomicTree.invoke(tp, parameters);
		assertEquals("(null, 0.0, MINUS, null,){(jaccard(x.title,y.name)|0.00, 0.0, null, null),(jaccard(x.title,y.name)|0.86, 0.857143, null, null),}", ls3.toStringOneLine());
	}
	
	//Root contains 2 subtrees
	@Test
	public void parseTreePrefixTestSymmetric() throws Exception{
	    	TreeParser tp = new TreeParser();
		Method parseTreePrefix = null;    
		Class[] parameterTypes = {String.class};
		parseTreePrefix = tp.getClass().getDeclaredMethod("parseTreePrefix", parameterTypes);
		parseTreePrefix.setAccessible(true);
		String tree = "trigrams#title|name: <= 0.888889,\n > 0.888889[cosine#manufacturer|manufacturer: <= 0.5, \n > 0.5[positive (3.0/1.0)][negative (3.0)]][jaccard#title|name: <= 0.606977, \n > 0.606977[negative (3.0/1.0)][positive (2.0)]]";
		Object[] parameters = {tree};
		LinkSpecification ls3 = (LinkSpecification) parseTreePrefix.invoke(tp, parameters);
		assertEquals("(null, 0.0, OR, null,)\n  ->(null, 0.0, AND, null,)\n  ->(null, 0.0, MINUS, null,)\n  ->(trigrams(x.title,y.name), 0.0, null, null)\n  ->(trigrams(x.title,y.name), 0.888889, null, null)\n  ->(null, 0.0, MINUS, null,)\n  ->(cosine(x.manufacturer,y.manufacturer), 0.0, null, null)\n  ->(cosine(x.manufacturer,y.manufacturer), 0.5, null, null)\n  ->(null, 0.0, AND, null,)\n  ->(trigrams(x.title,y.name), 0.888889, null, null)\n  ->(jaccard(x.title,y.name), 0.606977, null, null)", ls3.toString());
	}

	@Test
	public void parseTreePrefixTestComplexTree() throws Exception{
	    	TreeParser tp = new TreeParser();
		Method parseTreePrefix = null;    
		Class[] parameterTypes = {String.class};
		parseTreePrefix = tp.getClass().getDeclaredMethod("parseTreePrefix", parameterTypes);
		parseTreePrefix.setAccessible(true);
		String tree = "trigrams#title|name: <= 0.888889, \n > 0.888889[jaccard#description|description: <= 0.487179, \n > 0.487179[exactmatch#manufacturer|manufacturer: <= 0, \n > 0[jaccard#title|name: <= 0.230769, \n > 0.230769[jaro#manufacturer|manufacturer: <= 0, \n > 0[negative (26.0)][cosine#manufacturer|manufacturer: <= 0.5, \n > 0.5[positive (3.0/1.0)][negative (3.0)]]][negative (1369.0/1.0)]][negative (42.0/1.0)]][cosine#manufacturer|manufacturer: <= 0.288675, \n > 0.288675[negative (20.0/1.0)][cosine#title|name: <= 0.606977, \n > 0.606977[negative (3.0/1.0)][positive (2.0)]]]][positive (168.0)]";
		Object[] parameters = {tree};
		LinkSpecification ls3 = (LinkSpecification) parseTreePrefix.invoke(tp, parameters);
		assertEquals("(null, 0.0, OR, null,){(null, 0.0, AND, null,){(null, 0.0, MINUS, null,){(trigrams(x.title,y.name)|0.00, 0.0, null, null),(trigrams(x.title,y.name)|0.89, 0.888889, null, null),},(null, 0.0, OR, null,){(null, 0.0, AND, null,){(null, 0.0, MINUS, null,){(jaccard(x.description,y.description)|0.00, 0.0, null, null),(jaccard(x.description,y.description)|0.49, 0.487179, null, null),},(null, 0.0, AND, null,){(null, 0.0, MINUS, null,){(exactmatch(x.manufacturer,y.manufacturer)|0.00, 0.0, null, null),(exactmatch(x.manufacturer,y.manufacturer)|0.00, 0.0, null, null),},(null, 0.0, AND, null,){(null, 0.0, MINUS, null,){(jaccard(x.title,y.name)|0.00, 0.0, null, null),(jaccard(x.title,y.name)|0.23, 0.230769, null, null),},(null, 0.0, AND, null,){(jaro(x.manufacturer,y.manufacturer)|0.00, 0.0, null, null),(null, 0.0, MINUS, null,){(cosine(x.manufacturer,y.manufacturer)|0.00, 0.0, null, null),(cosine(x.manufacturer,y.manufacturer)|0.50, 0.5, null, null),},},},},},(null, 0.0, AND, null,){(jaccard(x.description,y.description)|0.49, 0.487179, null, null),(null, 0.0, AND, null,){(cosine(x.manufacturer,y.manufacturer)|0.29, 0.288675, null, null),(cosine(x.title,y.name)|0.61, 0.606977, null, null),},},},},(trigrams(x.title,y.name)|0.89, 0.888889, null, null),}", ls3.toStringOneLine());

	}
}
