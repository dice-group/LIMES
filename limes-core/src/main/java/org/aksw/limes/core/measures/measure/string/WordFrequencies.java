package org.aksw.limes.core.measures.measure.string;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;
import org.aksw.limes.core.exceptions.MissingStringMeasureResourceException;

/**
 * This class saves for all words of a language the frequency with which they occur.
 *
 * @author Swante Scholz
 */
public class WordFrequencies {
    
    private HashMap<String, Double> wordFrequencies = new HashMap<>();
    
    /**
     * @param wordFrequencies maps words to their frequencies
     */
    public WordFrequencies(HashMap<String, Double> wordFrequencies) {
        this.wordFrequencies.putAll(wordFrequencies);
    }
    
    /**
     * Scales all frequencies so that they sum up to 1.0
     */
    public void normalizeFrequencies() {
        double totalFrequency = wordFrequencies.values().stream().mapToDouble(Double::doubleValue)
            .sum();
        wordFrequencies.entrySet().forEach(it -> it.setValue(it.getValue() / totalFrequency));
    }
    
    /**
     * @param wordFrequenciesFile A file should have two columns, separated by a space, word first,
     * then frequency (as int or double). The frequencies doen't have to be normalized, as they will be normalized here.
     * @return WordFrequencies instance based on these frequencies, normalized
     */
    public static WordFrequencies fromWordFrequencyFile(Path wordFrequenciesFile) {
        try {
            HashMap<String, Double> map = new HashMap<>();
            Files.lines(wordFrequenciesFile).forEach(line -> {
                if (line.isEmpty()) {
                    return;
                }
                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    throw new RuntimeException("Invalid file format.");
                }
                String word = parts[0];
                double frequency = Double.parseDouble(parts[1]);
                map.put(word, frequency);
            });
            WordFrequencies result = new WordFrequencies(map);
            result.normalizeFrequencies();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new MissingStringMeasureResourceException(wordFrequenciesFile.toAbsolutePath().toString(), "A plain text word-frequncy file. "
                + "One line per entry: First the word, then a single space, then the relative frequence of that word in the language. "
                + "About 100k words would be great.",
                "Have a look here: https://github.com/hermitdave/FrequencyWords/tree/master/content/2016");
        }
    }
    
    /**
     * @return new WordFrequencies instance, the result of a merge of this and the other instance,
     * normalized
     */
    public WordFrequencies merge(WordFrequencies other) {
        HashMap<String, Double> resultMap = new HashMap<>();
        resultMap.putAll(this.wordFrequencies);
        for (String otherWord : other.wordFrequencies.keySet()) {
            if (!resultMap.containsKey(otherWord)) {
                resultMap.put(otherWord, 0.0);
            }
            resultMap.put(otherWord, resultMap.get(otherWord) + other.get(otherWord));
        }
        WordFrequencies result = new WordFrequencies(resultMap);
        result.normalizeFrequencies();
        return result;
    }
    
    public Set<String> wordSet() {
        return wordFrequencies.keySet();
    }
    
    public boolean containsWord(String word) {
        return wordFrequencies.containsKey(word);
    }
    
    public double get(String word) {
        return wordFrequencies.get(word);
    }
}
