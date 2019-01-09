package org.aksw.limes.core.gui.controller;

import java.io.File;
import java.io.IOException;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.InstanceProperty;
import org.aksw.limes.core.gui.model.Result;
import org.aksw.limes.core.gui.view.ResultView;
import org.aksw.limes.core.io.cache.ACache;
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
	private final ResultView view;

	private MainController mainController;

	/**
	 * Config to get instance information
	 */
	private Config currentConfig;

	/**
	 * source cache for this result necessary to not get affected by any changes
	 * to the cache that was used to get the results
	 */
	private ACache sourceCache;
	/**
	 * target cache for this result necessary to not get affected by any changes
	 * to the cache that was used to get the results
	 */
	private ACache targetCache;

	/**
	 * Constructor
	 *
	 * @param view
	 *            corresponding view
	 * @param config
	 *            current config
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

	public void setCachesFixed() {
		if (this.sourceCache == null) {
			this.sourceCache = this.currentConfig.getSourceEndpoint().getCache();
		}
		if (this.targetCache == null) {
			this.targetCache = this.currentConfig.getTargetEndpoint().getCache();
		}
	}

	/**
	 * shows the properties of matched instances
	 *
	 * @param item
	 *            the clicked matched instances of the Resultview
	 */
	public void showProperties(Result item) {
		final String sourceURI = item.getSourceURI();
		final String targetURI = item.getTargetURI();

		final ObservableList<InstanceProperty> sourcePropertyList = FXCollections.observableArrayList();
		final ObservableList<InstanceProperty> targetPropertyList = FXCollections.observableArrayList();

		final Instance i1 = this.sourceCache.getInstance(sourceURI);
		final Instance i2 = this.targetCache.getInstance(targetURI);
		for (final String prop : i1.getAllProperties()) {
			String value = "";
			for (final String s : i1.getProperty(prop)) {
				value += s + " ";
			}
			sourcePropertyList.add(new InstanceProperty(prop, value));
		}

		this.view.showSourceInstance(sourcePropertyList);

		for (final String prop : i2.getAllProperties()) {
			String value = "";
			for (final String s : i2.getProperty(prop)) {
				value += s + " ";
			}
			targetPropertyList.add(new InstanceProperty(prop, value));
		}
		this.view.showTargetInstance(targetPropertyList);
	}

	/**
	 * Save results to file
	 *
	 * @param mapping
	 *            results of ResultView
	 * @param file
	 *            Path to File
	 */
	public void saveResults(AMapping mapping, File file) {
		try {
			String format = "";
			if (!file.getName().contains(".")) {
				format = ".ttl";
				file = new File(file.toString() + format);
				System.out.println(file);
			} else {
				format = file.getName().substring(file.getName().lastIndexOf("."), file.getName().length());
			}
			if (format.equals(".csv")) {
				final CSVMappingWriter csvwriter = new CSVMappingWriter();
				csvwriter.write(mapping, file.getAbsolutePath());
			} else {
				final RDFMappingWriter rdfwriter = new RDFMappingWriter();
				rdfwriter.write(mapping, file.getAbsolutePath());
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Saves the learned LinkSpecification to the current configuration and
	 * updates the graph in the main view
	 * 
	 * @param ls
	 */
	public void saveLinkSpec(LinkSpecification ls) {
		this.currentConfig.setMetricExpression(ls.getFullExpression());
		this.mainController.setCurrentConfig(this.currentConfig);
	}

	/**
	 * returns current config
	 * 
	 * @return config
	 */
	public Config getCurrentConfig() {
		return this.currentConfig;
	}

	/**
	 * sets current config
	 * 
	 * @param currentConfig
	 *            current config
	 */
	public void setCurrentConfig(Config currentConfig) {
		this.currentConfig = currentConfig;
	}

}
