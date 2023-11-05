package org.aksw.limes.core.io.ls.NLGLS;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.ls.NLGLS.nlgDE.LsPostProcessorDE;
import org.aksw.limes.core.io.ls.NLGLS.nlgEN.LsPostProcessor;

import java.util.HashMap;
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
            switch (language) {
                case "None":
                    break;
                case "English":
                    try {
                        languageToVerbalizationMap.put(language, new LsPostProcessor().postProcessor(link));
                    } catch (UnsupportedMLImplementationException e) {
                        languageToVerbalizationMap.put(language, "Error " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                    break;
                case "German":
                    try {
                        languageToVerbalizationMap.put(language, new LsPostProcessorDE().postProcessor(link));
                    } catch (UnsupportedMLImplementationException e) {
                        languageToVerbalizationMap.put(language, "Error " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    languageToVerbalizationMap.put(language, "Language not implemented");
            }
        }
        return languageToVerbalizationMap;
    }

}
