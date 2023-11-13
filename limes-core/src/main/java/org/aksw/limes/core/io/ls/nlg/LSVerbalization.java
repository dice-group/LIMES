package org.aksw.limes.core.io.ls.nlg;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.ls.nlg.de.LSVerbalizerDE;
import org.aksw.limes.core.io.ls.nlg.en.LSVerbalizerEN;

import java.util.LinkedHashMap;
import java.util.Map;

public class LSVerbalization {

    public static Map<String, String> getLSVerbalizationByLanguage(String explainLSConfigString, String linkSpecString) {
        explainLSConfigString = explainLSConfigString.replaceAll(" ", "");
        String[] split = explainLSConfigString.split(",");
        Map<String, String> languageToVerbalizationMap = new LinkedHashMap<>();

        LinkSpecification link=new LinkSpecification();
        link.readSpec(linkSpecString, 1);

        for (String language : split) {
            ILSVerbalizer verbalizer;

            switch (language) {
                case "None":
                    verbalizer = null;
                case "English":
                    verbalizer = new LSVerbalizerEN();
                    break;
                case "German":
                    verbalizer = new LSVerbalizerDE();
                    break;
                default:
                    verbalizer = linkSpecification -> "Error: Language not implemented";
            }

            LinkSpecification linkSpec=new LinkSpecification();
            linkSpec.readSpec(linkSpecString, 1.0);
            String verbalized = verbalizer.verbalize(linkSpec);
            System.out.println("LS-Verbalization-" + language + " : " + verbalized);
            languageToVerbalizationMap.put(language, verbalized);

        }
        return languageToVerbalizationMap;
    }

}
