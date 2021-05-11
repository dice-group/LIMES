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

import org.jgap.InvalidConfigurationException;
import org.jgap.RandomGenerator;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.CloneException;
import org.jgap.util.ICloneable;

import java.util.ArrayList;
import java.util.List;

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
