package org.aksw.limes.core.ml.algorithm;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.aksw.limes.core.evaluation.quantity.PseudoFMeasure;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.planning.planner.CanonicalPlanner;
import org.aksw.limes.core.execution.planning.planner.Planner;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.MappingOperations.Operator;
import org.aksw.limes.core.ml.algorithm.lion.DefaultRefinementHeuristic;
import org.aksw.limes.core.ml.algorithm.lion.RefinementHeuristic;
import org.aksw.limes.core.ml.algorithm.lion.SearchTreeNode;
import org.aksw.limes.core.ml.algorithm.lion.operator.LengthLimitedRefinementOperator;
import org.aksw.limes.core.ml.algorithm.lion.operator.UpwardLengthLimitRefinementOperator;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.aksw.limes.core.ml.setting.UnsupervisedLearningSetting;
import org.apache.log4j.Logger;
import org.jgap.InvalidConfigurationException;


//
//import com.jamonapi.Monitor;
//import com.jamonapi.MonitorFactory;
//


/**
 * 
 * Refinement based learning algorithm for link specs.
 * @author Jens Lehmann
 * @author Klaus Lyko
 * 
 */
public class LionPrune extends MLAlgorithm {

	
	public boolean advancedThreholdSearch = false;
	public int thresholdGrid = 3;
	
	protected static Logger logger = Logger.getLogger("LIMES");
	protected static int globalTime = 1000;
	protected LengthLimitedRefinementOperator operator;
	//bMofeed
	protected static int IdCounter = 0;
	//eMofeed
	
	// all nodes in the search tree (used for selecting most promising node)
	protected TreeSet<SearchTreeNode> nodes;
	protected RefinementHeuristic heuristic;
	// root of search tree
	protected SearchTreeNode startNode; // it is null
	/**Gamma score for root node to enabling revisting*/
	protected double gammaScore = 0.15d;
	/** all specs in the search tree plus those which were too weak (for fast redundancy check)*/
	protected HashSet<LinkSpecification> specs; // m:note- here check the redundancy
	/**Expansion penalty*/
	protected double expansionPenalty = 0.7d;
	/**reward for better then parent*/
	protected double reward = 1.2;
	// used for logging evaluation data
//	protected ResultLogger resLog;
//	protected List<EvaluationMemory> memList = new LinkedList<EvaluationMemory>();

	protected Planner planner;
	protected ExecutionEngine engine;
	UnsupervisedLearningSetting setting;
	
	
	/**Beta for the PseudoFMeasure**/
	protected double beta = 1d;
	/**Set time of evaluation in seconds*/
	protected long maxDuration = 600;
	protected PseudoFMeasure pfm;
	/*for experiments*/
	protected static boolean debuggingInput = false;
	public static boolean hardRootExpansion = true;
	protected List<Integer> loopsRootExpanded = new LinkedList<Integer>();
	/**Best Score so far**/
	protected double bestScore = 0;
	/**LInk Spec with highest PFM*/
	protected SearchTreeNode best = null;
	
	protected boolean newBest = false; // needed to decide whether we log a new best node
	
//	LearningSetting setting;
	
	public LionPrune(Cache sourceCache, Cache targetCache,
			Configuration config) {
		super(sourceCache, targetCache, config);
		nodes= new TreeSet<SearchTreeNode>();
		specs = new HashSet<>();
		heuristic = new DefaultRefinementHeuristic();
		operator = new UpwardLengthLimitRefinementOperator();
		planner = new CanonicalPlanner();
	}

	@Override
	public void init(LearningSetting parameters, Mapping trainingData) throws InvalidConfigurationException {
		setting = (UnsupervisedLearningSetting) parameters;
		this.gammaScore = setting.getGammaScore();
		this.expansionPenalty = setting.getExpansionPenalty();
		this.maxDuration = setting.getMaxDuration();	
		this.reward = setting.getExpansionPenalty();
		this.loopsRootExpanded = new LinkedList<Integer>();
		
		
		pfm = new PseudoFMeasure();
		heuristic.setLearningSetting(setting);
		operator.setLearningSetting(setting);
		engine = ExecutionEngineFactory.getEngine("default", 
				this.getSourceCache(), 
				this.getTargetCache(), 
				this.getConfiguration().getSourceInfo().getVar(), 
				this.getConfiguration().getTargetInfo().getVar());
	
		
	}
	


