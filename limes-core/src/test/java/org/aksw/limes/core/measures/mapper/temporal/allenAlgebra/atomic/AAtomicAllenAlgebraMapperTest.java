package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.atomic;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AAtomicAllenAlgebraMapperTest {

    private static final Logger logger = LoggerFactory.getLogger(AAtomicAllenAlgebraMapperTest.class);
    @Test
    public void test() {

       
        
        String[] values = new String[] { 
                "2015-05-20T08:21:04.123Z",
                "2015-05-20T08:21:04.123+02:00",
                
                "2015-05-20T08:21:04.598",
                
                "2015-05-20T08:21:04Z",
                "2015-05-20T08:21:04+02:00",
                
                "2015-05-20T08:21:04",
                
                "2015-05-20T08:21Z",
                "2015-05-20T08:21+02:00",
                
                "2015-05-20T08:21",
                "2015-05-20T08:21:00",
                
                //"2015-05-20",
                
                //"2015-05",
                
                //"2015",
                
        };

        ArrayList<Long> numbers = new ArrayList<Long>();
        
        for(String value: values){
            long epoch = AAtomicAllenAlgebraMapper.getEpoch(value);
            logger.info("{}",epoch);
            numbers.add(epoch);
            logger.info("{}","-----------------------");
        }
        //7200000
        assertTrue(numbers.get(0)-numbers.get(1) == 7200000);
        
        assertTrue(numbers.get(3)-numbers.get(4) == 7200000);
        
        assertTrue(numbers.get(6)-numbers.get(7) == 7200000);
        
        assertTrue(numbers.get(8).equals(numbers.get(9)));

    }

}
