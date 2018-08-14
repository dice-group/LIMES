package org.aksw.limes.core.measures.measure.semantic.edgecounting.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.preprocessing.DB.DBImplementation;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.item.SynsetID;

public class MinMaxDepthFinder {

    static Map<ISynsetID, int[]> depths = new HashMap<>();
    public static long duration = 0l;
    
    public static int[] getMinMaxDepth(String synsetID, SemanticDictionary dictionary, DBImplementation db) {
        long begin = System.currentTimeMillis();
        int[] lengths = new int[3];
        POS[] posTags = new POS[1];
        posTags[0] = SynsetID.parseSynsetID(synsetID).getPOS();

        lengths = getLengths(synsetID, posTags, dictionary, db);

        long end = System.currentTimeMillis();
        duration = end - begin;
        
        return lengths;
    }

    public static List<ISynsetID> findRoots(POS pos, SemanticDictionary dictionary) {
        Iterator<ISynset> iterator = dictionary.getDictionary().getSynsetIterator(pos);

        List<ISynsetID> rootCandidates = new ArrayList<>();
        depths = new HashMap<>();
        while (iterator.hasNext()) {
            ISynset synset = iterator.next();
            if (synset.getRelatedSynsets(Pointer.HYPERNYM).isEmpty()
                    && synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE).isEmpty()) {
                rootCandidates.add(synset.getID());
            }
            depths.put(synset.getID(), new int[] { -1, -1, 0 });
        }
        if (rootCandidates.isEmpty()) {
            System.err.println("Couldn't find a root!");
            return new ArrayList<ISynsetID>();
        }
        return rootCandidates;

    }

    public static int[] getLengths(String synsetID, POS[] posTags, SemanticDictionary dictionary, DBImplementation db) {

        for (POS pos : posTags) {

            List<ISynsetID> rootCandidates = findRoots(pos, dictionary);

            Queue<ISynsetID> temp;
            Queue<ISynsetID> currentQueue = new LinkedList<>();
            Queue<ISynsetID> nextQueue = new LinkedList<>();
            nextQueue.addAll(rootCandidates);
            int depth;
            
            if (rootCandidates.size() > 1) {
                depth = 0;
            } else {
                depth = -1;
            }

            ISynsetID id;
            ISynset synset;
            int values[];
            boolean addToQueue;
            while (!nextQueue.isEmpty()) {

                ++depth;
                temp = currentQueue;
                currentQueue = nextQueue;
                nextQueue = temp;

                while (!currentQueue.isEmpty()) {

                    id = currentQueue.poll();

                    values = depths.get(id);
                    if (values[0] < 0) {
                        // We found this node the first time
                        addToQueue = true;
                        values[0] = depth;
                        values[1] = depth;
                    } else if (values[1] < depth) {
                        // We found a new max depth and have to update all
                        // children
                        // of this node :(
                        addToQueue = true;
                        values[1] = depth;
                    } else {
                        // We already know this node and don't have to update it
                        // :)
                        addToQueue = false;
                    }
                    // just for logging: we visited this node
                    ++values[2];
                    // If we want to visit the children as well
                    if (addToQueue) {
                        synset = dictionary.getSynset(id);
                        List<ISynsetID> hypo = synset.getRelatedSynsets(Pointer.HYPONYM);
                        List<ISynsetID> hypoInt = synset.getRelatedSynsets(Pointer.HYPONYM_INSTANCE);
                        nextQueue.addAll(hypo);
                        nextQueue.addAll(hypoInt);
                    }

                }
            }
            if (synsetID != null && posTags.length == 1) {
                return depths.get(SynsetID.parseSynsetID(synsetID));
            } else {
                for (ISynsetID sid : depths.keySet()) {
                    values = depths.get(sid);
                    db.addMinDepth(sid.toString(), values[0]);
                    db.addMaxDepth(sid.toString(), values[1]);
                }
            }

        }

        return null;
    }

}