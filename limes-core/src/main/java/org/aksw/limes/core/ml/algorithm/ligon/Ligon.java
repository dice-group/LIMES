package org.aksw.limes.core.ml.algorithm.ligon;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.mapper.MappingOperations;
import org.aksw.limes.core.ml.algorithm.ActiveMLAlgorithm;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLImplementationType;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.WombatSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ligon {
    static Logger logger = LoggerFactory.getLogger(Ligon.class);

    protected List<NoisyOracle> blackBoxOracles;
    protected List<NoisyOracle> estimatedOracles;

    protected AMapping posExamplesMap = MappingFactory.createDefaultMapping();   
    protected AMapping negExamplesMap = MappingFactory.createDefaultMapping();

    public enum ODDS {
        HARD, RANDOM, EQUIVALENCE, APPROXIMATE
    }

    double  randomOddL = 0.5;

    ACache sourceTrainCache;
    ACache targetTrainCache;

    List<NoisyOracle> noisyOracles;

    protected double wombatBestFmeasure = 1.0;


    public Ligon(AMapping trainigExamplesMap, ACache sourceTrainCache, ACache targetTrainCache, List<NoisyOracle> blackBoxOracles) {
        super();
        this.blackBoxOracles = blackBoxOracles;
//        this.estimatedOracles = FixedSizeList.decorate(Arrays.asList(new NoisyOracle[blackBoxOracles.size()]));
        this.sourceTrainCache = sourceTrainCache;
        this.targetTrainCache = targetTrainCache;
        this.noisyOracles = blackBoxOracles;
        this.estimatedOracles = new ArrayList<>();
        for(NoisyOracle o :blackBoxOracles){
            this.estimatedOracles.add(new NoisyOracle(null, new ConfusionMatrix(0.5d)));
        }
    }

    /**
     * Process 1 link by all black box oracles
     * 
     * @param subject
     * @param Object
     * @return vector of all black box oracles results
     */
    protected  List<Boolean> getOracleFeedback(String subject, String object){
        List<Boolean> result = new ArrayList<>();
        for(int i = 0 ; i < blackBoxOracles.size() ; i++){
            result.set(i, blackBoxOracles.get(i).predict(subject, object));
        }
        return result;
    }


    public void updateOraclesConfusionMatrices(AMapping labeledExamples){
        for (String subject : labeledExamples.getMap().keySet()) {
            for (String object : labeledExamples.getMap().get(subject).keySet()) {
                double confidence = labeledExamples.getConfidence(subject, object);
                for(int i = 0 ; i < blackBoxOracles.size() ; i++){
                    if(confidence == 1.0d){ //positive example
                        if(blackBoxOracles.get(i).predict(subject, object)){ //true prediction 
                            estimatedOracles.get(i).confusionMatrix.incrementCountOfRightClassifiedPositiveExamples();
                        }else{ //false prediction
                            estimatedOracles.get(i).confusionMatrix.incrementCountOfWrongClassifiedPositiveExamples();
                        }
                    }
                    else 
                        if(confidence == 0.0d){ //negative example
                            if(blackBoxOracles.get(i).predict(subject, object)){ //true prediction
                                estimatedOracles.get(i).confusionMatrix.incrementCountOfWrongClassifiedNegativeExamples();
                            }else{ //false prediction
                                estimatedOracles.get(i).confusionMatrix.incrementCountOfRightClassifiedNegativeExamples();
                            }
                        }
                }
            }
        }
    }

    public double computeOdds(String subject, String object, ODDS odds){
        double result = 0.0d;
        for(int i = 0 ; i < estimatedOracles.size() ; i++){
            result += 
                    Math.log(estimatedOracles.get(i).confusionMatrix.getProbabilityOfRightClassifiedPositiveExamples()) +
                    Math.log(estimatedOracles.get(i).confusionMatrix.getProbabilityOfWrongClassifiedPositiveExamples()) -
                    Math.log(estimatedOracles.get(i).confusionMatrix.getProbabilityOfRightClassifiedNegativeExamples()) -
                    Math.log(estimatedOracles.get(i).confusionMatrix.getProbabilityOfWrongClassifiedNegativeExamples());
        }
        double oddsL = 1.0d;
        switch (odds) {
            case APPROXIMATE :
                oddsL = wombatBestFmeasure;
                break;
            case EQUIVALENCE :
                double minKbSize = (sourceTrainCache.size() < targetTrainCache.size()) ? sourceTrainCache.size() : targetTrainCache.size();
                oddsL = minKbSize / (double)(sourceTrainCache.size() * targetTrainCache.size() - minKbSize);
                break;
            case RANDOM :
                oddsL = randomOddL;
                break;
            case HARD :
            default :
                oddsL = 1.0d;
                break;
        }
        return result + Math.log(oddsL);
    }

    protected AMapping classifyUnlabeledExamples(AMapping unlabeledexamples, double k, ODDS odds){
        AMapping labeledExamples = MappingFactory.createDefaultMapping(); 
        for (String subject : unlabeledexamples.getMap().keySet()) {
            for (String object : unlabeledexamples.getMap().get(subject).keySet()) {
                double OddsValue = computeOdds(subject, object, odds);
                if(OddsValue > k){
                    labeledExamples.add(subject, object, 1.0d);
                }else if(OddsValue < (1/k)){
                    labeledExamples.add(subject, object, 0.0d);
                }
            }
        }
        return labeledExamples;
    }

    
    public AMapping learn(AMapping labeledExamples,double k, ODDS odds, int mostInformativeExamplesCount) throws UnsupportedMLImplementationException{
        int i = 0;
        AMapping newLabeledExamples = labeledExamples;
        while(i < 10){
            updateOraclesConfusionMatrices(newLabeledExamples);
            labeledExamples = MappingOperations.union(labeledExamples, newLabeledExamples);
            AMapping mostInformativeExamples = getMostInformativeExamples(labeledExamples, mostInformativeExamplesCount);
            newLabeledExamples = classifyUnlabeledExamples(mostInformativeExamples, k, odds);
            i++;
        }
        return labeledExamples;
    }

    protected AMapping getMostInformativeExamples(AMapping labeledExamples, int mostInformativeExamplesCount) throws UnsupportedMLImplementationException {
        ActiveMLAlgorithm wombatSimpleActive = null;
        try {
            wombatSimpleActive = MLAlgorithmFactory.createMLAlgorithm(WombatSimple.class,
                    MLImplementationType.SUPERVISED_ACTIVE).asActive();
        } catch (UnsupportedMLImplementationException e) {
            e.printStackTrace();
            fail();
        }
        assert (wombatSimpleActive.getClass().equals(ActiveMLAlgorithm.class));
        wombatSimpleActive.init(null, sourceTrainCache, targetTrainCache);
        MLResults mlModel = wombatSimpleActive.activeLearn(labeledExamples);
        AMapping resultMap = wombatSimpleActive.predict(sourceTrainCache, targetTrainCache, mlModel);
        return wombatSimpleActive.getNextExamples(mostInformativeExamplesCount);
    }







}
