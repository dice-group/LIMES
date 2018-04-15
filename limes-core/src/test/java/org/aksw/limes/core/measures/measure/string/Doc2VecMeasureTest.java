package org.aksw.limes.core.measures.measure.string;

import static org.junit.Assert.*;

import org.aksw.limes.core.measures.measure.AMeasure;
import org.junit.Test;

public class Doc2VecMeasureTest {

  @Test
  public void testMeasure() {
    AMeasure measure = new Doc2VecMeasure(Doc2VecMeasure.DEFAULT_PRECOMPUTED_VECTORS_FILE_PATH);
    String a = "You eat an apple";
    String b = "You are eating an apple";
    String c = "That man eats an apple";
    String d = "That man is tall";
    String e = "That woman is tall";
    String f = "That woman is small";
    String g = "The domestic cat (Felis silvestris catus or Felis catus)[1][5] is a small, typically furry, carnivorous mammal. They are often called house cats[6] when kept as indoor pets or simply cats when there is no need to distinguish them from other felids and felines. They are often valued by humans for companionship and for their ability to hunt vermin. There are more than seventy cat breeds recognized by various cat registries.";
    String h = "The domestic dog (Canis lupus familiaris or Canis familiaris)[4] is a member of the genus Canis (canines), which forms part of the wolf-like canids,[5] and is the most widely abundant terrestrial carnivore.[6][7][8][9][10] The dog and the extant gray wolf are sister taxa[11][12][13] as modern wolves are not closely related to the wolves that were first domesticated,[12][13] which implies that the direct ancestor of the dog is extinct.[14] The dog was the first species to be domesticated[13][15] and has been selectively bred over millennia for various behaviors, sensory capabilities, and physical attributes.[16]";
    String ii = "Philosophy (from Greek φιλοσοφία, philosophia, literally \"love of wisdom\"[1][2][3][4]) is the study of general and fundamental problems concerning matters such as existence, knowledge, values, reason, mind, and language.[5][6] The term was probably coined by Pythagoras (c. 570–495 BCE). Philosophical methods include questioning, critical discussion, rational argument, and systematic presentation.[7][8] Classic philosophical questions include: Is it possible to know anything and to prove it?[9][10][11] What is most real? Philosophers also pose more practical and concrete questions such as: Is there a best way to live? Is it better to be just or unjust (if one can get away with it)?[12] Do humans have free will?[13]";
    String[] strings = new String[]{a, b, c, d, e, f, g, h, ii};
    for (int i = 0; i < strings.length - 1; i++) {
      for (int j = i + 1; j < strings.length; j++) {
        String x = strings[i];
        String y = strings[j];
        System.out.println(measure.getSimilarity(x, y) + ": " + x + " VS " + y);
      }
    }
    assertTrue(measure.getSimilarity(a, b) > measure.getSimilarity(b, d));
    assertTrue(measure.getSimilarity(g, h) > measure.getSimilarity(h, ii));
  }
}