	/**
	 *  Start algorithm
	 * @throws IOException 
	 */
	public MLResult start() throws IOException {
		MLResult result = new MLResult();
		// highest accuracy so far
		double highestAccuracy = 0.0;
		SearchTreeNode nextNode;
		startNode = new SearchTreeNode(new LinkSpecification(), gammaScore, expansionPenalty);
		startNode.addSpecificName("root");
		startNode.setExpansion(1);
//		startNode.incExpansion();
		nodes.add(startNode);
		//bMofeed
			startNode.nodeId = IdCounter++; //give id
			startNode.creationTime = globalTime ; //set creation time for node
			startNode.createHistoryevent(globalTime++); //add it to the history for attributes
		//eMofeed
		long startTime = System.currentTimeMillis();
		int loop = 0;
		long dur = 0;
		int nextRootNode = 100;
		while (!timeBasedTermination(dur, maxDuration)) {
			if(loop%10 == 0)
//				logger.error("Loop "+loop+" searchtree.size="+nodes.size());
			newBest = false;
			if(loop == 1) {
				logger.error("intial treesize: "+nodes.size());
				Iterator<SearchTreeNode> it = nodes.descendingIterator();
				while(it.hasNext()) {
					SearchTreeNode within = it.next();
					System.out.println("score= "+within.getScore()+" penScore="+within.getPenaltyScore()+" =>"+within);
				}
				System.out.println("Next:="+getNextNodeToExpand());
				
				
			}
			//mofeed
//			System.out.println("Size of the treeset "+nodes.size()+" nodes\n");
//			System.out.println("Nr of Childs of root "+startNode.getChildren().size()+" nodes\n");
//			if(nodes.size() > 100)
//			{
//				System.out.println("Generating graph-----------------------\n");
//				GraphGenerator g = new GraphGenerator();
//				g.createGraphRecursive(startNode);
//				
//				g.createGraphItersive(nodes);
//				g.writeGraphDynamically();
//				g.displayGraph();
//				g.writeGraph();
//			}
			// remember best node
			
//			SearchTreeNode best = getMaxNode();
			/**Should be the case upon initialization**/
			if(best == null) {
				best = startNode;
//				memList.add(new EvaluationMemory(best, loop, System.currentTimeMillis()-startTime));
			}
			dur = System.currentTimeMillis()-startTime;
			// chose best node according to heuristics
			if(hardRootExpansion) {
				if(loop==nextRootNode) {//0, 50, 100
				
				nextNode = startNode;
				if(loop == nextRootNode)
					nextRootNode *= 10; 
				logger.error("Loop "+loop+" expanding rootNode... Time passed "+(dur/1000)+" s");
				} else {
					nextNode = getNextNodeToExpand();	
				}
				
			} else {
				nextNode = getNextNodeToExpand();
			}
			logger.debug("BEST loop("+loop+")"+best);
			if(nextNode.isRoot()) {
				loopsRootExpanded.add(loop);
			}
			int horizExp = nextNode.getExpansion();
//			logger.error("Loop "+loop+" expansion="+horizExp+ " refining node:"+nextNode+"\n \twith score "+nextNode.getScore());//+ ": "+nextNode.toTreeString());
			// apply operator
			Set<LinkSpecification> refinements = refineNode(nextNode, startTime);
		
		
			logger.info("\t refined into "+refinements.size());
		
			int nrOfChilds = 0;
			long now =System.currentTimeMillis()-startTime;
			Iterator<LinkSpecification> it = refinements.iterator();
			while(refinements.size() != 0 && !timeBasedTermination(now, maxDuration) && it.hasNext()) {
				// pick element from set
				LinkSpecification refinement = it.next();

				if(refinement.getThreshold()<=0.1) { 
					logger.info("Ignoring refinement "+refinement+"\n due to too low threshold");
//					resLog.writeLogString("Ignoring refinement "+refinement+"\n due to too low threshold");
					continue;
				}
				if(!refinement.equals(nextNode.getSpec())) {
					SearchTreeNode added = addNode(refinement, nextNode);
					
					if(added!=null) {
						if(highestAccuracy<added.getScore()) {
							highestAccuracy = added.getScore();
							logger.info("New highest accuracy: "+highestAccuracy+" accieved by node "+added);
						}
						if(added.getScore() > nextNode.getScore()) {
							added.setReward(reward);
						}
						logger.info("\t added child: "+added);
						nrOfChilds++;
					}
				}
								
					// adding nodes is potentially computationally expensive, so we have
					// to check whether max time is exceeded	
				now = dur = System.currentTimeMillis()-startTime;
				
			}
//			resLog.writeLogString(loop+": Expanded exp (sc:="+nextNode.getPenaltyScore()+" - maxF:="+nextNode.maxAcchievablePFM+")="+nextNode+" "+nextNode.specificName+" into "+nrOfRef+" nodes. Successfully added: "+nrOfChilds);
//			if(newBest)
//				memList.add(new EvaluationMemory(best, loop, System.currentTimeMillis()-startTime));
			logger.info("Added "+nrOfChilds+" nodes to tree");
			if(nrOfChilds == 0) {
				logger.debug("Not added any childs don't expand this node again");
				nextNode.expand = false;
			}
//			if(nextNode.getExpansion()>=3)
//				resLog.logEntry(loop, nextNode, refinements, highestAccuracy);
//		if(debuggingInput && (!nextNode.getSpec().isAtomic() || nextNode.getExpansion()>2 )) {
//			System.out.println("Press enter for next loop");
//		   InputStreamReader converter = new InputStreamReader(System.in);
//		   BufferedReader in = new BufferedReader(converter);
//		   in.readLine();
//		}
			loop++;
			//TODO prune the tree (comment:pruning is done in the moment the spec proves less best achieved PFM as the node is not created)
	        logger.info("Start pruning levlel-2 scanning the tree");
			pruneSearchTree(highestAccuracy);
		} // while learning
		
//		memList.add(new EvaluationMemory(best, loop, System.currentTimeMillis()-startTime));
		
//		String s = "Loops of root was expanded: ";
//		for(Integer i : this.loopsRootExpanded) {
//			s+= ", "+i;
//		}
//		s+="\n";
//		s+=" SearchTree.size =" + nodes.size() + "\n";
//		resLog.writeString(s);
//		logger.error("Computing Scores");
//		computeScores();
//		System.out.println("Generating graph for "+nodes.size()+" nodes\n");
//		try {
//			FileUtils.writeStringToFile(new File("resources/results/searchTree.txt"), startNode.toTreeString());
//		}catch(Exception e){}
//		resLog.writeTreeString(startNode.toTreeString());
		result.setLinkSpecification(this.best.getSpec());
		result.setMapping(getMapping(this.best.getSpec()));
		result.setQuality(this.best.getScore());
		return result;
	}

