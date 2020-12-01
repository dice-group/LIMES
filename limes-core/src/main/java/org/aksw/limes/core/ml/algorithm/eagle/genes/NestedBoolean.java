package org.aksw.limes.core.ml.algorithm.eagle.genes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionProblem.ResourceTerminalType;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.CloneException;
import org.jgap.util.ICloneable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command for Link Spec trees with multiple Boolean Commands:
 * Childs are either Measures(StringMeasure or NumberMeasure) or NestedBooleans.
 *
 * @author Klaus Lyko
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class NestedBoolean extends CommandGene
        implements IMutateable, ICloneable {
    /**
     *
     */
    private static final long serialVersionUID = 7875735649199172973L;
    static Logger logger = LoggerFactory.getLogger("LIMES");
    boolean is_mutable = true;
    private String command = "AND";
    private List<String> supportedMetrics;

    /**
     * Basic Constructor.
     *
     * @param op
     *         Name of the boolean operator.
     * @param a_conf
     *         a_conf A JGAP GPconfiguration instance.
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public NestedBoolean(String op, final GPConfiguration a_conf)
            throws InvalidConfigurationException {
        this(op, a_conf, String.class, true);
    }

    /**
     * Constructor to define return type, and if NestedBoolean supports mutation.
     *
     * @param op
     *         Name of the boolean operator.
     * @param a_conf
     *         A JGAP GPconfiguration instance.
     * @param a_returnType
     *         Define the return type of this node.
     * @param mutable
     *         true means command(AND, OR, XOR) can mutate.
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public NestedBoolean(String op, final GPConfiguration a_conf, Class<?> a_returnType, boolean mutable)
            throws InvalidConfigurationException {
        super(a_conf, 3, a_returnType, 1
                //);
                , new int[]{1, 1, ResourceTerminalType.THRESHOLD.intValue()});
        command = op;
        // add default operators
        supportedMetrics = new ArrayList<String>();
        supportedMetrics.add("AND");
        supportedMetrics.add("OR");
//		supportedMetrics.add("XOR");
        is_mutable = mutable;
    }

    @Override
    public String toString() {
        return command + "(&1,&2)|&3";
    }

    /**
     * We expect the first 2 parameters to be resource identifier, the third to be an
     * (optional) double value as the threshold for this Similarity command.
     *
     * @param a_ind
     *         A GPProgram
     * @param a_chromNum
     *         The number of the chromosome.
     * @return Class type of the child.
     */
    public Class<?> getChildType(IGPProgram a_ind, int a_chromNum) {
        //	logger.info("Get child from "+a_ind+" at "+a_chromNum);
        if (a_chromNum == 0)
            return String.class;
        else if (a_chromNum == 1)
            return String.class;
        else
            return CommandGene.DoubleClass;

    }

    /**
     * Executes this CommandGene as object. Is called if the return type is set to String.class.
     * Thereby returning the atomic LIMES expression <code>"sim(a.resource, b.resource)|threshold"</code>.
     */
    @Override
    public Object execute_object(ProgramChromosome a_chrom, int a_n, Object[] args) {
        try {
            check(a_chrom, a_n);
        } catch (IllegalStateException e) {
            throw e;
        }
        double threshold;
        String s1 = (String) a_chrom.execute_object(a_n, 0, args);
        String s2 = (String) a_chrom.execute_object(a_n, 1, args);
        threshold = a_chrom.execute_double(a_n, 2, args);

        StringBuffer value = new StringBuffer(command);
        value.append("(");
        value.append(s1);
        value.append(",");
        value.append(s2);
        value.append(")");
        value.append("|");
        value.append(new BigDecimal(threshold).setScale(4, BigDecimal.ROUND_HALF_EVEN));
        return value.toString();
    }

    @Override
    public void execute_void(ProgramChromosome a_chrom, int a_n, Object[] args) {
        execute_object(a_chrom, a_n, args);
    }

    /**
     * Clones the object.
     *
     * @return cloned instance of this object
     */
    @Override
    public Object clone() {
        try {
            NestedBoolean result = new NestedBoolean(command, getGPConfiguration(), this.getReturnType(), is_mutable);
            return result;
        } catch (Throwable t) {
            throw new CloneException(t);
        }
    }

    public CommandGene applyMutation(int a_index, double a_percentage)
            throws InvalidConfigurationException {
        if (!is_mutable) {
            return this;
        }
        RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
        double random = randomGen.nextDouble();
        if (random < a_percentage) {
            return applyMutation();
        }
        return this;
    }

    /**
     * Performs the actual mutation
     *
     * @return a mutated booleanCommand.
     */
    private CommandGene applyMutation() {
        RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
        int index = randomGen.nextInt(supportedMetrics.size());
        command = supportedMetrics.get(index);
        return this;
    }
}
