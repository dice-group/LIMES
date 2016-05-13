package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra;


import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.IMapper;

import de.uni_leipzig.simba.data.Instance;

public interface IAllenAlgebraMapper extends IMapper{
    public Mapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps);
    public String getName();
}
