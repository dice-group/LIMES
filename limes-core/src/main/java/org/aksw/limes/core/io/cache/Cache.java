package org.aksw.limes.core.io.cache;

import java.util.List;

public abstract class Cache implements ICache{

    public abstract List<Instance> getAllInstances();

	public abstract void addInstance(Instance i);

}
