package org.aksw.limes.core.measures.mapper.pointsets;


import org.junit.Test;

public class OrchidMapperTest {

    @Test
    public void test() {
        System.out.println(OrchidMapper.getPoints("LINESTRING((129.1656696 43.1537336) (129.1653388 43.1494863))"));
        System.out.println(OrchidMapper.getPoints("POINT(-79.116667 -3.2)"));
        System.out.println(OrchidMapper.getPoints("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
    }

}
