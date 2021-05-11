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
package org.aksw.limes.core.ml.algorithm.euclid;

import org.aksw.limes.core.evaluation.qualititativeMeasures.PseudoFMeasure;
import org.aksw.limes.core.exceptions.UnsupportedMLImplementationException;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.MLAlgorithmFactory;
import org.aksw.limes.core.ml.algorithm.MLResults;
import org.aksw.limes.core.ml.algorithm.classifier.ComplexClassifier;
import org.aksw.limes.core.ml.algorithm.classifier.SimpleClassifier;
import org.aksw.limes.core.ml.algorithm.euclid.LinearSelfConfigurator.QMeasureType;
import org.apache.log4j.Logger;

import java.util.List;
/**
 * Class wraps around EUCLIDs meshbased classifiers to abide LIMES ml interface
 * @author Klaus Lyko (lyko@informatik.uni-leipzig.de)
 */
public class MeshEuclid extends BooleanEuclid {
    protected static Logger logger = Logger.getLogger(MeshEuclid.class);

    public static final String ALGORITHM_NAME = MLAlgorithmFactory.EUCLID_MESH;

    public static final String GRID_POINTS = "grid_points";

    @Override
    protected void init(List<LearningParameter> learningParameters, ACache sourceCache, ACache targetCache) {
        setDefaultParameters();
        super.init(learningParameters, sourceCache, targetCache);
        lsc = new MeshBasedSelfConfigurator(sourceCache, targetCache);
    }

    @Override
    public void setDefaultParameters() {
        int grid_points = 5;
        super.setDefaultParameters();
        learningParameters.add(new LearningParameter(GRID_POINTS, grid_points, Integer.class, 0d, 1d, 1, GRID_POINTS));
    }


    @Override
    protected MLResults learn(PseudoFMeasure pfm) throws UnsupportedMLImplementationException {
        MeshBasedSelfConfigurator lsc = new MeshBasedSelfConfigurator(sourceCache, targetCache);
        // first setup EUCLID
        configureEuclid(lsc);
        lsc.setPFMType(QMeasureType.UNSUPERVISED);
        // get initial classifiers
        List<SimpleClassifier> init_classifiers = lsc.getBestInitialClassifiers();
        logger.info("Initial classifiers: "+init_classifiers.size());
        ComplexClassifier complex =  lsc.getZoomedHillTop((int) getParameter(ITERATIONS_MAX), (int)getParameter(GRID_POINTS), init_classifiers);
        // compute results
        MLResults result = new MLResults();
        AMapping mapping = lsc.getMapping(complex.getClassifiers());
        result.setMapping(complex.getMapping());
        result.setQuality(lsc.computeQuality(mapping));
        result.setLinkSpecification(lsc.getLinkSpecification(complex.getClassifiers()));
        result.setClassifiers(complex.getClassifiers());
        for(int i = 0; i<complex.getClassifiers().size(); i++) {
            result.addDetail(i+". Classifier ", complex.getClassifiers().get(i));
            AMapping map = lsc.executeClassifier(complex.getClassifiers().get(i), complex.getClassifiers().get(i).getThreshold());
            result.addDetail(i+". Mapping.size= ", map.size());
        }
        return result;
    }

    @Override
    protected String getName() {
        return ALGORITHM_NAME;
    }

    @Override
    protected AMapping predict(ACache source, ACache target, MLResults mlModel) {
        assert (mlModel.classifiersSet());
        List<SimpleClassifier> classifiers = mlModel.getClassifiers();
        assert(classifiers.size()>0);
        MeshBasedSelfConfigurator le = new MeshBasedSelfConfigurator(source, target);
        configureEuclid(le);
        AMapping map = le.getMapping(classifiers);
        logger.info("Should predict with mlModel on Caches +"+source.size()+","+target.size()+"+ resulted in "+map.size()+" map.");
        return map;
    }

}
