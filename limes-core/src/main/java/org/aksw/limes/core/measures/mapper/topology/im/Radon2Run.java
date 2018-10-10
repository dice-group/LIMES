package org.aksw.limes.core.measures.mapper.topology.im;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;

import com.vividsolutions.jts.io.ParseException;

public class Radon2Run {

	public static void main(String[] args) throws ParseException {

		//String parameter=args[0];//"0.1";//
		String in1=args[0];//"/home/abddatascienceadmin/abdullah-2018/clc/datasets_3/nuts.nt";//
		String in2=args[1];//"/home/abddatascienceadmin/abdullah-2018/clc/datasets_3/nuts.nt";//
		
//		ACache sourceWithSimplification=PolygonSimplification.cacheWithSimpilification(parameter, in1);
//		ACache targetWithSimplification=PolygonSimplification.cacheWithSimpilification(parameter, in2);
		double simpT =0;
		ACache sourceWithoutSimplification=PolygonSimplification.cacheWithoutSimplification(in1);
		ACache targetWithoutSimplification=PolygonSimplification.cacheWithoutSimplification(in2);

		double t=System.currentTimeMillis();
		double t2=System.currentTimeMillis();

		List<AMapping> r2WithoutSimpilifcation = new ArrayList<AMapping>();
		t=System.currentTimeMillis();
		r2WithoutSimpilifcation = RADON2.getMapping(sourceWithoutSimplification,targetWithoutSimplification, "?x", "?y", "top_within(x.asWKT, y.asWKT)", 1.0d);
		t2=System.currentTimeMillis();
		FMeasure fMeasure=new FMeasure();

		double f1=fMeasure.calculate(r2WithoutSimpilifcation.get(0),new GoldStandard(r2WithoutSimpilifcation.get(0)));
		double f2=fMeasure.calculate(r2WithoutSimpilifcation.get(1),new GoldStandard(r2WithoutSimpilifcation.get(1)));
		double f3=fMeasure.calculate(r2WithoutSimpilifcation.get(2),new GoldStandard(r2WithoutSimpilifcation.get(2)));
		double f4=fMeasure.calculate(r2WithoutSimpilifcation.get(3),new GoldStandard(r2WithoutSimpilifcation.get(3)));
		double f5=fMeasure.calculate(r2WithoutSimpilifcation.get(4),new GoldStandard(r2WithoutSimpilifcation.get(4)));
		double f6=fMeasure.calculate(r2WithoutSimpilifcation.get(5),new GoldStandard(r2WithoutSimpilifcation.get(5)));
		double f7=fMeasure.calculate(r2WithoutSimpilifcation.get(6),new GoldStandard(r2WithoutSimpilifcation.get(6)));
		double f8=fMeasure.calculate(r2WithoutSimpilifcation.get(7),new GoldStandard(r2WithoutSimpilifcation.get(7)));
		double f9=fMeasure.calculate(r2WithoutSimpilifcation.get(8),new GoldStandard(r2WithoutSimpilifcation.get(8)));

		double r1=fMeasure.recall(r2WithoutSimpilifcation.get(0),new GoldStandard(r2WithoutSimpilifcation.get(0)));
		double r2=fMeasure.recall(r2WithoutSimpilifcation.get(1),new GoldStandard(r2WithoutSimpilifcation.get(1)));
		double r3=fMeasure.recall(r2WithoutSimpilifcation.get(2),new GoldStandard(r2WithoutSimpilifcation.get(2)));
		double r4=fMeasure.recall(r2WithoutSimpilifcation.get(3),new GoldStandard(r2WithoutSimpilifcation.get(3)));
		double r5=fMeasure.recall(r2WithoutSimpilifcation.get(4),new GoldStandard(r2WithoutSimpilifcation.get(4)));
		double r6=fMeasure.recall(r2WithoutSimpilifcation.get(5),new GoldStandard(r2WithoutSimpilifcation.get(5)));
		double r7=fMeasure.recall(r2WithoutSimpilifcation.get(6),new GoldStandard(r2WithoutSimpilifcation.get(6)));
		double r8=fMeasure.recall(r2WithoutSimpilifcation.get(7),new GoldStandard(r2WithoutSimpilifcation.get(7)));
		double r9=fMeasure.recall(r2WithoutSimpilifcation.get(8),new GoldStandard(r2WithoutSimpilifcation.get(8)));

		double p1=fMeasure.precision(r2WithoutSimpilifcation.get(0),new GoldStandard(r2WithoutSimpilifcation.get(0)));
		double p2=fMeasure.precision(r2WithoutSimpilifcation.get(1),new GoldStandard(r2WithoutSimpilifcation.get(1)));
		double p3=fMeasure.precision(r2WithoutSimpilifcation.get(2),new GoldStandard(r2WithoutSimpilifcation.get(2)));
		double p4=fMeasure.precision(r2WithoutSimpilifcation.get(3),new GoldStandard(r2WithoutSimpilifcation.get(3)));
		double p5=fMeasure.precision(r2WithoutSimpilifcation.get(4),new GoldStandard(r2WithoutSimpilifcation.get(4)));
		double p6=fMeasure.precision(r2WithoutSimpilifcation.get(5),new GoldStandard(r2WithoutSimpilifcation.get(5)));
		double p7=fMeasure.precision(r2WithoutSimpilifcation.get(6),new GoldStandard(r2WithoutSimpilifcation.get(6)));
		double p8=fMeasure.precision(r2WithoutSimpilifcation.get(7),new GoldStandard(r2WithoutSimpilifcation.get(7)));
		double p9=fMeasure.precision(r2WithoutSimpilifcation.get(8),new GoldStandard(r2WithoutSimpilifcation.get(8)));

//		List<AMapping> r2WithSimpilifcation = new ArrayList<AMapping>();
//		double r2WithSimpilifcationTime=System.currentTimeMillis();
//		double r2WithSimpilifcationTime2=System.currentTimeMillis();
//
//		r2WithSimpilifcationTime=System.currentTimeMillis();
//		r2WithSimpilifcation= RADON2.getMapping(sourceWithSimplification, targetWithSimplification, "?x", "?y", "top_within(x.asWKT, y.asWKT)", 1.0d);
//
//		r2WithSimpilifcationTime2=System.currentTimeMillis();

//		double f11=fMeasure.calculate(r2WithSimpilifcation.get(0),new GoldStandard(r2WithoutSimpilifcation.get(0)));
//		double f22=fMeasure.calculate(r2WithSimpilifcation.get(1),new GoldStandard(r2WithoutSimpilifcation.get(1)));
//		double f33=fMeasure.calculate(r2WithSimpilifcation.get(2),new GoldStandard(r2WithoutSimpilifcation.get(2)));
//		double f44=fMeasure.calculate(r2WithSimpilifcation.get(3),new GoldStandard(r2WithoutSimpilifcation.get(3)));
//		double f55=fMeasure.calculate(r2WithSimpilifcation.get(4),new GoldStandard(r2WithoutSimpilifcation.get(4)));
//		double f66=fMeasure.calculate(r2WithSimpilifcation.get(5),new GoldStandard(r2WithoutSimpilifcation.get(5)));
//		double f77=fMeasure.calculate(r2WithSimpilifcation.get(6),new GoldStandard(r2WithoutSimpilifcation.get(6)));
//		double f88=fMeasure.calculate(r2WithSimpilifcation.get(7),new GoldStandard(r2WithoutSimpilifcation.get(7)));
//		double f99=fMeasure.calculate(r2WithSimpilifcation.get(8),new GoldStandard(r2WithoutSimpilifcation.get(8)));
//
//		double r11=fMeasure.recall(r2WithSimpilifcation.get(0),new GoldStandard(r2WithoutSimpilifcation.get(0)));
//		double r22=fMeasure.recall(r2WithSimpilifcation.get(1),new GoldStandard(r2WithoutSimpilifcation.get(1)));
//		double r33=fMeasure.recall(r2WithSimpilifcation.get(2),new GoldStandard(r2WithoutSimpilifcation.get(2)));
//		double r44=fMeasure.recall(r2WithSimpilifcation.get(3),new GoldStandard(r2WithoutSimpilifcation.get(3)));
//		double r55=fMeasure.recall(r2WithSimpilifcation.get(4),new GoldStandard(r2WithoutSimpilifcation.get(4)));
//		double r66=fMeasure.recall(r2WithSimpilifcation.get(5),new GoldStandard(r2WithoutSimpilifcation.get(5)));
//		double r77=fMeasure.recall(r2WithSimpilifcation.get(6),new GoldStandard(r2WithoutSimpilifcation.get(6)));
//		double r88=fMeasure.recall(r2WithSimpilifcation.get(7),new GoldStandard(r2WithoutSimpilifcation.get(7)));
//		double r99=fMeasure.recall(r2WithSimpilifcation.get(8),new GoldStandard(r2WithoutSimpilifcation.get(8)));
//
//		double p11=fMeasure.precision(r2WithSimpilifcation.get(0),new GoldStandard(r2WithoutSimpilifcation.get(0)));
//		double p22=fMeasure.precision(r2WithSimpilifcation.get(1),new GoldStandard(r2WithoutSimpilifcation.get(1)));
//		double p33=fMeasure.precision(r2WithSimpilifcation.get(2),new GoldStandard(r2WithoutSimpilifcation.get(2)));
//		double p44=fMeasure.precision(r2WithSimpilifcation.get(3),new GoldStandard(r2WithoutSimpilifcation.get(3)));
//		double p55=fMeasure.precision(r2WithSimpilifcation.get(4),new GoldStandard(r2WithoutSimpilifcation.get(4)));
//		double p66=fMeasure.precision(r2WithSimpilifcation.get(5),new GoldStandard(r2WithoutSimpilifcation.get(5)));
//		double p77=fMeasure.precision(r2WithSimpilifcation.get(6),new GoldStandard(r2WithoutSimpilifcation.get(6)));
//		double p88=fMeasure.precision(r2WithSimpilifcation.get(7),new GoldStandard(r2WithoutSimpilifcation.get(7)));
//		double p99=fMeasure.precision(r2WithSimpilifcation.get(8),new GoldStandard(r2WithoutSimpilifcation.get(8)));

		int rwout = r2WithoutSimpilifcation.get(0).getSize()+r2WithoutSimpilifcation.get(1).getSize()
				+r2WithoutSimpilifcation.get(2).getSize()+r2WithoutSimpilifcation.get(3).getSize()
				+r2WithoutSimpilifcation.get(4).getSize()+r2WithoutSimpilifcation.get(5).getSize()
				+r2WithoutSimpilifcation.get(6).getSize()+r2WithoutSimpilifcation.get(7).getSize()
				+r2WithoutSimpilifcation.get(8).getSize();

//		int rw = r2WithSimpilifcation.get(0).getSize()+r2WithSimpilifcation.get(1).getSize()
//				+r2WithSimpilifcation.get(2).getSize()+r2WithSimpilifcation.get(3).getSize()
//				+r2WithSimpilifcation.get(4).getSize()+r2WithSimpilifcation.get(5).getSize()
//				+r2WithSimpilifcation.get(6).getSize()+r2WithSimpilifcation.get(7).getSize()
//				+r2WithSimpilifcation.get(8).getSize();
//
//		double rwitht = (r2WithSimpilifcationTime2-r2WithSimpilifcationTime);

		System.out.println(" Rel., F , R  , P , TIME OF R2, SIZE OF R2 ");
		System.out.println(" INTERSECT , "+simpT+", "+f1+", "+r1+","+p1+","+(t2-t)+","+rwout );
		System.out.println(" CONTAINS  , "+simpT+", "+f2+", "+r2+","+p2+","+(t2-t)+","+rwout );
		System.out.println(" COVEREDBY , "+simpT+", "+f3+", "+r3+","+p3+","+(t2-t)+","+rwout );
		System.out.println(" COVERS    , "+simpT+", "+f4+", "+r4+","+p4+","+(t2-t)+","+rwout );
		System.out.println(" CROSSES   , "+simpT+", "+f5+", "+r5+","+p5+","+(t2-t)+","+rwout );
		System.out.println(" OVERLAPS  , "+simpT+", "+f6+", "+r6+","+p6+","+(t2-t)+","+rwout );
		System.out.println(" EQUALS    , "+simpT+", "+f7+", "+r7+","+p7+","+(t2-t)+","+rwout );
		System.out.println(" WITHIN    , "+simpT+", "+f8+", "+r8+","+p8+","+(t2-t)+","+rwout);
		System.out.println(" TOUCHES   , "+simpT+", "+f9+", "+r9+","+p9+","+(t2-t)+","+rwout);

	}

}