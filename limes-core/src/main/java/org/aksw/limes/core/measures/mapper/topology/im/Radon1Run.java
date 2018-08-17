package org.aksw.limes.core.measures.mapper.topology.im;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.topology.RADON;

import com.vividsolutions.jts.io.ParseException;

public class Radon1Run {

	public static void main(String[] args) throws ParseException {


		//		String parameter=args[0];
		//		String in1= args[1];
		//		String in2= args[2];
		String parameter= "0.0";//
		String in1="/home/abddatascienceadmin/abdullah-2018/clc/datasets_3/nuts.nt";//
		String in2="/home/abddatascienceadmin/abdullah-2018/clc/datasets_3/nuts.nt";//
		String rel="covers";//args[3];
		//ACache sourceWithSimplification=PolygonSimplification.cacheWithSimpilification(parameter, in1);
		//ACache targetWithSimplification=PolygonSimplification.cacheWithSimpilification(parameter, in2);
		ACache sourceWithoutSimplification=PolygonSimplification.cacheWithoutSimplification(in1);
		ACache targetWithoutSimplification=PolygonSimplification.cacheWithoutSimplification(in1);
		double t1=System.currentTimeMillis();
		t1=System.currentTimeMillis();
		AMapping r1WithoutSimpilifcation=RADON.getMapping(sourceWithoutSimplification,targetWithoutSimplification, "?x", "?y", "top_within(x.asWKT, y.asWKT)", 1.0d, rel);
		double t1_1=System.currentTimeMillis();

		FMeasure fMeasure=new FMeasure();

		double f1=fMeasure.calculate(r1WithoutSimpilifcation,new GoldStandard(r1WithoutSimpilifcation));
		double r1=fMeasure.recall(r1WithoutSimpilifcation,new GoldStandard(r1WithoutSimpilifcation));
		double p1=fMeasure.precision(r1WithoutSimpilifcation,new GoldStandard(r1WithoutSimpilifcation));
		double t11=System.currentTimeMillis();
		AMapping r1WithSimpilifcation= RADON.getMapping(sourceWithoutSimplification, targetWithoutSimplification, "?x", "?y", "top_within(x.asWKT, y.asWKT)", 1.0d, rel);
		double t11_1=System.currentTimeMillis();
		double f11=fMeasure.calculate(r1WithSimpilifcation,new GoldStandard(r1WithoutSimpilifcation));
		double r11=fMeasure.recall(r1WithSimpilifcation,new GoldStandard(r1WithoutSimpilifcation));
		double p11=fMeasure.precision(r1WithSimpilifcation,new GoldStandard(r1WithoutSimpilifcation));
		int rwout = r1WithoutSimpilifcation.getSize();

		int rw = r1WithSimpilifcation.getSize();
		double simpT = 0;

		System.out.println("Simp. Par., Rel.,Simp. Time  , FG , RG , PG, F , R  , P , T TIME OF R without Simp., T SIZE OF R without, T TIME OF R with Simp., T SIZE OF R with");
		System.out.println(parameter+", "+rel+" ,"+simpT+", "+f1+", "+r1+","+p1+","+f11+","+r11+","+p11+","+(t1_1-t1)+","+rwout +","+(t11_1-t11)+","+rw);

	}



}

