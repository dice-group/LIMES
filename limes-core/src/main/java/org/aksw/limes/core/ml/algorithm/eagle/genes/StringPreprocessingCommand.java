package org.aksw.limes.core.ml.algorithm.eagle.genes;

import java.util.List;
import java.util.Vector;

import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.util.ICloneable;

/**
 * CommandGene to evolve preprocessing of properties. This is planned to be
 * child of a String property.
 *
 * @author Klaus Lyko
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class StringPreprocessingCommand extends CommandGene implements IMutateable,
        ICloneable {
    /**
     * 
     */
    private static final long serialVersionUID = 4353593664193207799L;
    /**
     * 
     */
    public static List<String> functions = new Vector<String>();
    boolean is_mutable = false;
    String function = "lowercase";

    {
        functions.add("lowercase");
        functions.add("uppercase");
        functions.add("nolang");
        functions.add("cleaniri");
    }

    /**
     * Default Constructor. Is not mutable, return type is String.
     *
     * @param a_config
     *         GPConfiguration, in most cases a LinkSpecGeneticLearnerConfig.
     * @param function
     *         The preprocessing function, e.g. "lowercase"
     * @param subReturnType
     *         A sub return type to distinguish from other commands.
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public StringPreprocessingCommand(final GPConfiguration a_config, String function,
                                      int subReturnType) throws InvalidConfigurationException {
        this(a_config, String.class, function, subReturnType, false);
    }

    /**
     * Constructor to also set if this command is mutable.
     *
     * @param a_config
     *         GPConfiguration, in most cases a LinkSpecGeneticLearnerConfig.
     * @param function
     *         The preprocessing function, e.g. "lowercase"
     * @param subReturnType
     *         A sub return type to distinguish from other commands.
     * @param is_mutable
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public StringPreprocessingCommand(final GPConfiguration a_config, String function,
                                      int subReturnType, boolean is_mutable) throws InvalidConfigurationException {
        this(a_config, String.class, function, subReturnType, is_mutable);
    }

    /**
     * Full Constructor gives controal over return type.
     *
     * @param a_config
     *         GPConfiguration, in most cases a LinkSpecGeneticLearnerConfig.
     * @param a_returnType
     *         Defines the return type.
     * @param function
     *         The preprocessing function, e.g. "lowercase"
     * @param subReturnType
     *         A sub return type to distinguish from other commands.
     * @param is_mutable boolean
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public StringPreprocessingCommand(final GPConfiguration a_config, Class<?> a_returnType,
                                      String function, int subReturnType, boolean is_mutable) throws InvalidConfigurationException {
        super(a_config, 0, a_returnType, subReturnType);
        if (functions.contains(function)) {
            this.function = function;
        }
        this.is_mutable = is_mutable;
    }


    @Override
    public Object clone() {
        try {
            return new StringPreprocessingCommand(this.getGPConfiguration(), this.getReturnType(), function, getSubReturnType(), is_mutable);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return this;
        }
    }

    //	@Override
    public org.jgap.gp.CommandGene applyMutation(int a_index, double a_percentage)
            throws InvalidConfigurationException {
        if (a_percentage > 0.5) {
            RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
            int random = randomGen.nextInt(functions.size());
            if (!functions.get(random).equalsIgnoreCase(function))
                this.function = functions.get(random);
        }
        return this;
    }

    @Override
    public String toString() {
        return function;
    }

}