	/**
	 * Traverses all nodes in the search space to find the next node to expand: the one with the 
	 * highest (penalty) score, which is allowed to be further exanded.
	 * @return SearchTreeNode to expand using the operator.
	 */
	protected SearchTreeNode getNextNodeToExpand() {
		// we expand the best node of those, which have not achieved 100% accuracy
		// already and have a horizontal expansion equal to their length
		// (rationale: further extension is likely to add irrelevant syntactical constructs)
//		logger.info("Get next node, treeset size="+nodes.size() );
		Iterator<SearchTreeNode> it = nodes.descendingIterator();
		
		double maxScore=-1d;
		double maxAchievable = -1d;
		SearchTreeNode next = null; 
		while(it.hasNext()) {
			
			SearchTreeNode node = it.next();
			if(hardRootExpansion) // ignore root if its choosen manually
				if(node.isRoot())
					continue;
			if(node.expand) {
				if(node.getPenaltyScore()>maxScore) {
					maxScore = node.getPenaltyScore();
					maxAchievable = node.maxAcchievablePFM;
					next = node;
				}
			
				else {
					if(node.maxAcchievablePFM>maxScore &&  maxAchievable < node.maxAcchievablePFM) {
							next = node;
							maxScore = node.getPenaltyScore();
							maxAchievable = node.maxAcchievablePFM;
						}
					}
				}// if node.expand
			}// end while

		if(maxScore > -1d) {
			return next;
		}
		// this should practically never be called, since for any reasonable learning
		// task, we will always have at least one node with less than 100% accuracy
		System.out.println("Not able to pick reasonable node to expand: just taking last");
		System.err.println("Not able to pick reasonable node to expand: just taking last");
		return nodes.last();
	}
	
	/**
	 * Refines a node: removes it from set, creates all refinement and adds it again, with incremented threshold.
	 * @param node Node to refine
	 * @return all refined LinkSpecs.
	 */
	protected Set<LinkSpecification> refineNode(SearchTreeNode node, long start) {
		// we have to remove and add the node since its heuristic evaluation changes through the expansion
		// (you *must not* include any criteria in the heuristic which are modified outside of this method,
		// otherwise you may see rarely occurring but critical false ordering in the nodes set)
		nodes.remove(node);
		
		int horizExp = node.getExpansion();
		Set<LinkSpecification> refinements =  operator.refine(node.getSpec(), horizExp);
		//bMofeed
		 // Threshold may be changed get snapshot for the new change
//		System.out.println("Created refinements "+refinements.size()+" for node "+node);
		node.createHistoryevent(globalTime++);
		//eMofeed
//		if(node.getSpec().isAtomic()) {String str = "refined "+node+" into\n";
//			for(LinkSpec ls : refinements) {
//				str += "\n"+ls;				
//			}
//			this.resLog.writeLogString(str);
//		}
		node.incExpansion();
		if(node.getExpansion() > 2) {
//			node.expand = false;
//			logger.info("DON'T expand this atomic anymore!!!: exp="+node.getExpansion()+"  node="+node.toTreeString());
		}
		if(!advancedThreholdSearch || node.isRoot()) {
//			logger.error("No optimized threshold search on root node of it isn't applied");
			nodes.add(node);
			return refinements;
		} else {
		
//			logger.error("Atempting optimized threshold on "+refinements.size()+" children of node "+node);
//			System.out.println("Trying advanced threshold opitimization");
			TreeSet<LinkSpecification> allOpt = new TreeSet<LinkSpecification>();
			
			List<Double> threshs = UpwardLengthLimitRefinementOperator.allOptimizedThresholds(node.lowThreshold, node.highThreshold, thresholdGrid);
			
			for(LinkSpecification spec : refinements) {
				allOpt.add(spec);
				double bestQuali = Double.MIN_NORMAL;
				if(spec.isAtomic()) {
					double low = 0; 
					boolean newMap = true;
					Mapping map = null;
					for(Double d : threshs) {
						LinkSpecification copy = spec.clone();
						copy.setThreshold(d);
						copy.lowThreshold = low;
						low = d;
						if(newMap) {
							map  = getMapping(copy);
							newMap = false;
						} else {
							map = map.getSubMap(d);
						}
						double score = getQuality(map);
						if(score >= bestQuali) {
							bestQuali = score;
							allOpt.add(copy);
						}						
					}
//					logger.error("Added "+allOpt.size()+" threshold-optimized atom specs");
				} else {
//					logger.error("Trying to optimize Thresholds on spec "+spec);
//					allOpt.add(spec);
					Collection<LinkSpecification> answer = complexOptimization(spec, start);
//					logger.error("Complex threshold opimization on spec created "+answer.size()+" new specs");
					allOpt.addAll(answer);
				}
			}
//			node.optimizeThreshold = false;
			nodes.add(node);
			return allOpt;
		}
	}
	
