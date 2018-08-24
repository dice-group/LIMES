package org.aksw.limes.core.evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.datastrutures.PairSimilar;
import org.aksw.limes.core.datastrutures.TaskData;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.qualititativeMeasures.FMeasure;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Precision;
import org.aksw.limes.core.evaluation.qualititativeMeasures.Recall;
import org.aksw.limes.core.execution.engine.ExecutionEngine;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory;
import org.aksw.limes.core.execution.engine.ExecutionEngineFactory.ExecutionEngineType;
import org.aksw.limes.core.execution.engine.SimpleExecutionEngine;
import org.aksw.limes.core.execution.planning.plan.Instruction;
import org.aksw.limes.core.execution.planning.plan.Plan;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.HamacherSetOperations;
import org.aksw.limes.core.measures.mapper.FuzzyOperators.YagerSetOperations;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.algorithm.fptld.fitness.SimFuzzyRMSE;
import org.apache.commons.math3.util.Pair;

public class EvalYager {
	public static final String[] defaultMeasures = { "jaccard", "trigrams", "cosine", "qgrams" };

	private static final double minPropertyCoverage = 0.4;
	private static final double propertyLearningRate = 0.95;

	public static void main(String[] args) throws IOException {
		String[] dataSetNames = new String[] {
				"restaurantsfixed", "person1", "person2", "DBLPACM",
				"ABTBUY"
				, "DBLPSCHOLAR", "AMAZONGOOGLEPRODUCTS", "DBPLINKEDMDB"
		};
		for (String d : dataSetNames) {
			System.out
					.println("\n =================================\n\t" + d + "\n=================================\n");
			String resultFMIntersection = "yagergrid\tyageroptimized\thamachergrid\thamacheroptimized\n";
			String resultPrecIntersection = "yagergrid\tyageroptimized\thamachergrid\thamacheroptimized\n";
			String resultRecIntersection = "yagergrid\tyageroptimized\thamachergrid\thamacheroptimized\n";
			String resultpIntersection = "yagergrid\tyageroptimized\thamachergrid\thamacheroptimized\n";
			String resultTimeIntersection = "yagergrid\tyageroptimized\thamachergrid\thamacheroptimized\n";

			String resultFMUnion = "yagergrid\tyageroptimized\thamachergrid\thamacheroptimized\n";
			String resultPrecUnion = "yagergrid\tyageroptimized\thamachergrid\thamacheroptimized\n";
			String resultRecUnion = "yagergrid\tyageroptimized\thamachergrid\thamacheroptimized\n";
			String resultpUnion = "yagergrid\tyageroptimized\thamachergrid\thamacheroptimized\n";
			String resultTimeUnion = "yagergrid\tyageroptimized\thamachergrid\thamacheroptimized\n";

			EvaluationData eval = DataSetChooser.getData(d);
			TaskData td = new TaskData(new GoldStandard(eval.getReferenceMapping(), eval.getSourceCache().getAllUris(),
					eval.getTargetCache().getAllUris()), eval.getSourceCache(), eval.getTargetCache(), eval);
			td.dataName = eval.getName();
			PropertyMapping pm = eval.getPropertyMapping();
			List<MappingWithFM> mappings = new ArrayList<>();
			GoldStandard gs = new GoldStandard(eval.getReferenceMapping());
			for (PairSimilar<String> propPair : pm.stringPropPairs) {
				for (String measure : defaultMeasures) {
					String metricExpression = measure + "(x." + propPair.a + ",y." + propPair.b + ")";
					MappingWithFM bestMapping = new MappingWithFM(null, 0.0);
					for (double threshold = 1d; threshold > minPropertyCoverage; threshold = threshold
							* propertyLearningRate) {
						AMapping set = executeAtomicMeasure(metricExpression, threshold, eval.getSourceCache(),
								eval.getTargetCache());
						double quality = new FMeasure().calculate(set, gs);
						if (quality > bestMapping.quality) {
							bestMapping = new MappingWithFM(set, quality);
						}
					}
					if (bestMapping.quality > 0.0) {
						mappings.add(bestMapping);
					}
				}
			}
			Collections.sort(mappings, (o1, o2) -> Double.compare(o1.quality, o2.quality) * -1);
			mappings = mappings.subList(0, 5);
			for (MappingWithFM m : mappings) {
				System.out.println("=====");
				System.out.println("fm: " + m.quality);
				System.out.println("p: " + new Precision().calculate(m.map, gs));
				System.out.println("r: " + new Recall().calculate(m.map, gs));
			}
			// FMeasure fm = new FMeasure();
			// Precision prec = new Precision();
			// Recall rec = new Recall();
			// List<List<Integer>> pairings = ImmutableList.of(ImmutableList.of(0, 1),
			// ImmutableList.of(0, 2),
			// ImmutableList.of(0, 3), ImmutableList.of(0, 4), ImmutableList.of(1, 2),
			// ImmutableList.of(1, 3),
			// ImmutableList.of(1, 4), ImmutableList.of(2, 3), ImmutableList.of(2, 4),
			// ImmutableList.of(3, 4));
			// for (List<Integer> l : pairings) {
			// long time = System.currentTimeMillis();
			// Pair<AMapping, Double> yagerfineGridedIntersection =
			// fineGrid(mappings.get(l.get(0)).map,
			// mappings.get(l.get(1)).map, gs, true);
			// long yagerfineGridedIntersectionTime = System.currentTimeMillis() - time;
			// time = System.currentTimeMillis();
			// Pair<AMapping, Double> yageroptimizedIntersection =
			// YagerSetOperations.INSTANCE
			// .intersection(mappings.get(l.get(0)).map, mappings.get(l.get(1)).map,
			// eval.getReferenceMapping());
			// long yageroptimizedIntersectionTime = System.currentTimeMillis() - time;
			// time = System.currentTimeMillis();
			// Pair<AMapping, Double> yagerfineGridedUnion =
			// fineGrid(mappings.get(l.get(0)).map,
			// mappings.get(l.get(1)).map, gs, false);
			// long yagerfineGridedUnionTime = System.currentTimeMillis() - time;
			// time = System.currentTimeMillis();
			// Pair<AMapping, Double> yageroptimizedUnion = YagerSetOperations.INSTANCE
			// .union(mappings.get(l.get(0)).map, mappings.get(l.get(1)).map,
			// eval.getReferenceMapping());
			// long yageroptimizedUnionTime = System.currentTimeMillis() - time;
			// time = System.currentTimeMillis();
			//
			// Pair<AMapping, Double> hamacherfineGridedIntersection =
			// fineGridHam(mappings.get(l.get(0)).map,
			// mappings.get(l.get(1)).map, gs, true);
			// long hamacherfineGridedIntersectionTime = System.currentTimeMillis() - time;
			// time = System.currentTimeMillis();
			// Pair<AMapping, Double> hamacheroptimizedIntersection =
			// HamacherSetOperations.INSTANCE
			// .intersection(mappings.get(l.get(0)).map, mappings.get(l.get(1)).map,
			// eval.getReferenceMapping());
			// long hamacheroptimizedIntersectionTime = System.currentTimeMillis() - time;
			// time = System.currentTimeMillis();
			// Pair<AMapping, Double> hamacherfineGridedUnion =
			// fineGridHam(mappings.get(l.get(0)).map,
			// mappings.get(l.get(1)).map, gs, false);
			// long hamacherfineGridedUnionTime = System.currentTimeMillis() - time;
			// time = System.currentTimeMillis();
			// Pair<AMapping, Double> hamacheroptimizedUnion =
			// HamacherSetOperations.INSTANCE
			// .union(mappings.get(l.get(0)).map, mappings.get(l.get(1)).map,
			// eval.getReferenceMapping());
			// long hamacheroptimizedUnionTime = System.currentTimeMillis() - time;
			// time = System.currentTimeMillis();
			//
			// double yagerfineGridedIntersectionFM =
			// fm.calculate(yagerfineGridedIntersection.getFirst(), gs);
			// double yageroptimizedIntersectionFM =
			// fm.calculate(yageroptimizedIntersection.getFirst(), gs);
			// double yagerfineGridedUnionFM = fm.calculate(yagerfineGridedUnion.getFirst(),
			// gs);
			// double yageroptimizedUnionFM = fm.calculate(yageroptimizedUnion.getFirst(),
			// gs);
			//
			// double hamacherfineGridedIntersectionFM =
			// fm.calculate(hamacherfineGridedIntersection.getFirst(), gs);
			// double hamacheroptimizedIntersectionFM =
			// fm.calculate(hamacheroptimizedIntersection.getFirst(), gs);
			// double hamacherfineGridedUnionFM =
			// fm.calculate(hamacherfineGridedUnion.getFirst(), gs);
			// double hamacheroptimizedUnionFM =
			// fm.calculate(hamacheroptimizedUnion.getFirst(), gs);
			// if (Math.abs(hamacheroptimizedUnionFM - hamacherfineGridedUnionFM) > 0) {
			// System.err.println("opti: " + hamacheroptimizedUnionFM + " grid: " +
			// hamacherfineGridedUnionFM);
			// System.err.println(
			// "opti: " + hamacheroptimizedUnion.getSecond() + " grid: " +
			// hamacherfineGridedUnion.getSecond());
			// }
			//
			// double yagerfineGridedIntersectionPREC =
			// prec.calculate(yagerfineGridedIntersection.getFirst(), gs);
			// double yageroptimizedIntersectionPREC =
			// prec.calculate(yageroptimizedIntersection.getFirst(), gs);
			// double yagerfineGridedUnionPREC =
			// prec.calculate(yagerfineGridedUnion.getFirst(), gs);
			// double yageroptimizedUnionPREC =
			// prec.calculate(yageroptimizedUnion.getFirst(), gs);
			//
			// double hamacherfineGridedIntersectionPREC =
			// prec.calculate(hamacherfineGridedIntersection.getFirst(),
			// gs);
			// double hamacheroptimizedIntersectionPREC =
			// prec.calculate(hamacheroptimizedIntersection.getFirst(), gs);
			// double hamacherfineGridedUnionPREC =
			// prec.calculate(hamacherfineGridedUnion.getFirst(), gs);
			// double hamacheroptimizedUnionPREC =
			// prec.calculate(hamacheroptimizedUnion.getFirst(), gs);
			//
			// double yagerfineGridedIntersectionREC =
			// rec.calculate(yagerfineGridedIntersection.getFirst(), gs);
			// double yageroptimizedIntersectionREC =
			// rec.calculate(yageroptimizedIntersection.getFirst(), gs);
			// double yagerfineGridedUnionREC =
			// rec.calculate(yagerfineGridedUnion.getFirst(), gs);
			// double yageroptimizedUnionREC = rec.calculate(yageroptimizedUnion.getFirst(),
			// gs);
			//
			// double hamacherfineGridedIntersectionREC =
			// rec.calculate(hamacherfineGridedIntersection.getFirst(), gs);
			// double hamacheroptimizedIntersectionREC =
			// rec.calculate(hamacheroptimizedIntersection.getFirst(), gs);
			// double hamacherfineGridedUnionREC =
			// rec.calculate(hamacherfineGridedUnion.getFirst(), gs);
			// double hamacheroptimizedUnionREC =
			// rec.calculate(hamacheroptimizedUnion.getFirst(), gs);
			//
			// resultFMIntersection += yagerfineGridedIntersectionFM + "\t" +
			// yageroptimizedIntersectionFM + "\t"
			// + hamacherfineGridedIntersectionFM + "\t" + hamacheroptimizedIntersectionFM +
			// "\n";
			// resultPrecIntersection += yagerfineGridedIntersectionPREC + "\t" +
			// yageroptimizedIntersectionPREC + "\t"
			// + hamacherfineGridedIntersectionPREC + "\t" +
			// hamacheroptimizedIntersectionPREC + "\n";
			// resultRecIntersection += yagerfineGridedIntersectionREC + "\t" +
			// yageroptimizedIntersectionREC + "\t"
			// + hamacherfineGridedIntersectionREC + "\t" + hamacheroptimizedIntersectionREC
			// + "\n";
			// resultpIntersection += yagerfineGridedIntersection.getSecond() + "\t"
			// + yageroptimizedIntersection.getSecond()
			// + "\t" + hamacherfineGridedIntersection.getSecond() + "\t"
			// + hamacheroptimizedIntersection.getSecond() + "\n";
			// resultTimeIntersection += yagerfineGridedIntersectionTime + "\t" +
			// yageroptimizedIntersectionTime + "\t"
			// + hamacherfineGridedIntersectionTime + "\t" +
			// hamacheroptimizedIntersectionTime + "\n";
			//
			// resultFMUnion += yagerfineGridedUnionFM + "\t" + yageroptimizedUnionFM + "\t"
			// + hamacherfineGridedUnionFM + "\t" + hamacheroptimizedUnionFM + "\n";
			// resultPrecUnion += yagerfineGridedUnionPREC + "\t" + yageroptimizedUnionPREC
			// + "\t"
			// + hamacherfineGridedUnionPREC + "\t" + hamacheroptimizedUnionPREC + "\n";
			// resultRecUnion += yagerfineGridedUnionREC + "\t" + yageroptimizedUnionREC +
			// "\t"
			// + hamacherfineGridedUnionREC + "\t" + hamacheroptimizedUnionREC + "\n";
			// resultpUnion += yagerfineGridedUnion.getSecond() + "\t" +
			// yageroptimizedUnion.getSecond() + "\t"
			// + hamacherfineGridedUnion.getSecond() + "\t" +
			// hamacheroptimizedUnion.getSecond() + "\n";
			// resultTimeUnion += yagerfineGridedUnionTime + "\t" + yageroptimizedUnionTime
			// + "\t"
			// + hamacherfineGridedUnionTime + "\t" + hamacheroptimizedUnionTime + "\n";
			// }
			//
			// System.out.println("===FM Intersection===");
			// System.out.println(resultFMIntersection);
			// System.out.println("===PREC Intersection===");
			// System.out.println(resultPrecIntersection);
			// System.out.println("===REC Intersection===");
			// System.out.println(resultRecIntersection);
			// System.out.println("===P Intersection===");
			// System.out.println(resultpIntersection);
			// System.out.println("===Time Intersection===");
			// System.out.println(resultTimeIntersection);
			//
			// System.out.println("===FM Union===");
			// System.out.println(resultFMUnion);
			// System.out.println("===PREC Union===");
			// System.out.println(resultPrecUnion);
			// System.out.println("===REC Union===");
			// System.out.println(resultRecUnion);
			// System.out.println("===P Union===");
			// System.out.println(resultpUnion);
			// System.out.println("===Time Union===");
			// System.out.println(resultTimeUnion);
			//
			// String folder = "/tmp/" + d + "/";
			// new File(folder).mkdir();
			// Files.write(Paths.get(folder + "FMIntersection"),
			// resultFMIntersection.getBytes());
			// Files.write(Paths.get(folder + "PrecIntersection"),
			// resultPrecIntersection.getBytes());
			// Files.write(Paths.get(folder + "RecIntersection"),
			// resultRecIntersection.getBytes());
			// Files.write(Paths.get(folder + "pIntersection"),
			// resultpIntersection.getBytes());
			// Files.write(Paths.get(folder + "TimeIntersection"),
			// resultTimeIntersection.getBytes());
			//
			// Files.write(Paths.get(folder + "FMUnion"), resultFMUnion.getBytes());
			// Files.write(Paths.get(folder + "PrecUnion"), resultPrecUnion.getBytes());
			// Files.write(Paths.get(folder + "RecUnion"), resultRecUnion.getBytes());
			// Files.write(Paths.get(folder + "pUnion"), resultpUnion.getBytes());
			// Files.write(Paths.get(folder + "TimeUnion"), resultTimeUnion.getBytes());
		}

	}

