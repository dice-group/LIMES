package org.aksw.limes.core.ml.algorithm.eagle.core;

import java.util.LinkedList;
import java.util.List;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.function.SubProgram;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.util.Pair;
import org.apache.log4j.Logger;



/**
 * JGAP GPProblem implementation for EAGLE and all derivates.
 * @since 1.2 Learning Preprocessing enhanced.
 * @version 1.2
 * @author Klaus Lyko
 *
 */
public class ExpressionProblem extends GPProblem{
	static Logger logger = Logger.getLogger("LIMES");
	public static CommandGene SUBPROGRAM;
	private boolean learnPreProcessing = false;
	
	/**
	 * Basic constructor for the EAGLE approaches. Dissables Preprocessing learning.
	 * @param a_conf
	 * @throws InvalidConfigurationException
	 */
	public ExpressionProblem(GPConfiguration a_conf)
			throws InvalidConfigurationException {
		super(a_conf);
	}
		
	/**
	 * Constrcutor to decide whether Preprocessing is part of evolution.
	 * @param a_conf
	 * @param learnPreprocessing
	 * @throws InvalidConfigurationException
	 */
	public ExpressionProblem(GPConfiguration a_conf, boolean learnPreprocessing)
	     throws InvalidConfigurationException {
	   this(a_conf);
	   this.learnPreProcessing = learnPreprocessing;
	}
	
	@Override
	public GPGenotype create() throws InvalidConfigurationException {

		LinkSpecGeneticLearnerConfig config = (LinkSpecGeneticLearnerConfig) getGPConfiguration();
			//	ExpressionApplicationData applData = new ExpressionApplicationData("PublicationData.xml");
				// a program has two chromosomes: first an expression, second a acceptance threshold 
				Class[] types = {LinkSpecification.class};
				Class[][] argTypes = {{
				}
				};
				SUBPROGRAM = new SubProgram(config, new Class[] {String.class, String.class, CommandGene.DoubleClass}, true);
				List<CommandGene> nodes;
				if(!this.learnPreProcessing) {
					logger.info("Creating basic commands ");
					nodes = getNormalSetup(config);
				} else {
					logger.info("Creating preprocessing commands ");
					nodes = getPreprocessingLearningSetup(config);
				}
					
				CommandGene[] nodeArray = new CommandGene[nodes.size()];
				for(int i = 0; i<nodes.size(); i++)
					nodeArray[i] = nodes.get(i);
				CommandGene[][] nodeSets = {
						nodeArray,
				};
				int[] minDepths= new int[1];
				int[] maxDepths = new int[1];
				minDepths[0] = 0;
				maxDepths[0] = 6;
				boolean[] fullModeAllowed= {true};
				int maxNodes = 100;
//				System.out.println("Nodes..."+nodes);
				return GPGenotype.randomInitialGenotype(config,
					      types, argTypes, nodeSets,
					      minDepths, maxDepths, maxNodes, fullModeAllowed,
					      true);
	}
	
	/**
	 * Constructs CommandGene setup for the basic EAGLE approach, i. e. without
	 * Learning Preprocessing.
	 * @param config
	 * @return
	 * @throws InvalidConfigurationException
	 */
	private List<CommandGene> getNormalSetup(LinkSpecGeneticLearnerConfig config) throws InvalidConfigurationException {
		List<CommandGene> nodes = getBasicNodes(config);
		nodes.addAll(getStringMeasures(config));
		return nodes;
	}
	
	/**
	 * Constructs CommandGene setup for the enhanced EAGLE approach to also learn preprocessing steps.
	 * @param config
	 * @return
	 * @throws InvalidConfigurationException
	 */
	private List<CommandGene> getPreprocessingLearningSetup(LinkSpecGeneticLearnerConfig config) throws InvalidConfigurationException {
		List<CommandGene> nodes = getBasicNodes(config);
		nodes.addAll(getPreprocessingMeasures(config));
		return nodes;
	}
	
