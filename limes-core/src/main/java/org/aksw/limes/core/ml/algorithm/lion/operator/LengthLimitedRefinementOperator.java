package org.aksw.limes.core.ml.algorithm.lion.operator;

import java.util.Set;

import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.ml.setting.LearningSetting;



/**
 * A refinement operator for which the syntactic length of the generated
 * refinements can be limited.
 * 
 * @author Jens Lehmann
 * @author Klaus Lyko
 */
public interface LengthLimitedRefinementOperator {

	/**
	 * Optional refinement operation, where the learning algorithm can
	 * specify an additional bound on the length of specs. 
	 * 
	 * @param spec The spec, which will be refined.
	 * @param maxLength The maximum length of returned specs, where length is defined by {@link LinkSpec#size()}.
	 * @return A set of refinements obeying the above restrictions.
	 */
	public Set<LinkSpecification> refine(LinkSpecification spec, int maxLength);

//	/**
//	 * Needed to specify source, target vars, and to get the appropriate PropertyMapping
//	 * @param evalData
//	 */
//	public void setEvalData(EvaluationData evalData);	
//	
	/**
	 * Sets LearningParameters.
	 * @param setting
	 */
	public void setLearningSetting(LearningSetting setting);
	/**
	 * Sets Configuration
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration);
}
