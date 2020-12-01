package org.aksw.limes.core.ml.algorithm.eagle.genes;

import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionProblem.ResourceTerminalType;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.CloneException;
import org.jgap.util.ICloneable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to chain several preprocessing commands.
 *
 * @author Klaus Lyko
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class ChainedPreprocessingCommand extends CommandGene implements IMutateable, ICloneable {
    private static final long serialVersionUID = 4070812489425199490L;

    static Logger logger = LoggerFactory.getLogger("LIMES");
    public Set<String> functions = new HashSet<String>();
    boolean is_mutable = true;
    String command = "lowercase";

    {
        functions.add("lowercase");
        functions.add("uppercase");
        functions.add("cleaniri");
        functions.add("nolang");
    }

    public ChainedPreprocessingCommand(String command, GPConfiguration a_conf)
            throws InvalidConfigurationException {
        super(a_conf, 1, String.class, ResourceTerminalType.PREPROCESS.intValue(),
                new int[]{ResourceTerminalType.PREPROCESS.intValue(),});
        this.command = command;
        functions.add(command);
    }

    //	@Override
    public CommandGene applyMutation(int a_index, double a_percentage)
            throws InvalidConfigurationException {
        if (!is_mutable) {
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
     *
     * @return A new instance using this command.
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public ChainedPreprocessingCommand applyMutation() throws InvalidConfigurationException {
        String[] aO = {};
        aO = functions.toArray(aO);
        RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
        String newOp = aO[randomGen.nextInt(aO.length)];
        ChainedPreprocessingCommand result = new ChainedPreprocessingCommand(newOp, getGPConfiguration());
        return result;
    }

    /**
     * Clones the object.
     *
     * @return cloned instance of this object
     */
    @Override
    public Object clone() {
        try {
            CommandGene result = new ChainedPreprocessingCommand(command, getGPConfiguration());
            return result;
        } catch (Throwable t) {
            throw new CloneException(t);
        }
    }

    @Override
    public String toString() {
        return command + "->" + "&1";
    }

    @Override
    public Object execute_object(ProgramChromosome a_chrom, int a_n, Object[] args) {
        String childPreProc = (String) a_chrom.execute_object(a_n, 0, args);
        if (childPreProc.indexOf(command) != -1) // avoid chaining the same preprocessings
            return childPreProc;
        for (String complement : getComplementFunctions())//avoid chaining complementary functions
            if (childPreProc.indexOf(complement) != -1)
                return childPreProc;
        return command + "->" + childPreProc;
    }

    @Override
    public void execute_void(ProgramChromosome a_chrom, int a_n, Object[] args) {
        execute_object(a_chrom, a_n, args);
    }

    /**
     * Return arry of preprocessing function which are complementary to this one. E.g. lowercase vs. uppercase.
     *
     * @return String[] holding all complementary functions.
     */
    private String[] getComplementFunctions() {
        if (command.equals("lowercase"))
            return new String[]{"uppercase"};
        if (command.equals("uppercase"))
            return new String[]{"lowercase"};
        if (command.equals("regularAlphabet"))
            return new String[]{"cleaniri", "removebraces", "nolang"};
        return new String[]{};
    }

}
