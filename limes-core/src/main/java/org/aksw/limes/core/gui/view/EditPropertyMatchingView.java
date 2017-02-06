package org.aksw.limes.core.gui.view;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.gui.controller.EditPropertyMatchingController;
import org.aksw.limes.core.gui.model.AutomatedPropertyMatchingNode;
import org.aksw.limes.core.gui.util.SourceOrTarget;
import org.aksw.limes.core.gui.util.sparql.PrefixHelper;
import org.aksw.limes.core.io.mapping.AMapping;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.input.MouseEvent;
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
	private Button switchModeButton = new Button("Default Text");
	private Label missingPropertiesLabel;
	/**
	 * mode of matching
	 */
	private BooleanProperty automated = new SimpleBooleanProperty(true);

	// ========== FIELDS FOR MANUAL MATCHING =================
	private ListView<String> sourcePropList;
	private ListView<String> targetPropList;
	private ListView<String> addedSourcePropsList;
	private ListView<String> addedTargetPropsList;
	private ObservableList<String> sourceProperties = FXCollections.observableArrayList();
	private ObservableList<String> targetProperties = FXCollections.observableArrayList();
	private Label sourceID;
	private Label targetID;

	// ========== FIELDS FOR AUTOMATED MATCHING ===============
	private TableView<AutomatedPropertyMatchingNode> automatedPropList;
	private TableView<AutomatedPropertyMatchingNode> addedAutomatedPropsList;
	private ObservableList<AutomatedPropertyMatchingNode> availableProperties = FXCollections.observableArrayList();
	private ObservableList<AutomatedPropertyMatchingNode> addedProperties = FXCollections.observableArrayList();
	

	/**
	 * Constructor creates the root pane and adds listeners
	 * 
	 * @param wizardView
	 *            corresponding view where this is embedded
	 */
	EditPropertyMatchingView(WizardView wizardView) {
		this.wizardView = wizardView;
		createRootPane();
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
		if (automated.get()) {
				rootPane = createAutomatedRootPane();
				addListeners();
		} else {
				rootPane = createManualRootPane();
				addListeners();
		}
		rootPane.setId("editPropertyMatchingRootPane");
		return rootPane;
	}

	/**
	 * Creates the root pane for automated matching
	 * 
	 * @return pane root pane
	 */
	@SuppressWarnings("unchecked")
	private ScrollPane createAutomatedRootPane() {
		// =========== CREATE TABLES FOR PROPERTIES =========================
		automatedPropList = new TableView<AutomatedPropertyMatchingNode>();
		TableColumn<AutomatedPropertyMatchingNode, String> sourcePropColumn = new TableColumn<AutomatedPropertyMatchingNode, String>();
		TableColumn<AutomatedPropertyMatchingNode, String> targetPropColumn = new TableColumn<AutomatedPropertyMatchingNode, String>();
		sourcePropColumn
				.setCellValueFactory(new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("sourceProperty"));
		sourcePropColumn.setId("sourcePropColumn");
		targetPropColumn
				.setCellValueFactory(new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("targetProperty"));
		targetPropColumn.setId("targetPropColumn");
		automatedPropList.getColumns().addAll(sourcePropColumn, targetPropColumn);
		automatedPropList.setItems(availableProperties);
		automatedPropList.setEditable(false);
		automatedPropList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		addedAutomatedPropsList = new TableView<AutomatedPropertyMatchingNode>();
		TableColumn<AutomatedPropertyMatchingNode, String> addedSourcePropColumn = new TableColumn<AutomatedPropertyMatchingNode, String>();
		TableColumn<AutomatedPropertyMatchingNode, String> addedTargetPropColumn = new TableColumn<AutomatedPropertyMatchingNode, String>();
		addedSourcePropColumn.setText("source");
		addedTargetPropColumn.setText("target");
		addedSourcePropColumn
				.setCellValueFactory(new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("sourceProperty"));
		addedTargetPropColumn
				.setCellValueFactory(new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("targetProperty"));
		addedAutomatedPropsList.getColumns().addAll(addedSourcePropColumn, addedTargetPropColumn);
		addedAutomatedPropsList.setItems(addedProperties);
		addedAutomatedPropsList.setEditable(false);
		addedAutomatedPropsList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// ============ ADD LABELS AND PUT TABLES INTO ACCORDING BOX
		// =================
		Label propertiesLabel = new Label("recommended properties:");
		propertiesLabel.setTextFill(Color.DARKSLATEGREY);
		Label addedPropertiesLabel = new Label("added properties:");
		addedPropertiesLabel.setTextFill(Color.DARKSLATEGREY);
		VBox propertiesColumn = new VBox();
		propertiesColumn.getChildren().addAll(propertiesLabel, automatedPropList, addedPropertiesLabel,
				addedAutomatedPropsList);
		addAllButton = new Button("Add all");
		removeAllButton = new Button("Remove all");
		missingPropertiesLabel = new Label("At least one property must be chosen!");
		missingPropertiesLabel.setTextFill(Color.RED);
		missingPropertiesLabel.setVisible(false);

		// ============ ADD BUTTONS =======================================
		HBox buttons = new HBox();
		switchModeButton.setText("Manual Matching");
		switchModeButton.setId("switchModeButton");
		// Regions are used to put switchModeButton to the left and other
		// buttons to right
		Region leftRegion = new Region();
		Region rightRegion = new Region();
		HBox.setHgrow(leftRegion, Priority.ALWAYS);
		HBox.setHgrow(rightRegion, Priority.ALWAYS);
		buttons.getChildren().addAll(switchModeButton, leftRegion, missingPropertiesLabel, rightRegion, addAllButton,
				removeAllButton);

		// ========== ADD EVERYTHING TO BORDERPANE ========================
		BorderPane root = new BorderPane();
		root.setCenter(propertiesColumn);
		root.setBottom(buttons);
		ScrollPane pane = new ScrollPane(root);
		pane.setOnMouseClicked(e -> {
			missingPropertiesLabel.setVisible(false);
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
		sourcePropList = new ListView<String>();
		sourcePropList.setId("sourcePropList");
		targetPropList = new ListView<String>();
		targetPropList.setId("targetPropList");
		addedSourcePropsList = new ListView<String>();
		addedSourcePropsList.setId("addedSourcePropsList");
		addedTargetPropsList = new ListView<String>();
		addedTargetPropsList.setId("addedTargetPropsList");
		Label sourceLabel = new Label("available Source Properties:");
		sourceID = new Label();
		Label targetLabel = new Label("available Target Properties:");
		targetID = new Label();
		Label addedSourceLabel = new Label("added Source Properties:");
		Label addedTargetLabel = new Label("added Target Properties:");
		VBox sourceColumn = new VBox();
		VBox targetColumn = new VBox();
		sourceColumn.getChildren().addAll(sourceID, sourceLabel, sourcePropList, addedSourceLabel,
				addedSourcePropsList);
		targetColumn.getChildren().addAll(targetID, targetLabel, targetPropList, addedTargetLabel,
				addedTargetPropsList);

		// =============== ADD BUTTONS AND ERROR LABEL ==============
		addAllButton = new Button("Add all");
		removeAllButton = new Button("Remove all");
		missingPropertiesLabel = new Label("At least one source and one target property must be chosen!");
		missingPropertiesLabel.setTextFill(Color.RED);
		missingPropertiesLabel.setVisible(false);
		HBox buttons = new HBox();
		switchModeButton.setText("Automated Matching");
		switchModeButton.setId("switchModeButton");
		// Regions are used to put switchModeButton to the left and other
		// buttons to right
		Region leftRegion = new Region();
		Region rightRegion = new Region();
		HBox.setHgrow(leftRegion, Priority.ALWAYS);
		HBox.setHgrow(rightRegion, Priority.ALWAYS);
		buttons.getChildren().addAll(switchModeButton, leftRegion, missingPropertiesLabel, rightRegion, addAllButton,
				removeAllButton);
		// ============= ADD EVERYTHING TO ROOT PANE AND CONFIGURE IT
		BorderPane root = new BorderPane();
		HBox hb = new HBox();
		hb.getChildren().addAll(sourceColumn, targetColumn);
		HBox.setHgrow(sourceColumn, Priority.ALWAYS);
		HBox.setHgrow(targetColumn, Priority.ALWAYS);
		root.setCenter(hb);
		root.setBottom(buttons);
		ScrollPane pane = new ScrollPane(root);
		pane.setOnMouseClicked(e -> {
			missingPropertiesLabel.setVisible(false);
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
		if (automated.get()) {
			addListenersForAutomated();
		} else {
			addListenersForManual();
		}

		// =========== functionality of switch mode button =================
		switchModeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				rootPane = null;
				// this also changes the root pane
				automated.set(!automated.get());
				wizardView.setToRootPane(rootPane);
				controller.load(automated.get());
			}
		});

		
		// ========== ensure the correct root pane is always loaded ============
		automated.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (rootPane == null){
            		System.err.println("Changed: " + automated.get());
					createRootPane();
				}
                //If automated is false and manual root pane has not been created yet
                if(!automated.get() && addedSourcePropsList == null){
                    createRootPane();
                }
			}
		});
		
	}

	public void addListenersForAutomated() {

		automatedPropList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (automatedPropList.getSelectionModel().getSelectedItem() != null) {
					addedAutomatedPropsList.getItems().add(automatedPropList.getSelectionModel().getSelectedItem());
					automatedPropList.getItems().remove(automatedPropList.getSelectionModel().getSelectedItem());
				}
			}
		});

		addedAutomatedPropsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (addedAutomatedPropsList.getSelectionModel().getSelectedItem() != null) {
					automatedPropList.getItems().add(addedAutomatedPropsList.getSelectionModel().getSelectedItem());
					addedAutomatedPropsList.getItems()
							.remove(addedAutomatedPropsList.getSelectionModel().getSelectedItem());
				}
			}
		});

		addAllButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				addedAutomatedPropsList.getItems().addAll(automatedPropList.getItems());
				automatedPropList.getItems().clear();
			}
		});

		removeAllButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				automatedPropList.getItems().addAll(addedAutomatedPropsList.getItems());
				addedAutomatedPropsList.getItems().clear();
			}
		});

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addListenersForManual() {

		sourceProperties.addListener(new ListChangeListener() {
			@Override
			public void onChanged(ListChangeListener.Change change) {
				if (sourceProperties.size() != 0 && targetProperties.size() != 0) {
					putAllPropertiesToTable();
				}
			}
		});

		targetProperties.addListener(new ListChangeListener() {
			@Override
			public void onChanged(ListChangeListener.Change change) {
				if (sourceProperties.size() != 0 && targetProperties.size() != 0) {
					putAllPropertiesToTable();
				}
			}
		});

		sourcePropList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (sourcePropList.getSelectionModel().getSelectedItem() != null) {
					addedSourcePropsList.getItems().add(sourcePropList.getSelectionModel().getSelectedItem());
					sourcePropList.getItems().remove(sourcePropList.getSelectionModel().getSelectedItem());
				}
			}
		});

		targetPropList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (targetPropList.getSelectionModel().getSelectedItem() != null) {
					addedTargetPropsList.getItems().add(targetPropList.getSelectionModel().getSelectedItem());
					targetPropList.getItems().remove(targetPropList.getSelectionModel().getSelectedItem());
				}
			}
		});

		addedSourcePropsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (addedSourcePropsList.getSelectionModel().getSelectedItem() != null) {
					sourcePropList.getItems().add(addedSourcePropsList.getSelectionModel().getSelectedItem());
					addedSourcePropsList.getItems().remove(addedSourcePropsList.getSelectionModel().getSelectedItem());
				}
			}
		});

		addedTargetPropsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (addedTargetPropsList.getSelectionModel().getSelectedItem() != null) {
					targetPropList.getItems().add(addedTargetPropsList.getSelectionModel().getSelectedItem());
					addedTargetPropsList.getItems().remove(addedTargetPropsList.getSelectionModel().getSelectedItem());
				}
			}
		});

		addAllButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				addedSourcePropsList.getItems().addAll(sourcePropList.getItems());
				addedTargetPropsList.getItems().addAll(targetPropList.getItems());
				sourcePropList.getItems().clear();
				targetPropList.getItems().clear();
			}
		});

		removeAllButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				sourcePropList.getItems().addAll(addedSourcePropsList.getItems());
				targetPropList.getItems().addAll(addedTargetPropsList.getItems());
				addedSourcePropsList.getItems().clear();
				addedTargetPropsList.getItems().clear();
			}
		});

	}

	/**
	 * Returns the rootPane
	 *
	 * @return rootPane
	 */
	@Override
	public Parent getPane() {
		return rootPane;
	}

	/**
	 * Saves the table items
	 */
	@Override
	public void save() {
		if (automated.get()) {
			List<String> sourceProperties = new ArrayList<String>();
			List<String> targetProperties = new ArrayList<String>();
			for (AutomatedPropertyMatchingNode addedPropertyNode : this.addedProperties) {
				sourceProperties.add(addedPropertyNode.getSourceProperty().get());
				targetProperties.add(addedPropertyNode.getTargetProperty().get());
			}
			controller.save(sourceProperties, targetProperties);
		} else {
			controller.save(this.addedSourcePropsList.getItems(), this.addedTargetPropsList.getItems());
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
		availableProperties.removeAll(availableProperties);
		// fill the list
		for (String sourceProperty : properties.getMap().keySet()) {
			for (String targetProperty : properties.getMap().get(sourceProperty).keySet()) {
				availableProperties.add(new AutomatedPropertyMatchingNode(PrefixHelper.abbreviate(sourceProperty),
						PrefixHelper.abbreviate(targetProperty)));
			}
		}
		automatedPropList.setItems(availableProperties);
		automatedPropList.getColumns().get(0)
				.setText(controller.getConfig().getSourceEndpoint().getCurrentClass().getName() + "  properties");
		automatedPropList.getColumns().get(1)
				.setText(controller.getConfig().getTargetEndpoint().getCurrentClass().getName() + "  properties");
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
		if(!automated.get()){
            addedSourcePropsList.getItems().clear();
            addedTargetPropsList.getItems().clear();
		}else{
			addedAutomatedPropsList.getItems().clear();
		}
		(sourceOrTarget == SOURCE ? sourceProperties : targetProperties).setAll(properties);
		(sourceOrTarget == SOURCE ? sourcePropList : targetPropList)
				.setItems((sourceOrTarget == SOURCE ? sourceProperties : targetProperties));
	}

	/**
	 * Puts the loaded properties to the table
	 */
	private void putAllPropertiesToTable() {
		sourcePropList.setItems(sourceProperties);
		targetPropList.setItems(targetProperties);
		sourceID.setText("ID: " + controller.getConfig().getSourceInfo().getId() + "\t Class: "
				+ controller.getConfig().getSourceEndpoint().getCurrentClass().getName());
		targetID.setText("ID: " + controller.getConfig().getTargetInfo().getId() + "\t Class: "
				+ controller.getConfig().getTargetEndpoint().getCurrentClass().getName());
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
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public ListView<String> getAddedSourcePropsList() {
		return addedSourcePropsList;
	}

	public ListView<String> getAddedTargetPropsList() {
		return addedTargetPropsList;
	}

	public Label getMissingPropertiesLabel() {
		return missingPropertiesLabel;
	}

	public TableView<AutomatedPropertyMatchingNode> getAddedAutomatedPropsList() {
		return addedAutomatedPropsList;
	}

	public Button getSwitchModeButton() {
		return switchModeButton;
	}

	@Override
	public void setAutomated(boolean automated) {
		this.automated.set(automated);
		this.switchModeButton.setVisible(automated);
	}

	@Override
	public Boolean isAutomated() {
		return automated.get();
	}

}
