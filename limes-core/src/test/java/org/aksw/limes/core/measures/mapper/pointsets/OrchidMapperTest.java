package org.aksw.limes.core.measures.mapper.pointsets;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrchidMapperTest {
    private static final Logger logger = LoggerFactory.getLogger(OrchidMapperTest.class);
    @Test
    public void test() {
        logger.info("{}",OrchidMapper.getPoints("MULTILINESTRING((129.1656696 43.1537336, 129.1653388 43.1494863), (29.1656696 43.1537336, 29.1653388 43.1494863))"));
        logger.info("{}",OrchidMapper.getPoints("POINT(-79.116667 -3.2)"));
        logger.info("{}",OrchidMapper.getPoints("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
        logger.info("{}",OrchidMapper.getPoints("<http://www.opengis.net/def/crs/EPSG/0/4326> POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))"));
    }

}
