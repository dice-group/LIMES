package org.aksw.limes.core.gui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.metric.Measure;
import org.aksw.limes.core.gui.model.metric.Node;
import org.aksw.limes.core.gui.model.metric.Operator;
import org.aksw.limes.core.gui.model.metric.Property;
import org.aksw.limes.core.gui.util.SourceOrTarget;
import org.aksw.limes.core.gui.util.sparql.PrefixHelper;
import org.aksw.limes.core.gui.view.graphBuilder.NodeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Panel in MainView which contains the specific nodes, measures and operators
 * to add to the graph
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class ToolBox extends VBox {
    private static final Logger logger = LoggerFactory.getLogger(ToolBox.class);

	/**
	 * List of the SourceProperties
	 */
	private ListView<PropertyItem> toolBoxSourceProperties;

	/**
	 * List of the Target Properties
	 */
	private ListView<PropertyItem> toolBoxTargetProperties;

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
		toolBoxSourceProperties = new ListView<PropertyItem>();
		toolBoxSourceProperties.setId("toolBoxSourceProperties");
		toolBoxSourceProperties.setTooltip(new Tooltip("Add source properties to metric builder by clicking.\n Optional properties are grey. You can make properties optional by right-clicking on them"));
		toolBoxTargetProperties = new ListView<PropertyItem>();
		toolBoxTargetProperties.setId("toolBoxTargetProperties");
		toolBoxTargetProperties.setTooltip(new Tooltip("Add target properties to metric builder by clicking.\n Optional properties are grey. You can make properties optional by right-clicking on them"));
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
			if (((MouseEvent) e).getButton().equals(MouseButton.SECONDARY)) {
				switchPropertyOptional(toolBoxSourceProperties, NodeView.SOURCE);
			} else {
				generateProperty(toolBoxSourceProperties, NodeView.SOURCE);
			}
		});
		toolBoxTargetProperties.setOnMouseClicked(e -> {
			if (((MouseEvent) e).getButton().equals(MouseButton.SECONDARY)) {
				switchPropertyOptional(toolBoxTargetProperties, NodeView.TARGET);
			} else {
				generateProperty(toolBoxTargetProperties, NodeView.TARGET);
			}
		});
		toolBoxMetrics.setOnMouseClicked(e -> {
			generateNode(toolBoxMetrics, NodeView.METRIC);
		});
		toolBoxOperators.setOnMouseClicked(e -> {
			generateNode(toolBoxOperators, NodeView.OPERATOR);
		});
	}

	private void switchPropertyOptional(ListView<PropertyItem> view, int shape) {
		if (view.getSelectionModel().getSelectedItem() != null) {
			if (shape == NodeView.SOURCE) {
			logger.debug("switch source");
				config.switchPropertyOptional(
						config.getPropertyString(view.getSelectionModel().getSelectedItem().getName(), SourceOrTarget.SOURCE), SourceOrTarget.SOURCE);
			} else {
			logger.debug("switch target");
				config.switchPropertyOptional(
						config.getPropertyString(view.getSelectionModel().getSelectedItem().getName(), SourceOrTarget.TARGET), SourceOrTarget.TARGET);
			}
			view.getSelectionModel().getSelectedItem()
					.setOptional(!view.getSelectionModel().getSelectedItem().isOptional());
			ObservableList<PropertyItem> listItems = view.getItems();
			view.setItems(null);
			view.setItems(listItems);
			this.view.getGraphBuild().draw();
		}
	}

	/**
	 * Set the Items in View
	 *
	 * @param view
	 *            to manipulate
	 * @param items
	 *            list of the items for the listview
	 */
	private void setListViewFromList(ListView<PropertyItem> view, List<PropertyItem> items) {
		ObservableList<PropertyItem> listItems = FXCollections.observableArrayList();
		items.forEach(item -> {
			listItems.add(item);
		});

		// Avoid not on FX application thread problem
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				view.setItems(listItems);
				view.setCellFactory(list -> new ListCell<PropertyItem>() {
					@Override
					protected void updateItem(PropertyItem item, boolean empty) {
						super.updateItem(item, empty);
						setText(empty ? null : item.getName());
						if(!empty && item.isOptional()){
							setStyle("-fx-text-fill:grey;");
						}else if(!empty && !item.isOptional()){
							setStyle("");
						}
					}
				});
			}
		});
	}
	

	/**
	 * Generates a List View from the Static Measure and Operator identifiers
	 *
	 * @param nodeIdentifiers
	 *            identifiers for the List
	 * @return ListView containing the identifiers as items
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
	 * Generates a new Property-Node from the selected item in the List
	 *
	 * @param view
	 *            ListView of the selected Property
	 * @param origin
	 *            True if source
	 */
	private void generateProperty(ListView<PropertyItem> view, int shape) {
		if (view.getSelectionModel().getSelectedItem() != null) {
			Property gen = null;
			if (shape == NodeView.SOURCE) {
				String propString = config.getPropertyString(view.getSelectionModel().getSelectedItem().getName(), SourceOrTarget.SOURCE);
				boolean optional = config.getSourceInfo().getOptionalProperties().contains(propString);
				gen = new Property(propString, SourceOrTarget.SOURCE, optional);
			} else {
				String propString = config.getPropertyString(view.getSelectionModel().getSelectedItem().getName(), SourceOrTarget.TARGET);
				boolean optional = config.getTargetInfo().getOptionalProperties().contains(propString);
				gen = new Property(propString, SourceOrTarget.TARGET, optional);
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
		view.getGraphBuild().addNode(200, 200, shape, e);
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
			List<PropertyItem> sourceProperties = new ArrayList<PropertyItem>();
			for (String prop : config.getSourceInfo().getProperties()) {
				if (config.getSourceInfo().getFunctions().get(prop).keySet().toArray().length == 1) {
					sourceProperties.add(new PropertyItem(
							(String) config.getSourceInfo().getFunctions().get(prop).keySet().toArray()[0]));
				} else {
					sourceProperties.add(new PropertyItem(PrefixHelper.abbreviate(prop)));
				}
			}
			for (String prop : config.getSourceInfo().getOptionalProperties()) {
				if (config.getSourceInfo().getFunctions().get(prop).keySet().toArray().length == 1) {
					sourceProperties.add(new PropertyItem(
							(String) config.getSourceInfo().getFunctions().get(prop).keySet().toArray()[0], true));
				} else {
					sourceProperties.add(new PropertyItem(PrefixHelper.abbreviate(prop), true));
				}
			}
			setListViewFromList(toolBoxSourceProperties, sourceProperties);
		} else {
			List<PropertyItem> sourceProperties = new ArrayList<PropertyItem>();
			for (String prop : config.getSourceInfo().getProperties()) {
				sourceProperties.add(new PropertyItem(prop));
			}
			for (String prop : config.getSourceInfo().getOptionalProperties()) {
				sourceProperties.add(new PropertyItem(prop));
			}
			setListViewFromList(toolBoxSourceProperties, sourceProperties);
		}
		if (config.getTargetInfo().getFunctions() != null) {
			List<PropertyItem> targetProperties = new ArrayList<PropertyItem>();
			for (String prop : config.getTargetInfo().getProperties()) {
				if (config.getTargetInfo().getFunctions().get(prop).keySet().toArray().length == 1) {
					targetProperties.add(new PropertyItem(
							(String) config.getTargetInfo().getFunctions().get(prop).keySet().toArray()[0]));
				} else {
					targetProperties.add(new PropertyItem(PrefixHelper.abbreviate(prop)));
				}
			}
			for (String prop : config.getTargetInfo().getOptionalProperties()) {
				if (config.getTargetInfo().getFunctions().get(prop).keySet().toArray().length == 1) {
					targetProperties.add(new PropertyItem(
							(String) config.getTargetInfo().getFunctions().get(prop).keySet().toArray()[0], true));
				} else {
					targetProperties.add(new PropertyItem(PrefixHelper.abbreviate(prop), true));
				}
			}
			setListViewFromList(toolBoxTargetProperties, targetProperties);
		} else {
			List<PropertyItem> targetProperties = new ArrayList<PropertyItem>();
			for (String prop : config.getTargetInfo().getProperties()) {
				targetProperties.add(new PropertyItem(prop));
			}
			for (String prop : config.getTargetInfo().getOptionalProperties()) {
				targetProperties.add(new PropertyItem(prop));
			}
			setListViewFromList(toolBoxTargetProperties, targetProperties);
		}
		// Avoid not on FX application thread problem
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				sourcePropertiesLabel.setText(config.getSourceEndpoint().getCurrentClass().getName() + " properties");
				targetPropertiesLabel.setText(config.getTargetEndpoint().getCurrentClass().getName() + " properties");
			}
		});
	}

	public static class PropertyItem {
		private final StringProperty name = new SimpleStringProperty();
		private final BooleanProperty optional = new SimpleBooleanProperty();

		public PropertyItem(String name) {
			this(name, false);
		}

		public PropertyItem(String name, boolean optional) {
			setName(name);
			setOptional(optional);
			
		}
		
		public final StringProperty nameProperty() {
			return this.name;
		}

		public final String getName() {
			return this.nameProperty().get();
		}

		public final void setName(final String name) {
			this.nameProperty().set(name);
		}

		public final BooleanProperty optionalProperty() {
			return this.optional;
		}

		public final boolean isOptional() {
			return this.optionalProperty().get();
		}

		public final void setOptional(final boolean optional) {
			this.optionalProperty().set(optional);
		}

		@Override
		public String toString() {
			return getName();
		}

	}

	public ListView<PropertyItem> getToolBoxSourceProperties() {
		return toolBoxSourceProperties;
	}

	public ListView<PropertyItem> getToolBoxTargetProperties() {
		return toolBoxTargetProperties;
	}

}
