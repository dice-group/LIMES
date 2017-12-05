package org.aksw.limes.core.ml.algorithm.ligon;

public class ConfusionMatrix {
    
    protected double [][] c = new double[2][2];
    
    public ConfusionMatrix(double[][] c) {
        super();
        this.c = c;
    }

    ConfusionMatrix(){
        this(0.5);
    }
    
    ConfusionMatrix(double x){
        for(int i = 0 ; i < 2 ; i++){
            for(int j = 0 ; j < 2 ; j++){
                c[i][j] = x;
            }
        }
    }
    
    public void setRightClassifiedPositiveExamplesCount(int x){
        c[0][0] = x;
    }
    
    public void incrementRightClassifiedPositiveExamplesCount(){
        c[0][0]++;
    }
    
    public double getTruePositiveProbability(){
        return c[0][0] / sumConfusionMatriceEntries();
    }
    
    public double getRightClassifiedPositiveExamplesCount(){
        return c[0][0];
    }
    
    public void setRightClassifiedNegativeExamplesCount(int x){
        c[0][1] = x;
    }
    
    public void incrementRightClassifiedNegativeExamplesCount(){
        c[0][1]++;
    }

    public double getRightClassifiedNegativeExamplesCount(){
        return c[0][1];
    }
    
    public double getTrueNegativeProbability(){
        return c[0][1] / sumConfusionMatriceEntries();
    }

    public void setWrongClassifiedPositiveExamplesCount(int x){
        c[1][0] = x;
    }
    
    public void incrementWrongClassifiedPositiveExamplesCount(){
        c[1][0]++;
    }
    
    public double getWrongClassifiedPositiveExamplesCount(){
        return c[1][0];
    }
    
    public double getFalsePositiveProbability(){
        return c[1][0] / sumConfusionMatriceEntries();
    }
    
    public void setWrongClassifiednegativeExamplesCount(int x){
        c[1][1] = x;
    }
    
    public void incrementWrongClassifiedNegativeExamplesCount(){
        c[1][1]++;
    }
    
    public double getWrongClassifiednegativeExamplesCount(){
        return c[1][1];
    }
    
    public double getFalseNegativeProbability(){
        return c[1][1] / sumConfusionMatriceEntries();
    }
    
    private double sumConfusionMatriceEntries(){
        double sum = 0.0d;
        for(int i = 0 ; i < 2 ; i++){
            for(int j = 0 ; j < 2 ; j++){
                sum += c[i][j];
            }
        }
        return sum;
    }

}
