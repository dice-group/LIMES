package org.aksw.limes.core.ml.algorithm.eagle.genes;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.ml.algorithm.eagle.core.LinkSpecGeneticLearnerConfig;
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
 * Wraps around LIMES string similarity commands for the JGAP library, e.g. trigram or levensthein.
 * They all build a expression like <i>similarityMeasure(resource1, resource2)|threshold</i>.
 * Whereas the threshold is ignored if the expression is atomic, i.e. the similarity measure
 * isn't part (argument) of another metric, such as <i>AND</i>).
 * We now expect at least two parameters: 2 Terminals. Whereas the first is of sub return type
 * ResourceTerminalType.SOURCE, the second of ResourceTerminalType.TARGET. This allows us to differentiate the
 * different resources of the two endpoints and avoid silly measures comparing the same resources of the same endpoint.
 * If mutation is turned on, the similarity command might be changed during the evolution process to one of
 * the allowed measures.
 *
 * @author Klaus Lyko
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class StringPreprocessMeasure
        extends CommandGene implements IMutateable, ICloneable {
    /**
     *
     */
    private static final long serialVersionUID = -4901752495126327127L;
    static Logger logger = LoggerFactory.getLogger("LIMES");
    // Holds the name of this similarity Measure.
    private String operationName = "sim";
    // Set of all allowed similarity measures. Needed for mutation.
    private Set<String> allowedOperations = new HashSet<String>();
    // mutation coeeficient
//	private float mutationCoefficient;
    // per default not mutable
    private boolean m_mutateable;

    /**
     * Constructor for atomic similarity measures. @FIXME antiquated and not actually used.
     *
     * @param opName
     *         Name of the LIMES similarity measure operation (e.g. "trigram").
     * @param a_conf
     *         JGAP GPConfiguration.
     * @param a_returnType
     *         The return type of this command.
     * @param a_mutateable
     *         true: this Commandgene is mutateable, viz. the LIMES similarity measure might be changed
     *         to another one out of the allowed operations.
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public StringPreprocessMeasure(String opName, final GPConfiguration a_conf,
                                   final Class<?> a_returnType, boolean a_mutateable)
            throws InvalidConfigurationException {
        super(a_conf, 4, a_returnType, 1,
                new int[]{
                        ResourceTerminalType.STRINGPROPPAIR.intValue(),
                        ResourceTerminalType.THRESHOLD.intValue(),
                        ResourceTerminalType.PREPROCESS.intValue(),
                        ResourceTerminalType.PREPROCESS.intValue(),
                }
        );
        fillOperationSet();
        setOperationName(opName);
        m_mutateable = a_mutateable;
        setNoValidation(false);
    }

    @Override
    public String toString() {
        return operationName + "(&1)|&2";
    }

    /**
     * We expect the first 2 parameters to be resource identifier, the third to be an
     * (optional) double value as the threshold for this Similarity command.
     *
     * @param a_ind
     *         A GPProgram
     * @param a_chromNum
     *         The number of the chromosome.
     * @return type of the child.
     */
    public Class<?> getChildType(IGPProgram a_ind, int a_chromNum) {
        if (a_chromNum == 0)
            return PairSimilar.class;
        else if (a_chromNum == 2 || a_chromNum == 3)
            return String.class;
        else
            return CommandGene.DoubleClass;
    }

    public String getName() {
        return getOperationName();
    }

    public String getOperationName() {
        return operationName;
    }

    /**
     * Setter for the operation of this String similarity measure.
     *
     * @param opName
     *         Name of the measure, e.g. "trigram"
     */
    private void setOperationName(String opName) {
        operationName = opName;
        allowedOperations.add(opName);
    }

    /**
     * Executes this CommandGene as object. Is called if the return type is set to String.class.
     * Thereby returning the atomic LIMES expression <code>"sim(a.resource, b.resource)|threshold"</code>.
     */
    @Override
    public Object execute_object(ProgramChromosome a_chrom, int a_n, Object[] args) {
        double threshold;
        @SuppressWarnings("unchecked")
        PairSimilar<String> propPair = (PairSimilar<String>) a_chrom.execute_object(a_n, 0, args);
        threshold = a_chrom.execute_double(a_n, 1, args);
        String preprocess1 = (String) a_chrom.execute_object(a_n, 2, args);
        String preprocess2 = (String) a_chrom.execute_object(a_n, 3, args);
        LinkSpecGeneticLearnerConfig ExpConfig = (LinkSpecGeneticLearnerConfig) getGPConfiguration();
        StringBuffer value = new StringBuffer(getOperationName());
        value.append("(");
        value.append(ExpConfig.getExpressionProperty("source", propPair.a));
        value.append("[" + preprocess1 + "]");
        value.append(",");
        value.append(ExpConfig.getExpressionProperty("target", propPair.b));
        value.append("[" + preprocess2 + "]");
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
     * Fill Set of allowed operations with default values. Which are all LIMES String similarity measures.
     */
    private void fillOperationSet() {
        allowedOperations.add("trigrams");
        allowedOperations.add("cosine");
        allowedOperations.add("jaccard");
        allowedOperations.add("levenshtein");
        allowedOperations.add("overlap");
    }

    public CommandGene applyMutation(int a_index, double a_percentage)
            throws InvalidConfigurationException {
        // we will change the measure to a random one out of the Set of allowed operations
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
     * Mutates this CommandGene. A random command out of the set of allowed similarity measures is picked.
     *
     * @return A random command out of the set of allowed similarity measures
     * @throws InvalidConfigurationException when an invalid value has been passed to a Configuration object
     */
    public CommandGene applyMutation() throws InvalidConfigurationException {
        String[] aO = {};
        aO = allowedOperations.toArray(aO);
        RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
        String newOp = aO[randomGen.nextInt(aO.length)];
        StringPreprocessMeasure result = new StringPreprocessMeasure(newOp, getGPConfiguration(), getReturnType(), m_mutateable);
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
            StringPreprocessMeasure result = new StringPreprocessMeasure(operationName, getGPConfiguration(), getReturnType(), m_mutateable);
            return result;
        } catch (Throwable t) {
            throw new CloneException(t);
        }
    }

    @Override
    public boolean isValid(ProgramChromosome a_program, int a_index) {
        Object[] o = new Object[0];
        LinkSpecGeneticLearnerConfig expConfig = (LinkSpecGeneticLearnerConfig) getGPConfiguration();
        PairSimilar<?> propPair = (PairSimilar<?>) a_program.execute_object(a_index, 0, o);
        return expConfig.getPropertyMapping().isMatch(propPair.a.toString(), propPair.b.toString());
    }

    public boolean isValid(ProgramChromosome a_program) {
        return isValid(a_program, 0);
    }
}
