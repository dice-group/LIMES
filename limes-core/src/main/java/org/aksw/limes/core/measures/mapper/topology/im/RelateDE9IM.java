package org.aksw.limes.core.measures.mapper.topology.im;

import com.vividsolutions.jts.geom.Dimension;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.operation.relate.RelateOp;

public class RelateDE9IM {

	private int[][] im;

	public RelateDE9IM(String elements) {
		this();
		im= new int[3][3];
		set(elements);
	}

	public RelateDE9IM( Geometry g1,Geometry g2) {
		this();
	}

	public RelateDE9IM() {

	}

	public void  relateIM(Geometry g1, Geometry g2) {

		RelateOp relateOp= new RelateOp(g1, g2);
		IntersectionMatrix deIM=relateOp.getIntersectionMatrix();
		im =new int[3][3];
		for(int i=0;i<9;i++) {
			int row = i / 3;
			int col = i % 3;
			im[row][col]=deIM.get(row, col);
			System.out.println("im = :)"+ im[row][col]);
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

		if(im[0][0]==
				Dimension.FALSE &&im[0][1]==
				Dimension.FALSE &&im[1][0]==
				Dimension.FALSE  &&im[1][1]==
				Dimension.FALSE )
			return true;

		else
			return false;

	}
	public boolean isIntersects() {
		return ! isDisjoint();
	}

	public boolean isTouches() {

		if( im[0][0]==
				Dimension.FALSE&&(im[0][1]>=0 ||im[0][1]==
				Dimension.TRUE)) return true;
		if(im[0][0]==Dimension.FALSE&&(im[1][0]>=0 ||im[1][0]==
				Dimension.TRUE)) return true;
		if( im[0][0]==Dimension.FALSE&&(im[1][1]>=0 ||im[1][1]==
				Dimension.TRUE))
			return true;
		else
			return false;
	}

	public boolean isContains() {

		if((im[0][0]>=0 ||im[0][0]==
				Dimension.TRUE)&&im[2][0]==
				Dimension.FALSE&&im[2][1]==
				Dimension.FALSE)
			return true;
		else
			return false;

	}

	public boolean isEquals() {
		if(                     (im[0][0]>=0 ||im[0][0]==
				Dimension.TRUE)&&im[0][2]==
				Dimension.FALSE&&im[1][2]==
				Dimension.FALSE&&im[2][0]==
				Dimension.FALSE&&im[2][1]==
				Dimension.FALSE)
			return true;
		else
			return false;

	}

	public boolean isOverlaps() {

		boolean b1= (             im[0][0]>=0||im[0][0]==
				Dimension.TRUE)&&(im[0][2]>=0||im[0][2]==
				Dimension.TRUE)&&(im[2][0]>=0||im[2][0]==
				Dimension.TRUE);
		boolean b2=                            im[0][0]==1
				&&(im[0][2]>=0||im[0][2]==
				Dimension.TRUE)&&(im[2][0]>=0||im[2][0]==
				Dimension.TRUE);
		if(b1==true || b2==true)
			return true;
		else 
			return false;

	}

	public boolean isCoveredBy() {
		boolean hasPointInCommon = ((im[0][0]>=0||im[0][0]==
				Dimension.TRUE)||   (im[0][1]>=0||im[0][1]==
				Dimension.TRUE)||   (im[1][0]>=0||im[1][0]==
				Dimension.TRUE)||   (im[1][1]>=0||im[1][1]==
				Dimension.TRUE));
		boolean b1=               im[0][2]==
				Dimension.FALSE&& im[1][2]==
				Dimension.FALSE;

		if(hasPointInCommon==true&& b1==true)
			return true;
		else 
			return false;

	}

	public boolean isCovers() {
		boolean hasPointInCommon =  ((im[0][0]>=0 || im[0][0]==
				Dimension.TRUE) ||  ( im[0][1]>=0 || im[0][1]==
				Dimension.TRUE) ||  ( im[1][0]>=0 || im[1][0]==
				Dimension.TRUE) ||  ( im[1][1]>=0 || im[1][1]==
				Dimension.TRUE));
		boolean b1=                im[2][0]==
				Dimension.FALSE && im[2][1]==
				Dimension.FALSE;

		if(hasPointInCommon==true&& b1==true)
			return true;
		else 
			return false;
	}


	public boolean isWithin() {
		if((                        im[0][0]>=0 || im[0][0]==
				Dimension.TRUE)  && im[0][2]==
				Dimension.FALSE  && im[1][2]==
				Dimension.FALSE)
			return true;
		else 
			return false;
	}

	public boolean isCrosses(Geometry g1,Geometry g2) {

		if (g1.getDimension()< g2.getDimension())
		{
			return (                    im[0][0]>=0||im[0][0]==
					Dimension.TRUE) && (im[0][2]>=0||im[0][2]==
					Dimension.TRUE);
		}
		if (g2.getDimension()> g1.getDimension())
		{
			return (                    im[0][0]>=0||im[0][0]==
					Dimension.TRUE) && (im[2][0]>=0||im[2][0]==
					Dimension.TRUE);
		}
		if (g1.getDimension() == Dimension.L && g2.getDimension() == Dimension.L)

		{
			return im[0][0]==0; 
		} 

		return false;

	}

}
