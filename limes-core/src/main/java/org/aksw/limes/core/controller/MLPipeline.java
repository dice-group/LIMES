/*
 * LIMES Core Library - LIMES – Link Discovery Framework for Metric Spaces.
 * Copyright © 2011 Data Science Group (DICE) (ngonga@uni-paderborn.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aksw.limes.core.controller;

import org.aksw.limes.core.evaluation.evaluator.EvaluatorFactory;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.Configuration;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.mapping.reader.AMappingReader;
import org.aksw.limes.core.io.mapping.reader.CSVMappingReader;
import org.aksw.limes.core.io.mapping.reader.RDFMappingReader;
import org.aksw.limes.core.ml.algorithm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Execution pipeline for generating mappings using ML.
 * Provides overloaded convenience methods.
 *
 * @author Kevin Dreßler
 */
public class MLPipeline {

    public static final Logger logger = LoggerFactory.getLogger(MLPipeline.class);

    public static AMapping execute(
            ACache source,
            ACache target,
            Configuration configuration,
            String mlAlgorithmName,
            MLImplementationType mlImplementationType,
            List<LearningParameter> learningParameters,
            String trainingDataFile,
            EvaluatorType pfmType,
            int maxIt,
            ActiveLearningOracle oracle
    ) throws UnsupportedMLImplementationException {
        Class<? extends ACoreMLAlgorithm> clazz = MLAlgorithmFactory.getAlgorithmType(mlAlgorithmName);
        MLResults mlm;
        AMapping trainingDataMap = MappingFactory.createDefaultMapping();
        if (mlImplementationType == MLImplementationType.SUPERVISED_BATCH){
            AMappingReader mappingReader;
            if(trainingDataFile.endsWith(".csv")){
                mappingReader = new CSVMappingReader(trainingDataFile);
            }else{
                mappingReader = new RDFMappingReader(trainingDataFile);
            }
            trainingDataMap = mappingReader.read();
        }

        switch (mlImplementationType) {
            case SUPERVISED_BATCH:
                SupervisedMLAlgorithm mls = new SupervisedMLAlgorithm(clazz);
                mls.init(learningParameters, source, target);
                mls.getMl().setConfiguration(configuration);
                mlm = mls.learn(trainingDataMap);
                logger.info("Learned: " + mlm.getLinkSpecification().getFullExpression() + " with threshold: " + mlm.getLinkSpecification().getThreshold());
                AMapping mappingMls = mls.predict(source, target, mlm);
                mappingMls.setLinkSpecification(mlm.getLinkSpecification());
                return mappingMls;
            case SUPERVISED_ACTIVE:
                // for active learning, need to reiterate and prompt the user for evaluation of examples:
                //            boolean stopLearning = false;
                ActiveMLAlgorithm mla = new ActiveMLAlgorithm(clazz);
                mla.init(learningParameters, source, target);
                mla.getMl().setConfiguration(configuration);
                mlm = mla.activeLearn();
                while (!oracle.isStopped()) {
                    AMapping nextExamplesMapping = mla.getNextExamples(maxIt);
                    if (nextExamplesMapping.getMap().isEmpty()) {
                        oracle.stop();
                        break;
                    }
                    logger.info(nextExamplesMapping.toString());
                    ActiveLearningExamples activeLearningExamples = new ActiveLearningExamples(nextExamplesMapping, source, target);
                    AMapping classify = oracle.classify(activeLearningExamples);
                    logger.info(classify.toString());
                    mlm = mla.activeLearn(classify);
                }
                logger.info("Learned: " + mlm.getLinkSpecification().getFullExpression() + " with threshold: " + mlm.getLinkSpecification().getThreshold());
                AMapping mappingMla = mla.predict(source, target, mlm);
                mappingMla.setLinkSpecification(mlm.getLinkSpecification());
                return mappingMla;
            case UNSUPERVISED:
                UnsupervisedMLAlgorithm mlu = new UnsupervisedMLAlgorithm(clazz);
                mlu.init(learningParameters, source, target);
                mlu.getMl().setConfiguration(configuration);
                PseudoFMeasure pfm = null;
                if(pfmType != null){
                    pfm = (PseudoFMeasure) EvaluatorFactory.create(pfmType);
                }
                mlm = mlu.learn(pfm);
                logger.info("Learned: " + mlm.getLinkSpecification().getFullExpression() + " with threshold: " + mlm.getLinkSpecification().getThreshold());
                AMapping mappingMlu = mlu.predict(source, target, mlm);
                mappingMlu.setLinkSpecification(mlm.getLinkSpecification());
                return mappingMlu;
            default:
                throw new UnsupportedMLImplementationException(clazz.getName());
        }
    }
}
