package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra;


import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.IMapper;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;


public interface IAllenAlgebraMapper extends IMapper {
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps);

    public String getName();
}
