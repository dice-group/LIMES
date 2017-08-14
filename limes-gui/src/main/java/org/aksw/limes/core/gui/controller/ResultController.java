package org.aksw.limes.core.gui.controller;

import java.io.File;
import java.io.IOException;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.InstanceProperty;
import org.aksw.limes.core.gui.model.Result;
import org.aksw.limes.core.gui.view.ResultView;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.ls.LinkSpecification;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.writer.CSVMappingWriter;
import org.aksw.limes.core.io.mapping.writer.RDFMappingWriter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Controller for Resultview
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class ResultController {

    /**
     * ResultView to manipulate
     */
    private ResultView view;
    
    private MainController mainController;

    /**
     * Config to get instance information
     */
    private Config currentConfig;
    

    /**
     * Constructor
     *
     * @param view
     *         corresponding view
     * @param config current config
     */
    public ResultController(ResultView view, Config config) {
        this.view = view;
        this.currentConfig = config;
    }

    public ResultController(ResultView view, Config config, MainController mainController) {
        this.view = view;
        this.currentConfig = config;
        this.mainController = mainController;
    }

    /**
     * shows the properties of matched instances
     *
     * @param item
     *         the clicked matched instances of the Resultview
     */
    public void showProperties(Result item) {
        String sourceURI = item.getSourceURI();
        String targetURI = item.getTargetURI();

        ObservableList<InstanceProperty> sourcePropertyList = FXCollections
                .observableArrayList();
        ObservableList<InstanceProperty> targetPropertyList = FXCollections
                .observableArrayList();

        Instance i1 = currentConfig.getSourceEndpoint().getCache()
                .getInstance(sourceURI);
        Instance i2 = currentConfig.getTargetEndpoint().getCache()
                .getInstance(targetURI);
        for (String prop : i1.getAllProperties()) {
            String value = "";
            for (String s : i1.getProperty(prop)) {
                value += s + " ";
            }
            sourcePropertyList.add(new InstanceProperty(prop, value));

        }

        view.showSourceInstance(sourcePropertyList);

        for (String prop : i2.getAllProperties()) {
            String value = "";
            for (String s : i2.getProperty(prop)) {
                value += s + " ";
            }
            targetPropertyList.add(new InstanceProperty(prop, value));
        }
        view.showTargetInstance(targetPropertyList);
    }

    /**
     * Save results to file
     *
     * @param mapping
     *         results of ResultView
     * @param file
     *         Path to File
     */
    public void saveResults(AMapping mapping, File file) {
        try {
            String format = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());
            if (format.equals(".csv")) {
                CSVMappingWriter csvwriter = new CSVMappingWriter();
                csvwriter.write(mapping, file.getAbsolutePath());
            } else {
                RDFMappingWriter rdfwriter = new RDFMappingWriter();
                rdfwriter.write(mapping, file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    /**
     * Saves the learned LinkSpecification to the current configuration and updates the graph in the main view
     * @param ls
     */
    public void saveLinkSpec(LinkSpecification ls){
	currentConfig.setMetricExpression(ls.getFullExpression());
	mainController.setCurrentConfig(currentConfig);
    }

    /**
     * returns current config
     * @return config
     */
    public Config getCurrentConfig() {
        return currentConfig;
    }

    /**
     * sets current config
     * @param currentConfig current config
     */
    public void setCurrentConfig(Config currentConfig) {
        this.currentConfig = currentConfig;
    }

}
