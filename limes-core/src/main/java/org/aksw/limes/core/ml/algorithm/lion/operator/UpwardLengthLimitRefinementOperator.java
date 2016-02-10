package org.aksw.limes.core.ml.algorithm.lion.operator;


import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.measures.mapper.MappingOperations.Operator;
import org.aksw.limes.core.measures.measure.Measure;
import org.aksw.limes.core.measures.measure.string.CosineMeasure;
import org.aksw.limes.core.measures.measure.string.JaccardMeasure;
import org.aksw.limes.core.measures.measure.string.Levenshtein;
import org.aksw.limes.core.measures.measure.string.StringMeasure;
import org.aksw.limes.core.measures.measure.string.TrigramMeasure;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.aksw.limes.core.util.SetUtilities;
import org.apache.log4j.Logger;

/**
 * Default implementation of the length limited refinement operator
 * @author Klaus Lyko
 * @version Feb 1, 2016
 */
public class UpwardLengthLimitRefinementOperator 
//		extends UpwardRefinementOperator 
		implements LengthLimitedRefinementOperator{
	
	static Logger logger = Logger.getLogger("LIMES");
	
	LearningSetting setting;
	Configuration configuration;
	
//	 EvaluationData evalData;
	
	ThresholdDecreaser thresDec = new ThresholdDecrement();
	
	static Set<StringMeasure> stringMeasures = new HashSet<StringMeasure>();

	Double[] intialThresholds = {1.0d};
	
	public UpwardLengthLimitRefinementOperator(){
			stringMeasures = new HashSet<StringMeasure>();
			stringMeasures.add(new TrigramMeasure());
			stringMeasures.add(new Levenshtein());	
			stringMeasures.add(new CosineMeasure());
//			stringMeasures.add(new OverlapMeasure());
			stringMeasures.add(new JaccardMeasure());
	}
	
	@Override
	public Set<LinkSpecification> refine(LinkSpecification spec, int maxLength) {
		Set<LinkSpecification> linkSpecs = new HashSet<LinkSpecification>();
		
		/*root/bottom case: all atomics with 1d threshold
		*/
		if((spec == null || spec.isEmpty() ) && maxLength<=1) {
			for(LinkSpecification ls: getAllAtomicMeasures(intialThresholds)) {
				linkSpecs.add(ls);
			}
			logger.info("Refined root to the first time. Created "+linkSpecs.size()+" children (atomic specs).");
		} 
		/*atomic at maxLength1: Threshold decrease or OR
		 */
		if( !spec.isEmpty() && spec.isAtomic() ) {
//			logger.info("Have to refine atomic LS "+spec);
			
			if(maxLength<=1) {
				Set<Double> thresholds = thresDec.decrease(spec);
				logger.info("Refining atomicspec into "+thresholds.size()+" new specs: "+spec.getShortendFilterExpression()+"|"+spec.getThreshold()+" with threshold decrease. MaxLength= "+maxLength);
				for(Double thres : thresholds) {
					LinkSpecification child = spec.clone();
					child.setThreshold(thres);
					linkSpecs.add(child);
				}
			} else {
//				logger.error("Creating disjuntions of size "+maxLength);
				List<LinkSpecification> atoms = getAllAtomicsExcept(spec);
				if(maxLength<3) {// only create disjunctions at length 2
					for(LinkSpecification child : atoms) {
	
						LinkSpecification disjunction = new LinkSpecification();
						disjunction.setOperator(Operator.OR);
						LinkSpecification oldChild = spec.clone();
						oldChild = disjunction;
						disjunction.addChild(oldChild);
						disjunction.setThreshold(oldChild.getThreshold());
						if(disjunction.getThreshold()>child.getThreshold()) {
							disjunction.setThreshold(child.getThreshold());
						}
				
						child.setParent(disjunction);
						disjunction.addChild(child);
						if(disjunction.size()>=2)
							linkSpecs.add(disjunction);
					}
				} else { // create disjunctions of size >= 3
					Set<LinkSpecification> conjunctions =  generateAllConjunctions(maxLength, Operator.OR);
					logger.error("Expanding root with maxlength="+maxLength+" into "+conjunctions.size()+" OR");
					for(LinkSpecification conjunction : conjunctions)
						linkSpecs.add(conjunction);
					return linkSpecs;
				}
				logger.error("Creating disjuntions of size "+maxLength+" result: "+linkSpecs.size()+" specs");
			}
			return linkSpecs;
		} //end atomics
		/***/
	
		else
		/* bottom again with expansion >= 2
		 * Create all possible conjunctions (of size maxlength)  of atomic measure*/
		if((spec== null || spec.isEmpty()) && maxLength >= 2) {
			Set<LinkSpecification> conjunctions =  generateAllConjunctions(maxLength, Operator.AND);
			logger.error("Expanding root with maxlength="+maxLength+" into "+conjunctions.size()+" ANDs");
			for(LinkSpecification conjunction : conjunctions)
				linkSpecs.add(conjunction);
			return linkSpecs;
		}
		
		/*recursive AND case: spec!=atomic, maxLength <= 2
		 *  new Root: same operator as spec AND
		 * 		refine each child: recursivRefined, should decrease thresholds
		 */
		if(!spec.isAtomic() && spec.getOperator()==Operator.AND) {
			logger.error("Refining complex AND LS with conjunction: "+spec+"");
			for(int i = 0; i<spec.getChildren().size(); i++) {
//				System.out.println("Generating "+i+"th new Root for "+spec.children.get(i));
			
				Set<LinkSpecification> recursivRefined = refine(spec.getChildren().get(i), maxLength-1);
				for(LinkSpecification recLS : recursivRefined) {
					System.out.println(i+"\tCreating LS for recursive "+recLS);
					LinkSpecification newRoot = new LinkSpecification();
					newRoot.setOperator(spec.getOperator());
					newRoot.setThreshold(spec.getThreshold());
					for(int j = 0; j<spec.getChildren().size(); j++) {
						if(i!=j) {
							LinkSpecification cloneJ = spec.getChildren().get(j).clone();
							cloneJ.setParent(newRoot);
							newRoot.addChild(cloneJ);
							if(newRoot.getThreshold() > cloneJ.getThreshold())
								newRoot.setThreshold(cloneJ.getThreshold());
						}
					}
					recLS.setParent(newRoot);
					newRoot.addChild(recLS);
					if(newRoot.getThreshold() > recLS.getThreshold())
						newRoot.setThreshold(recLS.getThreshold());
					
					linkSpecs.add(newRoot);
				}// for all refinements
			}// for all children of AND
			return linkSpecs;
		}

		/*recursive OR case: spec!=atomic, maxLength >= 2
		 *  new Root: same operator as spec
		 * 		refine each child: recursivRefined
		 */
		if(spec.getOperator() == Operator.OR) {
			logger.debug("Attempting to expand OR");
			if(spec.getChildren()==null || spec.getChildren().size()==0)
				return linkSpecs;
			if(maxLength <= 2) {
				logger.debug("Refining complex OR LS with length "+spec.size()+" and maxLength "+maxLength+" by refining a child.");
				for(int i = 0; i<spec.getChildren().size(); i++) {// forall children
					LinkSpecification orgChild = spec.getChildren().get(i);
					Set<LinkSpecification> recursivRefined = refine(orgChild, maxLength-1);
					for(LinkSpecification recLS : recursivRefined) {// forall refinements of child
						// add all other children
						LinkSpecification newRoot = new LinkSpecification(); // copy OR
						newRoot.setOperator(spec.getOperator());
						newRoot.setThreshold(spec.getThreshold());
						for(int j = 0; j<spec.getChildren().size(); j++)
							if(j!=i) {
								LinkSpecification cloneJ = spec.getChildren().get(j).clone();
								cloneJ.setParent(newRoot);
								newRoot.addChild(cloneJ);
							}
						recLS.setParent(newRoot);
						newRoot.addChild(recLS);
						if(newRoot.getThreshold()<recLS.getThreshold()) 
							newRoot.setThreshold(recLS.getThreshold());
						linkSpecs.add(newRoot);
					}// for all refinements of this node
				} // for all children of OR
			}// maxlength<=2
			else {
				logger.debug("Refining complex OR LS with length "+spec.size()+" and maxLength "+maxLength+ " by adding new atomic.");
				List<LinkSpecification> children = spec.getAllLeaves();
				List<LinkSpecification> allOthers;
				if(children.size()>=1)
					 allOthers = getAllAtomicsExcept(children.get(0));
				else 
					allOthers = getAllAtomicMeasures(intialThresholds);
				for(LinkSpecification newChild : allOthers) {
					boolean valid = true;
					for(int i = 1; i<children.size(); i++) {
						LinkSpecification child = children.get(i);
						if(child.isAtomic()) {
							if(//child.getAtomicMeasure().equalsIgnoreCase(newChild.getAtomicMeasure()) &&
								 child.getProperty1().equalsIgnoreCase(newChild.getProperty1()) &&
									child.getProperty2().equalsIgnoreCase(newChild.getProperty2())) {
								valid = false;
							}
						}
					}
					if(valid) {
						LinkSpecification newDisjunction = spec.clone();
						for(LinkSpecification child:children) {
							LinkSpecification copy = child.clone();
							copy.setParent(newDisjunction);
							newDisjunction.addChild(copy);
						}
							
						newChild.setParent(newDisjunction);
						newDisjunction.addChild(newChild);
						if(newDisjunction.size()>=2)
							linkSpecs.add(newDisjunction);
					}
				}
			}
			logger.debug("Refined Or into "+linkSpecs.size()+" specs");
			return linkSpecs;			
		}		
		return linkSpecs;
	}
	private List<LinkSpecification> getAllAtomicMeasures(Double[] thresholds) {
		List<LinkSpecification> linkSpecs = new LinkedList<LinkSpecification>();
		/*get all mapping properties*/
		PropertyMapping propMapper = setting.getPropMap();
		Mapping propMap = propMapper.getCompletePropMapping();
		String sourceVar = configuration.getSourceInfo().getVar();
		if(sourceVar.startsWith("?")&& sourceVar.length()>=2)
			sourceVar = sourceVar.substring(1);
		String targetVar = configuration.getTargetInfo().getVar();
		if(targetVar.startsWith("?")&& targetVar.length()>=2)
			targetVar = targetVar.substring(1);
		for(Double threshold : thresholds)
		for(String prop1 : propMap.getMap().keySet()) {
			for(String prop2 : propMap.getMap().get(prop1).keySet()) {
				for(Measure m : stringMeasures) {
						//for each (Propertypair x Measure) tupel: create an atomic LinkSpec
						LinkSpecification child = new LinkSpecification();
//						child.operator = Operator.
						child.setAtomicFilterExpression(m.getName(), sourceVar+"."+prop1, targetVar+"."+prop2);
//												child.filterExpression=m.getName()+"("+sourceVar+"."+prop1+","+targetVar+"."+prop2+")";
						child.setThreshold(threshold);
						linkSpecs.add(child);
				}//end for measure					
			}
		}//end each source Prop
		logger.info("Created "+linkSpecs.size()+" atomic measures");
		return linkSpecs;
	}

	/**
	 * Creates all other atomic measures for current EvaluationData except those over the same properties as the atomic LinkSpec ls0
	 * @param ls0
	 * @return
	 */
	private List<LinkSpecification> getAllAtomicsExcept(LinkSpecification ls0 ) {
		List<LinkSpecification> linkSpecs = new LinkedList<LinkSpecification>();
		/*get all mapping properties*/
		PropertyMapping propMapper = setting.getPropMap();
		Mapping propMap = propMapper.getCompletePropMapping();
	
		String sourceVar = configuration.getSourceInfo().getVar();
		if(sourceVar.startsWith("?")&& sourceVar.length()>=2)
			sourceVar = sourceVar.substring(1);
		String targetVar = configuration.getTargetInfo().getVar();
		if(targetVar.startsWith("?")&& targetVar.length()>=2)
			targetVar = targetVar.substring(1);
		for(Double threshold : new Double[]{1d, 0.5d})
		for(String prop1 : propMap.getMap().keySet()) {
			for(String prop2 : propMap.getMap().get(prop1).keySet()) {
				for(Measure m : stringMeasures) {
					
						//for each (Property pair x Measure) tuple: create an atomic LinkSpec
						if(!ls0.getAtomicMeasure().trim().equalsIgnoreCase(m.getName().trim()) && (
								!ls0.getProperty1().equalsIgnoreCase(sourceVar+"."+prop1) ||
								!ls0.getProperty2().equalsIgnoreCase(targetVar+"."+prop2)))
						{
							LinkSpecification child = new LinkSpecification();
	//						child.operator = Operator.
							child.setAtomicFilterExpression(m.getName(), sourceVar+"."+prop1, targetVar+"."+prop2);
							//						child.filterExpression=m.getName()+"("+sourceVar+"."+prop1+","+targetVar+"."+prop2+")";
							child.setThreshold(threshold);
//							System.out.println("Get all Atoms expect "+ls0+"\n\t" +
//									"returning "+m.getName()+"("+sourceVar+"."+prop1+","+targetVar+"."+prop2+")");
							linkSpecs.add(child);
						}else{
//							System.out.println("Get all Atoms expect "+ls0+"\n\t" +
//									"not returning "+m.getName()+"("+sourceVar+"."+prop1+","+targetVar+"."+prop2+")");
						}
					
				}//end for measure					
			}
		}//end each source Prop
//		logger.info("Created "+linkSpecs.size()+" atomic measures");
		return linkSpecs;
	}
	
	@Override
	public void setLearningSetting(LearningSetting setting) {
		this.setting = setting;
	}
	@Override
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Method to generate all LinkSpecs of size >=2 using conjunctions.
	 * @param size
	 * @return
	 */
	private Set<LinkSpecification> generateAllConjunctions(int size, Operator op) {
		Double[] atomsThres = { 1d };
		List<LinkSpecification> atomics = getAllAtomicMeasures(atomsThres);
		Set<LinkSpecification> allAtomics = new HashSet<LinkSpecification>();
		allAtomics.addAll(atomics);
		if(atomics.size() != allAtomics.size())
			logger.warn("Casting list of all atomic "+atomics.size()+" specs into set resulted only in"+allAtomics.size());
		Set<LinkSpecification> specs = new HashSet<LinkSpecification>();
		logger.info("create all conjunctions of size " + Math.min(atomics.size(), size));
		for(Set<LinkSpecification> set : SetUtilities.sizeRestrictPowerSet(allAtomics, Math.min(5, size))) {
			LinkSpecification conjunction = new LinkSpecification();
			conjunction.setOperator(Operator.AND);
//			conjunction.operator = op;
			double threshold = 0d;
			if(set.size()<=1)
				continue;
			for(LinkSpecification child : set) {
//				child.threshold = 0.9; //FIXME Why that????
				child.setParent(conjunction);
				conjunction.addChild(child);
				threshold += child.getThreshold();
			}
			if(conjunction.containsRedundantProperties()) // avoid different children over same properties
				continue;
			conjunction.setThreshold(threshold / set.size());
			boolean success = specs.add(conjunction);
			if(!success)
				logger.warn("Could'nt add conjunction as it already exists.");
		}	
		logger.info("Created "+specs.size()+" LinkSpec Conjunction of size "+size);
		return specs;
	}
	
//	public static void main(String args[]) {
//		UpwardLengthLimitRefinementOperator op = new UpwardLengthLimitRefinementOperator();
//		op.setEvalData(DataSetChooser.getData(DataSets.DBLPACM));
//		logger.setLevel(Level.INFO);
//		LinkSpec spec = new LinkSpec();
//		op.testRefineMentAnd();
//	}
//	
	/**
	 * Method generates the number of thresholds in [low,high]
	 * @param low lowest threshold
	 * @param high highest threshold
	 * @param number specifies the number of different thresholds.
	 * @return List of size number of doubled valued thresholds in [low,high]
	 */
	public static List<Double> allOptimizedThresholds(double low, double high, int number) {
		double range = high - low;
		
		double steps = range / number;
		double t = low;
		List<Double> thresh = new LinkedList<Double>();
		if(range < 0.01) {
					thresh.add(high);
					return thresh;
		}
		for(int i = 1; i<= number; i++) {
			t = t + steps;
			thresh.add(t);
		}
		return thresh;
	}
//	@Test
//	public void testRefineMent2() {
//		UpwardLengthLimitRefinementOperator op = new UpwardLengthLimitRefinementOperator();
//		
//		LinkSpec ls = new LinkSpec();
//		ls.operator = Operator.OR;
//		ls.threshold = 1;
//		LinkSpec ch1 = new LinkSpec();
//		ch1.threshold = 1;
//		ch1.setAtomicFilterExpression("trigrams", "x.prop1", "y.prop1");
//		LinkSpec ch2 = new LinkSpec();
//		ch2.threshold = 0.8;
//		ch2.setAtomicFilterExpression("trigrams", "x.prop2", "y.prop2");
//		ls.addChild(ch1);
//		ls.addChild(ch2);
//		Set<LinkSpec> list = op.refine(ls, 2);
//		System.out.println(list.size() == 1);
//		LinkSpec ref = list.iterator().next();
//		System.out.println(ref);
//		assertTrue(ref.children.size() == 2);
//	}
//	@Test
//	public void testRefineMentAnd() {
//		UpwardLengthLimitRefinementOperator op = new UpwardLengthLimitRefinementOperator();
//
//		EvaluationData data = DataSetChooser.getData(DataSets.DBLPACM);
//		op.setEvalData(data);
//		
//		
//		LinkSpec ls = new LinkSpec();
//		ls.operator = Operator.AND;
//		ls.threshold = 1;
//		LinkSpec ch1 = new LinkSpec();
//		ch1.threshold = 1;
//		ch1.setAtomicFilterExpression("trigrams", "x.title", "y.title");
//		LinkSpec ch2 = new LinkSpec();
//		ch2.threshold = 0.8;
//		ch2.setAtomicFilterExpression("cosine", "x.authors", "y.authors");
//		ls.addChild(ch1);
//		ls.addChild(ch2);
//		Set<LinkSpec> list = op.refine(ls, 4);
//		System.out.println("list.size()="+list.size());
//		LinkSpec ref = list.iterator().next();
//		System.out.println(ref);
//		assertTrue(ref.children.size() == 2);
//	}
	
}
