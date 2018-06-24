package org.aksw.limes.core.measures.measure.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.ArrayList;
import org.aksw.limes.core.util.Timer;
import org.junit.Test;

/**
 * @author Swante Scholz
 */
public class DictionaryUtilTest {
    
    @Test
    public void testDistance() {
        assertEquals(2, DictionaryUtil.damerauLevenshteinDistance("CA", "ABC"));
        assertEquals(3, DictionaryUtil.damerauLevenshteinDistance("abcdefg", "acbedgf"));
        assertEquals(2, DictionaryUtil.damerauLevenshteinDistance("abcd", "bac"));
        assertEquals(2, DictionaryUtil.damerauLevenshteinDistance("äöüß", "öäü"));
        assertEquals(3, DictionaryUtil.damerauLevenshteinDistance("abcd", "da"));
    }
    
    @Test
    public void testCorrectSpellingFast() {
        WordFrequencies wf = WordFrequencies
            .fromWordFrequencyFile(Paths.get("src/test/resources/test-freq.txt"));
        DictionaryUtil du = new DictionaryUtil(wf);
        assertEquals("verachteten", du.correctSpellingFast("veßrachtetexn"));
        assertEquals("universität", du.correctSpellingFast("universitätt"));
        assertEquals("custody", du.correctSpellingFast("cusstody"));
        assertEquals("verständnis", du.correctSpellingFast("verständnsi"));
        assertEquals("fußball", du.correctSpellingFast("fßbal"));
        assertEquals("the", du.correctSpellingFast("tze"));
    }
    
    @Test
    public void testCorrectSpellingNaive() {
        WordFrequencies wf = WordFrequencies
            .fromWordFrequencyFile(Paths.get("src/test/resources/test-freq.txt"));
        DictionaryUtil du = new DictionaryUtil(wf);
        assertEquals("universität", du.correctSpellingNaive("universitätt"));
        assertEquals("custody", du.correctSpellingNaive("cusstody"));
        assertEquals("verständnis", du.correctSpellingNaive("verständnsi"));
        assertEquals("fußball", du.correctSpellingNaive("fßbal"));
        assertEquals("the", du.correctSpellingNaive("tze"));
    }
    
    @Test
    public void testComparePerformances() {
        Timer timer = new Timer();
        WordFrequencies wfEn = WordFrequencies
            .fromWordFrequencyFile(Paths.get("src/test/resources/en-freq.txt"));
        WordFrequencies wfDe = WordFrequencies
            .fromWordFrequencyFile(Paths.get("src/test/resources/de-freq.txt"));
        WordFrequencies wf = wfEn.merge(wfDe);
        System.out
            .println("read frequency files: " + timer.checkElapsedSecondsSinceLastCheck() + "s");
        DictionaryUtil du = new DictionaryUtil(wf);
        System.out.println(
            "built spell correction data structure: " + timer.checkElapsedSecondsSinceLastCheck()
                + "s");
        String[] misspelledWords = {"trßilateraxl", "waßtermarkexd", "vaßcuolaxr", "qußarterfinaxl",
            "obßstetricaxl", "peßrfectxo", "opßerabilitxy", "deßdicationxs", "reßcurrencexs",
            "ulßceratioxn", "afßtertastxe", "daßmpenexd", "stßalkerxs", "poßconoxs", "coßrkxs",
            "lißthixa", "obßscurinxg", "meßthacrylatxe", "deßmotexd", "nußllifxy", "reßfinexs",
            "inßfarcxt", "trßickinxg", "lißquidatinxg", "coßmpactinxg", "coßrroboratxe",
            "shßufflexs", "abßortxs", "enßviexd", "dißrtiesxt", "chßinxs", "psßychosomatixc",
            "kißdnappingxs", "coßrinxg", "frßictionaxl", "rußnxt", "beßnadryxl", "reßzonxe",
            "nußrsexd", "moßnolayerxs", "plßaceholderxs", "sqßuirxm", "phßilatelxy", "reßpairablxe",
            "prßefecturaxl", "loßathsomxe", "dißsequilibriuxm", "spßringboxk", "tißberiaxn",
            "chßitxa", "alßthexa", "reßvolutionizinxg", "twßisterxs", "haßrmonizinxg", "raßncxe",
            "tußlxa", "icßebergxs", "saßckinxg", "skßinlesxs", "saßuerkrauxt", "coßstuminxg",
            "clßappexr", "seßttexe", "drßiesxt", "scßipixo", "sußbstationxs", "beßauticiaxn",
            "syßnthesexs", "unßderpaixd", "stßealthxy", "upßswinxg", "flßaunxt", "inßterrogatorxs",
            "doßubleheadexr", "mißstakinxg", "hoßoligaxn", "paßckagerxs", "daßshexr", "gußnnerxy",
            "dyßspepsixa", "veßrachtetexn", "nößtxe", "taßnzabenxd", "erßblicktexn", "taßugtxe",
            "feßstgelegtexn", "spßrudelxt", "boßstonexr", "näßchstgelegenexn", "veßrwirklichunxg",
            "unßamerikaniscxh", "unßgelöstexn", "irßregeführxt", "erßdreistexn", "mißtnichtexn",
            "ärßgerlichxe", "scßhlappxe", "obßjektivitäxt", "bußzzexr", "dußrchtränkxt"};
        ArrayList<String> correctedWords = new ArrayList<>();
        timer = new Timer();
        System.out.println("Slow naive spelling correction algorithm:");
        for (int i = 0; i < misspelledWords.length; i++) {
            String misspelledWord = misspelledWords[i];
            String correctedWord = du.correctSpellingNaive(misspelledWord);
            assertTrue(wf.containsWord(correctedWord));
            correctedWords.add(correctedWord);
            if (i % 5 == 4) {
                System.out.println((i + 1) + " " + timer.totalElapsedSecondsSinceBeginning());
            }
        }
        System.out.println("Fast symmetric deletion algorithm:");
        timer = new Timer();
        for (int i = 0; i < misspelledWords.length; i++) {
            String misspelledWord = misspelledWords[i];
            String correctedWord = du.correctSpellingFast(misspelledWord);
            assertTrue(correctedWord, wf.containsWord(correctedWord));
            assertEquals(correctedWord, correctedWords.get(i));
            if (i % 5 == 4) {
                System.out.println((i + 1) + " " + timer.totalElapsedSecondsSinceBeginning());
            }
        }
    }
    
}