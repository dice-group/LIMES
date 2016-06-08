package org.aksw.limes.core.ml.algorithm.wombat;

public class LinkEntropy implements Comparable<LinkEntropy>{
    
    protected String sourceUri;
    protected String targetUri;
    protected int entropy;
    
    
    public LinkEntropy(String sourceUri, String targetUri, int entropy) {
        super();
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
        this.entropy = entropy;
    }
    
    public String getSourceUri() {
        return sourceUri;
    }
    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }
    public String getTargetUri() {
        return targetUri;
    }
    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }
    public int getEntropy() {
        return entropy;
    }
    public void setEntropy(int entropy) {
        this.entropy = entropy;
    }

    @Override
    public int compareTo(LinkEntropy o) {
        return (int) (entropy - o.entropy);
    }
    
    
}
