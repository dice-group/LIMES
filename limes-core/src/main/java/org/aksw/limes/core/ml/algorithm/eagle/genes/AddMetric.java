package org.aksw.limes.core.ml.algorithm.eagle.genes;

import java.math.BigDecimal;

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


/**
 * Class to implement the ADD command for evolution of metric expressions.
 * A ADD Command combines Similarity Measures with coefficients.
 * E.g. "ADD(0.5*trigram(x.title,y.title)|0.9, 0.5*cosine(x.authors,y.authors)|0.7)".
 * So LIMES recalculates the thresholds: threshold1 = (threshold - coef2) / coef1;
 *
 * @author Klaus Lyko
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class AddMetric extends CommandGene
        implements IMutateable, ICloneable {
    /**
     *
     */
    private static final long serialVersionUID = -1303055852196726757L;
    // is this Command a candidate for mutation
    private boolean is_mutateable;

    /**
     * Default Constructor. Setting return type to String.class and making ADD no candidate for mutation.
     *
     * @param config
     *         A GPConfiguration
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public AddMetric(final GPConfiguration config)
            throws InvalidConfigurationException {
        this(config, String.class, false);
    }

    /**
     * Constructor to manually set return type.
     *
     * @param config
     *         A GPConfiguration
     * @param a_returnType
     *         Class this CommandGene returns on executing.
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public AddMetric(final GPConfiguration config, Class<?> a_returnType, boolean mutateable)
            throws InvalidConfigurationException {
        super(config, 4, a_returnType, 8, // 8 = subReturnType
                new int[]{1, ResourceTerminalType.THRESHOLD.intValue(),
                        1, ResourceTerminalType.THRESHOLD.intValue()}
        );
        is_mutateable = mutateable;
    }

    /**
     * ADD metrics only have coefficients no local thresholds. So this function removes them.
     *
     * @param measure,
     *         e.g. <i>trigrams(s.x,t.y)|0.8</i>
     * @return shortened measure, e.g. <i>trigrams(s.x,t.y)</i>
     */
    public static String removeThresholdFromMeasure(String measure) {
        if (measure.lastIndexOf("|") > measure.lastIndexOf(")")) {
            return measure.substring(0, measure.lastIndexOf("|"));
        }
        return measure;
    }

    public String toString() {
        return "ADD(&2*&1,&4*&3)";
    }

    /**
     * We're expecting 2 pairs of aSimilarityCommand and a coefficient.
     *
     * @param a_ind
     *         A GPProgram
     * @param a_chromNum
     *         The number of the chromosome.
     * @return Class type of the child.
     */
    public Class<?> getChildType(IGPProgram a_ind, int a_chromNum) {
        if (a_chromNum == 0 || a_chromNum == 2)
            return String.class;
        else
            return CommandGene.DoubleClass;
    }

    @Override
    public Object execute_object(ProgramChromosome a_chrom, int a_n, Object[] args) {
        double coef1 = 0.5, coef2 = 0.5;
        String sim1, sim2;
        sim1 = (String) a_chrom.execute_object(a_n, 0, args);
        coef1 = a_chrom.execute_double(a_n, 1, args);
        sim2 = (String) a_chrom.execute_object(a_n, 2, args);
        coef2 = a_chrom.execute_double(a_n, 3, args);

        StringBuffer value = new StringBuffer("ADD");
        value.append("(");
        value.append(new BigDecimal(coef1).setScale(4, BigDecimal.ROUND_HALF_EVEN));
        value.append("*");
        value.append(removeThresholdFromMeasure(sim1));
        value.append(",");
        value.append(new BigDecimal(coef2).setScale(4, BigDecimal.ROUND_HALF_EVEN));
        value.append("*");
        value.append(removeThresholdFromMeasure(sim2));
        value.append(")");
        return value.toString();
    }

    @Override
    public void execute_void(ProgramChromosome a_chrom, int a_n, Object[] args) {
        execute_object(a_chrom, a_n, args);
    }

    @Override
    public CommandGene applyMutation(int a_index, double a_percentage)
            throws InvalidConfigurationException {
        if (!is_mutateable) {
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
     * @return CommandGene
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public CommandGene applyMutation() throws InvalidConfigurationException {
    	// TODO implement
        return this;
    }

    @Override
    public Object clone() {
        try {
            AddMetric result = new AddMetric(getGPConfiguration(), getReturnType(), is_mutateable);
            return result;
        } catch (Throwable t) {
            throw new CloneException(t);
        }
    }
}
