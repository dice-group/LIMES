package org.aksw.limes.core.evaluation.oracle;

import org.aksw.limes.core.io.mapping.Mapping;
import org.apache.log4j.Logger;

/**
 * This class represents a naive oracle
 * @author ngonga
 * @author mofeed
 * @version 1.0
 */
public class SimpleOracle implements IOracle{
    static Logger logger = Logger.getLogger("LIMES");
	Mapping mapping;
	
	public SimpleOracle() {
		
	}
	
	public SimpleOracle(Mapping m) {
		loadData(m);
	}
	
	public boolean ask(String uri1, String uri2) {
   //         System.out.println(uri1 + "<->" + uri2);
		if(mapping == null) return false;
                return (mapping.contains(uri1, uri2) || mapping.contains(uri2, uri1));
	}

	public void loadData(Mapping m) {
		mapping = m;		
   //     System.out.println(m);
	}

    public int size() {
        return mapping.size();
    }

    public Mapping getMapping() {
        return mapping;
    }

    public String getType() {
        return "simple";
    }

}