	/**
	 * Method creates Basic List of Nodes for evolving only Link Specifications.
	 * @param config
	 * @return List<CommandGene>
	 * @throws InvalidConfigurationException
	 */
	private List<CommandGene> getBasicNodes(LinkSpecGeneticLearnerConfig config) throws InvalidConfigurationException {
		SUBPROGRAM = new SubProgram(config, new Class[] {String.class, String.class, CommandGene.DoubleClass}, true);
		List<CommandGene> nodes = new LinkedList<CommandGene>();
			nodes.add(SUBPROGRAM);
			nodes.add(new SubProgram(config, 2, String.class, 2, 4, true));

			//foreach string propPair atleast 1
			for(int i=0; i<config.getPropertyMapping().stringPropPairs.size(); i++) {
				nodes.add( new StringPropertyPair(config, Pair.class, ResourceTerminalType.STRINGPROPPAIR.intValue(), true, i));
			}
			
			nodes.add(new Terminal(config, CommandGene.DoubleClass, 0.0d, 1.0d, false, 
		    		ResourceTerminalType.THRESHOLD.intValue(), true));
			nodes.add( new Terminal(config, CommandGene.DoubleClass, 0.0d, 1.0d, false,
				    		ResourceTerminalType.GOBALTHRESHOLD.intValue(), true));	
			nodes.add(new NestedBoolean("AND", config));
			/**
			* FIXME reset out commenting additional metrics and operators
			*/
//			nodes.add(new NestedBoolean("MINUS", config))
			nodes.add(new NestedBoolean("OR", config));
//			nodes.add(new NestedBoolean("XOR", config));
//			nodes.add(new AddMetric(config));
			nodes.add(new MetricCommand(config, LinkSpecification.class));
			
//			if(config.hasPointSetProperties()) {					
//				nodes.add(new PointSetMeasure("hausdorff", config, String.class, 1, true));
//				nodes.add(new PointSetMeasure("geomean", config, String.class, 1, true));
//				for(int i=0; i<config.getPropertyMapping().pointsetPropPairs.size(); i++) {
//					nodes.add( new PointSetPropertyPair(config, Pair.class, ResourceTerminalType.POINTSETPROPPAIR.intValue(), true, i));
//				}
//			}	
			

//		if(config.hasNumericProperties()) {					
//			nodes.add(new NumberMeasure(config));
//			for(int i=0; i<config.getPropertyMapping().numberPropPairs.size(); i++) {
//				nodes.add( new NumberPropertyPair(config, Pair.class, ResourceTerminalType.NUMBERPROPPAIR.intValue(), true, i));
////				nodes.add( new StringPropertyPair(config, Pair.class, ResourceTerminalType.STRINGPROPPAIR.intValue(), true, i));
//			}
//			// threshold for numeric properties - more restrictive due to possible memory lacks		
//		    nodes.add(new Terminal(config, CommandGene.DoubleClass, 0.8d, 1.0d, false, 
//			   		ResourceTerminalType.NUMBERTHRESHOLD.intValue(), true));
//		}
		    
//		if(config.hasDateProperties()) {
//			nodes.add(new DateMeasure("yearsim", config));
//			System.out.println("Creating date props");
//			
//			for(int i=0; i<config.getPropertyMapping().datePropPairs.size(); i++) {
//				nodes.add( new DatePropertyPair(config, Pair.class, ResourceTerminalType.DATEPROPPAIR.intValue(), true, i));
//			}
//		}
	
		if(config.redundantCommands()) {
			nodes.add( new Terminal(config, CommandGene.DoubleClass, 0.0d, 1.0d, false,
		    		ResourceTerminalType.GOBALTHRESHOLD.intValue(), true));
			for(int anz = 0; anz < 11; anz++)
				nodes.add(new MetricCommand(config, LinkSpecification.class));
			// add pairs of Properties
			for(int anz = 0; anz < 11; anz++)
			for(int i=0; i<config.getPropertyMapping().stringPropPairs.size(); i++) {
				nodes.add( new StringPropertyPair(config, Pair.class, ResourceTerminalType.STRINGPROPPAIR.intValue(), true, i));
			}					
		}
		return nodes;
	}
	
