package org.aksw.limes.core.gui.view;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aksw.limes.core.gui.controller.EditPropertyMatchingController;
import org.aksw.limes.core.gui.model.AutomatedPropertyMatchingNode;
import org.aksw.limes.core.gui.util.SourceOrTarget;
import org.aksw.limes.core.gui.util.sparql.PrefixHelper;
import org.aksw.limes.core.io.mapping.AMapping;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * used for property matching step in
 * {@link org.aksw.limes.core.gui.view.WizardView} There are two different modes
 * implemented in this class: automated and manual, depending on the value of
 * {@link #automated}.
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class EditPropertyMatchingView implements IEditView {
	// ========== GENERAL FIELDS ==============================
	private EditPropertyMatchingController controller;
	private ScrollPane rootPane;
	WizardView wizardView;
	private Button addAllButton;
	private Button removeAllButton;
	/**
	 * Initializing the button at this point is important to be able to
	 * manipulate visibility in WizardController if automation is not possible
	 */
	private final Button switchModeButton = new Button("Default Text");
	private Label missingPropertiesLabel;
	/**
	 * mode of matching
	 */
	private final BooleanProperty automated = new SimpleBooleanProperty(true);

	// ========== FIELDS FOR MANUAL MATCHING =================
	private ListView<String> sourcePropList;
	private ListView<String> targetPropList;
	private ListView<String> addedSourcePropsList;
	private ListView<String> addedTargetPropsList;
	private final ObservableList<String> sourceProperties = FXCollections.observableArrayList();
	private final ObservableList<String> targetProperties = FXCollections.observableArrayList();
	private Label sourceID;
	private Label targetID;

	// ========== FIELDS FOR AUTOMATED MATCHING ===============
	private TableView<AutomatedPropertyMatchingNode> automatedPropList;
	private TableView<AutomatedPropertyMatchingNode> addedAutomatedPropsList;
	private final ObservableList<AutomatedPropertyMatchingNode> availableProperties = FXCollections
			.observableArrayList();
	private final ObservableList<AutomatedPropertyMatchingNode> addedProperties = FXCollections.observableArrayList();

	/**
	 * Constructor creates the root pane and adds listeners
	 *
	 * @param wizardView
	 *            corresponding view where this is embedded
	 */
	EditPropertyMatchingView(WizardView wizardView) {
		this.wizardView = wizardView;
		this.createRootPane();
	}

	/**
	 * Sets the corresponding Controller
	 *
	 * @param controller
	 *            controller
	 */
	public void setController(EditPropertyMatchingController controller) {
		this.controller = controller;
	}

	/**
	 * creates the root pane depending on the value of {@link #automated} and
	 * adds listeners
	 */
	private Parent createRootPane() {
		if (this.automated.get()) {
			this.rootPane = this.createAutomatedRootPane();
			this.addListeners();
		} else {
			this.rootPane = this.createManualRootPane();
			this.addListeners();
		}
		this.rootPane.setId("editPropertyMatchingRootPane");
		return this.rootPane;
	}

	/**
	 * Creates the root pane for automated matching
	 *
	 * @return pane root pane
	 */
	@SuppressWarnings("unchecked")
	private ScrollPane createAutomatedRootPane() {
		// =========== CREATE TABLES FOR PROPERTIES =========================
		this.automatedPropList = new TableView<>();
		this.automatedPropList.setId("automatedPropList");
		final TableColumn<AutomatedPropertyMatchingNode, String> sourcePropColumn = new TableColumn<>();
		final TableColumn<AutomatedPropertyMatchingNode, String> targetPropColumn = new TableColumn<>();
		sourcePropColumn
				.setCellValueFactory(new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("sourceProperty"));
		sourcePropColumn.setId("sourcePropColumn");
		targetPropColumn
				.setCellValueFactory(new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("targetProperty"));
		targetPropColumn.setId("targetPropColumn");
		this.automatedPropList.getColumns().addAll(sourcePropColumn, targetPropColumn);
		this.automatedPropList.setItems(this.availableProperties);
		this.automatedPropList.setEditable(false);
		this.automatedPropList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		this.addedAutomatedPropsList = new TableView<>();
		final TableColumn<AutomatedPropertyMatchingNode, String> addedSourcePropColumn = new TableColumn<>();
		final TableColumn<AutomatedPropertyMatchingNode, String> addedTargetPropColumn = new TableColumn<>();
		addedSourcePropColumn.setText("source");
		addedTargetPropColumn.setText("target");
		addedSourcePropColumn
				.setCellValueFactory(new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("sourceProperty"));
		addedTargetPropColumn
				.setCellValueFactory(new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("targetProperty"));
		this.addedAutomatedPropsList.getColumns().addAll(addedSourcePropColumn, addedTargetPropColumn);
		this.addedAutomatedPropsList.setItems(this.addedProperties);
		this.addedAutomatedPropsList.setEditable(false);
		this.addedAutomatedPropsList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// ============ ADD LABELS AND PUT TABLES INTO ACCORDING BOX
		// =================
		final Label propertiesLabel = new Label("recommended properties:");
		propertiesLabel.setTextFill(Color.DARKSLATEGREY);
		final Label addedPropertiesLabel = new Label("added properties:");
		addedPropertiesLabel.setTextFill(Color.DARKSLATEGREY);
		final VBox propertiesColumn = new VBox();
		propertiesColumn.getChildren().addAll(propertiesLabel, this.automatedPropList, addedPropertiesLabel,
				this.addedAutomatedPropsList);
		this.addAllButton = new Button("Add all");
		this.removeAllButton = new Button("Remove all");
		this.missingPropertiesLabel = new Label("At least one property must be chosen!");
		this.missingPropertiesLabel.setTextFill(Color.RED);
		this.missingPropertiesLabel.setVisible(false);

		// ============ ADD BUTTONS =======================================
		final HBox buttons = new HBox();
		this.switchModeButton.setText("Manual Matching");
		this.switchModeButton.setId("switchModeButton");
		// Regions are used to put switchModeButton to the left and other
		// buttons to right
		final Region leftRegion = new Region();
		final Region rightRegion = new Region();
		HBox.setHgrow(leftRegion, Priority.ALWAYS);
		HBox.setHgrow(rightRegion, Priority.ALWAYS);
		buttons.getChildren().addAll(this.switchModeButton, leftRegion, this.missingPropertiesLabel, rightRegion,
				this.addAllButton, this.removeAllButton);

		// ========== ADD EVERYTHING TO BORDERPANE ========================
		final BorderPane root = new BorderPane();
		root.setId("root");
		root.setCenter(propertiesColumn);
		root.setBottom(buttons);
		final ScrollPane pane = new ScrollPane(root);
		pane.setOnMouseClicked(e -> {
			this.missingPropertiesLabel.setVisible(false);
		});
		pane.setFitToHeight(true);
		pane.setFitToWidth(true);
		pane.setPadding(new Insets(5.0));
		return pane;
	}

	/**
	 * Creates the root pane for manual matching
	 *
	 * @return pane root pane
	 */
	private ScrollPane createManualRootPane() {
		// =============== CREATE SOURCE AND TARGET COLUMNS WITH LISTS
		this.sourcePropList = new ListView<>();
		this.sourcePropList.setId("sourcePropList");
		this.targetPropList = new ListView<>();
		this.targetPropList.setId("targetPropList");
		this.addedSourcePropsList = new ListView<>();
		this.addedSourcePropsList.setId("addedSourcePropsList");
		this.addedTargetPropsList = new ListView<>();
		this.addedTargetPropsList.setId("addedTargetPropsList");
		final Label sourceLabel = new Label("available Source Properties:");
		this.sourceID = new Label();
		final Label targetLabel = new Label("available Target Properties:");
		this.targetID = new Label();
		final Label addedSourceLabel = new Label("added Source Properties:");
		final Label addedTargetLabel = new Label("added Target Properties:");
		final VBox sourceColumn = new VBox();
		final VBox targetColumn = new VBox();
		sourceColumn.getChildren().addAll(this.sourceID, sourceLabel, this.sourcePropList, addedSourceLabel,
				this.addedSourcePropsList);
		targetColumn.getChildren().addAll(this.targetID, targetLabel, this.targetPropList, addedTargetLabel,
				this.addedTargetPropsList);

		// =============== ADD BUTTONS AND ERROR LABEL ==============
		this.addAllButton = new Button("Add all");
		this.removeAllButton = new Button("Remove all");
		this.missingPropertiesLabel = new Label("At least one source and one target property must be chosen!");
		this.missingPropertiesLabel.setTextFill(Color.RED);
		this.missingPropertiesLabel.setVisible(false);
		final HBox buttons = new HBox();
		this.switchModeButton.setText("Automated Matching");
		this.switchModeButton.setId("switchModeButton");
		// Regions are used to put switchModeButton to the left and other
		// buttons to right
		final Region leftRegion = new Region();
		final Region rightRegion = new Region();
		HBox.setHgrow(leftRegion, Priority.ALWAYS);
		HBox.setHgrow(rightRegion, Priority.ALWAYS);
		buttons.getChildren().addAll(this.switchModeButton, leftRegion, this.missingPropertiesLabel, rightRegion,
				this.addAllButton, this.removeAllButton);
		// ============= ADD EVERYTHING TO ROOT PANE AND CONFIGURE IT
		final BorderPane root = new BorderPane();
		final HBox hb = new HBox();
		hb.getChildren().addAll(sourceColumn, targetColumn);
		HBox.setHgrow(sourceColumn, Priority.ALWAYS);
		HBox.setHgrow(targetColumn, Priority.ALWAYS);
		root.setCenter(hb);
		root.setBottom(buttons);
		final ScrollPane pane = new ScrollPane(root);
		pane.setOnMouseClicked(e -> {
			this.missingPropertiesLabel.setVisible(false);
		});
		pane.setFitToHeight(true);
		pane.setFitToWidth(true);
		pane.setPadding(new Insets(5.0));
		return pane;
	}

	/**
	 * adds listener to properties to display changes after loading them is
	 * finished. also adds functionality
	 */
	private void addListeners() {
		if (this.automated.get()) {
			this.addListenersForAutomated();
		} else {
			this.addListenersForManual();
		}

		// =========== functionality of switch mode button =================
		this.switchModeButton.setOnAction(e -> {
			EditPropertyMatchingView.this.rootPane = null;
			// this also changes the root pane
			EditPropertyMatchingView.this.automated.set(!EditPropertyMatchingView.this.automated.get());
			EditPropertyMatchingView.this.wizardView.setToRootPane(EditPropertyMatchingView.this.rootPane);
			EditPropertyMatchingView.this.controller.load(EditPropertyMatchingView.this.automated.get());
		});

		// ========== ensure the correct root pane is always loaded ============
		this.automated.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (EditPropertyMatchingView.this.rootPane == null) {
				EditPropertyMatchingView.this.createRootPane();
			}
			// If automated is false and manual root pane has not been created
			// yet
			if (!EditPropertyMatchingView.this.automated.get()
					&& EditPropertyMatchingView.this.addedSourcePropsList == null) {
				EditPropertyMatchingView.this.createRootPane();
			}
		});

	}

	public void addListenersForAutomated() {

		this.automatedPropList.setOnMouseClicked(e -> {
			if (EditPropertyMatchingView.this.automatedPropList.getSelectionModel().getSelectedItem() != null) {
				EditPropertyMatchingView.this.addedAutomatedPropsList.getItems()
						.add(EditPropertyMatchingView.this.automatedPropList.getSelectionModel().getSelectedItem());
				EditPropertyMatchingView.this.automatedPropList.getItems()
						.remove(EditPropertyMatchingView.this.automatedPropList.getSelectionModel().getSelectedItem());
			}
		});

		this.addedAutomatedPropsList.setOnMouseClicked(e -> {
			if (EditPropertyMatchingView.this.addedAutomatedPropsList.getSelectionModel().getSelectedItem() != null) {
				EditPropertyMatchingView.this.automatedPropList.getItems().add(
						EditPropertyMatchingView.this.addedAutomatedPropsList.getSelectionModel().getSelectedItem());
				EditPropertyMatchingView.this.addedAutomatedPropsList.getItems().remove(
						EditPropertyMatchingView.this.addedAutomatedPropsList.getSelectionModel().getSelectedItem());
			}
		});

		this.addAllButton.setOnAction(e -> {
			EditPropertyMatchingView.this.addedAutomatedPropsList.getItems()
					.addAll(EditPropertyMatchingView.this.automatedPropList.getItems());
			EditPropertyMatchingView.this.automatedPropList.getItems().clear();
		});

		this.removeAllButton.setOnAction(e -> {
			EditPropertyMatchingView.this.automatedPropList.getItems()
					.addAll(EditPropertyMatchingView.this.addedAutomatedPropsList.getItems());
			EditPropertyMatchingView.this.addedAutomatedPropsList.getItems().clear();
		});

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addListenersForManual() {

		this.sourceProperties.addListener((ListChangeListener) change -> {
			if (EditPropertyMatchingView.this.sourceProperties.size() != 0
					&& EditPropertyMatchingView.this.targetProperties.size() != 0) {
				EditPropertyMatchingView.this.putAllPropertiesToTable();
			}
		});

		this.targetProperties.addListener((ListChangeListener) change -> {
			if (EditPropertyMatchingView.this.sourceProperties.size() != 0
					&& EditPropertyMatchingView.this.targetProperties.size() != 0) {
				EditPropertyMatchingView.this.putAllPropertiesToTable();
			}
		});

		this.sourcePropList.setOnMouseClicked(e -> {
			if (EditPropertyMatchingView.this.sourcePropList.getSelectionModel().getSelectedItem() != null) {
				EditPropertyMatchingView.this.addedSourcePropsList.getItems()
						.add(EditPropertyMatchingView.this.sourcePropList.getSelectionModel().getSelectedItem());
				EditPropertyMatchingView.this.sourcePropList.getItems()
						.remove(EditPropertyMatchingView.this.sourcePropList.getSelectionModel().getSelectedItem());
			}
		});

		this.targetPropList.setOnMouseClicked(e -> {
			if (EditPropertyMatchingView.this.targetPropList.getSelectionModel().getSelectedItem() != null) {
				EditPropertyMatchingView.this.addedTargetPropsList.getItems()
						.add(EditPropertyMatchingView.this.targetPropList.getSelectionModel().getSelectedItem());
				EditPropertyMatchingView.this.targetPropList.getItems()
						.remove(EditPropertyMatchingView.this.targetPropList.getSelectionModel().getSelectedItem());
			}
		});

		this.addedSourcePropsList.setOnMouseClicked(e -> {
			if (EditPropertyMatchingView.this.addedSourcePropsList.getSelectionModel().getSelectedItem() != null) {
				EditPropertyMatchingView.this.sourcePropList.getItems()
						.add(EditPropertyMatchingView.this.addedSourcePropsList.getSelectionModel().getSelectedItem());
				Collections.sort(EditPropertyMatchingView.this.sourcePropList.getItems());
				EditPropertyMatchingView.this.addedSourcePropsList.getItems().remove(
						EditPropertyMatchingView.this.addedSourcePropsList.getSelectionModel().getSelectedItem());
			}
		});

		this.addedTargetPropsList.setOnMouseClicked(e -> {
			if (EditPropertyMatchingView.this.addedTargetPropsList.getSelectionModel().getSelectedItem() != null) {
				EditPropertyMatchingView.this.targetPropList.getItems()
						.add(EditPropertyMatchingView.this.addedTargetPropsList.getSelectionModel().getSelectedItem());
				Collections.sort(EditPropertyMatchingView.this.targetPropList.getItems());
				EditPropertyMatchingView.this.addedTargetPropsList.getItems().remove(
						EditPropertyMatchingView.this.addedTargetPropsList.getSelectionModel().getSelectedItem());
			}
		});

		this.addAllButton.setOnAction(e -> {
			EditPropertyMatchingView.this.addedSourcePropsList.getItems()
					.addAll(new SortedList(EditPropertyMatchingView.this.sourcePropList.getItems()));
			EditPropertyMatchingView.this.addedTargetPropsList.getItems()
					.addAll(new SortedList(EditPropertyMatchingView.this.targetPropList.getItems()));
			EditPropertyMatchingView.this.sourcePropList.getItems().clear();
			EditPropertyMatchingView.this.targetPropList.getItems().clear();
		});

		this.removeAllButton.setOnAction(e -> {
			EditPropertyMatchingView.this.sourcePropList.getItems()
					.addAll(new SortedList(EditPropertyMatchingView.this.addedSourcePropsList.getItems()));
			EditPropertyMatchingView.this.targetPropList.getItems()
					.addAll(new SortedList(EditPropertyMatchingView.this.addedTargetPropsList.getItems()));
			EditPropertyMatchingView.this.addedSourcePropsList.getItems().clear();
			EditPropertyMatchingView.this.addedTargetPropsList.getItems().clear();
		});
	}

	/**
	 * Returns the rootPane
	 *
	 * @return rootPane
	 */
	@Override
	public Parent getPane() {
		return this.rootPane;
	}

	/**
	 * Saves the table items
	 */
	@Override
	public void save() {
		if (this.automated.get()) {
			final List<String> sourceProperties = new ArrayList<>();
			final List<String> targetProperties = new ArrayList<>();
			for (final AutomatedPropertyMatchingNode addedPropertyNode : this.addedProperties) {
				sourceProperties.add(addedPropertyNode.getSourceProperty().get());
				targetProperties.add(addedPropertyNode.getTargetProperty().get());
			}
			this.controller.save(sourceProperties, targetProperties);
		} else {
			this.controller.save(this.addedSourcePropsList.getItems(), this.addedTargetPropsList.getItems());
		}
	}

	/**
	 * Called by controller after loading properties and puts them to the table
	 *
	 * @param properties
	 *            the loaded properties
	 */
	public void showAutomatedProperties(AMapping properties) {
		// clear the list
		this.availableProperties.removeAll(this.availableProperties);
		// fill the list
		for (final String sourceProperty : properties.getMap().keySet()) {
			for (final String targetProperty : properties.getMap().get(sourceProperty).keySet()) {
				this.availableProperties.add(new AutomatedPropertyMatchingNode(PrefixHelper.abbreviate(sourceProperty),
						PrefixHelper.abbreviate(targetProperty)));
			}
		}
		Collections.sort(this.availableProperties,
				AutomatedPropertyMatchingNode.AUTOMATED_PROPERTY_MATCHING_NODE_COMPARATOR);
		this.automatedPropList.setItems(this.availableProperties);
		this.automatedPropList.getColumns().get(0)
				.setText(this.controller.getConfig().getSourceEndpoint().getCurrentClass().getName() + "  properties");
		this.automatedPropList.getColumns().get(1)
				.setText(this.controller.getConfig().getTargetEndpoint().getCurrentClass().getName() + "  properties");
	}

	/**
	 * Shows the available properties
	 *
	 * @param sourceOrTarget
	 *            enum for source or target
	 * @param properties
	 *            list of properties to show
	 */
	public void showAvailableProperties(SourceOrTarget sourceOrTarget, List<String> properties) {
		if (!this.automated.get()) {
			this.addedSourcePropsList.getItems().clear();
			this.addedTargetPropsList.getItems().clear();
		} else {
			this.addedAutomatedPropsList.getItems().clear();
		}
		Collections.sort(properties);
		(sourceOrTarget == SOURCE ? this.sourceProperties : this.targetProperties).setAll(properties);
		(sourceOrTarget == SOURCE ? this.sourcePropList : this.targetPropList)
				.setItems(sourceOrTarget == SOURCE ? this.sourceProperties : this.targetProperties);
	}

	/**
	 * Puts the loaded properties to the table
	 */
	private void putAllPropertiesToTable() {
		this.sourcePropList.setItems(this.sourceProperties);
		this.targetPropList.setItems(this.targetProperties);
		this.sourceID.setText("ID: " + this.controller.getConfig().getSourceInfo().getId() + "\t Class: "
				+ this.controller.getConfig().getSourceEndpoint().getCurrentClass().getName());
		this.targetID.setText("ID: " + this.controller.getConfig().getTargetInfo().getId() + "\t Class: "
				+ this.controller.getConfig().getTargetEndpoint().getCurrentClass().getName());
	}

	/**
	 * Shows an error if something went wrong
	 *
	 * @param header
	 *            header of error
	 * @param content
	 *            content of message
	 */
	public void showError(String header, String content) {
		final Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public ListView<String> getAddedSourcePropsList() {
		return this.addedSourcePropsList;
	}

	public ListView<String> getAddedTargetPropsList() {
		return this.addedTargetPropsList;
	}

	public Label getMissingPropertiesLabel() {
		return this.missingPropertiesLabel;
	}

	public TableView<AutomatedPropertyMatchingNode> getAddedAutomatedPropsList() {
		return this.addedAutomatedPropsList;
	}

	public Button getSwitchModeButton() {
		return this.switchModeButton;
	}

	@Override
	public void setAutomated(boolean automated) {
		this.automated.set(automated);
		this.switchModeButton.setVisible(automated);
	}

	@Override
	public Boolean isAutomated() {
		return this.automated.get();
	}

}
