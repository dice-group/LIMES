package org.aksw.limes.core.evaluation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aksw.limes.core.datastrutures.EvaluationRun;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.evaluator.Summary;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class SummaryTest {
	public List<EvaluationRun> runs;
	public static final String algo1 = "algo1";
	public static final String algo2 = "algo2";

	@Before
	public void prepareData() {
		EvaluationRun e11 = new EvaluationRun(algo1, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1));
		EvaluationRun e12 = new EvaluationRun(algo1, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1));
		EvaluationRun e13 = new EvaluationRun(algo1, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1));
		EvaluationRun e14 = new EvaluationRun(algo1, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1));
		EvaluationRun e15 = new EvaluationRun(algo1, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1));

		EvaluationRun e21 = new EvaluationRun(algo1, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1));
		EvaluationRun e22 = new EvaluationRun(algo1, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1));
		EvaluationRun e23 = new EvaluationRun(algo1, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1));
		EvaluationRun e24 = new EvaluationRun(algo1, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1));
		EvaluationRun e25 = new EvaluationRun(algo1, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1));

		EvaluationRun e31 = new EvaluationRun(algo2, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.1));
		EvaluationRun e32 = new EvaluationRun(algo2, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.2));
		EvaluationRun e33 = new EvaluationRun(algo2, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.4));
		EvaluationRun e34 = new EvaluationRun(algo2, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.6));
		EvaluationRun e35 = new EvaluationRun(algo2, "supervised", "data1",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.5));

		EvaluationRun e41 = new EvaluationRun(algo2, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.9));
		EvaluationRun e42 = new EvaluationRun(algo2, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 1.0));
		EvaluationRun e43 = new EvaluationRun(algo2, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.999));
		EvaluationRun e44 = new EvaluationRun(algo2, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.9828201));
		EvaluationRun e45 = new EvaluationRun(algo2, "supervised", "data2",
				ImmutableMap.of(EvaluatorType.F_MEASURE, 0.978));

		runs = new ArrayList<>();
		runs.add(e11);
		runs.add(e12);
		runs.add(e13);
		runs.add(e14);
		runs.add(e15);
		runs.add(e21);
		runs.add(e22);
		runs.add(e23);
		runs.add(e24);
		runs.add(e25);
		runs.add(e31);
		runs.add(e32);
		runs.add(e33);
		runs.add(e34);
		runs.add(e35);
		runs.add(e41);
		runs.add(e42);
		runs.add(e43);
		runs.add(e44);
		runs.add(e45);
	}

	@Test
	public void testSummary() {
		Summary s = new Summary(runs, 5);
		List<EvaluationRun> avgRuns = s.getAveragedRuns();
		assertEquals(4, avgRuns.size());
		avgRuns.sort((e1, e2) -> {
			if (e1.getAlgorithmName().equals(e2.getAlgorithmName())) {
				return e1.getDatasetName().compareTo(e2.getDatasetName());
			}
			return e1.getAlgorithmName().compareTo(e2.getAlgorithmName());
		});
		System.out.println(avgRuns);
		assertEquals(0.1, avgRuns.get(0).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);
		assertEquals(0.1, avgRuns.get(1).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);
		assertEquals(0.36, avgRuns.get(2).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);
		assertEquals(0.97196402, avgRuns.get(3).qualititativeScores.get(EvaluatorType.F_MEASURE), 0);
		Map<String, Map<String, Double>> statistics = new HashMap<>();
		Map<String, Double> inner = new HashMap<>();
		inner.put(algo2, 23.44);
		statistics.put(algo1, inner);
		s.setStatisticalTestResults(statistics);
		System.out.println(s);
	}

}
