package org.aksw.limes.core.gui.view;

import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.gui.controller.EditPropertyMatchingController;
import org.aksw.limes.core.gui.model.AutomatedPropertyMatchingNode;
import org.aksw.limes.core.gui.util.SourceOrTarget;
import org.aksw.limes.core.gui.util.sparql.PrefixHelper;
import org.aksw.limes.core.io.mapping.AMapping;

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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * used for property matching step in {@link org.aksw.limes.core.gui.view.WizardView}
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class EditPropertyMatchingView implements IEditView {
    private EditPropertyMatchingController controller;
    private ScrollPane rootPane;
    WizardView wizardView;
    private ListView<String> sourcePropList;
    private ListView<String> targetPropList;
    private ListView<String> addedSourcePropsList;
    private ListView<String> addedTargetPropsList;
    private Button addAllButton;
    private Button removeAllButton;
    private ObservableList<String> sourceProperties = FXCollections
            .observableArrayList();
    private ObservableList<String> targetProperties = FXCollections
            .observableArrayList();
    private ObservableList<String> automatedProperties = FXCollections
            .observableArrayList();
    private TableView<AutomatedPropertyMatchingNode> automatedPropList;
    private TableView<AutomatedPropertyMatchingNode> addedAutomatedPropsList;
    private ObservableList<AutomatedPropertyMatchingNode> availableProperties = FXCollections.observableArrayList();
    private ObservableList<AutomatedPropertyMatchingNode> addedProperties = FXCollections.observableArrayList();
    private Label missingPropertiesLabel;
    private Label sourceID;
    private Label targetID;
    public boolean automated = true;

    /**
     * Constructor creates the root pane and adds listeners
     */
    EditPropertyMatchingView(WizardView wizardView) {
    	this.wizardView = wizardView;
        createRootPane();
    }

    /**
     * Sets the corresponding Controller
     *
     * @param controller controller
     */
    public void setController(EditPropertyMatchingController controller) {
        this.controller = controller;
    }


    /**
     * creates the root pane
     */
    private Parent createRootPane() {
    	ScrollPane pane = null;
    	if(automated){
    		pane = createAutomatedRootPane();
    	}else{
    		pane = createManualRootPane();
    	}
        addListeners();
        return pane;
    }
    
    @SuppressWarnings("unchecked")
	private ScrollPane createAutomatedRootPane(){
    	automatedPropList = new TableView<AutomatedPropertyMatchingNode>();
    	TableColumn<AutomatedPropertyMatchingNode, String> sourcePropColumn = new TableColumn<AutomatedPropertyMatchingNode, String>();
    	TableColumn<AutomatedPropertyMatchingNode, String> targetPropColumn = new TableColumn<AutomatedPropertyMatchingNode, String>();
    	sourcePropColumn.setCellValueFactory(
    			new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("sourceProperty")
    	);
    	targetPropColumn.setCellValueFactory(
    			new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("targetProperty")
    	);
    	automatedPropList.getColumns().addAll(sourcePropColumn, targetPropColumn);
    	automatedPropList.setItems(availableProperties);
    	automatedPropList.setEditable(false);
    	automatedPropList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addedAutomatedPropsList = new TableView<AutomatedPropertyMatchingNode>();
    	TableColumn<AutomatedPropertyMatchingNode, String> addedSourcePropColumn = new TableColumn<AutomatedPropertyMatchingNode, String>();
    	TableColumn<AutomatedPropertyMatchingNode, String> addedTargetPropColumn = new TableColumn<AutomatedPropertyMatchingNode, String>();
    	addedSourcePropColumn.setText("source");
    	addedTargetPropColumn.setText("target");
    	addedSourcePropColumn.setCellValueFactory(
    			new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("sourceProperty")
    	);
    	addedTargetPropColumn.setCellValueFactory(
    			new PropertyValueFactory<AutomatedPropertyMatchingNode, String>("targetProperty")
    	);
    	addedAutomatedPropsList.getColumns().addAll(addedSourcePropColumn, addedTargetPropColumn);
    	addedAutomatedPropsList.setItems(addedProperties);
    	addedAutomatedPropsList.setEditable(false);
    	addedAutomatedPropsList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Label propertiesLabel = new Label("recommended properties:");
        propertiesLabel.setTextFill(Color.DARKSLATEGREY);
        HBox idsBox = new HBox();
        sourceID = new Label();
        targetID = new Label();
        idsBox.getChildren().addAll(sourceID, targetID);
        Label addedPropertiesLabel = new Label("added properties:");
        addedPropertiesLabel.setTextFill(Color.DARKSLATEGREY);
        VBox propertiesColumn = new VBox();
        propertiesColumn.getChildren().addAll(idsBox, propertiesLabel, automatedPropList, addedPropertiesLabel, addedAutomatedPropsList);
        addAllButton = new Button("Add all");
        removeAllButton = new Button("Remove all");
        missingPropertiesLabel = new Label("At least one property must be chosen!");
        missingPropertiesLabel.setTextFill(Color.RED);
        missingPropertiesLabel.setVisible(false);
        HBox buttons = new HBox();

		Button switchMode = new Button("Manual Matching");
			switchMode.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					automated = false;
					rootPane = null;
					wizardView.setToRootPane(createRootPane());
					controller.load();
				}
			});
        buttons.getChildren().addAll(missingPropertiesLabel, switchMode, addAllButton, removeAllButton);
        BorderPane root = new BorderPane();
        root.setCenter(propertiesColumn);
        root.setBottom(buttons);
        rootPane = new ScrollPane(root);
        rootPane.setOnMouseClicked(e -> {
            missingPropertiesLabel.setVisible(false);
        });
        rootPane.setFitToHeight(true);
        rootPane.setFitToWidth(true);
		rootPane.setPadding(new Insets(5.0));
		return rootPane;
    }
    
    private ScrollPane createManualRootPane(){
    	sourcePropList = new ListView<String>();
        targetPropList = new ListView<String>();
        addedSourcePropsList = new ListView<String>();
        addedTargetPropsList = new ListView<String>();
        Label sourceLabel = new Label("available Source Properties:");
        sourceID = new Label();
        Label targetLabel = new Label("available Target Properties:");
        targetID = new Label();
        Label addedSourceLabel = new Label("added Source Properties:");
        Label addedTargetLabel = new Label("added Target Properties:");
        VBox sourceColumn = new VBox();
        VBox targetColumn = new VBox();
        sourceColumn.getChildren().addAll(sourceID, sourceLabel, sourcePropList, addedSourceLabel, addedSourcePropsList);
        targetColumn.getChildren().addAll(targetID, targetLabel, targetPropList, addedTargetLabel, addedTargetPropsList);
        addAllButton = new Button("Add all");
        removeAllButton = new Button("Remove all");
        missingPropertiesLabel = new Label("At least one source and one target property must be chosen!");
        missingPropertiesLabel.setTextFill(Color.RED);
        missingPropertiesLabel.setVisible(false);
        HBox buttons = new HBox();

		Button switchMode = new Button("Automated Matching");
			switchMode.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					automated = true;
					rootPane = null;
					wizardView.setToRootPane(createRootPane());
					controller.load();
				}
			});
        buttons.getChildren().addAll(missingPropertiesLabel, switchMode, addAllButton, removeAllButton);
        BorderPane root = new BorderPane();
        HBox hb = new HBox();
        hb.getChildren().addAll(sourceColumn, targetColumn);
        HBox.setHgrow(sourceColumn, Priority.ALWAYS);
        HBox.setHgrow(targetColumn, Priority.ALWAYS);
        root.setCenter(hb);
        root.setBottom(buttons);
        rootPane = new ScrollPane(root);
        rootPane.setOnMouseClicked(e -> {
            missingPropertiesLabel.setVisible(false);
        });
        rootPane.setFitToHeight(true);
        rootPane.setFitToWidth(true);
		rootPane.setPadding(new Insets(5.0));
		return rootPane;
    }

    /**
     * adds listener to properties to display changes after loading them is finished.
     * also adds functionality
     */
    private void addListeners() {
    	if(automated){
    		addListenersForAutomated();
    	}else{
    		addListenersForManual();
    	}
    }
    
    @SuppressWarnings({"unchecked","rawtypes"})
    public void addListenersForAutomated(){

        automatedProperties.addListener(new ListChangeListener() {
            @Override
            public void onChanged( ListChangeListener.Change change) {
                if (automatedProperties.size() != 0)
                    putAllPropertiesToTable();
            }
        });

        automatedPropList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
        	if(automatedPropList.getSelectionModel().getSelectedItem() != null){
                addedAutomatedPropsList.getItems().add(automatedPropList.getSelectionModel().getSelectedItem());
                automatedPropList.getItems().remove(automatedPropList.getSelectionModel().getSelectedItem());
        	}
            }
        });

        addedAutomatedPropsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
        	if(addedAutomatedPropsList.getSelectionModel().getSelectedItem() != null){
                automatedPropList.getItems().add(addedAutomatedPropsList.getSelectionModel().getSelectedItem());
                addedAutomatedPropsList.getItems().remove(addedAutomatedPropsList.getSelectionModel().getSelectedItem());
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
    
    @SuppressWarnings({"unchecked","rawtypes"})
	public void addListenersForManual(){

        sourceProperties.addListener(new ListChangeListener() {
            @Override
            public void onChanged( ListChangeListener.Change change) {
                if (sourceProperties.size() != 0
                        && targetProperties.size() != 0) {
                    putAllPropertiesToTable();
                }
            }
        });

        targetProperties.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                if (sourceProperties.size() != 0
                        && targetProperties.size() != 0) {
                    putAllPropertiesToTable();
                }
            }
        });

        sourcePropList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
        	if(sourcePropList.getSelectionModel().getSelectedItem() != null){
                addedSourcePropsList.getItems().add(sourcePropList.getSelectionModel().getSelectedItem());
                sourcePropList.getItems().remove(sourcePropList.getSelectionModel().getSelectedItem());
        	}
            }
        });

        targetPropList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
        	if(targetPropList.getSelectionModel().getSelectedItem() != null){
                addedTargetPropsList.getItems().add(targetPropList.getSelectionModel().getSelectedItem());
                targetPropList.getItems().remove(targetPropList.getSelectionModel().getSelectedItem());
        	}
            }
        });

        addedSourcePropsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
        	if(addedSourcePropsList.getSelectionModel().getSelectedItem() != null){
                sourcePropList.getItems().add(addedSourcePropsList.getSelectionModel().getSelectedItem());
                addedSourcePropsList.getItems().remove(addedSourcePropsList.getSelectionModel().getSelectedItem());
        	}
            }
        });

        addedTargetPropsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
        	if(addedTargetPropsList.getSelectionModel().getSelectedItem() != null){
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
    	if(automated){
    		List<String>sourceProperties = new ArrayList<String>();
    		List<String>targetProperties = new ArrayList<String>();
    		for(AutomatedPropertyMatchingNode addedPropertyNode: this.addedProperties){
    			sourceProperties.add(addedPropertyNode.getSourceProperty().get());
    			targetProperties.add(addedPropertyNode.getTargetProperty().get());
    		}
    		controller.save(sourceProperties, targetProperties);
    	}else{
    		controller.save(this.addedSourcePropsList.getItems(), this.addedTargetPropsList.getItems());
    	}
    }
    
    public void showAutomatedProperties(AMapping properties){
    	//clear the list
    	availableProperties.removeAll(availableProperties);
    	//fill the list
    	for(String sourceProperty : properties.getMap().keySet()){
    		for(String targetProperty : properties.getMap().get(sourceProperty).keySet()){
    			availableProperties.add(new AutomatedPropertyMatchingNode(PrefixHelper.abbreviate(sourceProperty), PrefixHelper.abbreviate(targetProperty)));
    		}
    	}
    	System.out.println("availableProperties: ");
    	for(AutomatedPropertyMatchingNode apmn: availableProperties){
    		System.out.println(apmn.toString());
    	}
    	automatedPropList.setItems(availableProperties);
    	automatedPropList.getColumns().get(0).setText(controller.getConfig().getSourceEndpoint().getCurrentClass().getName() + "  properties");
    	automatedPropList.getColumns().get(1).setText(controller.getConfig().getTargetEndpoint().getCurrentClass().getName() + "  properties");
    }

    /**
     * Shows the available properties
     *
     * @param sourceOrTarget enum for source or target
     * @param properties list of properties to show
     */
    public void showAvailableProperties(SourceOrTarget sourceOrTarget,
                                        List<String> properties) {
        addedSourcePropsList.getItems().clear();
        addedTargetPropsList.getItems().clear();
        (sourceOrTarget == SOURCE ? sourceProperties : targetProperties)
                .setAll(properties);
        (sourceOrTarget == SOURCE ? sourcePropList : targetPropList).setItems(
        (sourceOrTarget == SOURCE ? sourceProperties : targetProperties));
    }

    /**
     * Puts the loaded properties to the table
     */
    private void putAllPropertiesToTable() {
        sourcePropList.setItems(sourceProperties);
        targetPropList.setItems(targetProperties);
        sourceID.setText("ID: " + controller.getConfig().getSourceInfo().getId() + "\t Class: " + controller.getConfig().getSourceEndpoint().getCurrentClass().getName());
        targetID.setText("ID: " + controller.getConfig().getTargetInfo().getId() + "\t Class: " + controller.getConfig().getTargetEndpoint().getCurrentClass().getName());
    }


    /**
     * Shows an error if something went wrong
     *
     * @param header header of error 
     * @param content content of message
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
    
    
}
