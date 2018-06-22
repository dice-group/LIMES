package org.aksw.limes.core.measures.mapper.bags;

import com.google.common.collect.Multiset;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.IMapper;

import java.util.Map;

/**
 * @author Cedric Richter
 */
public interface IBagMapper extends IMapper {

    public <T> AMapping getMapping(Map<String, Multiset<T>> source, Map<String, Multiset<T>> target, double threshold);

}
