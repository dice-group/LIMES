package org.aksw.limes.core.io.ls.nlg;

import org.aksw.limes.core.io.ls.LinkSpecification;

public abstract class ALSPreprocessor {

    public String leftProperty(LinkSpecification linkSpec) {
        String prop = linkSpec.getMeasure().substring(linkSpec.getMeasure().indexOf("(")+1, linkSpec.getMeasure().indexOf(","));
        if (prop.contains("#")) {
            prop = prop.substring(prop.indexOf("#") + 1);
        }

        if (prop.contains(".")) {
            prop = prop.substring(prop.indexOf(".") + 1);
        }

        if (prop.contains("_")) {
            prop = prop.replace("_", " ");
        }

        return prop;
    }

    public String rightProperty(LinkSpecification linkSpec) {
        String prop = linkSpec.getMeasure().substring(linkSpec.getMeasure().indexOf(",")+1, linkSpec.getMeasure().indexOf(")"));
        if (prop.contains("#")) {
            prop = prop.substring(prop.indexOf("#") + 1);
        }

        if (prop.contains(".")) {
            prop = prop.substring(prop.indexOf(".") + 1);
        }

        if (prop.contains("_")) {
            prop = prop.replace("_", " ");
        }

        return prop;
    }

    protected String makeFirstCharUppercaseRestLowercase(String str) {
        // Create a char array of given String
        char ch[] = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {

            // If first character of a word is found
            if (i == 0 && ch[i] != ' ' ||
                    ch[i] != ' ' && ch[i - 1] == ' ') {

                // If it is in lower-case
                if (ch[i] >= 'a' && ch[i] <= 'z') {

                    // Convert into Upper-case
                    ch[i] = (char) (ch[i] - 'a' + 'A');
                }
            } else if (ch[i] >= 'A' && ch[i] <= 'Z')

                // Convert into Lower-Case
                ch[i] = (char) (ch[i] + 'a' - 'A');
        }

        // Convert the char array to equivalent String
        String st = new String(ch);
        return st;
    }

}
