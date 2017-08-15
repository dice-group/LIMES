package org.aksw.limes.core.gui.controller.ml;

import org.aksw.limes.core.gui.model.ml.MachineLearningModel;
import org.aksw.limes.core.gui.view.ml.MachineLearningView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class handles the interaction between the {@link org.aksw.limes.core.gui.view.ml.MachineLearningView}
 *  and the {@link org.aksw.limes.core.gui.model.ml.MachineLearningModel} according to the MVC Pattern for the machine learning.
 *  It implements all methods except the learn method.
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public abstract class MachineLearningController {

    /**
     * the logger for this class
     */
    protected static Logger logger = LoggerFactory.getLogger("LIMES");
    
    /**
     * the corresponding view
     */
    protected MachineLearningView mlView;
    
    /**
     * the corresponding model
     */
    protected MachineLearningModel mlModel;

    /**
     * returns the corresponding model
     * @return mlView
     */
    public MachineLearningView getMlView() {
        return mlView;
    }

    /**
     * sets the corresponding view
     * @param mlView corresponding view
     */
    public void setMlView(MachineLearningView mlView) {
        this.mlView = mlView;
    }

    /**
     * calls {@link MachineLearningModel#initializeData(String)}
     * @param algorithmName name of the algorithm
     */
    public void setMLAlgorithmToModel(String algorithmName) {
        this.mlModel.initializeData(algorithmName);
    }

    /**
     * the abstract learn function to be implemented by the subclasses
     * @param view corresponding view
     */
    public abstract void learn(MachineLearningView view);

    /**
     * returns the corresponding model
     * @return mlModel
     */
    public MachineLearningModel getMlModel() {
        return mlModel;
    }

    /**
     * sets the corresponding model
     * @param mlModel corresponding model
     */
    public void setMlModel(MachineLearningModel mlModel) {
        this.mlModel = mlModel;
    }

}
