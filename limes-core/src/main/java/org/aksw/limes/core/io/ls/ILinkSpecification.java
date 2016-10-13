package org.aksw.limes.core.io.ls;

import org.aksw.limes.core.util.Clonable;

/**
 * @author Mohamed Sherif (sherif@informatik.uni-leipzig.de)
 * @version Jul 12, 2016
 */
public interface ILinkSpecification extends Clonable<ILinkSpecification> {

    int compareTo(Object o);

}
