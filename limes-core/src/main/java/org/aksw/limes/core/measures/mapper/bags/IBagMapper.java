package org.aksw.limes.core.measures.mapper.bags;

import com.google.common.collect.Multiset;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.IMapper;

import java.util.Map;

/**
 * Interface class for BagMappers contains the abstract methods that Cosine and Jaccard Bag Mappers can implement.
 * @author Cedric Richter
 */
public interface IBagMapper extends IMapper {

    public <T> AMapping getMapping(Map<String, Multiset<T>> source, Map<String, Multiset<T>> target, double threshold);

}
