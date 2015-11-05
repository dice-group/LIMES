package org.aksw.limes.core.io.mapping;

import java.util.HashMap;

public abstract class Mapping implements IMapping{

    public HashMap<String, HashMap<String, Double>> map;
    public int size;

    public abstract double getSimilarity(String key, String value);

    public abstract void add(String key, String value, double sim);

    public abstract void add(String key, HashMap<String, Double> hashMap);

    public abstract int size();

    public abstract Mapping reverseSourceTarget();

    public abstract int getNumberofMappings();

    public abstract boolean contains(String key, String value);
}
