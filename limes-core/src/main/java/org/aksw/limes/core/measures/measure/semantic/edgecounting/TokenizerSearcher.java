package org.aksw.limes.core.measures.measure.semantic.edgecounting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.TreeSet;

import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.measures.measure.semantic.edgecounting.dictionary.SemanticDictionary;

import com.opencsv.CSVWriter;

import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.POS;
import weka.core.Stopwords;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WordTokenizer;

public class TokenizerSearcher {

    public SemanticDictionary dictionary = null;

    public String[] datasets = new String[] { "Abt-Buy", "Amazon-GoogleProducts", "drugs", "DBLP-ACM", "DBLP-Scholar",
            "DBPLINKEDMDB", "OAEI2014BOOKS" };

    public String[] predicates = new String[] { "description-description", "description-description", "name-name", "title-title",
            "title-title", "title-title", "rdfs:label-rdfs:label" };

    public String[] tokenize(String[] input) {
        String[] tokens = null;
        try {
            tokens = Tokenizer.tokenize(new WordTokenizer(), input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tokens;
    }

    public IIndexWord getIIndexWord(String str) {
        if (str == null)
            return null;

        IIndexWord idxWord1 = dictionary.getIndexWord(str, POS.NOUN);
        if (idxWord1 == null) {
            idxWord1 = dictionary.getIndexWord(str, POS.ADJECTIVE);
            if (idxWord1 == null) {
                idxWord1 = dictionary.getIndexWord(str, POS.ADVERB);
                if (idxWord1 == null) {
                    idxWord1 = dictionary.getIndexWord(str, POS.VERB);
                }
            }

        }
        return idxWord1;
    }

    public void tokenize(EvaluationData data, ACache cache, String property, String tokensFileName,
            String wordNetFileName) {

        File tokensFile = new File(data.getDatasetFolder() + tokensFileName);
        if (!tokensFile.exists()) {
            try {
                tokensFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                extracted();
            }
        }
        CSVWriter csvWriterTokens = null;
        try {
            csvWriterTokens = new CSVWriter(new FileWriter(tokensFile, false));
            csvWriterTokens.writeNext(
                    new String[] { "No", "URI", "Number of predicate values", "Total time", "Average Time" }, false);
        } catch (IOException e) {
            e.printStackTrace();
            extracted();
        }
        //////////////////////////////////////////////////////////////////////////
        File wordnetFile = new File(data.getDatasetFolder() + wordNetFileName);
        if (!wordnetFile.exists()) {
            try {
                tokensFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                extracted();
            }
        }
        CSVWriter csvWriterWordnet = null;
        try {
            csvWriterWordnet = new CSVWriter(new FileWriter(wordnetFile, false));
            csvWriterWordnet.writeNext(
                    new String[] { "No", "URI", "Tokens searched", "Tokens found", "Total time", "Average Time" },
                    false);

        } catch (IOException e) {
            e.printStackTrace();
            extracted();
        }

        int j = 1;

        for (Instance instance : cache.getAllInstances()) {
            System.out.println(instance.getUri());
            long tokenizingDuration = 0;
            long searchingDuration = 0;
            int searchCounter = 0;
            int foundCounter = 0;

            TreeSet<String> values = instance.getProperty(property);
            
            for (String value : values) {
                
                if(value.equals(""))
                    continue;
                
                long bTokenizing = System.currentTimeMillis();
                String tokens[] = tokenize(new String[] { value });
                long eTokenizing = System.currentTimeMillis();

                tokenizingDuration += eTokenizing - bTokenizing;
                //////////////////////////////////////////////////////////////////////////////

                for (String token : tokens) {
                    if (!Stopwords.isStopword(token)) {
                        searchCounter++;
                        long bWord = System.currentTimeMillis();
                        IIndexWord word = this.getIIndexWord(token);
                        long eWord = System.currentTimeMillis();
                        if (word != null)
                            foundCounter++;
                        searchingDuration += eWord - bWord;
                    }
                }

            }
            
            int valuesNumber = instance.getProperty(property).size();
            double averageTokenTime = 0.0d;
            if(valuesNumber != 0)
                averageTokenTime = (double) tokenizingDuration / (double) valuesNumber;
            
            csvWriterTokens.writeNext(new String[] { String.valueOf(j), instance.getUri(), String.valueOf(valuesNumber),
                    String.valueOf(tokenizingDuration), String.valueOf(averageTokenTime) }, false);

            //////////////////////////////////////////////////////////////////////////////////

            double averageSearchTime = 0.0d;
            if (searchCounter != 0)
                averageSearchTime = (double) searchingDuration / (double) searchCounter;

            csvWriterWordnet.writeNext(new String[] { String.valueOf(j), instance.getUri(),
                    String.valueOf(searchCounter), String.valueOf(foundCounter), String.valueOf(searchingDuration),
                    String.valueOf(averageSearchTime) }, false);
            j++;
        }

        try {
            csvWriterTokens.close();
        } catch (IOException e) {
            e.printStackTrace();
            extracted();
        }

        try {
            csvWriterWordnet.close();
        } catch (IOException e) {
            e.printStackTrace();
            extracted();
        }
    }

    private void extracted() {
        throw new RuntimeException();
    }

    public void search() {

        dictionary = new SemanticDictionary();
        dictionary.exportDictionaryToFile();
        dictionary.openDictionaryFromFile();

        for (int i = 0; i < datasets.length; i++) {

            System.out.println(datasets[i]);
            String dataset = datasets[i];
            String predicate = predicates[i];
            String property1 = predicate.split("-")[0];
            String property2 = predicate.split("-")[1];

            EvaluationData data = DataSetChooser.getData(dataset);

            ACache source = data.getSourceCache();
            ACache target = data.getTargetCache();

            this.tokenize(data, source, property1, "sourcesToken.csv", "sourcesWordnet.csv");
            System.out.println("Done with source");
            this.tokenize(data, target, property2, "targetToken.csv", "targetWordnet.csv");
            System.out.println("Done with target");

        }

        dictionary.removeDictionary();
    }

    public static void main(String[] args) {
        TokenizerSearcher s = new TokenizerSearcher();
        s.search();
    }
}
