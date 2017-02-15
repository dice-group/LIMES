package org.aksw.limes.core.gui.view;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.metric.Measure;
import org.aksw.limes.core.gui.model.metric.Node;
import org.aksw.limes.core.gui.model.metric.Operator;
import org.aksw.limes.core.gui.model.metric.Property;
import org.aksw.limes.core.gui.util.sparql.PrefixHelper;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

/**
 * Panel in MainView which contains the specific nodes, measures and operators
 * to add to the graph
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class ToolBox extends VBox {

	/**
	 * List of the SourceProperties
	 */
	private ListView<String> toolBoxSourceProperties;

	/**
	 * List of the Target Properties
	 */
	private ListView<String> toolBoxTargetProperties;

	/**
	 * List of the available Operators
	 */
	private ListView<String> toolBoxOperators;

	/**
	 * List of the available Metrics/Measurements
	 */
	private ListView<String> toolBoxMetrics;

	/**
	 * label describing the source properties if no class name is given it is
	 * going to be "source properties"
	 */
	private Label sourcePropertiesLabel = new Label("source properties");

	/**
	 * label describing the target properties if no class name is given it is
	 * going to be "target properties"
	 */
	private Label targetPropertiesLabel = new Label("target properties");

	/**
	 * Config of current Limesquery
	 */
	private Config config;

	private MainView view;

	/**
	 * Constructor builds view and adds listeners to elements
	 * 
	 * @param view
	 *            main view
	 */
	public ToolBox(MainView view) {
		generateView(this);
		setListeners();
		this.config = null;
		this.view = view;
	}

	/**
	 * Generates the View adds the ListViews to the right Places
	 *
	 * @param box
	 */
	private void generateView(VBox box) {
		toolBoxSourceProperties = new ListView<String>();
		toolBoxSourceProperties.setId("toolBoxSourceProperties");
		toolBoxSourceProperties.setTooltip(new Tooltip("Add source properties to metric builder by clicking"));
		toolBoxTargetProperties = new ListView<String>();
		toolBoxTargetProperties.setId("toolBoxTargetProperties");
		toolBoxTargetProperties.setTooltip(new Tooltip("Add target properties to metric builder by clicking"));
		toolBoxMetrics = generateListViewFromNodeIdentifiers(new Measure("").identifiers());
		toolBoxMetrics.setTooltip(new Tooltip("Add measures to metric builder by clicking"));
		toolBoxOperators = generateListViewFromNodeIdentifiers(Operator.identifiers);
		toolBoxOperators.setTooltip(new Tooltip("Add operators to metric builder by clicking"));

		box.getChildren().add(sourcePropertiesLabel);
		box.getChildren().add(toolBoxSourceProperties);
		box.getChildren().add(targetPropertiesLabel);
		box.getChildren().add(toolBoxTargetProperties);
		box.getChildren().add(new Label("Measures"));
		box.getChildren().add(toolBoxMetrics);
		box.getChildren().add(new Label("Operators"));
		box.getChildren().add(toolBoxOperators);

	}

	/**
	 * Sets the CLickListeners for the ListViews On each Click a Node will
	 * appear in the GraphCanvas
	 */
	private void setListeners() {
		toolBoxSourceProperties.setOnMouseClicked(e -> {
			generateProperty(toolBoxSourceProperties, 4, true);
		});
		toolBoxTargetProperties.setOnMouseClicked(e -> {
			generateProperty(toolBoxTargetProperties, 5, false);
		});
		toolBoxMetrics.setOnMouseClicked(e -> {
			generateNode(toolBoxMetrics, 1);
		});
		toolBoxOperators.setOnMouseClicked(e -> {
			generateNode(toolBoxOperators, 3);
		});
	}

	/**
	 * Set the Items in View
	 *
	 * @param view
	 *            to manipulate
	 * @param items
	 *            list of the items for the listview
	 */
	private void setListViewFromList(ListView<String> view, List<String> items) {
		ObservableList<String> listItems = FXCollections.observableArrayList();
		items.forEach(itemString -> {
			listItems.add(itemString);
		});
		
		//Avoid not on FX application thread problem
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				view.setItems(listItems);
			}
		});

	}

	/**
	 * Generates a List View from the Static Measure and Operator identifiers
	 *
	 * @param nodeIdentifiers
	 *            identifiers for the List
	 * @return ListView containing the indentifiers as items
	 */
	private ListView<String> generateListViewFromNodeIdentifiers(Set<String> nodeIdentifiers) {
		ObservableList<String> listItems = FXCollections.observableArrayList();

		nodeIdentifiers.forEach((identifier) -> {
			listItems.add(identifier);
		});
		java.util.Collections.sort(listItems);
		return new ListView<String>(listItems);

	}

	/**
	 * Generates a new Porperty-Node from the seledted item in the List
	 *
	 * @param view
	 *            ListView of the selected Property
	 * @param origin
	 *            True if source
	 */
	private void generateProperty(ListView<String> view, int shape, boolean origin) {
		if (view.getSelectionModel().getSelectedItem() != null) {
			Property gen = null;
			if (origin) {
				gen = new Property(config.getPropertyString(view.getSelectionModel().getSelectedItem(), SOURCE),
						Property.Origin.SOURCE);
			} else {
				gen = new Property(config.getPropertyString(view.getSelectionModel().getSelectedItem(), TARGET),
						Property.Origin.TARGET);

			}

			setNodeToGraph(gen, shape);
		}
	}

	/**
	 * Adds the Node in the GraphCanvas
	 *
	 * @param e
	 *            Node to be added
	 */
	private void setNodeToGraph(Node e, int shape) {
		view.graphBuild.addNode(200, 200, shape, e);
	}

	/**
	 * Generates Operator or Measure Node from ListView
	 *
	 * @param view
	 *            ListView of the selected Item
	 */
	private void generateNode(ListView<String> view, int shape) {
		if (view.getSelectionModel().getSelectedItem() != null) {
			Node node = Node.createNode(view.getSelectionModel().getSelectedItem());
			setNodeToGraph(node, shape);
		}

	}

	/**
	 * Called on changing of the Source and Target Properties
	 *
	 * @param config
	 *            Config of current Limesquery
	 */
	public void showLoadedConfig(Config config) {
		this.config = config;
		if (config.getSourceInfo().getFunctions() != null) {
			List<String> sourceProperties = new ArrayList<String>();
			for (String prop : config.getSourceInfo().getProperties()) {
				if (config.getSourceInfo().getFunctions().get(prop).keySet().toArray().length == 1) {
					sourceProperties
							.add((String) config.getSourceInfo().getFunctions().get(prop).keySet().toArray()[0]);
				} else {
					sourceProperties.add(PrefixHelper.abbreviate(prop));
				}
			}
			setListViewFromList(toolBoxSourceProperties, sourceProperties);
		} else {
			setListViewFromList(toolBoxSourceProperties, config.getSourceInfo().getProperties());
		}
		if (config.getTargetInfo().getFunctions() != null) {
			List<String> targetProperties = new ArrayList<String>();
			for (String prop : config.getTargetInfo().getProperties()) {
				if (config.getTargetInfo().getFunctions().get(prop).keySet().toArray().length == 1) {
					targetProperties
							.add((String) config.getTargetInfo().getFunctions().get(prop).keySet().toArray()[0]);
				} else {
					targetProperties.add(PrefixHelper.abbreviate(prop));
				}
			}
			setListViewFromList(toolBoxTargetProperties, targetProperties);
		} else {
			setListViewFromList(toolBoxTargetProperties, config.getTargetInfo().getProperties());
		}
		//Avoid not on FX application thread problem
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				sourcePropertiesLabel.setText(config.getSourceEndpoint().getCurrentClass().getName() + " properties");
				targetPropertiesLabel.setText(config.getTargetEndpoint().getCurrentClass().getName() + " properties");
			}
		});
	}

	public ListView<String> getToolBoxSourceProperties() {
		return toolBoxSourceProperties;
	}

	public ListView<String> getToolBoxTargetProperties() {
		return toolBoxTargetProperties;
	}

}
