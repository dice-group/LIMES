package org.aksw.limes.core.measures.mapper.topology.im;

import com.vividsolutions.jts.geom.Dimension;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.operation.relate.RelateOp;

public class RelateDE9IM {

	private static int[][] im;
	private static int dimg1;
	private static int dimg2;
	public RelateDE9IM(String elements) {
		this();
		im= new int[3][3];
		set(elements);
	}
	public RelateDE9IM( Geometry g1,Geometry g2) {
		this();
		computeIM(g1,g2);
	}
	public RelateDE9IM() {

	}
	public void computeIM(Geometry g1, Geometry g2) {
		dimg1=g1.getDimension();
		dimg2=g2.getDimension();
		int row = 0,col = 0;
		RelateOp relateOp= new RelateOp(g1, g2);
		IntersectionMatrix deIM=relateOp.getIntersectionMatrix();
		im =new int[3][3];
		for(int i=0;i<9;i++) {
			row = i / 3;
			col = i % 3;
			im[row][col]=deIM.get(row, col);
			//System.out.println("im = :)"+ im[row][col]);
		}
		//return im;
	}
	public void set(String dimensionSymbols) {
		for (int i = 0; i < dimensionSymbols.length(); i++) {
			int row = i / 3;
			int col = i % 3;
			im[row][col] = Dimension.toDimensionValue(dimensionSymbols.charAt(i));
			//System.out.println(" the matrix is "+im[i]);
		}
	}
	public boolean isDisjoint() {
		//System.out.println("DISJOINT");
		boolean b = im[0][0]==Dimension.FALSE &&im[0][1]==Dimension.FALSE &&im[1][0]==Dimension.FALSE  &&im[1][1]==Dimension.FALSE;
		return b;
	}
	public boolean isIntersects() {
		//System.out.println("INTERSECTS");
		return ! isDisjoint();
	}
	public boolean isTouches() {
		boolean b = im[0][0]==Dimension.FALSE&&(im[0][1]>=0 ||im[0][1]==Dimension.TRUE);
		boolean b2 = im[0][0]==Dimension.FALSE&&(im[1][0]>=0 ||im[1][0]==Dimension.TRUE);
		boolean b3 = im[0][0]==Dimension.FALSE&&(im[1][1]>=0 ||im[1][1]==Dimension.TRUE);

		return b||b2||b3;
	}
	public boolean isContains() {
		//System.out.println("CONTAINS");
		boolean b = (im[0][0]>=0 ||im[0][0]==Dimension.TRUE)&&im[2][0]==Dimension.FALSE&&im[2][1]==Dimension.FALSE;
		return b;
	}
	public boolean isEquals() {
		//System.out.println("EQUALS");
		boolean b = (im[0][0]>=0 ||im[0][0]==Dimension.TRUE)&&im[0][2]==Dimension.FALSE&&im[1][2]==Dimension.FALSE&&im[2][0]==
				Dimension.FALSE&&im[2][1]==Dimension.FALSE;

		return b;
	}
	public boolean isOverlaps() {
		//System.out.println("OVERLAPS");
		boolean b1=(dimg1==0&&dimg2==0)||(dimg1==2&&dimg2==2);
		boolean b2= (im[0][0]>=0||im[0][0]==Dimension.TRUE)&&(im[0][2]>=0||im[0][2]==Dimension.TRUE)&&(im[2][0]>=0||im[2][0]==
				Dimension.TRUE);
		boolean b3=(dimg1==1&&dimg2==1);
		boolean b4= im[0][0]==1&&(im[0][2]>=0||im[0][2]==Dimension.TRUE)&&(im[2][0]>=0||im[2][0]==Dimension.TRUE);
		return (b1&&b2)||(b3 && b4);
	}
	public boolean isCoveredBy() {
		//System.out.println("COVEREDBY");
		boolean b  = (im[0][0]>=0||im[0][0]==Dimension.TRUE) && im[0][2]==Dimension.FALSE && im[1][2]==Dimension.FALSE;
		boolean b1 = (im[0][1]>=0||im[0][1]==Dimension.TRUE) && im[0][2]==Dimension.FALSE && im[1][2]==Dimension.FALSE;
		boolean b2 = (im[1][0]>=0||im[1][0]==Dimension.TRUE) && im[0][2]==Dimension.FALSE && im[1][2]==Dimension.FALSE;
		boolean b3 = (im[1][1]>=0||im[1][1]==Dimension.TRUE) && im[0][2]==Dimension.FALSE && im[1][2]==Dimension.FALSE;
		return (b||b1||b2||b3);
	}
	public boolean isCovers() {
		//System.out.println("COVERS");
		boolean b =  (im[0][0]>=0 || im[0][0]==Dimension.TRUE) && im[2][0]==Dimension.FALSE && im[2][1]==Dimension.FALSE;
		boolean b1 = (im[0][1]>=0 || im[0][1]==Dimension.TRUE) && im[2][0]==Dimension.FALSE && im[2][1]==Dimension.FALSE;
		boolean b2 = (im[1][0]>=0 || im[1][0]==Dimension.TRUE) && im[2][0]==Dimension.FALSE && im[2][1]==Dimension.FALSE;
		boolean b3 = (im[1][1]>=0 || im[1][1]==Dimension.TRUE) && im[2][0]==Dimension.FALSE && im[2][1]==Dimension.FALSE;
		return (b||b1||b2||b3);
	}
	public boolean isWithin() {
		//System.out.println("WITHIN");
		boolean b = (im[0][0]>=0 || im[0][0]==Dimension.TRUE)  && im[0][2]==Dimension.FALSE  && im[1][2]==Dimension.FALSE;
		return b;
	}
	public boolean isCrosses() {
		//System.out.println("CORESSES");
		boolean b  = (im[0][0]>=0||im[0][0]==Dimension.TRUE) && (im[0][2]>=0||im[0][2]==Dimension.TRUE);
		boolean b1 = (im[0][0]>=0||im[0][0]==Dimension.TRUE) && (im[2][0]>=0||im[2][0]==Dimension.TRUE);
		boolean b2=im[0][0]==0;
		return (dimg1< dimg2&&b)||(dimg2> dimg1&&b1)||((dimg1 == Dimension.L && dimg2== Dimension.L)&&b2);
	}
}
