package org.aksw.limes.core.measures.mapper.topology.RCC8;

import com.vividsolutions.jts.geom.Dimension;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.geom.Location;
import com.vividsolutions.jts.operation.relate.RelateOp;

public class ReducedIntersectionMatrix {

	private static int[][] im;

	public ReducedIntersectionMatrix(String elements) {
		this();
		im= new int[2][2];
		set(elements);
	}
	public ReducedIntersectionMatrix( Geometry g1,Geometry g2) {
		this();
		computeIM(g1,g2);
	}
	public ReducedIntersectionMatrix() {

	}
	public void computeIM(Geometry g1, Geometry g2) {
		int row = 0,col = 0;
		RelateOp relateOp= new RelateOp(g1, g2);
		IntersectionMatrix deIM=relateOp.getIntersectionMatrix();
		im =new int[2][2];
		for(int i=0;i<4;i++) {
			row = i / 2;
			col = i % 2;
			im[row][col]=deIM.get(row, col);
		}
	}

	public static boolean isTrue(int actualDimensionValue) {
		if (actualDimensionValue >= 0 || actualDimensionValue  == Dimension.TRUE) {
			return true;
		}
		return false;
	}
	public void set(String dimensionSymbols) {
		for (int i = 0; i < 4; i++) {
			int row = i / 2;
			int col = i % 2;
			im[row][col] = Dimension.toDimensionValue(dimensionSymbols.charAt(i));
			//System.out.println(" the matrix is "+im[i]);
		}
	}

	public boolean disConnected_DC() {

		return im[Location.INTERIOR][Location.INTERIOR]==Dimension.FALSE &&
				im[Location.BOUNDARY][Location.BOUNDARY]==Dimension.FALSE;
	}

	public boolean connected_C() {
		return !disConnected_DC();
	}

	public boolean externallyConnected_EC() {
		return im[Location.INTERIOR][Location.INTERIOR]==Dimension.FALSE && 
				(isTrue(im[Location.BOUNDARY][Location.BOUNDARY]));
	}

	public boolean properlyOverlap_PO() {

		return ( isTrue(im[Location.INTERIOR][Location.INTERIOR]))&&
				(isTrue(im[Location.INTERIOR][Location.BOUNDARY]))&&
				(isTrue(im[Location.BOUNDARY][Location.INTERIOR]))&&
				(isTrue(im[Location.BOUNDARY][Location.BOUNDARY]));
	}

	public boolean equal_EQ() {
		return ( isTrue(im[Location.INTERIOR][Location.INTERIOR]))&&
				(isTrue(im[Location.BOUNDARY][Location.BOUNDARY]))&&
				im[Location.INTERIOR][Location.BOUNDARY]==Dimension.FALSE &&
				im[Location.BOUNDARY][Location.INTERIOR]==Dimension.FALSE; 
	}

	public boolean tangentialProperPart_TPP() {
		return ( isTrue(im[Location.INTERIOR][Location.INTERIOR]))&&
				(isTrue(im[Location.BOUNDARY][Location.BOUNDARY]))&&
				( isTrue(im[Location.BOUNDARY][Location.INTERIOR]))&&
				im[Location.INTERIOR][Location.BOUNDARY]==Dimension.FALSE; 	
	}
	public boolean nonTangentialProperPart_NTPP() {
		return ( isTrue(im[Location.INTERIOR][Location.INTERIOR]))&&
				im[Location.BOUNDARY][Location.BOUNDARY]==Dimension.FALSE&&
				im[Location.INTERIOR][Location.BOUNDARY]==Dimension.FALSE&&
				( isTrue(im[Location.BOUNDARY][Location.INTERIOR])); 	
	}
	public boolean tangentialProperPartConvers_TPPc() {
		return ( isTrue(im[Location.INTERIOR][Location.INTERIOR]))&&
				(isTrue(im[Location.BOUNDARY][Location.BOUNDARY]))&&
				( isTrue(im[Location.INTERIOR][Location.BOUNDARY]))&&
				im[Location.BOUNDARY][Location.INTERIOR]==Dimension.FALSE; 	
	}
	public boolean nonTangentialProperPartConvers_NTPPc() {
		return ( isTrue(im[Location.INTERIOR][Location.INTERIOR]))&&
				im[Location.BOUNDARY][Location.BOUNDARY]==Dimension.FALSE&&
				( isTrue(im[Location.INTERIOR][Location.BOUNDARY]))&&
				im[Location.BOUNDARY][Location.INTERIOR]==Dimension.FALSE; 	
	}

}
