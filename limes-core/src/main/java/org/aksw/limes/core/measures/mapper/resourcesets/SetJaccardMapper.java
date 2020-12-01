package org.aksw.limes.core.measures.mapper.resourcesets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.mapper.pointsets.PropertyFetcher;

/**
 * @author Kevin Dreßler
 * @since 1.0
 */
public class SetJaccardMapper extends AMapper {

    public static class SetSizeQuicksort {

        /**
         * Public class method to quicksort lists
         *
         * @param values
         *         list of sets of strings
         */
        public static void sort(List<Set<String>> values) {
            if (values == null || values.size() == 0)
                return;
            quicksort(values, 0, values.size() - 1);
        }

        /**
         * Quicksort class method
         *
         * @param strings
         *         list of sets of strings
         * @param low
         *         low index
         * @param high
         *         high index
         */
        private static void quicksort(List<Set<String>> strings, int low, int high) {
            int i = low, j = high;
            int pivot = strings.get(low + (high - low) / 2).size();
            while (i <= j) {
                while (strings.get(i).size() < pivot)
                    i++;
                while (strings.get(j).size() > pivot)
                    j--;
                if (i <= j) {
                    exchange(strings, i, j);
                    i++;
                    j--;
                }
            }
            if (low < j)
                quicksort(strings, low, j);
            if (i < high)
                quicksort(strings, i, high);
        }

        /**
         * Swap elements
         *
         * @param strings
         *         list of sets of strings
         * @param i
         *         index of element a to be swapped
         * @param j
         *         index of element b to be swapped
         */
        private static void exchange(List<Set<String>> strings, int i, int j) {
            Set<String> temp = strings.get(i);
            strings.set(i, strings.get(j));
            strings.set(j, temp);
        }
    }

    public static class NamedHashSet<E> implements Set<E> {
        private String uri;
        private Set<E> decoratedSet;

        public NamedHashSet(Set<E> set, String uri) {
            this.decoratedSet = set;
            this.uri = uri;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        @Override
        public int size() {
            return decoratedSet.size();
        }

        @Override
        public boolean isEmpty() {
            return decoratedSet.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return decoratedSet.contains(o);
        }

        @Override
        public Iterator<E> iterator() {
            return decoratedSet.iterator();
        }

        @Override
        public Object[] toArray() {
            return decoratedSet.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return decoratedSet.toArray(a);
        }

        @Override
        public boolean add(E e) {
            return decoratedSet.add(e);
        }

        @Override
        public boolean remove(Object o) {
            return decoratedSet.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return decoratedSet.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return decoratedSet.addAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return decoratedSet.retainAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return decoratedSet.removeAll(c);
        }

        @Override
        public void clear() {
            decoratedSet.clear();
        }
    }

    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression, double threshold) {
        // Phase 0: Initialize
        List<String> properties;
        List<Set<String>> sourceIndex, targetIndex, small, big;
        properties = PropertyFetcher.getProperties(expression, threshold);
        sourceIndex = buildIndex(source, properties.get(0));
        targetIndex = buildIndex(target, properties.get(1));
        // Swap if necessary
        if (sourceIndex.size() > targetIndex.size()) {
            big = sourceIndex;
            small = targetIndex;
        } else {
            big = targetIndex;
            small = sourceIndex;
        }
        List<List<Set<String>>> possibleMatches = getPossibleMatches(small, big, threshold);
        AMapping result = MappingFactory.createDefaultMapping();
        for (int i = 0; i < possibleMatches.size(); i+=2) {
            List<Set<String>> s = possibleMatches.get(i);
            List<Set<String>> t = possibleMatches.get(i+1);
            for (Set<String> setS : s) {
                for (Set<String> setT : t) {
                    double score = fastSimCheck(setS, setT, threshold);
                    if (score >= threshold) {
                        result.add(((NamedHashSet<String>)setS).getUri(), ((NamedHashSet<String>)setT).getUri(), score);
                    }
                }
            }
        }
        return result;
    }

    private List<Set<String>> buildIndex(ACache c, String p) {
        List<Set<String>> index = new ArrayList<>();
        // Phase 1: Build index
        for (String uri : c.getAllUris()) {
            Set<String> values = new NamedHashSet<>(c.getInstance(uri).getProperty(p), uri);
            if (values.size() > 0) {
                index.add(values);
            }
        }
        SetSizeQuicksort.sort(index);
        return index;
    }

    private List<List<Set<String>>> getPossibleMatches(List<Set<String>> small, List<Set<String>> big, double threshold) {
        // Phase 2: Iterate over smaller KB
        int startBig = 0;
        int oldSize = small.get(0).size();
        List<Set<String>> tempSource = new ArrayList<>();
        List<Set<String>> tempTarget = new ArrayList<>();
        List<List<Set<String>>> possibleMatches = new ArrayList<>();
        for (Set<String> values : small) {
            // add sets of same size in small to temp
            if (values.size() > oldSize) {
                // Phase 2.1: Use boundaries to lookup possible matches
                int loB, hiB, nextStartSize;
                boolean nextStartSizeNotFixedYet = true;
                loB = (int) Math.ceil((double) oldSize * threshold);
                hiB = (int) Math.floor((double) oldSize / threshold);
                nextStartSize = (int) Math.ceil(threshold * (double) values.size());
                for (int j = startBig; j < big.size(); j++) {
                    Set<String> targetValues = big.get(j);
                    if (targetValues.size() < loB) {
                        continue;
                    }
                    if (targetValues.size() > hiB) {
                        break;
                    }
                    if (nextStartSizeNotFixedYet && targetValues.size() >= nextStartSize) {
                        nextStartSizeNotFixedYet = false;
                        startBig = (startBig == 0) ? 0 : j - 1;
                    }
                    tempTarget.add(targetValues);
                }
                // Phase 2.2: Schedule temp lists for similarity check
                possibleMatches.add(tempSource);
                possibleMatches.add(tempTarget);
                // Reset temps
                tempSource = new ArrayList<>();
                oldSize = values.size();
            }
            tempSource.add(values);
        }
        int loB = (int) Math.ceil((double) oldSize * threshold);
        int hiB = (int) Math.floor((double) oldSize / threshold);
        for (int j = startBig; j < big.size(); j++) {
            Set<String> targetValues = big.get(j);
            if (targetValues.size() < loB) {
                continue;
            }
            if (targetValues.size() > hiB) {
                break;
            }
            tempTarget.add(targetValues);
        }
        possibleMatches.add(tempSource);
        possibleMatches.add(tempTarget);
        return possibleMatches;
    }

    private double fastSimCheck (Set<String> s, Set<String> t, double threshold) {
        int matches = 0;
        int mismatches = 0;
        int maxMismatches = (int) Math.floor((Math.min(s.size(), t.size()) -
                threshold * Math.max(s.size(), t.size())) / (1 + threshold));
        for (String x : s) {
            if (t.contains(x)) {
                matches++;
            } else {
                mismatches++;
                if (mismatches > maxMismatches)
                    return 0d;
            }
        }
        return matches / ((double) s.size() + (double) t.size() - (double) matches);
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public String getName() {
        return "set_jaccard";
    }

}
