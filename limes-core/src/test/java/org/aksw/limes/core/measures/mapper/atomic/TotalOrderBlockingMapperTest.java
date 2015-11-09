package org.aksw.limes.core.measures.mapper.atomic;

import static org.junit.Assert.*;

import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.measures.mapper.atomic.TotalOrderBlockingMapper;
import org.junit.Test;

public class TotalOrderBlockingMapperTest {

    @Test
    public void test() {
	MemoryCache source = new MemoryCache();
        MemoryCache target = new MemoryCache();

        target.addTriple("0", "lat", "0");
        target.addTriple("0", "lon", "0");

        target.addTriple("1", "lat", "4");
        target.addTriple("1", "lon", "4");

        target.addTriple("2", "lat", "4");
        target.addTriple("2", "lon", "3");

        target.addTriple("3", "lat", "3");
        target.addTriple("3", "lon", "4");

        source.addTriple("4", "lat", "2");
        source.addTriple("4", "lon", "2");

        source.addTriple("5", "lat", "5");
        source.addTriple("5", "lon", "2");

        TotalOrderBlockingMapper bm = new TotalOrderBlockingMapper();
    }

}
