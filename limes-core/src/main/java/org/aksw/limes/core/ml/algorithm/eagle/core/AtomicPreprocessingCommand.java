package org.aksw.limes.core.ml.algorithm.eagle.core;

import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionProblem.ResourceTerminalType;
import org.apache.log4j.Logger;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.CloneException;
import org.jgap.util.ICloneable;

/**
 * CommandGene to also support evolution of preprocessing.
 * @TODO untested
 * @TODO unused
 * @TODO check mutation
 * @author Klaus Lyko
 *
 */
public class AtomicPreprocessingCommand extends CommandGene 
implements IMutateable, ICloneable{
	private static final long serialVersionUID = 8798097200717090109L;

	static Logger logger = Logger.getLogger("LIMES");
	
	boolean is_mutable = true;

	public Set<String> functions = new HashSet<String>();
	{
		functions.add("lowercase");
		functions.add("uppercase");
		functions.add("cleaniri");
		functions.add("nolang");
	}
	String command = "lowercase";
	
	public AtomicPreprocessingCommand(String command, final GPConfiguration a_conf) throws InvalidConfigurationException {
		super(a_conf, 0, String.class, ResourceTerminalType.PREPROCESS.intValue());
		this.command = command;
		functions.add(command);
	}
	
//	@Override
	public CommandGene applyMutation(int a_index, double a_percentage)
			throws InvalidConfigurationException {
		if (!is_mutable ) {
			return this;
		}
		//FIXME somehow silly to do this !
		RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
	     double random = randomGen.nextDouble();
	     if (random < a_percentage) {
	       return applyMutation();
	     }
	     return this;
	}
	
	/**
	 * Mutates this CommandGene. A preprocessing funtion out of the set of 
	 * allowed preprocessing functions is picked.
	 * @return A new instance using this command.
	 * @throws InvalidConfigurationException
	 */
	public CommandGene applyMutation() throws InvalidConfigurationException {
		String[] aO = {};
		aO=functions.toArray(aO);
		RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
		String newOp = aO[randomGen.nextInt(aO.length)];
		AtomicPreprocessingCommand result = new AtomicPreprocessingCommand(newOp, getGPConfiguration());
		return result;
	   }
	
	/**
	   * Clones the object.
	   * @return cloned instance of this object
	   */
	@Override
	public Object clone() {
	    try {
	    	AtomicPreprocessingCommand result =  new AtomicPreprocessingCommand(command, getGPConfiguration());
	      return result;
	    } catch (Throwable t) {
	      throw new CloneException(t);
	    }
	}

	@Override
	public String toString() {
		return command;
	}
	
	@Override
	public Object execute_object(ProgramChromosome a_chrom, int a_n, Object[] args) {
		return command;
	}
	@Override
	public void execute_void(ProgramChromosome a_chrom, int a_n, Object[] args) {
		execute_object(a_chrom, a_n, args);
	}

}
