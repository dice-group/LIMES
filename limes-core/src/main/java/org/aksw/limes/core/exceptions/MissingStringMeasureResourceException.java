package org.aksw.limes.core.exceptions;

/**
 * RuntimeException that is thrown whenever a certain resource that is needed for the string measures
 * is missing.
 * The message should contain the location where the resource should be, and what the resource should
 * look like (or where to get it from the internet).
 *
 * @author Swante Scholz
 */
public class MissingStringMeasureResourceException extends RuntimeException {
    
    /**
     * Creates a RuntimeException indicating that a required resource for the string measures
     * (e.g. dicitonaries, pre-computed word-vectors, etc.) is missing.
     *
     * @param pathToResource Where the resource should be located
     * @param resourceFormatDescription What the resource should look like
     * @param howToGetIt How to create or where to download the resource
     */
    public MissingStringMeasureResourceException(String pathToResource, String resourceFormatDescription, String howToGetIt) {
        super(String.format(
            "String measure resource %s is missing"
            + "\nFormat description: %s"
            + "\nHow to get it: %s", pathToResource, resourceFormatDescription, howToGetIt));
    }
    
}