	/**
	 * Add node to search tree if it is not too weak
	 * returns true if node was added and false otherwise
	 * @param spec LinkSpec of the node to be created
	 * @param parentNode parent of the new one => to create search tree
	 * @return new SearchNode based upon the spec.
	 */
	protected SearchTreeNode addNode(LinkSpecification spec, SearchTreeNode parentNode) {
	
		// redundancy check (return if redundant)
//		Monitor monRed = MonitorFactory.start("redundancy");
//		boolean nonRedundant = specs.add(spec);
//		monRed.stop();
////		logger.info("adding Node: "+nonRedundant+": "+spec);
//		if(!nonRedundant) {
//			logger.info("redundandt: "+spec);
//			return null;
//		}
		//m:note-accuracy = F measure
//		Monitor monPFM = MonitorFactory.start("pfm");
		Mapping map = getMapping(spec);
		double accuracy = getQuality(map);
	
		
//		monPFM.stop();
		if(accuracy == -1) {
			logger.error("Not adding "+spec+" due to PFM==-1");
			return null;
		}
		
		SearchTreeNode node = new SearchTreeNode(parentNode, spec, accuracy, expansionPenalty);
		node.highThreshold = spec.getThreshold();
		node.lowThreshold = spec.lowThreshold;
		
		if(bestScore < accuracy || best.isRoot()) {
			bestScore = accuracy;
			logger.error("Setting new best"+node);
			best = node;
			newBest = true;
		}
//		if(spec.isAtomic())
//		{
			node.setExpansion(0);
//		}
		//bMofeed
			node.creationTime=globalTime; //assign the creation time
			node.createHistoryevent(globalTime++); // record the event for attributes values
		//eMofeed

		double maxAcievable = maxAcievableFScore(map, 
				this.getSourceCache().getAllUris().size(), 
				this.getTargetCache().getAllUris().size());
		node.maxAcchievablePFM = maxAcievable; 
		boolean added = false;
		if(best != null && !best.isRoot())
			if(maxAcievable < best.getScore()) {
				logger.info("Not adding spec with score "+ node.getScore() + " due to maxAcievableTest");
	//			resLog.writeLogString("Not adding spec "+ node + " due to maxAcievableTest");
				return null;
			}
//		if(maxAcievable < parentNode.maxAcchievablePFM) {
//			logger.info("Not adding spec with score "+ node.getScore() +" due to maxAcievableTest with parent");
//			resLog.writeLogString("Not adding spec "+ node + " due to maxAcievableTest with parent");
//			return null;
//		}
		added = nodes.add(node);
		if(!added) {
//			Iterator<SearchTreeNode>nodes.iterator()
//			logger.error("Failed adding node "+node+" to treeset");
//			Iterator<SearchTreeNode> it = nodes.iterator();
//			while(it.hasNext()) {
//				SearchTreeNode o = it.next();
//				if(o.equals(node))
//					logger.error("Because it already contains:"+o);
//			}
			
			
			return null;
//			resLog.writeLogString("Failed adding node "+node+" to treeset");
		}
		else {
				node.nodeId = IdCounter++; //assign an id to the new node and increment it for future
				parentNode.addChild(node);
//			resLog.writeLogString("Added node (score="+node.getScore()+" | "+node.getPenaltyScore()+") with thresh"+node.getSpec().threshold+": "+node);
			logger.info("Node added score"+ node.getScore()+" max="+node.maxAcchievablePFM +"to treeset: "+node);
		}
		logger.debug("Adding node with with thresholdborders: "+node.lowThreshold+" - "+node.highThreshold);
		return node;
	}	

	boolean timeBasedTermination(long dur, long end) {
//		if(debuggingInput)
//			return false;
		if(dur/1000>end) {
			System.out.println("dur"+dur+"/1000 > "+end+" ==true");
			return true;
			
		}
			
		return false;
	}
	
