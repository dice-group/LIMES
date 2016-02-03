package org.aksw.limes.core.io.ls;

import org.aksw.limes.core.util.Clonable;

/**
 * @author Mohamed Sherif <sherif@informatik.uni-leipzig.de>
 * @version Nov 12, 2015
 */
public interface ILinkSpecification extends Clonable<ILinkSpecification>{

	int compareTo(Object o);

}
