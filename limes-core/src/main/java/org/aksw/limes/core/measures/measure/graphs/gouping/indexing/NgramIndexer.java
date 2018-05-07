package org.aksw.limes.core.measures.measure.graphs.gouping.indexing;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

public class NgramIndexer {

    private int window = 3;
    private Multimap<String, String> index;
    private Multimap<String, String> ngramIndex;

    public NgramIndexer(int window){
        this.window = window;
        this.index = HashMultimap.create();
        this.ngramIndex = HashMultimap.create();
    }

    private Set<String> ngrams(String s){
        Set<String> ngrams = new HashSet<>();
        for(int i = 0; i < s.length() - window+1; i++){
            int j = i + window;
            ngrams.add(s.substring(i, j));
        }
        return ngrams;
    }

    public void index(String s){
        for(String ngram: ngrams(s)){
            index.put(ngram, s);
            ngramIndex.put(s, ngram);
        }
    }

    public void index(Set<String> strings){
        for(String s: strings)index(s);
    }

    public Map<String, NgramStats> getOverlaps(String s){
        Map<String, NgramStats> map = new HashMap<>();
        Set<String> ngrams = ngrams(s);
        for(String ngram: ngrams){
            for(String found: index.get(ngram)){
                if(!map.containsKey(found)){
                    NgramStats stats = new NgramStats();
                    Collection<String> foundNgrams = ngramIndex.get(found);
                    for(String foundNgram: foundNgrams){
                        if(ngrams.contains(foundNgram))
                            stats.commonCount++;
                        else
                            stats.uncommonCount++;
                    }
                    stats.srcNgramsCount = ngrams.size();
                    map.put(found, stats);
                }
            }
        }
        return map;
    }

    public class NgramStats{

        private int commonCount = 0;
        private int uncommonCount = 0;
        private int srcNgramsCount = 0;

        public int getCommonCount() {
            return commonCount;
        }

        public int getUncommonCount() {
            return uncommonCount;
        }

        public int getSrcNgramsCount() {
            return srcNgramsCount;
        }
    }

}
