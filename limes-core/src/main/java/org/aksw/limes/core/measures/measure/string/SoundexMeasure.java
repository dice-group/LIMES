package org.aksw.limes.core.measures.measure.string;

import org.aksw.limes.core.io.cache.Instance;

public class SoundexMeasure extends StringMeasure {

    public static final int codeLength = 6;

    public static String getCode(String string) {
        char[] in = string.toUpperCase().toCharArray();
        char[] out = new char[codeLength];
        int i = 0;
        int j = 0;
        while (i < in.length && j < codeLength) {
            if (in[i] != 'A' && in[i] != 'E' && in[i] != 'I' && in[i] != 'O' && in[i] != 'U' && in[i] != 'Y'
                    && in[i] != 'H' && in[i] != 'W') {
                // consonants are added to output
                if (j == 0) {
                    out[j] = in[i];
                    j++;
                } else {
                    int t = getCode(in[i]);
                    if (t > 0) {
                        out[j] = String.valueOf(t).charAt(0);
                        j++;
                    }
                }
                // double consonants are skipped
                if (i < in.length - 1 && in[i] == in[i + 1])
                    i++;
                // double consonants with 'h' or 'w' inbetween are skipped too
                else if (i < in.length - 2 && in[i] == in[i + 2] && (in[i + 1] == 'H' || in[i + 1] == 'W'))
                    i += 2;
            }
            i++;
        }
        while (j < codeLength) {
            out[j] = '0';
            j++;
        }
        return String.valueOf(out);
    }

    private static int getCode(char x) {
        switch (x) {
        case 'B':
        case 'F':
        case 'P':
        case 'V':
            return 1;
        case 'C':
        case 'G':
        case 'J':
        case 'K':
        case 'Q':
        case 'S':
        case 'X':
        case 'Z':
            return 2;
        case 'D':
        case 'T':
            return 3;
        case 'L':
            return 4;
        case 'M':
        case 'N':
            return 5;
        case 'R':
            return 6;
        default:
            return -1;
        }
    }

    public double proximity(String s1, String s2) {
        char[] c1, c2;
        c1 = SoundexMeasure.getCode(s1).toCharArray();
        c2 = SoundexMeasure.getCode(s2).toCharArray();
        double distance = 0d;
        for (int i = 0; i < c1.length; i++)
            if (c1[i] != c2[i])
                distance += 1d;
        return (1.0d - (distance / (double) SoundexMeasure.codeLength));
    }

    @Override
    public int getPrefixLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getMidLength(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSizeFilteringThreshold(int tokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAlpha(int xTokensNumber, int yTokensNumber, double threshold) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getSimilarity(int overlap, int lengthA, int lengthB) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean computableViaOverlap() {
        return false;
    }

    @Override
    public double getSimilarity(Object object1, Object object2) {
        return proximity(object1.toString(), object2.toString());
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public double getSimilarity(Instance instance1, Instance instance2, String property1, String property2) {
        double value = 0;
        double sim = 0;
        for (String source : instance1.getProperty(property1)) {
            for (String target : instance2.getProperty(property2)) {
                sim = proximity(source, target);
                if (sim > value) {
                    value = sim;
                }
            }
        }
        return sim;
    }

    @Override
    public String getName() {
        return "ratcliff-obershelp";
    }

    @Override
    public double getRuntimeApproximation(double mappingSize) {
        return mappingSize / 1000d;
    }

}
