package org.aksw.limes.core.execution.planning.planner;

import java.util.Map;

import org.aksw.limes.core.execution.planning.plan.ExecutionPlan;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.measures.mapper.Mapper.Language;
import org.apache.log4j.Logger;

/**
 *
 * Impelements Dynamic Planner class.
 * 
 * @author ngonga
 * @author kleanthi
 */
public class DynamicPlanner extends ExecutionPlanner {
    static Logger logger = Logger.getLogger("LIMES");
    public Cache source;
    public Cache target;
    public Language lang;

    public DynamicPlanner(Cache s, Cache t) {
	source = s;
	target = t;
	lang = Language.NULL;
    }

    @Override
    public ExecutionPlan plan(LinkSpec spec) {
	// TODO Auto-generated method stub

    }

}