	/**
	 * /**
	 * Prototype to test whether it makes sense to further explore this node.
	 * This is done by computing the maximum accievable PFM.
	 * @param actMapping
	 * @param K_i size of source caches (#uris)
	 * @param K_j size of target caches (#uris)
	 * @return True if the test holds and there is space to further explore this node.
	 */
	protected double maxAcievableFScore(Mapping actMapping, double K_i, double K_j) {
		
//		double realPFM = getPseudoFMeasure(spec);
//		logger.error("Computing maximal achievable for a spec with actual PFM="+realPFM+" ...");
		// variables
		double max_prec = 0;
		double max_rec = 0;

		
		// first get actual Mapping
//		Mapping actMapping = engine.run(planner.plan(spec));
		// all source instances with atleast 1 map partner
		double lambda_ij = actMapping.getMap().keySet().size();
		// all target instances with atleast 2 map patner
		double lambda_ji = actMapping.reverseSourceTarget().getMap().keySet().size();
	
		if(lambda_ij > lambda_ji) {
//			logger.error("Switching lambdas: lambda_ij="+lambda_ij+" lambda_ji="+lambda_ji+". Switching now...");
			double mem = lambda_ij;
			lambda_ij = lambda_ji;
			lambda_ji = mem;
		}
//		double K_i = evalData.getSourceCache().getAllUris().size();
//		double K_j = evalData.getTargetCache().getAllUris().size();
		double M_ij = actMapping.size();
//		logger.info("K_i="+K_i+"  K_j="+K_j+" M_ij="+M_ij);
		double test_1 = (2*K_i + 2*lambda_ji - 2*actMapping.size())/(K_i+K_j);
//		logger.error("test_1="+test_1);
		
		if((beta*beta) > test_1 ){			
//			logger.info("Using 1st definition of maxPFM:");
			max_prec = (K_i+K_j) / (2*(M_ij + K_j - lambda_ji));
			max_rec = 1;
		}
		else {			
//			logger.info("Using 2nd definition of maxPFM:");
			max_prec = (2*K_i + lambda_ji - lambda_ij) / (2*(M_ij+K_i-lambda_ij));
			max_rec = (2*K_i + lambda_ji - lambda_ij)/(K_i+K_j);
		}
	
		double max_PFM = (1+beta*beta)*max_prec / ((beta*beta*max_prec) + max_rec);

		return max_PFM;
	}
	
//	public testMax
	
/*	public static void main( String[] args )
	{
		RefinementBasedLearningAlgorithm runner= new RefinementBasedLearningAlgorithm();
		runner.init();
		runner.start();
	}*/
	/**
	 * Method computes PFM on an LinkSpec
	 * @param spec
	 * @return Double PFM [0,1]
	 */
	protected double getQuality(Mapping map) {
		Double res = 0d;
		if(map.size() == 0) {
//			logger.info("getScore of empty LS: return 0");
			return 0;
		}
			
//		logger.info("Executing LinkSpec "+spec);
		
//		mapping = Mapping.getBestOneToOneMappings(mapping);
		res = pfm.getPseudoFMeasure(this.getSourceCache().getAllUris(),
				this.getTargetCache().getAllUris(), map, 
				beta);
//		logger.info("getQuality():  PFM="+res);

		return res;
	}
	
	protected Mapping getMapping(LinkSpecification spec) {
		if(spec.isEmpty())
			return new MemoryMapping();
		Mapping mapping = new MemoryMapping();
		try {
			mapping = engine.execute(planner.plan(spec));
		}catch(Exception e) {
			System.err.print("Error executing spec "+spec);
//			resLog.writeLogString("Exception executing spec "+spec);
			e.printStackTrace();
			
		} catch(OutOfMemoryError err) {
			System.err.print("Error executing spec "+spec);
//			resLog.writeLogString("Error executing spec "+spec);
			err.printStackTrace();
			
		}
		return mapping;
	}
	
//	private EvaluationMemory setRealScores(EvaluationMemory mem) {
//		if(mem.node.getSpec().isEmpty()) {
//			logger.info("getScore of empty LS: return 0");
//			return mem;
//		}
//		Mapping mapping = new Mapping();
//		PRFCalculator comp = new PRFCalculator();	try {
//			 mapping = engine.runNestedPlan(planner.plan(mem.node.getSpec()));
//		}catch(Exception e) {
//			
//		}
//		mem.pfm = getQuality(mapping);
//		// compute scores
//		mem.setRealfFull(comp.fScore(mapping, evalData.getReferenceMapping()));
//		mem.setRealPrecFull(comp.precision(mapping, evalData.getReferenceMapping()));
//		mem.setRealRecallFull(comp.recall(mapping, evalData.getReferenceMapping()));
//		mem.truePositives = comp.getOverlap(mapping,  evalData.getReferenceMapping());
//		// compute scores based upon 1 : 1 Mapping
//		mapping = Mapping.getBestOneToOneMappings(mapping);
//		mem.setRealF(comp.fScore(mapping, evalData.getReferenceMapping()));
//		mem.setRealPrec(comp.precision(mapping, evalData.getReferenceMapping()));
//		mem.setRealRecall(comp.recall(mapping, evalData.getReferenceMapping()));
//		
//		return mem;
//	}
	
	
	////////////////////////////////////////
	boolean checkTreeScores(double upperlimit)
	{
		for (SearchTreeNode treeNode : nodes) {
			if(treeNode.getScore() < upperlimit ) 
				return false;
		}
		return true;
	}
	/**
	 * Best node according to its score in SearchTree. Doesn't return root!.
	 * @return
	 */
	SearchTreeNode getMaxNode()	{
		double maxScore=-1;
		SearchTreeNode maxTreeNode = null;
		
		for (SearchTreeNode treeNode : nodes) {
			if(treeNode.getScore() > maxScore && !treeNode.isRoot() && treeNode.getSpec()!=null) 
				{
					maxTreeNode = treeNode;
					maxScore = treeNode.getScore();
				}
			if(treeNode.getScore() == maxScore) {
				if(maxTreeNode.getExpansion() > treeNode.getExpansion())
					maxTreeNode = treeNode;
			}
		}
		
		return maxTreeNode;
	}
	