	/**
	 * Constructs normal String Measures without preprocessing children.
	 * @param config
	 * @return
	 * @throws InvalidConfigurationException
	 */
	private List<CommandGene> getStringMeasures(LinkSpecGeneticLearnerConfig config) throws InvalidConfigurationException {
		List<CommandGene> nodes = new LinkedList<CommandGene>();
		nodes.add(new StringMeasure("trigrams", config, String.class, 1, true));
		nodes.add(new StringMeasure("jaccard", config, String.class, 1, true));
		nodes.add(new StringMeasure("cosine", config, String.class, 1, true));
		nodes.add(new StringMeasure("levenshtein", config, String.class, 1, true));
		nodes.add(new StringMeasure("overlap", config, String.class, 1, true));
		nodes.add(new StringMeasure("qgrams", config, String.class, 1, true));
		//nodes.add(new NumberMeasure(config, String.class, 1, true));
		return nodes;
	}
	
	/**
	 * Returns new Measures and other CommandGenes to also learn Preprocessing.
	 * @param config
	 * @return
	 * @throws InvalidConfigurationException
	 */
	private List<CommandGene> getPreprocessingMeasures(LinkSpecGeneticLearnerConfig config) throws InvalidConfigurationException {
		List<CommandGene> nodes = new LinkedList<CommandGene>();
		nodes.add(new AtomicPreprocessingCommand("nolang", config));
		nodes.add(new AtomicPreprocessingCommand("cleaniri", config));
		nodes.add(new AtomicPreprocessingCommand("uppercase", config));
		nodes.add(new AtomicPreprocessingCommand("lowercase", config));
		nodes.add(new AtomicPreprocessingCommand("removebraces", config));
		nodes.add(new AtomicPreprocessingCommand("regularAlphabet", config));
		
		nodes.add(new ChainedPreprocessingCommand("nolang", config));
//		nodes.add(new ChainedPreprocessingCommand("cleaniri", config));
		nodes.add(new ChainedPreprocessingCommand("uppercase", config));
		nodes.add(new ChainedPreprocessingCommand("lowercase", config));
//		nodes.add(new ChainedPreprocessingCommand("removebraces", config));
//		nodes.add(new ChainedPreprocessingCommand("regularAlphabet", config));
		
		nodes.add(new StringPreprocessMeasure("trigrams", config, String.class, true));
		nodes.add(new StringPreprocessMeasure("jaccard", config, String.class, true));
		nodes.add(new StringPreprocessMeasure("cosine", config, String.class, true));
		nodes.add(new StringPreprocessMeasure("levenshtein", config, String.class, true));
		nodes.add(new StringPreprocessMeasure("overlap", config, String.class, true));
		return nodes;
	}
	

	public boolean getLearnPreProcessing() {
		return learnPreProcessing;
	}

	public void setLearnPreProcessing(boolean learnPreProcessing) {
		this.learnPreProcessing = learnPreProcessing;
	}

	
	/**
	 * TerminalType help to differentiate Children subtypes.
	 * @author Klaus Lyko
	 */
	public enum ResourceTerminalType {
		THRESHOLD(3),
		NUMBERTHRESHOLD(6),
		GOBALTHRESHOLD(7),
		STRINGPROPPAIR(1),
		NUMBERPROPPAIR(2),
		POINTSETPROPPAIR(5),
		DATEPROPPAIR(4),
		PREPROCESS(10);
		private int m_value;
		public int intValue() {
			return m_value;
		}
		ResourceTerminalType (int a_value) {
			m_value = a_value;
		}
	}
	

}
