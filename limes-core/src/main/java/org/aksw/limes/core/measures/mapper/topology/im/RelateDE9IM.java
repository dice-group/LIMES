package org.aksw.limes.core.measures.mapper.topology.im;

import com.vividsolutions.jts.geom.Dimension;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.operation.relate.RelateOp;

public class RelateDE9IM {

	private int[][] im;
	private int dimg1;
	private int dimg2;
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
	public void  computeIM(Geometry g1, Geometry g2) {
		dimg1=g1.getDimension();
		dimg2=g2.getDimension();

		RelateOp relateOp= new RelateOp(g1, g2);
		IntersectionMatrix deIM=relateOp.getIntersectionMatrix();
		im =new int[3][3];
		for(int i=0;i<9;i++) {
			int row = i / 3;
			int col = i % 3;
			im[row][col]=deIM.get(row, col);
			//System.out.println("im = :)"+ im[row][col]);
		}
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
		if(im[0][0]==Dimension.FALSE &&im[0][1]==Dimension.FALSE &&im[1][0]==Dimension.FALSE  &&im[1][1]==Dimension.FALSE )
			return true;
		else
			return false;
	}
	public boolean isIntersects() {
		//System.out.println("INTERSECTS");
		return ! isDisjoint();
	}
	public boolean isTouches() {

		if(im[0][0]==Dimension.FALSE&&(im[0][1]>=0 ||im[0][1]==Dimension.TRUE)) 
			return true;
		if(im[0][0]==Dimension.FALSE&&(im[1][0]>=0 ||im[1][0]==Dimension.TRUE)) 
			return true;
		if(im[0][0]==Dimension.FALSE&&(im[1][1]>=0 ||im[1][1]==Dimension.TRUE))
			return true;
		else
			return false;
	}
	public boolean isContains() {
		//System.out.println("CONTAINS");

		if((im[0][0]>=0 ||im[0][0]==Dimension.TRUE)&&im[2][0]==Dimension.FALSE&&im[2][1]==Dimension.FALSE)
			return true;
		else
			return false;
	}
	public boolean isEquals() {
		//System.out.println("EQUALS");

		if((im[0][0]>=0 ||im[0][0]==Dimension.TRUE)&&im[0][2]==Dimension.FALSE&&im[1][2]==Dimension.FALSE&&im[2][0]==
				Dimension.FALSE&&im[2][1]==Dimension.FALSE)
			return true;
		else
			return false;
	}
	public boolean isOverlaps() {
		//System.out.println("OVERLAPS");
		boolean b1=(dimg1==0&&dimg2==0)||(dimg1==2&&dimg2==2);

		boolean b2= (im[0][0]>=0||im[0][0]==Dimension.TRUE)&&(im[0][2]>=0||im[0][2]==Dimension.TRUE)&&(im[2][0]>=0||im[2][0]==
				Dimension.TRUE);
		if(b1==true&&b2==true)
			return true;
		boolean b3=(dimg1==1&&dimg2==1);
		boolean b4= im[0][0]==1&&(im[0][2]>=0||im[0][2]==Dimension.TRUE)&&(im[2][0]>=0||im[2][0]==Dimension.TRUE);
		if(b3==true && b4==true)
			return true;
		else 
			return false;
	}
	public boolean isCoveredBy() {
		//System.out.println("COVEREDBY");

		if((im[0][0]>=0||im[0][0]==Dimension.TRUE) && im[0][2]==Dimension.FALSE && im[1][2]==Dimension.FALSE)
			return true; 
		if((im[0][1]>=0||im[0][1]==Dimension.TRUE) && im[0][2]==Dimension.FALSE && im[1][2]==Dimension.FALSE)
			return true; 
		if((im[1][0]>=0||im[1][0]==Dimension.TRUE) && im[0][2]==Dimension.FALSE && im[1][2]==Dimension.FALSE)
			return true; 
		if((im[1][1]>=0||im[1][1]==Dimension.TRUE) && im[0][2]==Dimension.FALSE && im[1][2]==Dimension.FALSE)
			return true;
		else 
			return false;
	}
	public boolean isCovers() {
		//System.out.println("COVERS");
		if((im[0][0]>=0 || im[0][0]==Dimension.TRUE) && im[2][0]==Dimension.FALSE && im[2][1]==Dimension.FALSE) 
			return true;
		if((im[0][1]>=0 || im[0][1]==Dimension.TRUE) && im[2][0]==Dimension.FALSE && im[2][1]==Dimension.FALSE)
			return true;
		if((im[1][0]>=0 || im[1][0]==Dimension.TRUE) && im[2][0]==Dimension.FALSE && im[2][1]==Dimension.FALSE)
			return true;  
		if((im[1][1]>=0 || im[1][1]==Dimension.TRUE) && im[2][0]==Dimension.FALSE && im[2][1]==Dimension.FALSE)
			return true;
		else 
			return false;
	}
	public boolean isWithin() {
		//System.out.println("WITHIN");
		if((im[0][0]>=0 || im[0][0]==Dimension.TRUE)  && im[0][2]==Dimension.FALSE  && im[1][2]==Dimension.FALSE)
			return true;
		else 
			return false;
	}
	public boolean isCrosses() {
		//System.out.println("CORESSES");

		if (dimg1< dimg2)
		{
			return (im[0][0]>=0||im[0][0]==Dimension.TRUE) && (im[0][2]>=0||im[0][2]==Dimension.TRUE);
		}
		if (dimg2> dimg1)
		{
			return (im[0][0]>=0||im[0][0]==Dimension.TRUE) && (im[2][0]>=0||im[2][0]==Dimension.TRUE);
		}
		if (dimg1 == Dimension.L && dimg2== Dimension.L)
		{
			return im[0][0]==0; 
		} 

		return false;
	}
}