	public static Pair<AMapping, Double> fineGrid(AMapping a, AMapping b, GoldStandard gs, boolean intersection) {
		double best = 0.0;
		double bestP = 0;
		AMapping bestMapping = MappingFactory.createDefaultMapping();
		for (double p = 0; p < 50; p += 0.05) {
			AMapping set = MappingFactory.createDefaultMapping();
			double fm;
			if (intersection) {
				set = YagerSetOperations.INSTANCE.intersection(a, b, p);
				fm = new FMeasure().calculate(set, gs);
			} else {
				set = YagerSetOperations.INSTANCE.union(a, b, p);
				fm = SimFuzzyRMSE.INSTANCE.getSimilarity(set, gs.referenceMappings);
			}
			if (fm > best) {
				bestMapping = set;
				best = fm;
				bestP = p;
			}
		}
		return new Pair<AMapping, Double>(bestMapping, bestP);
	}

	public static Pair<AMapping, Double> fineGridHam(AMapping a, AMapping b, GoldStandard gs, boolean intersection) {
		double best = 0.0;
		double bestP = 0;
		AMapping bestMapping = MappingFactory.createDefaultMapping();
		for (double p = 0; p < 50; p += 0.05) {
			AMapping set = MappingFactory.createDefaultMapping();
			if (intersection) {
				set = HamacherSetOperations.INSTANCE.intersection(a, b, p);
			} else {
				set = HamacherSetOperations.INSTANCE.union(a, b, p);
			}
			double fm = SimFuzzyRMSE.INSTANCE.getSimilarity(set, gs.referenceMappings);
			if (fm > best) {
				bestMapping = set;
				best = fm;
				bestP = p;
			}
		}
		return new Pair<AMapping, Double>(bestMapping, bestP);
	}
	private static class MappingWithFM {
		AMapping map;
		double quality;

		public MappingWithFM(AMapping map, double quality) {
			this.map = map;
			this.quality = quality;
		}
	}

	private static AMapping executeAtomicMeasure(String measureExpression, double threshold, ACache sourceCache,
			ACache targetCache) {
		Instruction inst = new Instruction(Instruction.Command.RUN, measureExpression, threshold + "", -1, -1, -1);
		ExecutionEngine ee = ExecutionEngineFactory.getEngine(ExecutionEngineType.DEFAULT, sourceCache, targetCache,
				"?x", "?y");
		Plan plan = new Plan();
		plan.addInstruction(inst);
		return ((SimpleExecutionEngine) ee).executeInstructions(plan);
	}
}
