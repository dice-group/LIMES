package org.aksw.limes.core.gui.view;


import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.metric.Measure;
import org.aksw.limes.core.gui.model.metric.Node;
import org.aksw.limes.core.gui.model.metric.Operator;
import org.aksw.limes.core.gui.model.metric.Property;

/**
 * Panel in MainView which contains the specific Nodes to add to the Graph
 * 
 * @author Sascha Hahne
 *
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
	 * Config of current Limesquery
	 */
	private Config config;

	private MainView view;

	/**
	 * Constructor
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
		toolBoxTargetProperties = new ListView<String>();
		toolBoxMetrics = generateListViewFromNodeIdentifiers(Measure.identifiers);
		toolBoxOperators = generateListViewFromNodeIdentifiers(Operator.identifiers);
		box.getChildren().add(new Label("Source Properties"));
		box.getChildren().add(toolBoxSourceProperties);
		box.getChildren().add(new Label("Target Properties"));
		box.getChildren().add(toolBoxTargetProperties);
		box.getChildren().add(new Label("Metrics"));
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
		view.setItems(listItems);

	}

	/**
	 * Generates a List View from the Static Measure and Operator identifiers
	 * 
	 * @param nodeIdentifiers
	 *            identifiers for the List
	 * @return ListView containing the indentifiers as items
	 */
	private ListView<String> generateListViewFromNodeIdentifiers(
			Set<String> nodeIdentifiers) {
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
	private void generateProperty(ListView<String> view, int shape,
			boolean origin) {
		if (view.getSelectionModel().getSelectedItem() != null) {
			Property gen = null;
			if (origin) {
				gen = new Property(config.getPropertyString(view
						.getSelectionModel().getSelectedIndex(), SOURCE),
						Property.Origin.SOURCE);
			} else {
				gen = new Property(config.getPropertyString(view
						.getSelectionModel().getSelectedIndex(), TARGET),
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
			Node node = Node.createNode(view.getSelectionModel()
					.getSelectedItem());
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
		setListViewFromList(toolBoxSourceProperties,
				config.getSourceInfo().getProperties());
		setListViewFromList(toolBoxTargetProperties,
				config.getTargetInfo().getProperties());
	}

}
