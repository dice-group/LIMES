package org.aksw.limes.core.measures.mapper.topology.im;

import java.util.HashSet;
import java.util.Set;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.pointsets.Polygon;
import org.aksw.limes.core.measures.measure.pointsets.average.NaiveAverageMeasure;
import org.aksw.limes.core.measures.measure.pointsets.hausdorff.NaiveHausdorffMeasure;
import org.aksw.limes.core.measures.measure.pointsets.link.NaiveLinkMeasure;
import org.aksw.limes.core.measures.measure.pointsets.max.NaiveMaxMeasure;
import org.aksw.limes.core.measures.measure.pointsets.mean.NaiveMeanMeasure;
import org.aksw.limes.core.measures.measure.pointsets.min.NaiveMinMeasure;
import org.aksw.limes.core.measures.measure.pointsets.sumofmin.NaiveSumOfMinMeasure;
import org.aksw.limes.core.measures.measure.pointsets.surjection.FairSurjectionMeasure;
import org.aksw.limes.core.measures.measure.pointsets.surjection.NaiveSurjectionMeasure;

import com.vividsolutions.jts.io.ParseException;

public class PointsSetRun {


	public static void main(String[] args) throws ParseException {
		final double threshold = 0.999216689;
		String par=args[0];
		String in1=args[1];//"/home/abddatascienceadmin/clc/datasets_3/nuts.nt";//args[1];
		String in2=args[2];// "/home/abddatascienceadmin/clc/datasets_3/nuts.nt"; //args[2];

		final String HAUSDORFF = "hausdorff";
		final String MEAN = "mean";
		final String MIN = "min";
		final String LINK = "link";
		final String MAX = "max";
		final String SUMOFMIN = "sumofmin";
		final String AVERAGE = "average";
		final String SURJECTION = "surjection";
		//final String FRECHE = "freche";
		final String FAIRSURJECTION="fairSurjection";


		PolygonSimplification polygonSiplification=new PolygonSimplification();
		Set<Polygon> sourceWithoutSimpilification =new HashSet<Polygon>();
		Set<Polygon> targetWithoutSimpilification=new HashSet<Polygon>();

		sourceWithoutSimpilification= polygonSiplification.sourceWithoutSimpilification(in1);
		targetWithoutSimpilification= polygonSiplification.sourceWithoutSimpilification(in2);

		Set<Polygon> sourceWithSimpilification =new HashSet<Polygon>();
		Set<Polygon> targetWithSimpilification=new HashSet<Polygon>();

		sourceWithSimpilification= polygonSiplification.sourceWithSimpilification(par,in1);
		targetWithSimpilification= polygonSiplification.sourceWithSimpilification(par,in2);
		AMapping goldMApping=goldMApping(sourceWithoutSimpilification, targetWithoutSimpilification, 1.0);

		NaiveAverageMeasure naiveAverageMeasure =new NaiveAverageMeasure();
		//NaiveFrechetMeasure naiveFrechetMeasure=new NaiveFrechetMeasure();
		NaiveHausdorffMeasure naiveHausdorffMeasure=new NaiveHausdorffMeasure();
		NaiveLinkMeasure naiveLinkMeasure=new NaiveLinkMeasure();
		NaiveMaxMeasure naiveMaxMeasure=new NaiveMaxMeasure();
		NaiveMeanMeasure naiveMeanMeasure=new NaiveMeanMeasure();
		NaiveMinMeasure naiveMinMeasure=new NaiveMinMeasure();
		NaiveSumOfMinMeasure naiveSumOfMinMeasure=new NaiveSumOfMinMeasure();
		NaiveSurjectionMeasure naiveSurjectionMeasure=new NaiveSurjectionMeasure();
		FairSurjectionMeasure fairSurjectionMeasure=new FairSurjectionMeasure();
		FMeasure fMeasure=new FMeasure();
		double simplificationTime=0;



		if(AVERAGE.equals(AVERAGE)) {

			double nAm1=System.currentTimeMillis();
			AMapping nAm = naiveAverageMeasure.run(sourceWithoutSimpilification, targetWithoutSimpilification, threshold);
			double nAm2=System.currentTimeMillis();
			double f1=fMeasure.calculate(nAm,new GoldStandard(goldMApping));
			double nAm11=System.currentTimeMillis();
			AMapping nAm_1 = naiveAverageMeasure.run(sourceWithSimpilification, targetWithSimpilification, threshold);
			double nAm22=System.currentTimeMillis();
			double f11=fMeasure.calculate(nAm_1,new GoldStandard(goldMApping));
			System.out.println("par, Points Measure, simplificationTime, FG, F, time without Simplification, time with Simplification");
			System.out.println(par+", Average," + simplificationTime+ ","+ f1+","+f11+","+(nAm2-nAm1)+","+(nAm22-nAm11));
		}
		if(HAUSDORFF.equals(HAUSDORFF)) {
			double nHm1=System.currentTimeMillis();
			AMapping nHm = naiveHausdorffMeasure.run(sourceWithoutSimpilification, targetWithoutSimpilification, threshold);
			double nHm2=System.currentTimeMillis();
			double f3=fMeasure.calculate(nHm,new GoldStandard(goldMApping));
			double nHm11=System.currentTimeMillis();
			AMapping nHm_1 = naiveHausdorffMeasure.run(sourceWithSimpilification, targetWithSimpilification, threshold);
			double nHm22=System.currentTimeMillis();
			double f33=fMeasure.calculate(nHm_1,new GoldStandard(goldMApping));
			System.out.println("par, Points Measure, simplificationTime, FG, F, time without Simplification, time with Simplification");
			System.out.println(par+", Hausdorff, " + simplificationTime+ ","+ f3+","+f33+","+(nHm2-nHm1)+","+(nHm22-nHm11));

		}
		if(MEAN.equals(MEAN)) {
			double nMem1=System.currentTimeMillis();
			AMapping nMem = naiveMeanMeasure.run(sourceWithoutSimpilification, targetWithoutSimpilification, threshold);
			double nMem2=System.currentTimeMillis();
			double f6=fMeasure.calculate(nMem,new GoldStandard(goldMApping));
			double nMem11=System.currentTimeMillis();
			AMapping nMem_1 = naiveMeanMeasure.run(sourceWithSimpilification, targetWithSimpilification, threshold);
			double nMem22=System.currentTimeMillis();
			double f66=fMeasure.calculate(nMem_1,new GoldStandard(goldMApping));
			System.out.println("par, Points Measure, simplificationTime, FG, F, time without Simplification, time with Simplification");
			System.out.println(par+", Mean, " + simplificationTime+ ","+ f6+","+f66+","+(nMem2-nMem1)+","+(nMem22-nMem11));
		}
		if(MAX.equals(MAX)) {
			double nMm1=System.currentTimeMillis();
			AMapping nMm = naiveMaxMeasure.run(sourceWithoutSimpilification, targetWithoutSimpilification, threshold);
			double nMm2=System.currentTimeMillis();
			double f5=fMeasure.calculate(nMm,new GoldStandard(goldMApping));
			double nMm11=System.currentTimeMillis();
			AMapping nMm_1 = naiveMaxMeasure.run(sourceWithSimpilification, targetWithSimpilification, threshold);
			double nMm22=System.currentTimeMillis();
			double f55=fMeasure.calculate(nMm_1,new GoldStandard(goldMApping));
			System.out.println("par, Points Measure, simplificationTime, FG, F, time without Simplification, time with Simplification");
			System.out.println(par+", Max, " + simplificationTime+ ","+ f5+","+f55+","+(nMm2-nMm1)+","+(nMm22-nMm11));

		}
		if(MIN.equals(MIN)) {
			double nMinm1=System.currentTimeMillis();
			AMapping nMinm = naiveMinMeasure.run(sourceWithoutSimpilification, targetWithoutSimpilification, threshold);
			double nMinm2=System.currentTimeMillis();
			double f7=fMeasure.calculate(nMinm,new GoldStandard(goldMApping));
			double nMinm11=System.currentTimeMillis();
			AMapping nMinm_1 = naiveMinMeasure.run(sourceWithSimpilification, targetWithSimpilification, threshold);
			double nMinm22=System.currentTimeMillis();
			double f77=fMeasure.calculate(nMinm_1,new GoldStandard(goldMApping));
			System.out.println("par, Points Measure, simplificationTime, FG, F, time without Simplification, time with Simplification");
			System.out.println(par+", Min," + simplificationTime+ ","+ f7+","+f77+","+(nMinm2-nMinm1)+","+(nMinm22-nMinm11));
		}
		if(LINK.equals(LINK)) {
			double nLm1=System.currentTimeMillis();
			AMapping nLm = naiveLinkMeasure.run(sourceWithoutSimpilification, targetWithoutSimpilification, threshold);
			double nLm2=System.currentTimeMillis();
			double f4=fMeasure.calculate(nLm,new GoldStandard(goldMApping));
			double nLm11=System.currentTimeMillis();
			AMapping nLm_1 = naiveLinkMeasure.run(sourceWithSimpilification, targetWithSimpilification, threshold);
			double nLm22=System.currentTimeMillis();
			double f44=fMeasure.calculate(nLm_1,new GoldStandard(goldMApping));

			System.out.println("par, Points Measure, simplificationTime, FG, F, time without Simplification, time with Simplification");
			System.out.println(par+", Link, " + simplificationTime+ ","+ f4+","+f44+","+(nLm2-nLm1)+","+(nLm22-nLm11));

		}
		if(SUMOFMIN.equals(SUMOFMIN)) {
			double nSMinm1=System.currentTimeMillis();
			AMapping nSMinm = naiveSumOfMinMeasure.run(sourceWithoutSimpilification, targetWithoutSimpilification, threshold);
			double nSMinm2=System.currentTimeMillis();
			double f8=fMeasure.calculate(nSMinm,new GoldStandard(goldMApping));
			double nSMinm11=System.currentTimeMillis();
			AMapping nSMinm_1 = naiveSumOfMinMeasure.run(sourceWithSimpilification, targetWithSimpilification, threshold);
			double nSMinm22=System.currentTimeMillis();

			double f88=fMeasure.calculate(nSMinm_1,new GoldStandard(goldMApping));

			System.out.println("par, Points Measure, simplificationTime, FG, F, time without Simplification, time with Simplification");
			System.out.println(par+", SumOfMin, " + simplificationTime+ ","+ f8+","+f88+","+(nSMinm2-nSMinm1)+","+(nSMinm22-nSMinm11));

		}
		if(SURJECTION.equals(SURJECTION)) {

			double nSm1=System.currentTimeMillis();
			AMapping nSm = naiveSurjectionMeasure.run(sourceWithoutSimpilification, targetWithoutSimpilification, threshold);
			double nSm2=System.currentTimeMillis();
			double f9=fMeasure.calculate(nSm,new GoldStandard(goldMApping));
			double nSm11=System.currentTimeMillis();
			AMapping nSm_1 = naiveSurjectionMeasure.run(sourceWithSimpilification, targetWithSimpilification, threshold);
			double nSm22=System.currentTimeMillis();
			double f99=fMeasure.calculate(nSm_1,new GoldStandard(goldMApping));
			System.out.println("par, Points Measure, simplificationTime, FG, F, time without Simplification, time with Simplification");
			System.out.println(par+", Surjection,"+simplificationTime+ ","+ f9+","+f99+","+(nSm2-nSm1)+","+(nSm22-nSm11));

		}
		//		if(FRECHE.equals(FRECHE)) {
		//
		//			double nFm1=System.currentTimeMillis();
		//			AMapping fM = naiveFrechetMeasure.run(sourceWithoutSimpilification, targetWithoutSimpilification, threshold);
		//			double nFm2=System.currentTimeMillis();
		//			double f10=fMeasure.calculate(fM,new GoldStandard(goldMApping));
		//			double nFm11=System.currentTimeMillis();
		//			AMapping fM_1 = naiveFrechetMeasure.run(sourceWithSimpilification, targetWithSimpilification, threshold);
		//			double nFm22=System.currentTimeMillis();
		//			double f10_1=fMeasure.calculate(fM_1,new GoldStandard(goldMApping));
		//			System.out.println("par, Points Measure, simplificationTime, FG, F, time without Simplification, time with Simplification");
		//			System.out.println(par+", Freche,"+simplificationTime+ ","+ f10+","+f10_1+","+(nFm2-nFm1)+","+(nFm22-nFm11));
		//
		//		}
		if(FAIRSURJECTION.equals(FAIRSURJECTION)) {

			double nFsm1=System.currentTimeMillis();
			AMapping fsM = fairSurjectionMeasure.run(sourceWithoutSimpilification, targetWithoutSimpilification, threshold);
			double nFsm2=System.currentTimeMillis();
			double f11=fMeasure.calculate(fsM,new GoldStandard(goldMApping));
			double nFsm11=System.currentTimeMillis();
			AMapping fsM_1 = fairSurjectionMeasure.run(sourceWithSimpilification, targetWithSimpilification, threshold);
			double nFsm22=System.currentTimeMillis();
			double f11_1=fMeasure.calculate(fsM_1,new GoldStandard(goldMApping));
			System.out.println("par, Points Measure, simplificationTime, FG, F, time without Simplification, time with Simplification");
			System.out.println(par+", Fairsurjection,"+simplificationTime+ ","+ f11+","+f11_1+","+(nFsm2-nFsm1)+","+(nFsm22-nFsm11));

		}

	}
	static AMapping goldMApping(Set<Polygon> source, Set<Polygon> target, double threshold) {
		AMapping m = MappingFactory.createDefaultMapping();
		for (Polygon s : source) {
			for (Polygon t : target) {
				// Double d = computeDistance(s, t, threshold);
				if (s.uri.contains(t.uri)) {
					m.add(s.uri, t.uri,threshold);
				}
			}
		}
		return m;
	}

}
