package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.IMapper;

/**
 * Implements the Allen's temporal relation mapper interface.
 *
 * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
 * @version 1.0
 */
public interface IAllenAlgebraMapper extends IMapper {
    /**
     * Returns a mapping given a set of atomic relation mappings.
     *
     * @param maps,
     *            The input set of atomic mappings
     * 
     * @return a mapping, the resulting mapping
     */
    public AMapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps);

}
