package org.aksw.limes.core.measures.mapper.topology.im;

import com.vividsolutions.jts.geom.Dimension;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.operation.relate.RelateOp;

public class RelateDE9IM {


	private int[] im;

	public RelateDE9IM(String elements) {
		this();
		//System.out.println(" the first constuctor");
		//System.out.println(" the first p= "+g1.toString());
		im= new int[9];
		set(elements);
	}


	public RelateDE9IM( Geometry g1,Geometry g2) {
		this();
		//System.out.println(" the second constuctor");
		// TODO Auto-generated constructor stub
	}

	public RelateDE9IM() {
		// TODO Auto-generated constructor stub
	}


	public int[] relateIM(Geometry g1, Geometry g2) {
		//int[] im ;
		//		RelateDE9IM.g1=g1;
		//		this.g2=g2;
		RelateOp relateOp= new RelateOp(g1, g2);
		IntersectionMatrix deIM=relateOp.getIntersectionMatrix();
		im =new int[9];
		im[0]=deIM.get(0, 0);
		im[1]=deIM.get(0, 1);
		im[2]=deIM.get(0, 2);
		im[3]=deIM.get(1, 0);
		im[4]=deIM.get(1, 1);
		im[5]=deIM.get(1, 2);
		im[6]=deIM.get(2, 0);
		im[7]=deIM.get(2, 1);
		im[8]=deIM.get(2, 2);

		return im;
	}


	public void set(String dimensionSymbols) {
		for (int i = 0; i < dimensionSymbols.length(); i++) {

			im[i] = Dimension.toDimensionValue(dimensionSymbols.charAt(i));
			//System.out.println(" the matrix is "+im[i]);
		}
	}

	public boolean isDisjoint() {

		if(im[0]==Dimension.FALSE &&im[1]==Dimension.FALSE &&im[3]==Dimension.FALSE  &&im[4]==Dimension.FALSE )
			return true;

		else
			return false;

	}
	public boolean isIntersects() {
		return ! isDisjoint();
	}



	public boolean isTouches() {


		if(im[0]==Dimension.FALSE&&(im[1]>=0 ||im[1]==Dimension.TRUE)||(im[3]>=0 ||im[3]==Dimension.TRUE)||(im[4]>=0 ||im[4]==Dimension.TRUE))
			return true;

		else
			return false;
	}

	public boolean isContains() {


		if((im[0]>=0 ||im[0]==Dimension.TRUE)&&im[6]==Dimension.FALSE&&im[7]==Dimension.FALSE)
			return true;
		else
			return false;

	}



	public boolean isEquals() {
		if((im[0]>=0 ||im[0]==Dimension.TRUE)&&im[2]==Dimension.FALSE&&im[5]==Dimension.FALSE&&im[6]==Dimension.FALSE&&im[7]==Dimension.FALSE)
			return true;
		else
			return false;

	}


	public boolean isOverlaps() {

		boolean b1= (im[0]>=0||im[0]==Dimension.TRUE)&&(im[2]>=0||im[2]==Dimension.TRUE)&&(im[6]>=0||im[6]==Dimension.TRUE);
		boolean b2=im[0]==1&&(im[2]>=0||im[2]==Dimension.TRUE)&&(im[6]>=0||im[6]==Dimension.TRUE);
		if(b1==true || b2==true)
			return true;
		else return false;

	}

	public boolean isCoveredBy() {
		boolean hasPointInCommon = ((im[0]>=0||im[0]==Dimension.TRUE)||(im[1]>=0||im[1]==Dimension.TRUE)||(im[3]>=0||im[3]==Dimension.TRUE)||(im[4]>=0||im[4]==Dimension.TRUE));
		boolean b1=im[2]==Dimension.FALSE&&im[5]==Dimension.FALSE;

		if(hasPointInCommon==true&& b1==true)
			return true;
		else 
			return false;

	}

	public boolean isCovers() {
		boolean hasPointInCommon = ((im[0]>=0||im[0]==Dimension.TRUE)||(im[1]>=0||im[1]==Dimension.TRUE)||(im[3]>=0||im[3]==Dimension.TRUE)||(im[4]>=0||im[4]==Dimension.TRUE));
		boolean b1=im[6]==Dimension.FALSE&&im[7]==Dimension.FALSE;

		if(hasPointInCommon==true&& b1==true)
			return true;
		else 
			return false;
	}


	public boolean isWithin() {
		if((im[0]>=0||im[0]==Dimension.TRUE)&&im[2]==Dimension.FALSE&&im[5]==Dimension.FALSE)
			return true;
		else 
			return false;

	}


	public boolean isCrosses(Geometry g1,Geometry g2) {
		//
		//		System.out.println(" the second constuctor inside crosse");
		System.out.println(" g1.getDimension() "+g1.getDimension());
		System.out.println(" g2.getDimension() "+g2.getDimension());
		if (g1.getDimension()< g2.getDimension())
		{
			return (im[0]>=0||im[0]==Dimension.TRUE)&&(im[2]>=0||im[2]==Dimension.TRUE);

		}
		if (g2.getDimension()> g1.getDimension())
		{
			return (im[0]>=0||im[0]==Dimension.TRUE)&&(im[6]>=0||im[6]==Dimension.TRUE);
		}
		if (g1.getDimension() == Dimension.L && g2.getDimension() == Dimension.L)

		{
			return im[0]==0; 
		} 



		return false;

	}

}
