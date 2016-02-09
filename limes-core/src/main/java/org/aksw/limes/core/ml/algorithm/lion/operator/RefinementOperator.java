package org.aksw.limes.core.ml.algorithm.lion.operator;

import java.util.Set;

import org.aksw.limes.core.io.ls.LinkSpecification;

/**
 * Interface for all refinement operators based on link specifications.
 * A refinement operator maps a link spec to a set of link specs. 
 * For downward refinement operators those specs are more special. 
 * For upward refinement operators, those specs are more general.
 * 
 * @author Jens Lehmann
 * @author Klaus Lyko
 *
 */
public interface RefinementOperator {

	/**
	 * Standard refinement operation.
	 * @param spec Link spec, which will be refined.
	 * @return Set of refined specs.
	 */
	public Set<LinkSpecification> refine(LinkSpecification spec);
	
}
