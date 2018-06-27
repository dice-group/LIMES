package org.aksw.limes.core.measures.measure.bags;

import com.google.common.collect.Multiset;
import org.aksw.limes.core.io.cache.Instance;

/**
 * Class implements the basic idea of BagToWords Model
 * Data (such as a string or from a file) is represented as the bag (multiset) of its words
 *
 * @author Cedric Richter
 */
public abstract class ABagMeasure implements IBagMeasure {

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        double max = 0;
        for (String p1 : instance1.getProperty(property1)) {
            Multiset<String> p1Set = String2BagMapper.getBag(p1);
            if(p1Set == null)continue;
            for (String p2 : instance2.getProperty(property2)) {
                Multiset<String> p2Set = String2BagMapper.getBag(p2);
                if(p2Set == null)continue;
                double sim = getSimilarity(p1Set, p2Set);
                if(sim > max)
                    max = sim;
            }
        }

        return max;
    }

    /**
     * @return the similarity between two multiset instances
     */
    @Override
    public double getSimilarity(Object o1, Object o2){
        if(o1 instanceof String)
            o1 = String2BagMapper.getBag((String)o1);
        if(o1 instanceof Multiset){
            if(o2 instanceof String)
                o2 = String2BagMapper.getBag((String)o2);
            if(o2 instanceof Multiset)
                return getSimilarity((Multiset)o1, (Multiset)o2);
        }
        return 0;
    }

    @Override
    public String getType(){
        return "bag";
    }

}
