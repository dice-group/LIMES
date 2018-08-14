package org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class SemanticDictionary {
    private IRAMDictionary dictionary = null;
    private String wordNetFolder = System.getProperty("user.dir") + "/src/main/resources/wordnet/dict/";
    private String exFile = wordNetFolder + "JWI_Export_.wn";
    private static final Logger logger = LoggerFactory.getLogger(SemanticDictionary.class);

    public void exportDictionaryToFile() {
        File dictionaryFolder = new File(wordNetFolder);
        
        if (!dictionaryFolder.exists()) {
            logger.error("Wordnet dictionary folder doesn't exist. Can't do anything");
            logger.error(
                    "Please read the instructions in the README.md file on how to download the worndet database files.");
            throw new RuntimeException();
        } else {
            File dicFile = new File(exFile);
            if (!dicFile.exists()) {
                logger.info("No exported wordnet file is found. Creating one..");
                dictionary = new RAMDictionary(dictionaryFolder);
                dictionary.setLoadPolicy(ILoadPolicy.IMMEDIATE_LOAD);
                logger.info("Loaded dictionary into memory. Now exporting it to file.");
                try {
                    dictionary.open();
                    dictionary.export(new FileOutputStream(dicFile));
                    // logger.info("Export is " + (exFile.length() / 1048576) +
                    // " MB");
                } catch (IOException e1) {
                    logger.error("Couldn't open wordnet dictionary. Exiting..");
                    e1.printStackTrace();
                    throw new RuntimeException();
                } finally {
                    removeDictionary();
                }
            }
        }

    }

    public void removeDictionary() {
        if (dictionary != null) {
            dictionary.close();
            dictionary = null;
        }
    }

    public void openDictionaryFromFile() {
        File dicFile = new File(exFile);
        
        if (!dicFile.exists()) {
            logger.error("No exported wordnet file is found. Exiting..");
            logger.error("Please execute the exportDictionaryToFile function first.");
            logger.error(
                    "Please read the instructions in the README.md file on how to download the worndet database files.");
            throw new RuntimeException();
        } else {
            if (dictionary == null) {
                dictionary = new RAMDictionary(dicFile);
                try {
                    dictionary.open();
                } catch (IOException e2) {
                    logger.error("Couldn't open wordnet dictionary. Exiting..");
                    e2.printStackTrace();
                    throw new RuntimeException();
                }
            }
        }

    }

    public IWord getWord(IWordID wordID) {
        return dictionary.getWord(wordID);
    }

    public IIndexWord getIndexWord(String str, POS pos) {
        return dictionary.getIndexWord(str, pos);
    }

    public ISynset getSynset(ISynsetID hypernymId) {
        return dictionary.getSynset(hypernymId);
    }

    public IRAMDictionary getDictionary() {
        return dictionary;
    }
}
