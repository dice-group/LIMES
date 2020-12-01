package org.aksw.limes.core.ml.algorithm.eagle.genes;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.ml.algorithm.eagle.core.ExpressionProblem.ResourceTerminalType;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.IMutateable;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.CloneException;
import org.jgap.util.ICloneable;

/**
 * This is the top of the tree, e.g. root of every gene for link specification learning.
 *
 * @author Klaus Lyko
 */

/**
 * GP Command for a Link Specification. Either atomic or a complex
 * similarity measure. Either case, it is basically a chromosome expecting two children:
 * a metric (String) and a threshold (double).
 * 
 * @author Klaus Lyko
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 21, 2016
 */
public class MetricCommand extends CommandGene
        implements IMutateable, ICloneable {

    /** */
    private static final long serialVersionUID = -5555554301086427498L;

    public MetricCommand(final GPConfiguration config) throws InvalidConfigurationException {
        this(config, LinkSpecification.class);
    }

    public MetricCommand(final GPConfiguration config, Class<?> returnType) throws InvalidConfigurationException {
        super(config, 2, returnType,
                88
                , new int[]{1, ResourceTerminalType.GOBALTHRESHOLD.intValue()}
        );
    }

    public Class<?> getChildType(IGPProgram a_ind, int a_chromNum) {
        if (a_chromNum == 0)
            return String.class;
        else
            return CommandGene.DoubleClass;
    }

    @Override
    public String toString() {
        return "Metric(&1, &2)";
    }

    public CommandGene applyMutation(int a_index, double a_percentage)
            throws InvalidConfigurationException {
        return this;
    }

    @Override
    public Object clone() {
        try {
            MetricCommand result = new MetricCommand(getGPConfiguration(), this.getReturnType());
            return result;
        } catch (Throwable t) {
            throw new CloneException(t);
        }
    }

    public LinkSpecification execute_object(ProgramChromosome a_chrom, int a_n, Object[] args) {
        String expr = AddMetric.removeThresholdFromMeasure((String) a_chrom.execute_object(a_n, 0, args));
        double thres = a_chrom.execute_double(a_n, 1, args);
        //@FIXME really necessary?
        if (expr.startsWith("euclidean") && thres < 0.4d) {
            thres = 0.4d;
        }
        return new LinkSpecification(expr, thres);
    }

}
