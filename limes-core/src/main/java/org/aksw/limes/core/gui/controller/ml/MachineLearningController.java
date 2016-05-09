package org.aksw.limes.core.gui.controller.ml;

import org.aksw.limes.core.gui.model.ml.MachineLearningModel;
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;
import org.aksw.limes.core.ml.setting.LearningSetting;
import org.apache.log4j.Logger;

public abstract class MachineLearningController {

	protected MachineLearningView mlView;

	protected MachineLearningModel mlModel;

	protected static Logger logger = Logger.getLogger("LIMES");

	public MachineLearningView getMlView() {
		return mlView;
	}

	public void setMlView(MachineLearningView mlView) {
		this.mlView = mlView;
	}

	public void setMLAlgorithmToModel(String algorithmName) {
		this.mlModel.initializeData(algorithmName);
	}

	public void setParameters() {
		LearningSetting params = this.mlModel.getLearningsetting();
		params.setInquerySize(this.mlView.getInquiriesSpinner().getValue());
		params.setMaxDuration(this.mlView.getMaxDurationSpinner().getValue());
		params.setMaxIteration(this.mlView.getMaxIterationSpinner().getValue());
		params.setMaxQuality(this.mlView.getMaxQualitySlider().getValue());
		params.setTerminationCriteria(
				LearningSetting.TerminationCriteria.valueOf(this.mlView
						.getTerminationCriteriaSpinner().getValue()),
				this.mlView.getTerminationCriteriaValueSlider().getValue());
		params.setBeta(this.mlView.getBetaSlider().getValue());
		if (this.mlModel.getMlalgorithm().getName().startsWith("EAGLE")) {
			System.out.println(this.mlModel.getMlalgorithm().getName());
			params.setGenerations(this.mlView.getGenerationsSpinner()
					.getValue());
			params.setPopulation(this.mlView.getPopulationSpinner().getValue());
			params.setMutationRate((float) this.mlView.getMutationRateSlider()
					.getValue());
			params.setReproductionRate((float) this.mlView
					.getReproductionRateSlider().getValue());
			params.setCrossoverRate((float) this.mlView
					.getCrossoverRateSlider().getValue());
			params.setPreserveFittest(this.mlView.getPreserveFittestCheckBox()
					.isSelected());
			// TODO set measure
		} else if (this.mlModel.getMlalgorithm().getName().equals("LION")) {
			System.out.println(this.mlModel.getMlalgorithm().getName());
			params.setGammaScore(this.mlView.getGammaScoreSlider().getValue());
			params.setExpansionPenalty(this.mlView.getExpansionPenaltySlider()
					.getValue());
			params.setReward(this.mlView.getRewardSlider().getValue());
			params.setPrune(this.mlView.getPruneCheckBox().isSelected());
		}
		if(this.mlModel.getConfig().propertyMapping == null){
			PropertyMapping propMap = new PropertyMapping();
			propMap.setDefault(this.mlModel.getConfig().getSourceInfo(), this.mlModel.getConfig().getTargetInfo());
			params.setPropMap(propMap);
		}
		this.mlModel.setLearningsetting(params);
//		System.out.println("---------------- getLearningSetting()------------------------");
//		System.out.println("maxDuration: " + this.mlModel.getLearningsetting().getMaxDuration());
//		System.out.println("maxIteration: " + this.mlModel.getLearningsetting().getMaxIteration());
//		System.out.println("maxQuality: " + this.mlModel.getLearningsetting().getMaxQuality());
//		System.out.println("terminationCriteria: " + this.mlModel.getLearningsetting().getTerminationCriteria());
//		System.out.println("terminationCriteriaValue: " + this.mlModel.getLearningsetting().getTerminationCriteriaValue());
//		System.out.println("beta: " + this.mlModel.getLearningsetting().getBeta());
//		System.out.println("generations: " + this.mlModel.getLearningsetting().getGenerations());
//		System.out.println("population: " + this.mlModel.getLearningsetting().getPopulation());
//		System.out.println("mutationRate: " + this.mlModel.getLearningsetting().getMutationRate());
//		System.out.println("reproductionRate: " + this.mlModel.getLearningsetting().getReproductionRate());
//		System.out.println("crossoverRate: " + this.mlModel.getLearningsetting().getCrossoverRate());
//		System.out.println("preserveFittest: " + this.mlModel.getLearningsetting().isPreserveFittest());
//		System.out.println("gammaScore: " + this.mlModel.getLearningsetting().getGammaScore());
//		System.out.println("expansionPenalty: " + this.mlModel.getLearningsetting().getExpansionPenalty());
//		System.out.println("reward: " + this.mlModel.getLearningsetting().getReward());
//		System.out.println("prune: " + this.mlModel.getLearningsetting().isPrune());
	}

	public abstract void learn(MachineLearningView view);	

	public MachineLearningModel getMlModel() {
		return mlModel;
	}

	public void setMlModel(MachineLearningModel mlModel) {
		this.mlModel = mlModel;
	}

}
