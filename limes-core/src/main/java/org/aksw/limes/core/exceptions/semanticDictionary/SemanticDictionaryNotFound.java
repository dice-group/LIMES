package org.aksw.limes.core.exceptions.semanticDictionary;

public class SemanticDictionaryNotFound extends RuntimeException{

    /**
     * Exception class for non-existing wordnet folder and files.
     * 
     * @author Kleanthi Georgala (georgala@informatik.uni-leipzig.de)
     * @version 1.0
     */
    private static final long serialVersionUID = -6560806432378384421L;
    
    public SemanticDictionaryNotFound(String message){
        super(message);
    }

       

}
