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
	private final Label sourcePropertiesLabel = new Label("source properties");

	/**
	 * label describing the target properties if no class name is given it is
	 * going to be "target properties"
	 */
	private final Label targetPropertiesLabel = new Label("target properties");

	/**
	 * Config of current Limesquery
	 */
	private Config config;

	private final MainView view;

	/**
	 * Constructor builds view and adds listeners to elements
	 *
	 * @param view
	 *            main view
	 */
	public ToolBox(MainView view) {
		this.generateView(this);
		this.setListeners();
		this.config = null;
		this.view = view;
	}

	/**
	 * Generates the View adds the ListViews to the right Places
	 *
	 * @param box
	 */
	private void generateView(VBox box) {
		this.toolBoxSourceProperties = new ListView<>();
		this.toolBoxSourceProperties.setId("toolBoxSourceProperties");
		this.toolBoxSourceProperties.setTooltip(new Tooltip(
				"Add source properties to metric builder by clicking.\n Optional properties are grey. You can make properties optional by right-clicking on them"));
		this.toolBoxTargetProperties = new ListView<>();
		this.toolBoxTargetProperties.setId("toolBoxTargetProperties");
		this.toolBoxTargetProperties.setTooltip(new Tooltip(
				"Add target properties to metric builder by clicking.\n Optional properties are grey. You can make properties optional by right-clicking on them"));
		this.toolBoxMetrics = this.generateListViewFromNodeIdentifiers(new Measure("").identifiers());
		this.toolBoxMetrics.setTooltip(new Tooltip("Add measures to metric builder by clicking"));
		this.toolBoxOperators = this.generateListViewFromNodeIdentifiers(Operator.identifiers);
		this.toolBoxOperators.setTooltip(new Tooltip("Add operators to metric builder by clicking"));

		box.getChildren().add(this.sourcePropertiesLabel);
		box.getChildren().add(this.toolBoxSourceProperties);
		box.getChildren().add(this.targetPropertiesLabel);
		box.getChildren().add(this.toolBoxTargetProperties);
		box.getChildren().add(new Label("Measures"));
		box.getChildren().add(this.toolBoxMetrics);
		box.getChildren().add(new Label("Operators"));
		box.getChildren().add(this.toolBoxOperators);

	}

	/**
	 * Sets the CLickListeners for the ListViews On each Click a Node will
	 * appear in the GraphCanvas
	 */
	private void setListeners() {
		this.toolBoxSourceProperties.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.SECONDARY)) {
				this.switchPropertyOptional(this.toolBoxSourceProperties, NodeView.SOURCE);
			} else {
				this.generateProperty(this.toolBoxSourceProperties, NodeView.SOURCE);
			}
		});
		this.toolBoxTargetProperties.setOnMouseClicked(e -> {
			if (e.getButton().equals(MouseButton.SECONDARY)) {
				this.switchPropertyOptional(this.toolBoxTargetProperties, NodeView.TARGET);
			} else {
				this.generateProperty(this.toolBoxTargetProperties, NodeView.TARGET);
			}
		});
		this.toolBoxMetrics.setOnMouseClicked(e -> {
			this.generateNode(this.toolBoxMetrics, NodeView.METRIC);
		});
		this.toolBoxOperators.setOnMouseClicked(e -> {
			this.generateNode(this.toolBoxOperators, NodeView.OPERATOR);
		});
	}

	private void switchPropertyOptional(ListView<PropertyItem> view, int shape) {
		if (view.getSelectionModel().getSelectedItem() != null) {
			if (shape == NodeView.SOURCE) {
				logger.debug("switch source");
				this.config.switchPropertyOptional(this.config
						.getPropertyString(view.getSelectionModel().getSelectedItem().getName(), SourceOrTarget.SOURCE),
						SourceOrTarget.SOURCE);
			} else {
				logger.debug("switch target");
				this.config.switchPropertyOptional(this.config
						.getPropertyString(view.getSelectionModel().getSelectedItem().getName(), SourceOrTarget.TARGET),
						SourceOrTarget.TARGET);
			}
			view.getSelectionModel().getSelectedItem()
					.setOptional(!view.getSelectionModel().getSelectedItem().isOptional());
			final ObservableList<PropertyItem> listItems = view.getItems();
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
		final ObservableList<PropertyItem> listItems = FXCollections.observableArrayList();
		items.forEach(item -> {
			listItems.add(item);
		});

		// Avoid not on FX application thread problem
		Platform.runLater(() -> {
			view.setItems(listItems);
			view.setCellFactory(list -> new ListCell<PropertyItem>() {
				@Override
				protected void updateItem(PropertyItem item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(empty ? null : item.getName());
					if (!empty && item.isOptional()) {
						this.setStyle("-fx-text-fill:grey;");
					} else if (!empty && !item.isOptional()) {
						this.setStyle("");
					}
				}
			});
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
		final ObservableList<String> listItems = FXCollections.observableArrayList();
		nodeIdentifiers.forEach((identifier) -> {
			listItems.add(identifier);
		});
		java.util.Collections.sort(listItems);
		return new ListView<>(listItems);

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
				final String propString = this.config
						.getPropertyString(view.getSelectionModel().getSelectedItem().getName(), SourceOrTarget.SOURCE);
				final boolean optional = this.config.getSourceInfo().getOptionalProperties().contains(propString);
				gen = new Property(propString, SourceOrTarget.SOURCE, optional);
			} else {
				final String propString = this.config
						.getPropertyString(view.getSelectionModel().getSelectedItem().getName(), SourceOrTarget.TARGET);
				final boolean optional = this.config.getTargetInfo().getOptionalProperties().contains(propString);
				gen = new Property(propString, SourceOrTarget.TARGET, optional);
			}
			this.setNodeToGraph(gen, shape);
		}
	}

	/**
	 * Adds the Node in the GraphCanvas
	 *
	 * @param e
	 *            Node to be added
	 */
	private void setNodeToGraph(Node e, int shape) {
		this.view.getGraphBuild().addNode(200, 200, shape, e);
	}

	/**
	 * Generates Operator or Measure Node from ListView
	 *
	 * @param view
	 *            ListView of the selected Item
	 */
	private void generateNode(ListView<String> view, int shape) {
		if (view.getSelectionModel().getSelectedItem() != null) {
			final Node node = Node.createNode(view.getSelectionModel().getSelectedItem());
			this.setNodeToGraph(node, shape);
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
			final List<PropertyItem> sourceProperties = new ArrayList<>();
			for (final String prop : config.getSourceInfo().getProperties()) {
				if (config.getSourceInfo().getFunctions().get(prop).keySet().toArray().length == 1) {
					sourceProperties.add(new PropertyItem(
							(String) config.getSourceInfo().getFunctions().get(prop).keySet().toArray()[0]));
				} else {
					sourceProperties.add(new PropertyItem(PrefixHelper.abbreviate(prop)));
				}
			}
			for (final String prop : config.getSourceInfo().getOptionalProperties()) {
				if (config.getSourceInfo().getFunctions().get(prop).keySet().toArray().length == 1) {
					sourceProperties.add(new PropertyItem(
							(String) config.getSourceInfo().getFunctions().get(prop).keySet().toArray()[0], true));
				} else {
					sourceProperties.add(new PropertyItem(PrefixHelper.abbreviate(prop), true));
				}
			}
			this.setListViewFromList(this.toolBoxSourceProperties, sourceProperties);
		} else {
			final List<PropertyItem> sourceProperties = new ArrayList<>();
			for (final String prop : config.getSourceInfo().getProperties()) {
				sourceProperties.add(new PropertyItem(prop));
			}
			for (final String prop : config.getSourceInfo().getOptionalProperties()) {
				sourceProperties.add(new PropertyItem(prop));
			}
			this.setListViewFromList(this.toolBoxSourceProperties, sourceProperties);
		}
		if (config.getTargetInfo().getFunctions() != null) {
			final List<PropertyItem> targetProperties = new ArrayList<>();
			for (final String prop : config.getTargetInfo().getProperties()) {
				if (config.getTargetInfo().getFunctions().get(prop).keySet().toArray().length == 1) {
					targetProperties.add(new PropertyItem(
							(String) config.getTargetInfo().getFunctions().get(prop).keySet().toArray()[0]));
				} else {
					targetProperties.add(new PropertyItem(PrefixHelper.abbreviate(prop)));
				}
			}
			for (final String prop : config.getTargetInfo().getOptionalProperties()) {
				if (config.getTargetInfo().getFunctions().get(prop).keySet().toArray().length == 1) {
					targetProperties.add(new PropertyItem(
							(String) config.getTargetInfo().getFunctions().get(prop).keySet().toArray()[0], true));
				} else {
					targetProperties.add(new PropertyItem(PrefixHelper.abbreviate(prop), true));
				}
			}
			this.setListViewFromList(this.toolBoxTargetProperties, targetProperties);
		} else {
			final List<PropertyItem> targetProperties = new ArrayList<>();
			for (final String prop : config.getTargetInfo().getProperties()) {
				targetProperties.add(new PropertyItem(prop));
			}
			for (final String prop : config.getTargetInfo().getOptionalProperties()) {
				targetProperties.add(new PropertyItem(prop));
			}
			this.setListViewFromList(this.toolBoxTargetProperties, targetProperties);
		}
		// Avoid not on FX application thread problem
		Platform.runLater(() -> {
			ToolBox.this.sourcePropertiesLabel
					.setText(config.getSourceEndpoint().getCurrentClass().getName() + " properties");
			ToolBox.this.targetPropertiesLabel
					.setText(config.getTargetEndpoint().getCurrentClass().getName() + " properties");
		});
	}

	public static class PropertyItem {
		private final StringProperty name = new SimpleStringProperty();
		private final BooleanProperty optional = new SimpleBooleanProperty();

		public PropertyItem(String name) {
			this(name, false);
		}

		public PropertyItem(String name, boolean optional) {
			this.setName(name);
			this.setOptional(optional);

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
			return this.getName();
		}

	}

	public ListView<PropertyItem> getToolBoxSourceProperties() {
		return this.toolBoxSourceProperties;
	}

	public ListView<PropertyItem> getToolBoxTargetProperties() {
		return this.toolBoxTargetProperties;
	}

}
