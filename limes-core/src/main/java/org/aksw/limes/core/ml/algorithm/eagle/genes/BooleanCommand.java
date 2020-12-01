package org.aksw.limes.core.ml.algorithm.eagle.genes;

import java.util.ArrayList;
import java.util.List;

import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.CloneException;
import org.jgap.util.ICloneable;

/**
 * Class implements Boolean Operators. Children are either NesetedBooleans or Measures.
 * Therefore, it's not required anymore and can be replaced with NestedBoolean.
 *
 * @author Klaus Lyko
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 * 
 * @deprecated use NestedBoolean.
 */
public class BooleanCommand extends CommandGene
        implements ICloneable, IMutateable {

    /**
     *
     */
    private static final long serialVersionUID = 8309825108305749003L;

    private String metric = "AND";

    private List<String> supportedMetrics;

    private boolean m_mutateable = true;

    /**
     * Basic Constructor.
     *
     * @param a_conf
     *         a_conf A JGAP GPconfiguration instance.
     * @param command
     *         the actual boolean command (AND, OR, XOR)
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public BooleanCommand(final GPConfiguration a_conf, String command)
            throws InvalidConfigurationException {
        this(a_conf, String.class, command);
    }

    /**
     * Default Constructor. A Boolean Metric is a boolean expression combining two
     * similarity measures as of now.
     *
     * @param a_conf
     *         A JGAP GPconfiguration instance.
     * @param a_returnType
     *         Define the return type of this node.
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     * TODO For a future version also allow other boolean measures to be children of this node.
     */
    public BooleanCommand(final GPConfiguration a_conf, Class<?> a_returnType, String command)
            throws InvalidConfigurationException {
        super(a_conf, 2, a_returnType, 4,
                new int[]{1, 1}
        );
        metric = command;
        // add default operators
        supportedMetrics = new ArrayList<String>();
        supportedMetrics.add("AND");
        supportedMetrics.add("OR");
//		supportedMetrics.add("XOR");
//		supportedMetrics.add("MINUS");
//		supportedMetrics.add("MIN");
//		supportedMetrics.add("MAX");
    }

    @Override
    public Class<?> getChildType(IGPProgram a_ind, int a_chromNum) {
        return String.class;
    }

    @Override
    public String toString() {
        return metric + "(sim1, sim2)";
    }

    /**
     * Executes BooleanCommand as and Object returning String of resulting expression.
     */
    public Object execute_object(ProgramChromosome a_chrom, int a_n, Object[] args) {
        StringBuffer value = new StringBuffer(metric);
        value.append("(");
        value.append(a_chrom.execute_object(a_n, 0, args));
        value.append(",");
        value.append(a_chrom.execute_object(a_n, 1, args));
        value.append(")");
        return value.toString();
    }

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
            BooleanCommand result = new BooleanCommand(getGPConfiguration(), metric);
            return result;
        } catch (Throwable t) {
            throw new CloneException(t);
        }
    }

    public CommandGene applyMutation(int a_index, double a_percentage)
            throws InvalidConfigurationException {
        if (!m_mutateable) {
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
        metric = supportedMetrics.get(index);
        return this;
    }
}
