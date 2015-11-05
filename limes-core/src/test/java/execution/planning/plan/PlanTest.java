package execution.planning.plan;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.aksw.limes.core.config.LinkSpecification;
import org.aksw.limes.core.execution.engine.DefaultExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.execution.planning.planner.CannonicalPlanner;
import org.aksw.limes.core.execution.planning.planner.HeliosPlanner;
import org.aksw.limes.core.execution.rewriter.AlgebraicRewriter;
import org.aksw.limes.core.execution.rewriter.IRewriter;
import org.aksw.limes.core.measures.mapper.SetOperations;
import org.apache.commons.lang.StringEscapeUtils;




public class PlanTest {
    private static final String CSVSEPARATOR = ",";

    /**
     * Read a file into the cache
     *
     * @param file
     *            Input fille
     * @return Cache containing the data from the file
     */
    public static Cache readFile(String file) {
	Cache c = new HybridCache();
	String s = "";
	try {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
	    // read properties;
	    s = reader.readLine();

	    String properties[] = s.split(CSVSEPARATOR);

	    String split[];
	    int size = 0;
	    s = reader.readLine();
	    while (s != null && size < 250000) {
		s = s.toLowerCase();
		s = StringEscapeUtils.unescapeHtml(s);

		split = s.split(CSVSEPARATOR);

		for (int i = 1; i < properties.length; i++) {
		    try {

			c.addTriple(split[0], properties[i], split[i]);
			size++;
		    } catch (Exception e) {
			e.printStackTrace();

		    }
		}
		s = reader.readLine();
	    }
	    reader.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    // logger.info(s);
	}
	return c;
    }
    
    public static void test() {
        CannonicalPlanner cp = new CannonicalPlanner();
        Cache source = readFile("Examples\\GeneticEval\\Datasets\\DBLP-Scholar/DBLP1.csv");
        Cache target = readFile("Examples\\GeneticEval\\Datasets\\DBLP-Scholar/Scholar.csv");
//        Cache source = Experiment.readFile("C:\\Users\\Lyko\\workspace\\LIMES\\Examples\\GeneticEval\\Datasets\\DBLP-Scholar\\DBLP1.csv");
//        Cache target = Experiment.readFile("C:\\Users\\Lyko\\workspace\\LIMES\\Examples\\GeneticEval\\Datasets\\DBLP-Scholar\\Scholar.csv");
        HeliosPlanner hp = new HeliosPlanner(source, target);        
        LinkSpecification spec = new LinkSpecification();
        spec.readSpec("AND(euclidean(x.year,y.year)|0.8019,OR(cosine(x.title,y.title)|0.5263,AND(cosine(x.authors,y.authors)|0.5263,overlap(x.title,y.title)|0.5263)|0.2012)|0.2012)", 0.3627);
//        spec.readSpec("OR(cosine(x.title,y.title)|0.5263,AND(cosine(x.authors,y.authors)|0.5263,overlap(x.title,y.title)|0.5263)|0.2012)", 0.3627);
//        spec.readSpec("AND(jaccard(x.title,y.title)|0.4278,trigrams(x.authors,y.authors)|0.4278)", 0.36);
        
        System.out.println("Orginal: "+spec);
        IRewriter rewriter = new AlgebraicRewriter();
        spec = rewriter.rewrite(spec);
        System.out.println("Rewritten: "+spec);
        System.out.println("Canonical plan:\n"+cp.plan(spec));
        cp.plan(spec).draw();
        ExecutionPlan np = hp.plan(spec);
        np.draw();
        System.out.println("HELIOS plan:\n"+np);
        long begin = System.currentTimeMillis();
        DefaultExecutionEngine ee = new DefaultExecutionEngine(source, target, "?x", "?y");
        Mapping m1 = ee.execute(np);
        long end = System.currentTimeMillis();
        System.out.println((end - begin)+" ms for HELIOS, "+m1.getNumberofMappings()+" results.");
        begin = System.currentTimeMillis();
        Mapping m2 = ee.execute(cp.plan(spec));
        end = System.currentTimeMillis();
        System.out.println((end - begin)+" ms for CANONICAL, "+m2.getNumberofMappings()+" results.");        
        System.out.println(SetOperations.difference(m1, m2).getNumberofMappings() +" are missing somewhere");
        System.out.println(SetOperations.difference(m2, m1).getNumberofMappings() +" are missing somewhere");
    }

    public static void main(String args[]) {
        test();
    }
}
