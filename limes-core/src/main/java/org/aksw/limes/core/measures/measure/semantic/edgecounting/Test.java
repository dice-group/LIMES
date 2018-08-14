package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.HypernymPathsFinder;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.utils.ShortestPathFinder;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;

public class Test {

    public static void main(String args[]){

        SemanticDictionary dictionary = new SemanticDictionary();
        dictionary = new SemanticDictionary();
        dictionary.exportDictionaryToFile();
        dictionary.openDictionaryFromFile();

        long[] duration1 = new long[3];
        for (int i = 0; i < 3; i++) {
            duration1[i] = 0l;
        }
        long[] duration2 = new long[3];
        for (int i = 0; i < 3; i++) {
            duration2[i] = 0l;
        }

        for (int i = 0; i < 3; i++) {
            System.out.println("Iteration: " + (i + 1));
            for (POS pos : POS.values()) {
                System.out.println("POS: " + pos.name());

                Iterator<ISynset> iterator1 = dictionary.getDictionary().getSynsetIterator(pos);
                while (iterator1.hasNext()) {
                    ISynset synset1 = iterator1.next();

                    Iterator<ISynset> iterator2 = dictionary.getDictionary().getSynsetIterator(pos);
                    while (iterator2.hasNext()) {
                        ISynset synset2 = iterator2.next();
                        System.out.println("========================================");

                        // first method
                        long begin1 = System.currentTimeMillis();
                        ArrayList<ArrayList<ISynsetID>> paths1 = HypernymPathsFinder.getHypernymPaths(dictionary,
                                synset1);
                        ArrayList<ArrayList<ISynsetID>> paths2 = HypernymPathsFinder.getHypernymPaths(dictionary,
                                synset2);
                        int length1 = ShortestPathFinder.shortestPath(paths1, paths2);
                        long end1 = System.currentTimeMillis();
                        duration1[i] += end1 - begin1;
                        System.out.println("........");
                        // second method
                        long begin2 = System.currentTimeMillis();
                        int length2 = ShortestPathFinder.shortestPath(synset1, synset2, dictionary);
                        long end2 = System.currentTimeMillis();
                        duration2[i] += end2 - begin2;

                        System.out.println(synset1.getID() + " " + synset2.getID());
                        System.out.println(length1 + " " + length2);
                        if(length1 != length2)
                            System.exit(1);

                    }

                }
            }
        }

        long avg1 = 0l;
        long avg2 = 0l;
        for (int i = 0; i < 3; i++) {
            System.out.println("Duration1: " + duration1[i]);
            avg1 += duration1[i];
            System.out.println("Duration2: " + duration2[i]);
            avg2 += duration2[i];
        }

        System.out.println("Avg Duration1: " + (float) (avg1 / 3.0));
        System.out.println("Avg Duration2: " + (float) (avg2 / 3.0));

        dictionary.removeDictionary();
    
    }
}