	protected void computeScores() {
//		resLog.writeRealTitle();
//		int i = 0;
//		logger.error("Serializing "+memList.size()+" entries.");
//		for(EvaluationMemory mem : memList) {
//			mem = setRealScores(mem);
////			resLog.logEntry(mem);
//			if(i%100 == 0)
//				logger.info("Computing "+i+"th entry of "+memList.size());
//			i++;
//		}
//		logger.error("Start Writing");
//		try {
//			resLog.logEntries(memList);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	///////////////////////////////////////
	
//	public static void main(String args[]) throws IOException {
//		testMaxF();
//		long maxDur = 60;//in seconds
////		debuggingInput = true;
//		DataSets allEvalData[] = {
////				DataSets.DBLPACM,
//				DataSets.PERSON1, 	
////				DataSets.PERSON2,
////				DataSets.RESTAURANTS,
////				DataSets.ABTBUY,
////				DataSets.AMAZONGOOGLE,
//////				
////				DataSets.DBPLINKEDMDB,
////				DataSets.DBLPSCHOLAR,
//				
//		}; 		
//		
////		Monitor mon = MonitorFactory.start("overall runtime");
//		double exp[] = {0.98};//{0.95, 0.9, 0.8}; 
//		double gamma[] = {0.1};//{0.75, 0.1, 0.15, 0.3};
//		double reward[] = {1.2};//{1, 1.2, 1.4};
//		Monitor mon = MonitorFactory.start("overall runtime");
//		for(DataSets ds : allEvalData) {
//			EvaluationData data = DataSetChooser.getData(ds);
//			System.out.println("Running dataset "+data.getName());
//			logger.setLevel(Level.INFO);
//			RefinementBasedLearningAlgorithm.debuggingInput = true;
//			RefinementBasedLearningAlgorithm.hardRootExpansion = true;
//			RefinementBasedLearningAlgorithm algo = new RefinementBasedLearningAlgorithm(data);
//			if(!debuggingInput)
//			try {
//				algo.setOutStreams(ds.name());
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			for(double e : exp) {
//				for(double ga : gamma) {
//					for(double re : reward) {
//						algo.init(data, ga, e, re, maxDur);
//						algo.start();
//					}// for reward
//				}// for gamma
//			}//for exp
//		}// for data set

//		mon.stop();
		
//		FileUtils.writeStringToFile(new File("resources/results/jamon.html"), MonitorFactory.getReport());
//	}
	
	public void setExpansion(double exp) {
		this.expansionPenalty = exp;
	}
	public void setGamma(double gamma) {
		this.gammaScore = gamma;
	}
	public void setReward(double reward) {
		this.reward = reward;
	}
	
//	/**
//	 * Just to test the high accievable test.
//	 */
//	public static void testMaxAchievableFMeasure() {
//		RefinementBasedLearningAlgorithm alg = new RefinementBasedLearningAlgorithm(DataSetChooser.getData(DataSets.PERSON1));
//		Mapping m = new MemoryMapping();
//		PseudoFMeasure pfm = new PseudoFMeasure();
//		List<String> sL = new LinkedList();
//		List<String> tL = new LinkedList();
//		double K_i = 10;
//		double K_j = 10;
//		System.out.println("MaxAchievable F(empty) = "+alg.maxAcievableFScore(m,  K_i, K_j));
//		
//		for(int i=1;i<=10;i++){
//			sL.add(""+i);
//			tL.add(""+i);
//			m.add(""+((i%3)+1), ""+i, 1d);
//		}
//		System.out.println("MaxAchievable F(full) = "+
//				alg.maxAcievableFScore(m,  K_i, K_j)+
//				" real PFM="+
//				pfm.getPseudoFMeasure(sL, tL, m, 1d));
//	}
//	

	
	/**
	 * Attempt to implement a more effective threshold Optimization on complex LinkSpecs.
	 * TODO optimize Mapping computation: remember both child maps => apply new Thresholds if possible => compute either AND or OR
	 * @param spec
	 * @return
	 */
	private Collection<LinkSpecification> complexOptimization(LinkSpecification spec, long start) {
		List<LinkSpecification> specs = new LinkedList<LinkSpecification>();
		if(spec.isAtomic())
			return specs;
		
		List<LinkSpecification> childs = spec.getAllLeaves();
		if(childs.size()!=2) {
//			logger.error("Complex LinkSpec has more then 2 Children");
			specs.add(spec);
			return specs;
		} else {
			LinkSpecification child1 = childs.get(0).clone();
			LinkSpecification child2 = childs.get(1).clone();
			List<Double> threshs1 = UpwardLengthLimitRefinementOperator.allOptimizedThresholds(child1.lowThreshold, child1.getThreshold(), thresholdGrid);
			List<Double> threshs2 = UpwardLengthLimitRefinementOperator.allOptimizedThresholds(child2.lowThreshold, child2.getThreshold(), thresholdGrid);
			double low1 = 0; double low2 = 0;
			double bestQuali = Double.MIN_NORMAL;
			for(Double d1: threshs1) {
				for(Double d2: threshs2) {
					if(timeBasedTermination(System.currentTimeMillis()-start, maxDuration)) {
						return specs;
					}
					child1 = child1.clone();
					child2 = child2.clone();
					child1.setThreshold(d1);
					child2.setThreshold(d2);
					child2.lowThreshold = low2;
					low2 = d2;
					
					LinkSpecification copy = spec.clone();
					try {
						copy.setChildren(new ArrayList<LinkSpecification>());
						copy.setDependencies(new ArrayList<LinkSpecification>());
					}catch(Exception e){}
						copy.addChild(child1);
						copy.addChild(child2);
						
						if(spec.getOperator()==Operator.AND)
							copy.setThreshold(Math.max(d1,d2));
						else 
							copy.setThreshold(Math.min(d1,d2));
						double score = getQuality(getMapping(copy));
//						System.out.println("tetsted quali ("+score+") of spec: "+copy);
						if(score > bestQuali) {
							bestQuali = score;
							copy.setQuality(score);
							specs.add(copy);
						}
				}
				
				child1.lowThreshold = low1;
				low1 = d1;
			}
				
		}
			
		return specs;
		
	}
	

	
	
	
	//---------------------------------------------------------------------------------
	//-------TESTING METHODS------------------------------------------------------
	//---------------------------------------------------------------------------------
//	public static void testOptima() {
//		EvaluationData data = DataSetChooser.getData(DataSets.PERSON1);
//		RefinementBasedLearningAlgorithm algo = new RefinementBasedLearningAlgorithm(data);
//		SearchTreeNode startNode = new SearchTreeNode(new LinkSpec(), 0.2d, 0.95d);
//		startNode.addSpecificName("root");
//		startNode.setExpansion(2);
//		Set<LinkSpec> specsComplex1 = algo.refineNode(startNode, System.currentTimeMillis());
//		int anz = 0;
//		for(LinkSpec cSpec : specsComplex1) {
//			System.out.println(++anz+"nd cSpec: "+cSpec);
//			if(anz == 50) {
//				algo.testComplexOptimization(cSpec);
//				return;
//			}
//		}
//	}
//	
//	public void testComplexOptimization(LinkSpec spec) {
//		Collection<LinkSpec> specs = complexOptimization(spec, System.currentTimeMillis());
//		int i = 0;
//		for(LinkSpec opti:specs) {
//			System.out.println(++i+"("+opti.quality+").: "+opti);
//		}
//	}
//	
//	public static void testComplexThreshOptimization() {
//		String pers1Pref = "http://www.okkam.org/ontology_person1.owl#";
//		String pers2Pref = "http://www.okkam.org/ontology_person2.owl#";
//		
//		EvaluationData data = DataSetChooser.getData(DataSets.PERSON1);
//		RefinementBasedLearningAlgorithm algo = new RefinementBasedLearningAlgorithm(data);
//		algo.advancedThreholdSearch = false;
//		LinkSpec title = new LinkSpec();
//		title.setAtomicFilterExpression("levensthein", "x."+pers1Pref+"soc_sec_id", "y."+pers2Pref+"soc_sec_id");
//		title.threshold = 1d;
//		LinkSpec authors = new LinkSpec();
//		authors.setAtomicFilterExpression("levensthein", "x."+pers1Pref+"phone_numer", "y."+pers2Pref+"phone_numer");
//		authors.threshold = 1d;
//		
//		LinkSpec complex = new LinkSpec();
//		complex.operator = de.uni_leipzig.simba.specification.Operator.OR;
//		complex.addChild(title);
//		complex.addChild(authors);
//		complex.threshold = 1d;
//
//		algo.init(data, 0.3, 0.95, 1.2, 600);
//		SearchTreeNode startNode = new SearchTreeNode(new LinkSpec(), 0.2, 0.95);
//		startNode.addSpecificName("root");
//		startNode.setExpansion(1);
//		algo.nodes.add(startNode);
////		algo.addNode(title, startNode);
//		
//
//		SearchTreeNode s1 = algo.addNode(title, startNode);
//		System.out.println(s1);
//	    s1.incExpansion();	
//		Set<LinkSpec> specs = algo.refineNode(s1, System.currentTimeMillis());
//		int oi = 0;
//		for(LinkSpec ors: specs) {
//			++oi;
//			if(ors.getAllLeaves().get(1).getFilterExpression().contains("phone_numer")) {
//				System.out.println(oi+"st: "+ors);
//			}
//			
//			
//		}
//	}
//	
//	public static void testMaxF() {
//		String pers1Pref = "http://www.okkam.org/ontology_person1.owl#";
//		String pers2Pref = "http://www.okkam.org/ontology_person2.owl#";
//		
//		EvaluationData data = DataSetChooser.getData(DataSets.PERSON1);
//		RefinementBasedLearningAlgorithm algo = new RefinementBasedLearningAlgorithm(data);
//		algo.init(data, 0.1, 0.95, 1.0, 600);
//		algo.advancedThreholdSearch = false;
//		LinkSpec title = new LinkSpec();
//		title.setAtomicFilterExpression("levensthein", "x."+pers1Pref+"soc_sec_id", "y."+pers2Pref+"soc_sec_id");
//		title.threshold = 1d;
//
//		double levMax = algo.maxAcievableFScore(algo.getMapping(title),data.getSourceCache().size(), data.getTargetCache().size());
//		System.out.println("lev max ="+levMax);
//	}
	
	
	private void pruneSearchTree(double bestPFM)
	{
		Iterator<SearchTreeNode> iterator = nodes.descendingIterator();
        logger.info("Check Weak Nodes");

		while(iterator.hasNext())
		{
			SearchTreeNode current = iterator.next();
	        logger.info("Check Weak =[spec-->"+ current.getSpec()+": score -->"+current.getScore()+": best score -->"+current.getBestScore());

			if(Math.abs(current.getBestScore()-bestPFM) > 0.01 && current.getParent() != null)// this node is weak adn not root
			{
				current.setWeakNode(true);
		        logger.info("Assigned to be Weak =[spec-->"+ current.getSpec()+": score -->"+current.getScore()+": best score -->"+current.getBestScore());
			}
		}
		iterator = nodes.descendingIterator();
		while(iterator.hasNext())
		{
			SearchTreeNode current = iterator.next();
			if(current.isWeakNode())// this node is weak adn not root
			{
				prune(current,"cutoff" );
			}
		}
		iterator = nodes.descendingIterator();
		while(iterator.hasNext())
		{
			SearchTreeNode current = iterator.next();
			if(current.isWeakNode())// this node is weak and not root
				nodes.remove(current);
		}
	}
	private void prune(SearchTreeNode weakNode,String pruneMethod)
	{
		if(pruneMethod.equals("shiftUp"))
		{
			//add the node children to it parent
			for(SearchTreeNode child : weakNode.getChildren() )
			{
				weakNode.getParent().addChild(child);
			}
			//remove the weak node from its parent children
			weakNode.getParent().getChildren().remove(weakNode);
			//remove from list of traversed nodes
			nodes.remove(weakNode);
			//remove its spec form the list
			specs.remove(weakNode.getSpec());
		}
		else if(pruneMethod.equals("cutoff"))
		{
			//remove it from the list of parent children
			weakNode.getParent().getChildren().remove(weakNode);
			//remove its connection to it parent
			weakNode.setParent(null);
			/*
			 * Note:
			 * Here the weak node spec. is not removed from the specs list, in purpose when it is generated again due to the same
			 * parent refinement it won't be added as it is already useless
			 */
		}
	}
	private boolean TerminateUsingNoise(double noise,int positiveEx)
	{
		Iterator<SearchTreeNode> iterator = nodes.descendingIterator();
		while(iterator.hasNext())
		{
			SearchTreeNode current = iterator.next();
			if(current.getScore() < noise * positiveEx)
				return false;
		}
		return true;
	}
	private boolean Terminate(String termType,double value)
	{
		//value can be max fmeasure required or max expamsion required
		if(termType.equals("fMeasure") || termType.equals("expansion"))
		{
			Iterator<SearchTreeNode> iterator = nodes.descendingIterator();
			while(iterator.hasNext())
			{
				SearchTreeNode current = iterator.next();
				if(current.getScore() > value )
					return true;
			}
			return false;
		}
		else if (termType.equals("iterate") && value < setting.getMaxIteration())
			return false;
		return true;
	}
	

	@Override
	public String getName() {
		return "LION prune unsupervised";
	}

	@Override
	public MLResult learn(Mapping trainingData) {
		MLResult result = new MLResult();
		try {
			return start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.addDetail("error", e);
		}
		return result;
	}

	@Override
	public Mapping computePredictions() {
		return getMapping(this.best.getSpec());
	}

	@Override
	public void terminate() {
		// TODO Auto-generated method stub
		
	}
}
