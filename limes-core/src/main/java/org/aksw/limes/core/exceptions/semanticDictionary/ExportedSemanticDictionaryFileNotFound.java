package org.aksw.limes.core.exceptions.semanticDictionary;

public class ExportedSemanticDictionaryFileNotFound extends RuntimeException {

    /**
     * Exception class for non-existing exported wordnet file.
     * 
     * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
     * @version 1.0
     */
    private static final long serialVersionUID = -1807089848876365171L;

    public ExportedSemanticDictionaryFileNotFound(String message) {
        super(message);
    }
}
