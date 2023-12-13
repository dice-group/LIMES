package org.aksw.limes.core.io.ls.nlg;

import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.ls.nlg.de.LSVerbalizerDE;
import org.aksw.limes.core.io.ls.nlg.en.LSVerbalizerEN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class LSVerbalization {

    private static Logger logger = LoggerFactory.getLogger(LSVerbalization.class.getName());


    /**
     * Returns the link specification verbalization for the given languages and link specification
     * @param explainLSConfigString A string consisting of one or more languages, seperated by commas
     */
    public static Map<String, String> getLSVerbalizationByLanguage(String explainLSConfigString, LinkSpecification linkSpec) {
        explainLSConfigString = explainLSConfigString.replaceAll(" ", "");
        String[] split = explainLSConfigString.split(",");
        Map<String, String> languageToVerbalizationMap = new LinkedHashMap<>();

        for (String language : split) {
            ILSVerbalizer verbalizer;

            switch (language) {
                case "None":
                    verbalizer = null;
                    break;
                case "English":
                    verbalizer = new LSVerbalizerEN();
                    break;
                case "German":
                    verbalizer = new LSVerbalizerDE();
                    break;
                default:
                    verbalizer = linkSpecification -> "Error: Language not implemented";
                    break;
            }

            if(verbalizer != null){
                String verbalized = verbalizer.verbalize(linkSpec);
                logger.info("LS-Verbalization-" + language + " : " + verbalized);
                languageToVerbalizationMap.put(language, verbalized);
            }

        }
        return languageToVerbalizationMap;
    }

}
