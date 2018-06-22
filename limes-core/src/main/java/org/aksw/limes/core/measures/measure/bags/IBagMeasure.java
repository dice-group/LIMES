package org.aksw.limes.core.measures.measure.bags;

import com.google.common.collect.Multiset;
import org.aksw.limes.core.measures.measure.IMeasure;

public interface IBagMeasure extends IMeasure {

    public <T> double getSimilarity(Multiset<T> A, Multiset<T> B);

}
