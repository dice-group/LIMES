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
		mixedContentMeasureS(A,B);
		mixedContentMeasureT(B,A);
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
	public boolean projectionX(Geometry a,Geometry b) {
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
	public boolean projectionY(Geometry a,Geometry b) {
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
	public double distance(Geometry a,Geometry b) {

		double d = 0;
		double min1=0;
		double min2=0;

		Envelope mbra= a.getEnvelopeInternal();
		Envelope mbrb=  b.getEnvelopeInternal();
		Envelope mbraIntersectsmbrb=  mbra.intersection(mbrb);
		double areaAintersectB=mbraIntersectsmbrb.getArea();
		double areaA=mbra.getArea();
		double areaB=mbrb.getArea();
		if(!projectionX(a,b)&& !projectionY(a,b)) {
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
		else if(projectionX(a,b )&& !projectionY(a,b)) {
			double y1=Math.abs(mbra.getMinY()-mbrb.getMaxY());
			double y2=Math.abs(mbra.getMaxY()-mbrb.getMinY());
			
			//System.out.println("y1: "+y1);
			//System.out.println("y2: "+y2);
			if(y1<y2) d=y1;
			else d=y2; 
			//System.out.println("DDD2: "+d);
			return d;
		}

		else if(!projectionX(a,b )&& projectionY( a,b)) {
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
	public double areaBasedMeasureS(Geometry a,Geometry b) {

		Envelope mbra= a.getEnvelopeInternal();
		Geometry mbraUnionmbra=a.union(b);
		Envelope envelopeUnion = mbraUnionmbra.getEnvelopeInternal();
		double f_a_s=0;
		f_a_s=mbra.getArea()/envelopeUnion.getArea();
		return f_a_s;
	}

	
	public double areaBasedMeasureT(Geometry b,Geometry a) {

		Envelope mbrb= b.getEnvelopeInternal();
		Geometry mbraUnionmbra=a.union(b);
		Envelope envelopeUnion = mbraUnionmbra.getEnvelopeInternal();
		double f_a_t=0;
		f_a_t=mbrb.getArea()/envelopeUnion.getArea();
		return f_a_t;
	}
	/**
	 * @param a
	 * @param b
	 * @return measure of similarity based on the diagonal computing
	 */
	public double diagonalBasedMeasureS(Geometry a,Geometry b) {

		Envelope mbra= a.getEnvelopeInternal();
		Geometry mbraUnionmbrb=a.union(b);
		Envelope envelopeUnion = mbraUnionmbrb.getEnvelopeInternal();
		double f_d_s=0;
		double diagonal=Math.sqrt(Math.pow((mbra.getMaxX()-mbra.getMinX()), 2)
				+Math.pow((mbra.getMinY()-mbra.getMaxY()), 2));

		double diagonalOfUnion=Math.sqrt(Math.pow((envelopeUnion.getMaxX()-envelopeUnion.getMinX()), 2)
				+Math.pow((envelopeUnion.getMinY()-envelopeUnion.getMaxY()), 2));

		f_d_s=diagonal/diagonalOfUnion;
		return f_d_s;
	}
	
	public double diagonalBasedMeasureT(Geometry b,Geometry a) {

		Envelope mbrb= b.getEnvelopeInternal();
		Geometry mbraUnionmbra=a.union(b);
		Envelope envelopeUnion = mbraUnionmbra.getEnvelopeInternal();
		double f_d_t=0;
		double diagonal=Math.sqrt(Math.pow((mbrb.getMaxX()-mbrb.getMinX()), 2)
				+Math.pow((mbrb.getMinY()-mbrb.getMaxY()), 2));

		double diagonalOfUnion=Math.sqrt(Math.pow((envelopeUnion.getMaxX()-envelopeUnion.getMinX()), 2)
				+Math.pow((envelopeUnion.getMinY()-envelopeUnion.getMaxY()), 2));

		f_d_t=diagonal/diagonalOfUnion;
		return f_d_t;
	}
	
	public double diagonalS(Geometry a) {

		Envelope mbra= a.getEnvelopeInternal();
		double diagonalS=0;
		 diagonalS=Math.sqrt(Math.pow((mbra.getMaxX()-mbra.getMinX()), 2)
				+Math.pow((mbra.getMinY()-mbra.getMaxY()), 2));

		
		return diagonalS;
	}
	
	public double diagonalT(Geometry b) {

		Envelope mbrb= b.getEnvelopeInternal();
		double diagonalT=0;
		 diagonalT=Math.sqrt(Math.pow((mbrb.getMaxX()-mbrb.getMinX()), 2)
				+Math.pow((mbrb.getMinY()-mbrb.getMaxY()), 2));

		
		return diagonalT;
	}


	/**
	 * @param a
	 * @param b
	 * @return measure of similarity based on the combination of area and diagonal measure, it can distinguish 
	 * the 8 topological relations taking in account
	 */
	public int mixedContentMeasureS(Geometry a,Geometry b) {

		Envelope mbra= a.getEnvelopeInternal();
		Envelope mbrb=  b.getEnvelopeInternal();
		Envelope mbraIntersectsmbrb=  mbra.intersection(mbrb);

		int f_m_s=0;
		f_m_s=(int) ((mbra.getArea()-2*(mbraIntersectsmbrb.getArea()))/mbra.getArea()+
				(distance(a,b)/diagonalS(a)));


		return f_m_s;

	}
	public int mixedContentMeasureT(Geometry b,Geometry a) {

		Envelope mbra= a.getEnvelopeInternal();
		Envelope mbrb=  b.getEnvelopeInternal();
		Envelope mbraIntersectsmbrb=  mbra.intersection(mbrb);

		int f_m_s=0;
		f_m_s=(int) ((mbrb.getArea()-2*(mbraIntersectsmbrb.getArea()))/mbrb.getArea()+
				(distance(a,b)/diagonalT(b)));


		return f_m_s;

	}
	


}