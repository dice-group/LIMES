package org.aksw.limes.core.measures.mapper.resourcesets;

import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.junit.Test;

/**
 * @author Kevin Dreßler
 * @since 1.0
 */
public class SetJaccardMapperTest {

    @Test
    public void testGetMapping() throws Exception {
        SetJaccardMapper mapper = new SetJaccardMapper();
        ACache s = new MemoryCache();
        s.addTriple("spielberg","movies","ET");
        s.addTriple("spielberg","movies","birds");
        s.addTriple("spielberg","movies","snails");
        ACache t = new MemoryCache();
        t.addTriple("spilberg","movies","ET");
        t.addTriple("spilberg","movies","birds");
        t.addTriple("spilberg","movies","rats");
        AMapping mapping1 = mapper.getMapping(s, t, "?x", "?y", "set_jaccard(x.movies, y.movies)", 0.4d);
        AMapping mapping2 = MappingFactory.createDefaultMapping();
        mapping2.add("spielberg", "spilberg", 0.5d);
        assertEquals(mapping2, mapping1);
    }
}