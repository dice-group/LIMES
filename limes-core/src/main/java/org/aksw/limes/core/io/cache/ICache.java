package org.aksw.limes.core.io.cache;

import java.util.ArrayList;

public interface ICache {

    void addTriple(String string, String string2, String string3);

    Instance getInstance(String string);

    int size();

    ArrayList<String> getAllUris();

}
