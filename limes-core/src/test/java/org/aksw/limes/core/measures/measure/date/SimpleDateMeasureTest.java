package org.aksw.limes.core.measures.measure.date;


import org.aksw.limes.core.measures.measure.date.SimpleDateMeasure;
import org.junit.Test;

public class SimpleDateMeasureTest {

    @Test
    public void test() {
	String date = "1992-09-25";
	SimpleDateMeasure m = new SimpleDateMeasure();
	System.out.println(m.extractDate(date));
    }

}
