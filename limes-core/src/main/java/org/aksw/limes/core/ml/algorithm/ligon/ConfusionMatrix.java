package org.aksw.limes.core.ml.algorithm.ligon;

import java.util.Arrays;

/**
 * @author mohamedsherif
 *
 * The first and second row of each ConfusionMatrix C contains counts for links where the oracle returned true resp. false.
 * The first and second column of C contain counts for positive resp. negative examples. 
 * Hence, C[1][1] basically contains counts for positive examples that were rightly classified by the oracle.
 * More intuitively, C = { {truePositive, trueNegative} {falsePositive, falseNegative} }
 * Based on the updated C, we approximate all conditional probability necessary to describe the oracle using the characteristic Matrix D,
 * where D = C / ( C[1][1] + C[1][2] + C[2][1] + C[2][2] ) 
 */
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

	public void setTruePositiveCount(int x){
		c[0][0] = x;
	}

	public void incrementTruePositiveCount(){
		c[0][0]++;
	}

	public double getTruePositiveProbability(){
		return c[0][0] / ( c[0][0] + c[1][0] );
	}

	public double getTruePositiveCount(){
		return c[0][0];
	}

	public void setFalseNegativeCount(int x){
		c[0][1] = x;
	}

	public void incrementFalseNegativeCount(){
		c[0][1]++;
	}

	public double getFalseNegativeCount(){
		return c[0][1];
	}

	public double getFalseNegativeProbability(){
		return c[0][1] / ( c[0][1] + c[1][1] );
	}

	public void setFalsePositiveCount(int x){
		c[1][0] = x;
	}

	public void incrementFalsePositiveCount(){
		c[1][0]++;
	}

	public double getFalsePositiveCount(){
		return c[1][0];
	}

	public double getFalsePositiveProbability(){
		return c[1][0] / ( c[0][0] + c[1][0] );
	}

	public void setTrueNegativeCount(int x){
		c[1][1] = x;
	}

	public void incrementTrueNegativeCount(){
		c[1][1]++;
	}

	public double getTrueNegativeCount(){
		return c[1][1];
	}

	public double getTrueNegativeProbability(){
		return c[1][1] / ( c[0][1] + c[1][1] );
	}

//	private double sumConfusionMatriceEntries(){
//		double sum = 0.0d;
//		for(int i = 0 ; i < 2 ; i++){
//			for(int j = 0 ; j < 2 ; j++){
//				sum += c[i][j];
//			}
//		}
//		return sum;
//	}

	@Override
	public String toString() {
		return "ConfusionMatrix [c=" + Arrays.toString(c) + "]";
	}

	public String characteristicMatrixToString() {
		return "characteristicMatrix { {"+ 
				getTruePositiveProbability() + ", " +
				getFalseNegativeProbability() + "}, {" +
				getFalsePositiveProbability() + ", " +
				getTrueNegativeProbability() + 
				"} }";
	}

}
