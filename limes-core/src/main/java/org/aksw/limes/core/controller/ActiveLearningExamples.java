/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.controller;

import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.mapping.AMapping;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 */
public class ActiveLearningExamples implements Iterable<ActiveLearningExamples.Example> {

    private AMapping exampleMap;
    private ACache sourceCache;
    private ACache targetCache;

    public ActiveLearningExamples(AMapping exampleMap, ACache sourceCache, ACache targetCache) {
        this.exampleMap = exampleMap;
        this.sourceCache = sourceCache;
        this.targetCache = targetCache;
    }

    @Override
    public Iterator<Example> iterator() {
        return new ExampleIterator();
    }

    public AMapping getExampleMap() {
        return exampleMap;
    }

    public class Example {
        private Instance source;
        private Instance target;

        public Example(String source, String target) {
            this.source = sourceCache.getInstance(source);
            this.target = targetCache.getInstance(target);
        }

        public Instance getSource() {
            return source;
        }

        public Instance getTarget() {
            return target;
        }

        public void classify(double score) {
            if (exampleMap.contains(source.getUri(), target.getUri())) {
                exampleMap.getMap().get(source.getUri()).put(target.getUri(), score);
            }
        }

    }

    public class ExampleIterator implements Iterator<Example> {

        Map<String, Map<String, Double>> map;
        private Iterator<String> sourceIt;
        private Iterator<String> targetIt;
        private String source;

        private ExampleIterator() {
            Map<String, ? extends Map<String, Double>> ogMap = exampleMap.getMap();
            // use LinkedHashMap for stable iteration order
            map = new LinkedHashMap<>(ogMap.size(), 1);
            ogMap.forEach((s, sMap) -> {
                map.put(s, new LinkedHashMap<>(sMap.size(), 1));
                sMap.forEach((t, score) -> map.get(s).put(t,score));
            });
            sourceIt = map.keySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return sourceIt.hasNext() || (targetIt != null && targetIt.hasNext()) ;
        }

        @Override
        public Example next() {
            if (source == null) {
                if (sourceIt.hasNext()) {
                    source = sourceIt.next();
                    targetIt = map.get(source).keySet().iterator();
                } else {
                    throw new NoSuchElementException();
                }
            }
            while (!targetIt.hasNext()) {
                if (!sourceIt.hasNext()) {
                    throw new NoSuchElementException();
                } else {
                    source = sourceIt.next();
                }
                targetIt = map.get(source).keySet().iterator();
            }
            return new Example(source, targetIt.next());
        }
    }
}
