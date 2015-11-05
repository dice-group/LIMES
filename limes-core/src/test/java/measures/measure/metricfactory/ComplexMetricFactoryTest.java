package measures.measure.metricfactory;

import static org.junit.Assert.*;

import org.aksw.limes.core.data.Instance;
import org.aksw.limes.core.measures.measure.metricfactory.ComplexMetricFactory;
import org.junit.Test;

public class ComplexMetricFactoryTest {

    @Test
    public void test() {
	ComplexMetricFactory cmf = new ComplexMetricFactory();
	cmf.setExpression("0.5 *Euclidean (x_title, y_title)+0.5*QGrams (y_name, x_familyName)");
	Instance a = new Instance("uri1");
	Instance b = new Instance("uri2");

	a.addProperty("title", "Dr. rer. nat");
	a.addProperty("familyname", "NgongaNgomo");
	b.addProperty("name", "AxelNgonga");
	b.addProperty("title", "Dr.");

	System.out.println("Similarity is " + cmf.getSimilarity(a, b));
    }

}
