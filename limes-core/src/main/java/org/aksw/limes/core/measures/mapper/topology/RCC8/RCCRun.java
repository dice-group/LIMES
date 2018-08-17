package org.aksw.limes.core.measures.mapper.topology.RCC8;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.topology.im.PolygonSimplification;

import com.vividsolutions.jts.io.ParseException;

public class RCCRun {

	public static void main(String[] args) throws ParseException {

		String in1=args[0];//"/home/abddatascienceadmin/abdullah-2018/clc/datasets_3/111.nt";//
		String in2=args[1];//"/home/abddatascienceadmin/abdullah-2018/clc/datasets_3/nuts.nt";//

		ACache sourceWithoutSimplification=PolygonSimplification.cacheWithoutSimplification(in1);
		ACache targetWithoutSimplification=PolygonSimplification.cacheWithoutSimplification(in2);

		double t=System.currentTimeMillis();
		double t2=System.currentTimeMillis();

		List<AMapping> r2WithoutSimpilifcation = new ArrayList<AMapping>();
		t=System.currentTimeMillis();
		r2WithoutSimpilifcation = LDbasedRCC.getMapping(sourceWithoutSimplification,targetWithoutSimplification, "?x", "?y", "top_within(x.asWKT, y.asWKT)", 1.0d);
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
		//	double f9=fMeasure.calculate(r2WithoutSimpilifcation.get(8),new GoldStandard(r2WithoutSimpilifcation.get(8)));

		double r1=fMeasure.recall(r2WithoutSimpilifcation.get(0),new GoldStandard(r2WithoutSimpilifcation.get(0)));
		double r2=fMeasure.recall(r2WithoutSimpilifcation.get(1),new GoldStandard(r2WithoutSimpilifcation.get(1)));
		double r3=fMeasure.recall(r2WithoutSimpilifcation.get(2),new GoldStandard(r2WithoutSimpilifcation.get(2)));
		double r4=fMeasure.recall(r2WithoutSimpilifcation.get(3),new GoldStandard(r2WithoutSimpilifcation.get(3)));
		double r5=fMeasure.recall(r2WithoutSimpilifcation.get(4),new GoldStandard(r2WithoutSimpilifcation.get(4)));
		double r6=fMeasure.recall(r2WithoutSimpilifcation.get(5),new GoldStandard(r2WithoutSimpilifcation.get(5)));
		double r7=fMeasure.recall(r2WithoutSimpilifcation.get(6),new GoldStandard(r2WithoutSimpilifcation.get(6)));
		double r8=fMeasure.recall(r2WithoutSimpilifcation.get(7),new GoldStandard(r2WithoutSimpilifcation.get(7)));
		//	double r9=fMeasure.recall(r2WithoutSimpilifcation.get(8),new GoldStandard(r2WithoutSimpilifcation.get(8)));

		double p1=fMeasure.precision(r2WithoutSimpilifcation.get(0),new GoldStandard(r2WithoutSimpilifcation.get(0)));
		double p2=fMeasure.precision(r2WithoutSimpilifcation.get(1),new GoldStandard(r2WithoutSimpilifcation.get(1)));
		double p3=fMeasure.precision(r2WithoutSimpilifcation.get(2),new GoldStandard(r2WithoutSimpilifcation.get(2)));
		double p4=fMeasure.precision(r2WithoutSimpilifcation.get(3),new GoldStandard(r2WithoutSimpilifcation.get(3)));
		double p5=fMeasure.precision(r2WithoutSimpilifcation.get(4),new GoldStandard(r2WithoutSimpilifcation.get(4)));
		double p6=fMeasure.precision(r2WithoutSimpilifcation.get(5),new GoldStandard(r2WithoutSimpilifcation.get(5)));
		double p7=fMeasure.precision(r2WithoutSimpilifcation.get(6),new GoldStandard(r2WithoutSimpilifcation.get(6)));
		double p8=fMeasure.precision(r2WithoutSimpilifcation.get(7),new GoldStandard(r2WithoutSimpilifcation.get(7)));
		//	double p9=fMeasure.precision(r2WithoutSimpilifcation.get(8),new GoldStandard(r2WithoutSimpilifcation.get(8)));


		int rwout = r2WithoutSimpilifcation.get(0).getSize()+r2WithoutSimpilifcation.get(1).getSize()
				+r2WithoutSimpilifcation.get(2).getSize()+r2WithoutSimpilifcation.get(3).getSize()
				+r2WithoutSimpilifcation.get(4).getSize()+r2WithoutSimpilifcation.get(5).getSize()
				+r2WithoutSimpilifcation.get(6).getSize()+r2WithoutSimpilifcation.get(7).getSize();


		System.out.println(" Rel., F , R , P, TIME, SIZE");
		System.out.println(" connected_C ,  "+f1+", "+r1+","+p1+","+(t2-t)+","+rwout);
		System.out.println(" externallyConnected_EC  ,  "+f2+", "+r2+","+p2+","+(t2-t)+","+rwout);
		System.out.println(" properlyOverlap_PO ,  "+f3+", "+r3+","+p3+","+(t2-t)+","+rwout);
		System.out.println(" equal_EQ    ,  "+f4+", "+r4+","+p4+","+(t2-t)+","+rwout);
		System.out.println(" tangentialProperPart_TPP   ,  "+f5+", "+r5+","+p5+","+(t2-t)+","+rwout);
		System.out.println(" nonTangentialProperPart_NTPP  ,  "+f6+", "+r6+","+p6+","+(t2-t)+","+rwout);
		System.out.println(" tangentialProperPartConvers_TPPc    ,  "+f7+", "+r7+","+p7+","+(t2-t)+","+rwout);
		System.out.println(" nonTangentialProperPartConvers_NTPPc    ,  "+f8+", "+r8+","+p8+","+","+(t2-t)+","+rwout);

	}

}

