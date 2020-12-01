package org.aksw.limes.core.ml.algorithm.eagle.genes;

import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.ml.algorithm.eagle.core.LinkSpecGeneticLearnerConfig;
import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.ICloneable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to evolve number properites as Pairs.
 *
 * @author Klaus
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class NumberPropertyPair extends CommandGene implements IMutateable, ICloneable {
    /**
     *
     */
    private static final long serialVersionUID = 5370919913629489323L;
    final Logger logger = LoggerFactory.getLogger("LIMES");
    int pairIndex;
    PairSimilar<String> pair;
    boolean mutateable;
    LinkSpecGeneticLearnerConfig config;

    
    public NumberPropertyPair(final LinkSpecGeneticLearnerConfig a_conf, Class<?> a_returnType,
                              int a_subReturnType, boolean a_mutateable, int propPairIndex) throws InvalidConfigurationException {
        super(a_conf, 0, a_returnType, a_subReturnType);
        mutateable = a_mutateable;
        config = a_conf;
        this.pairIndex = propPairIndex;
        pair = config.getPropertyMapping().numberPropPairs.get(pairIndex);
    }


    public NumberPropertyPair(final LinkSpecGeneticLearnerConfig a_conf, Class<?> a_returnType,
                              int a_subReturnType, int propPairIndex) throws InvalidConfigurationException {
        this(a_conf, a_returnType, a_subReturnType, true, propPairIndex);
    }

    @Override
    public String toString() {
        return pair.a + "," + pair.b;
    }

    public CommandGene applyMutation(int arg0, double arg1)
            throws InvalidConfigurationException {
        if (!mutateable)
            return this;
        int maxIndex = config.getPropertyMapping().numberPropPairs.size() - 1;
        int randomAdd;

        if ((arg1 > 0.5d && pairIndex < maxIndex) || pairIndex == 0) {
            RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
            randomAdd = randomGen.nextInt(Math.max(0, maxIndex - pairIndex + 1));
        } else {
            RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
            randomAdd = randomGen.nextInt(pairIndex);
            randomAdd *= -1;
        }
        try {
            pair = config.getPropertyMapping().numberPropPairs.get(pairIndex + randomAdd);
            pairIndex += randomAdd;
        } catch (IndexOutOfBoundsException e) {
            logger.warn("Failed to mutate (max=" + maxIndex + ") to PropertyPairIndex from " + pairIndex + " + " + randomAdd + " " + arg1);
        }
        return this;
    }

    public CommandGene clone() {
        try {
            return new NumberPropertyPair(config, getReturnType(), getSubReturnType(), mutateable, pairIndex);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return this;
        }
    }

    @Override
    public void execute_void(ProgramChromosome a_chrom, int a_n, Object[] args) {
        execute_object(a_chrom, a_n, args);
    }

    @Override
    public Object execute_object(ProgramChromosome a_chrom, int a_n, Object[] args) {
        return pair;//pair.a+","+pair.b;
    }


}
