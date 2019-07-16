package org.aksw.limes.core.measures.mapper.topology.contentSimilarity;


import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Abdullah Ahmed
 *
 */
public class ContentMeasure {


	Geometry a;
	Geometry b;


	/**
	 * @param A
	 * @param B
	 * class constructor takes 2 input geometry
	 */
	ContentMeasure (Geometry A, Geometry B){


		this.a=A;
		this.b=B;
		mixedContentMeasure();

	}
	/**
	 * class constructor
	 */
	ContentMeasure(){


	}

	/**
	 * @param a
	 * @param b
	 * @return  boolean value defines whether or not the projections of these mbra and mbrb over x-axis overlap
	 */
	public boolean projectionX() {
		//mbra is the minimum bounding box of geometry a
		//mbrb is the minimum bounding box of geometry a
		 boolean projX=false;
		Envelope mbra= a.getEnvelopeInternal();
		Envelope mbrb= b.getEnvelopeInternal();

		if((mbra.getMinX()> mbrb.getMaxX())) 
			return projX;
		if((mbra.getMaxX()< mbrb.getMinX()))
			return projX;
		else
			return true;
	}

	/**
	 * @param a
	 * @param b
	 * @return boolean value defines whether or not the projections of these mbra and mbrb over y-axis overlap
	 */
	public boolean projectionY() {
  boolean projY=false;
		Envelope mbra= a.getEnvelopeInternal();
		Envelope mbrb= b.getEnvelopeInternal();

		if(mbra.getMinY()> mbrb.getMaxY()) 
			return projY;
		if(mbrb.getMaxY()< mbra.getMinY())
		return projY;
		else
			return true;

	}
	/**
	 * @param a
	 * @param b
	 * @return the distance based on many conditions
	 */
	public double distance() {

		double d = 0;
		double min1=0;
		double min2=0;

		Envelope mbra= a.getEnvelopeInternal();
		Envelope mbrb=  b.getEnvelopeInternal();
		Envelope mbraIntersectsmbrb=  mbra.intersection(mbrb);
		double areaAintersectB=mbraIntersectsmbrb.getArea();
		double areaA=mbra.getArea();
		double areaB=mbrb.getArea();
		if(!projectionX()&& !projectionY()) {
			double x1=Math.abs((mbra.getMinX()-mbrb.getMaxX()));
			double x2=Math.abs((mbra.getMaxX()-mbrb.getMinX()));
			double y1=Math.abs((mbra.getMinY()-mbrb.getMaxY()));
			double y2=Math.abs((mbra.getMaxY()-mbrb.getMinY()));
			if(x1>x2) min1=x2;
			else min1=x1;
			if(y1>y2) min2=y2;
			else min2=y1;
			d=Math.sqrt(Math.pow(min1, 2.0)+Math.pow(min2, 2.0));	
			//System.out.println("DDD: "+d);
			return d;
		}
		else if((areaA>areaB && areaAintersectB==areaA)||(
				areaB>areaA && areaAintersectB==areaB)) {
			double x1=Math.abs(mbra.getMinX()-mbrb.getMinX());
			double x2=Math.abs(mbra.getMaxX()-mbrb.getMaxX());
			double y1=Math.abs(mbra.getMinY()-mbrb.getMinY());
			double y2=Math.abs(mbra.getMaxY()-mbrb.getMaxY());
			if(x1<x2&&x1<y1&&x1<y2) d=-x1;
			if(x2<x1&&x2<y1&&x2<y2) d=-x2;
			if(y1<x1&&y1<x2&&y1<y2) d=-y1;
			if(y2<x1&&y2<y1&&y2<x2) d=-y2;
			//System.out.println("DDD1: "+d);
			return d;
		}
		else if(projectionX( )&& !projectionY()) {
			double y1=Math.abs(mbra.getMinY()-mbrb.getMaxY());
			double y2=Math.abs(mbra.getMaxY()-mbrb.getMinY());
			
			//System.out.println("y1: "+y1);
			//System.out.println("y2: "+y2);
			if(y1<y2) d=y1;
			else d=y2; 
			//System.out.println("DDD2: "+d);
			return d;
		}

		else if(!projectionX( )&& projectionY( )) {
			double x1=Math.abs(mbra.getMinX()-mbrb.getMaxX());
			double x2=Math.abs(mbra.getMaxX()-mbrb.getMinX());
			if(x1<x2) d=x1;	
			else d=x2;
			//System.out.println("DDD3: "+d);
			return d;
		}
		else d=0.0;
		return d;

	}
	/**
	 * @param a
	 * @param b
	 * @return measure of similarity based on the area computing 
	 */
	public double areaBasedMeasure() {

		Envelope mbra= a.getEnvelopeInternal();
		Geometry mbraUnionmbra=a.union(b);
		Envelope envelopeInternal = mbraUnionmbra.getEnvelopeInternal();
		double f_a=0;
		f_a=mbra.getArea()/envelopeInternal.getArea();
		return f_a;
	}

	/**
	 * @param a
	 * @param b
	 * @return measure of similarity based on the diagonal computing
	 */
	public double diagonalBasedMeasure() {

		Envelope mbra= a.getEnvelopeInternal();
		Geometry mbraUnionmbra=a.union(b);
		Envelope envelopeInternal = mbraUnionmbra.getEnvelopeInternal();
		double f_d=0;
		double diagonal=Math.sqrt(Math.pow((mbra.getMaxX()-mbra.getMinX()), 2)
				+Math.pow((mbra.getMinY()-mbra.getMaxY()), 2));

		double diagonalOfUnion=Math.sqrt(Math.pow((envelopeInternal.getMaxX()-envelopeInternal.getMinX()), 2)
				+Math.pow((envelopeInternal.getMinY()-envelopeInternal.getMaxY()), 2));

		f_d=diagonal/diagonalOfUnion;
		return f_d;
	}


	/**
	 * @param a
	 * @param b
	 * @return measure of similarity based on the combination of area and diagonal measure, it can distinguish 
	 * the 8 topological relations taking in account
	 */
	public int mixedContentMeasure() {

		Envelope mbra= a.getEnvelopeInternal();
		Envelope mbrb=  b.getEnvelopeInternal();
		Envelope mbraIntersectsmbrb=  mbra.intersection(mbrb);

		int f_m=0;
		f_m=(int) ((mbra.getArea()-2*(mbraIntersectsmbrb.getArea()))/mbra.getArea()+
				(distance()/diagonalBasedMeasure()));


		return f_m;

	}
	/**
	 * @return the distance
	 */
	public double getDistance() {

		return distance( );

	}
	/**
	 * @return area based measure
	 */
	public double getAreaBasedMeasure() {

		return areaBasedMeasure( );

	}
	/**
	 * @return diagonal based measure
	 */
	public double getDiagonalBasedMeasure() {

		return diagonalBasedMeasure( );

	}
	/**
	 * @return mixed content measure
	 */
	public double getMixedContentMeasure() {

		return mixedContentMeasure( );

	}


}