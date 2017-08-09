package org.aksw.limes.core.ml.algorithm.ligon;

public class ConfusionMatricex {
    
    protected double [][] c = new double[2][2];
    
    ConfusionMatricex(){
        this(0.5);
    }
    
    ConfusionMatricex(double x){
        for(int i = 0 ; i < 2 ; i++){
            for(int j = 0 ; j < 2 ; j++){
                c[i][j] = x;
            }
        }
    }
    
    public void setCountOfRightClassifiedPositiveExamples(int x){
        c[0][0] = x;
    }
    
    public double getProbabilityOfRightClassifiedPositiveExamples(){
        return c[0][0] / sumConfusionMatriceEntries();
    }
    
    public double getCountOfRightClassifiedPositiveExamples(){
        return c[0][0];
    }
    
    public void setCountOfRightClassifiedNegativeExamples(int x){
        c[0][1] = x;
    }

    public double getCountOfRightClassifiedNegativeExamples(){
        return c[0][1];
    }
    
    public double getProbabilityOfRightClassifiedNegativeExamples(){
        return c[0][1] / sumConfusionMatriceEntries();
    }

    public void setCountOfWrongClassifiedPositiveExamples(int x){
        c[1][0] = x;
    }
    
    public double getCountOfWrongClassifiedPositiveExamples(){
        return c[1][0];
    }
    
    public double getProbabilityOfWrongClassifiedPositiveExamples(){
        return c[1][0] / sumConfusionMatriceEntries();
    }
    
    public void setCountOfWrongClassifiednegativeExamples(int x){
        c[1][1] = x;
    }
    
    public double getCountOfWrongClassifiednegativeExamples(){
        return c[1][1];
    }
    
    public double getProbabilityOfWrongClassifiednegativeExamples(){
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
