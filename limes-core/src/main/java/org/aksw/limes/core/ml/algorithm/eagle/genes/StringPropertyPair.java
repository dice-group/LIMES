/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
 * Class to evolve properties as Pairs.
 *
 * @author Klaus Lyko
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class StringPropertyPair extends CommandGene implements IMutateable, ICloneable {
    /**
     *
     */
    private static final long serialVersionUID = 7725242441490770801L;
    final Logger logger = LoggerFactory.getLogger("LIMES");
    int pairIndex;
    PairSimilar<String> pair;
    boolean mutateable;
    LinkSpecGeneticLearnerConfig config;

    public StringPropertyPair(final LinkSpecGeneticLearnerConfig a_conf, Class<?> a_returnType,
                              int a_subReturnType, boolean a_mutateable, int propPairIndex) throws InvalidConfigurationException {
        super(a_conf, 0, a_returnType, a_subReturnType);
        mutateable = a_mutateable;
        config = a_conf;
        this.pairIndex = propPairIndex;
        pair = config.getPropertyMapping().stringPropPairs.get(pairIndex);
    }

    public StringPropertyPair(final LinkSpecGeneticLearnerConfig a_conf, Class<?> a_returnType,
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
        int maxIndex = config.getPropertyMapping().stringPropPairs.size() - 1;
        int randomAdd;

        if ((arg1 > 0.5d && pairIndex < maxIndex) || pairIndex == 0) {
            RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
            randomAdd = randomGen.nextInt(Math.max(0, maxIndex - pairIndex + 1));
        } else {
            RandomGenerator randomGen = getGPConfiguration().getRandomGenerator();
            randomAdd = randomGen.nextInt(pairIndex + 1);
            randomAdd *= -1;
        }
        try {
            pair = config.getPropertyMapping().stringPropPairs.get(pairIndex + randomAdd);
//			logger.info("Mutation of String prop match from "+pairIndex+" to "+(pairIndex+randomAdd));
            pairIndex += randomAdd;
        } catch (IndexOutOfBoundsException e) {
            logger.warn("Failed to mutate (max=" + maxIndex + ") to PropertyPairIndex from " + pairIndex + " + " + randomAdd + " " + arg1);
        }
        return this;
    }

    public CommandGene clone() {
        try {
            StringPropertyPair newPair = new StringPropertyPair(config, getReturnType(), getSubReturnType(), mutateable, pairIndex);
            return newPair;
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
