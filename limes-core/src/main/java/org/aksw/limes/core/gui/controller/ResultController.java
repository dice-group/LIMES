package org.aksw.limes.core.gui.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.InstanceProperty;
import org.aksw.limes.core.gui.model.Result;
import org.aksw.limes.core.gui.view.ResultView;

/**
 * Controller for Resultview
 * 
 * @author Daniel Obraczka, Sascha Hahne
 *
 */
public class ResultController {

	/**
	 * ResultView to manipulate
	 */
	private ResultView view;

	/**
	 * Config to get instance information
	 */
	private Config currentConfig;

	/**
	 * Constructor
	 * 
	 * @param view
	 *            corresponding view
	 * @param config
	 */
	public ResultController(ResultView view, Config config) {
		this.view = view;
		this.currentConfig = config;
	}

	/**
	 * shows the properties of an instancematch
	 * 
	 * @param item
	 *            the clicked instancematch of the Resultview
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
	 * Save Results to File
	 * 
	 * @param results
	 *            Results of ResultView
	 * @param file
	 *            Path to File
	 */
	public void saveResults(ObservableList<Result> results, File file) {
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			// TODO set relation of thresholds to owl:sameAs, implement factory
			// for .nt

			HashMap<String, String> prefixes = this.currentConfig
					.getPrefixes();
			for (String name : prefixes.keySet()) {
				String prefixToNT = "@prefix ";
				prefixToNT += name.toString() + ": ";
				prefixToNT += "<" + prefixes.get(name) + "> .";
				bufferedWriter.write(prefixToNT);
				bufferedWriter.newLine();
			}

			// For now relation between instances is hardcoded as owl:sameAs
			bufferedWriter
					.write("@prefix owl: <http://www.w3.org/2002/07/owl#> .");
			bufferedWriter.newLine();

			for (Result item : results) {
				String nTriple = "<" + item.getSourceURI() + "> owl:sameAs <"
						+ item.getTargetURI() + "> .";
				bufferedWriter.write(nTriple);
				bufferedWriter.newLine();
			}

			bufferedWriter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
