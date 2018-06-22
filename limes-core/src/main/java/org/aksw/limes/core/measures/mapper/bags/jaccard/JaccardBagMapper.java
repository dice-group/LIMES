package org.aksw.limes.core.measures.mapper.bags.jaccard;

import com.google.common.collect.Multiset;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.bags.IBagMapper;
import org.aksw.limes.core.measures.measure.bags.IBagMeasure;
import org.aksw.limes.core.measures.measure.bags.JaccardBagMeasure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This bag mapper uses the same algorithm as FastNGramMapper.
 *
 * In general, the bounds used for filtering only apply to jaccard
 * similarity for sets.
 *
 * But we can easily transform every bag to a set by the following
 * transformation rule:
 *
 * Let X be n times contained in the bag, then we add
 *  X_1, X_2, X_3, ..., X_n to the corresponding set.
 *
 *
 * @author Cedric Richter
 */
public class JaccardBagMapper implements IBagMapper {

    private IBagMeasure sim = new JaccardBagMeasure();

    @Override
    public <T> AMapping getMapping(Map<String, Multiset<T>> source, Map<String, Multiset<T>> target, double threshold) {

        Index<T> index = new Index<>();
        double kappa = (1 + threshold)/threshold;
        Set<KeyedBag<T>> sourceSet = fromMap(source);
        Set<KeyedBag<T>> targetSet = fromMap(target);

        AMapping result = MappingFactory.createDefaultMapping();

        for(KeyedBag<T> t: targetSet)
            index.addBag(t);

        for(KeyedBag<T> s : sourceSet){
            Set<Integer> allSizes = index.getAllSizes();

            double sourceSize = (double)s.getBag().size();
            for (int size = (int) Math.ceil(sourceSize * threshold); size <= (int) Math
                    .floor(sourceSize / threshold); size++) {
                if(allSizes.contains(size)){
                    Map<T, Set<KeyedBag<T>>> bagsOfSize = index.getBags(size);
                    Map<KeyedBag<T>, Integer> countMap = new HashMap<>();

                    for(T e: s.getBag().elementSet()){
                        if(bagsOfSize.containsKey(e)) {
                            Set<KeyedBag<T>> candidates = bagsOfSize.get(e);
                            for(KeyedBag<T> candidate : candidates) {
                                if(!countMap.containsKey(candidate)){
                                    countMap.putIfAbsent(candidate, 0);
                                }
                                countMap.put(candidate, countMap.get(candidate)
                                                        + Math.min(candidate.getBag().count(e), s.getBag().count(e)));
                            }
                        }
                    }

                    for(KeyedBag<T> candidate: countMap.keySet()){
                        double count = (double) countMap.get(candidate);
                        if(kappa * count >= (sourceSize + size)){
                            double similarity = sim.getSimilarity(s.getBag(), candidate.getBag());
                            if(similarity >= threshold){
                                result.add(s.getKey(), candidate.getKey(), similarity);
                            }
                        }
                    }

                }
            }

        }



        return result;
    }

    private <T> Set<KeyedBag<T>> fromMap(Map<String, Multiset<T>> map){
        Set<KeyedBag<T>> output = new HashSet<>();

        for(Map.Entry<String, Multiset<T>> e: map.entrySet()){
            output.add(new KeyedBag<>(e.getKey(), e.getValue()));
        }

        return output;
    }

    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression, double threshold) {
        return null;
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 0;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 0;
    }

    @Override
    public String getName() {
        return "bag_jaccard";
    }
}
