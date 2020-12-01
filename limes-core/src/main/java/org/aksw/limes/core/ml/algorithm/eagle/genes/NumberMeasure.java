package org.aksw.limes.core.ml.algorithm.eagle.genes;


import java.math.BigDecimal;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.ml.algorithm.eagle.core.LinkSpecGeneticLearnerConfig;
import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionProblem.ResourceTerminalType;
import org.jgap.InvalidConfigurationException;
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
 * Class implements a command to compare
 *
 * @author Lyko
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class NumberMeasure extends CommandGene implements IMutateable, ICloneable {
    /**
     *
     */
    private static final long serialVersionUID = -2615468774498850743L;
    static Logger logger = LoggerFactory.getLogger("LIMES");
    // per default not mutable
    private boolean m_mutateable;

    /**
     * Constructor for similarity measures for numeric values bound by a threshold.
     *
     * @param a_conf
     *         JGAP GPConfiguration.
     * @param a_returnType
     *         The return type of this command.
     * @param a_subReturnType
     *         Specifies the SubReturnType.
     * @param a_mutateable
     *         true: enables mutation.
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public NumberMeasure(final GPConfiguration a_conf, Class<?> a_returnType,
                         int a_subReturnType, boolean a_mutateable) throws InvalidConfigurationException {
        super(a_conf, 2, a_returnType,
                a_subReturnType,
                new int[]{
                        ResourceTerminalType.NUMBERPROPPAIR.intValue(),
                        ResourceTerminalType.NUMBERTHRESHOLD.intValue(),}
        );
        m_mutateable = a_mutateable;
        setNoValidation(false);
    }

    /**
     * Constructor for atomic similarity measures for numeric properties.
     *
     * @param a_conf
     *         JGAP GPConfiguration.
     * @param a_returnType
     *         The return type of this command.
     * @param a_mutateable
     *         true: enables mutation.
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public NumberMeasure(final GPConfiguration a_conf,
                         final Class<?> a_returnType, boolean a_mutateable)
            throws InvalidConfigurationException {
        super(a_conf, 2, a_returnType, 1,
                new int[]{
                        ResourceTerminalType.NUMBERPROPPAIR.intValue(),
                        ResourceTerminalType.NUMBERTHRESHOLD.intValue(),}
        );
        m_mutateable = a_mutateable;
        setNoValidation(false);
    }

    /**
     * Default Constructor. Sets return type to String.class, disables mutation.
     *
     * @param a_conf
     *         JGAP Configuration instance.
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public NumberMeasure(final GPConfiguration a_conf)
            throws InvalidConfigurationException {
        this(a_conf, String.class, false);
    }


    public CommandGene applyMutation(int a_index, double a_percentage)
            throws InvalidConfigurationException {
        // as of now no mutation is supported
        return this;
    }

    @Override
    public String toString() {
        return "euclidean(&1)|&2";
    }

    public Class<?> getChildType(IGPProgram a_ind, int a_chromNum) {
        if (a_chromNum == 0)
            return PairSimilar.class;
        else
            return CommandGene.DoubleClass;

    }

    /**
     * Executes this CommandGene as object. Is called if the return type is set to String.class.
     * Thereby returning the atomic LIMES expression <code>"sim(a.resource, b.resource)|threshold"</code>.
     */
    @Override
    public Object execute_object(ProgramChromosome a_chrom, int a_n, Object[] args) {
        @SuppressWarnings("unchecked")
        PairSimilar<String> propPair = (PairSimilar<String>) a_chrom.execute_object(a_n, 0, args);
        double threshold = a_chrom.execute_double(a_n, 1, args);
        ;

        LinkSpecGeneticLearnerConfig ExpConfig = (LinkSpecGeneticLearnerConfig) getGPConfiguration();
        StringBuffer value = new StringBuffer("euclidean");
        value.append("(");
        value.append(ExpConfig.getExpressionProperty("source", propPair.a));
        value.append(",");
        value.append(ExpConfig.getExpressionProperty("target", propPair.b));
        value.append(")");
        value.append("|");
        value.append(new BigDecimal(threshold).setScale(4, BigDecimal.ROUND_HALF_EVEN));
        return value.toString();
    }

    @Override
    public void execute_void(ProgramChromosome a_chrom, int a_n, Object[] args) {
        execute_object(a_chrom, a_n, args);
    }

    @Override
    public boolean isValid(ProgramChromosome a_program, int a_index) {
        Object[] o = new Object[0];
        LinkSpecGeneticLearnerConfig expConfig = (LinkSpecGeneticLearnerConfig) getGPConfiguration();
        @SuppressWarnings("unchecked")
        PairSimilar<String> propPair = (PairSimilar<String>) a_program.execute_object(a_index, 0, o);
        return expConfig.hasNumericProperties() && expConfig.getPropertyMapping().isMatch(propPair.a, propPair.b);
    }

    public boolean isValid(ProgramChromosome a_program) {
        return isValid(a_program, 0);
    }

    @Override
    public Object clone() {
        try {
            NumberMeasure result = new NumberMeasure(getGPConfiguration(), getReturnType(), getSubReturnType(), m_mutateable);
            return result;
        } catch (Throwable t) {
            throw new CloneException(t);
        }
    }

}