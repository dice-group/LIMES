package org.aksw.limes.core.gui.view.ml;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.view.ToolBox;
import org.aksw.limes.core.gui.view.ToolBox.PropertyItem;
import org.aksw.limes.core.ml.algorithm.LearningParameter;
import org.aksw.limes.core.ml.algorithm.eagle.util.PropertyMapping;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Class that creates a view to let the user match the properties
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class MLPropertyMatchingView {

	private ScrollPane rootPane;
	private ListView<String> sourcePropList;
	private ListView<String> targetPropList;
	private boolean sourcePropertyUnmatched = false;
	private boolean targetPropertyUnmatched = false;
	private Button cancelButton;
	private Button finishButton;
	private Stage stage;
	private final LearningParameter param;
	/**
	 * contains all the unmatchedPropertyBoxes where Properties have already
	 * been matched plus one empty/unfinished one
	 */
	private VBox matchedPropertiesBox;
	/**
	 * the downmost HBox inside matchedPropertiesBox is either empty or contains
	 * an unfinished matching
	 */
	private HBox unmatchedPropertyBox;
	private final Config config;

	private final ToolBox mainViewToolBox;

	/**
	 * Constructor sets the parameters, creates the view and adds listeners
	 *
	 * @param config
	 * @param param
	 */
	public MLPropertyMatchingView(Config config, LearningParameter param, ToolBox tb) {
		this.config = config;
		this.param = param;
		this.mainViewToolBox = tb;
		this.createRootPane();
		this.addListeners();
	}

	/**
	 * Creates the root pane and opens a stage with all the elements
	 */
	private void createRootPane() {
		this.sourcePropList = new ListView<>();
		this.sourcePropList.setId("MLPropertyMatchingViewSourcePropList");
		this.targetPropList = new ListView<>();
		this.sourcePropList.setId("MLPropertyMatchingViewTargetPropList");
		if (this.mainViewToolBox.getToolBoxSourceProperties() != null) {
			for (final PropertyItem prop : this.mainViewToolBox.getToolBoxSourceProperties().getItems()) {
				this.sourcePropList.getItems().add(prop.getName());
			}
		} else {
			this.sourcePropList.getItems().addAll(this.config.getSourceInfo().getProperties());
		}
		if (this.mainViewToolBox.getToolBoxTargetProperties() != null) {
			for (final PropertyItem prop : this.mainViewToolBox.getToolBoxTargetProperties().getItems()) {
				this.targetPropList.getItems().add(prop.getName());
			}
		} else {
			this.targetPropList.getItems().addAll(this.config.getTargetInfo().getProperties());
		}
		final Label sourceLabel = new Label("available Source Properties:");
		final Label targetLabel = new Label("available Target Properties:");
		final VBox sourceColumn = new VBox();
		final VBox targetColumn = new VBox();
		sourceColumn.getChildren().addAll(sourceLabel, this.sourcePropList);
		targetColumn.getChildren().addAll(targetLabel, this.targetPropList);
		this.matchedPropertiesBox = new VBox();
		this.cancelButton = new Button("cancel");
		this.cancelButton.setTooltip(new Tooltip("cancel property matching"));
		this.finishButton = new Button("finish");
		this.finishButton.setTooltip(new Tooltip("set this property matching"));
		this.makeUnmatchedPropertyBox();
		this.finishButton.setDisable(true);
		final HBox buttons = new HBox();
		buttons.getChildren().addAll(this.cancelButton, this.finishButton);
		final BorderPane root = new BorderPane();
		final HBox hb = new HBox();
		hb.getChildren().addAll(sourceColumn, targetColumn);
		HBox.setHgrow(sourceColumn, Priority.ALWAYS);
		HBox.setHgrow(targetColumn, Priority.ALWAYS);
		final VBox topBox = new VBox();
		topBox.getChildren().addAll(hb);
		root.setTop(topBox);
		root.setCenter(this.matchedPropertiesBox);
		root.setBottom(buttons);
		this.rootPane = new ScrollPane(root);
		this.rootPane.setFitToHeight(true);
		this.rootPane.setFitToWidth(true);
		final Scene scene = new Scene(this.rootPane, 300, 400);
		scene.getStylesheets().add("gui/main.css");
		this.stage = new Stage();
		this.stage.setTitle("LIMES - Property Matching");
		this.stage.setScene(scene);
		this.stage.show();
	}

	/**
	 * adds the listeners
	 */
	private void addListeners() {
		this.cancelButton.setOnAction(e -> {
			this.stage.close();
		});

		this.finishButton.setOnAction(e -> {
			if (!this.sourcePropertyUnmatched && !this.targetPropertyUnmatched) {
				final PropertyMapping propMap = new PropertyMapping();
				// size -1 because there is always another empty
				// unmatchedPropertyBox
				// inside
				for (int i = 0; i < this.matchedPropertiesBox.getChildren().size() - 1; i++) {
					final HBox row = (HBox) this.matchedPropertiesBox.getChildren().get(i);
					final String sourceProp = ((Label) ((VBox) row.getChildren().get(0)).getChildren().get(0))
							.getText();
					final String targetProp = ((Label) ((VBox) row.getChildren().get(1)).getChildren().get(0))
							.getText();
					@SuppressWarnings("unchecked")
					final String type = ((Spinner<String>) ((HBox) row.getChildren().get(2)).getChildren().get(0))
							.getValue();
					switch (type) {
					case "String":
						propMap.addStringPropertyMatch(sourceProp, targetProp);
						break;
					case "Number":
						propMap.addNumberPropertyMatch(sourceProp, targetProp);
						break;
					case "Date":
						propMap.addDatePropertyMatch(sourceProp, targetProp);
						break;
					case "Pointset":
						propMap.addPointsetPropertyMatch(sourceProp, targetProp);
						break;
					}
				}
				this.param.setValue(propMap);
				this.config.propertyMapping = propMap;
				this.stage.close();
			}
		});
		this.sourcePropList.setOnMouseClicked(e -> {
			if (MLPropertyMatchingView.this.sourcePropList.getSelectionModel().getSelectedItem() != null) {
				if (MLPropertyMatchingView.this.sourcePropertyUnmatched
						|| !MLPropertyMatchingView.this.sourcePropertyUnmatched
								&& !MLPropertyMatchingView.this.targetPropertyUnmatched) {
					final Label sourceProp = new Label(
							MLPropertyMatchingView.this.sourcePropList.getSelectionModel().getSelectedItem());
					sourceProp.setAlignment(Pos.BASELINE_LEFT);
					((VBox) MLPropertyMatchingView.this.unmatchedPropertyBox.getChildren().get(0)).getChildren()
							.add(sourceProp);
					MLPropertyMatchingView.this.sourcePropList.getItems()
							.remove(MLPropertyMatchingView.this.sourcePropList.getSelectionModel().getSelectedItem());

					// because always at least two VBoxes are contained
					if (MLPropertyMatchingView.this.unmatchedPropertyBox.getChildren().size() < 3) {
						MLPropertyMatchingView.this.addSpinnerAndButton();
					}
					if (MLPropertyMatchingView.this.sourcePropertyUnmatched) {
						MLPropertyMatchingView.this.sourcePropertyUnmatched = false;
						MLPropertyMatchingView.this.makeUnmatchedPropertyBox();
					} else {
						MLPropertyMatchingView.this.finishButton.setDisable(true);
						MLPropertyMatchingView.this.targetPropertyUnmatched = true;
					}
				}
			}
		});

		this.targetPropList.setOnMouseClicked(e -> {
			if (MLPropertyMatchingView.this.targetPropList.getSelectionModel().getSelectedItem() != null) {
				if (MLPropertyMatchingView.this.targetPropertyUnmatched
						|| !MLPropertyMatchingView.this.sourcePropertyUnmatched
								&& !MLPropertyMatchingView.this.targetPropertyUnmatched) {
					final Label targetProp = new Label(
							MLPropertyMatchingView.this.targetPropList.getSelectionModel().getSelectedItem());
					targetProp.setAlignment(Pos.BASELINE_LEFT);
					((VBox) MLPropertyMatchingView.this.unmatchedPropertyBox.getChildren().get(1)).getChildren()
							.add(targetProp);
					MLPropertyMatchingView.this.targetPropList.getItems()
							.remove(MLPropertyMatchingView.this.targetPropList.getSelectionModel().getSelectedItem());
					if (MLPropertyMatchingView.this.unmatchedPropertyBox.getChildren().size() < 3) {
						MLPropertyMatchingView.this.addSpinnerAndButton();
					}
					if (MLPropertyMatchingView.this.targetPropertyUnmatched) {
						MLPropertyMatchingView.this.targetPropertyUnmatched = false;
						MLPropertyMatchingView.this.makeUnmatchedPropertyBox();
					} else {
						MLPropertyMatchingView.this.finishButton.setDisable(true);
						MLPropertyMatchingView.this.sourcePropertyUnmatched = true;
					}
				}
			}
		});
	}

	private void addSpinnerAndButton() {
		final List<String> propertyTypeList = new ArrayList<>();
		propertyTypeList.add("String");
		propertyTypeList.add("Number");
		propertyTypeList.add("Date");
		propertyTypeList.add("Pointset");
		final ObservableList<String> obsPropTypeList = FXCollections.observableList(propertyTypeList);
		final SpinnerValueFactory<String> svf = new SpinnerValueFactory.ListSpinnerValueFactory<>(obsPropTypeList);
		final Spinner<String> propertyTypeSpinner = new Spinner<>();
		propertyTypeSpinner.setValueFactory(svf);
		final Button deleteRowButton = new Button("x");
		final HBox spinnerAndButtonBox = new HBox();
		spinnerAndButtonBox.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(spinnerAndButtonBox, Priority.ALWAYS);
		spinnerAndButtonBox.getChildren().addAll(propertyTypeSpinner, deleteRowButton);
		this.unmatchedPropertyBox.getChildren().add(spinnerAndButtonBox);
		this.unmatchedPropertyBox.setAlignment(Pos.CENTER_LEFT);
		this.unmatchedPropertyBox.setPrefWidth(this.stage.getWidth());
		deleteRowButton.setOnAction(e_ -> {
			final HBox column = (HBox) deleteRowButton.getParent();
			final String matchedSource = ((Label) ((VBox) column.getChildren().get(0)).getChildren().get(0)).getText();
			final String matchedTarget = ((Label) ((VBox) column.getChildren().get(1)).getChildren().get(0)).getText();
			if (matchedSource != null) {
				this.sourcePropList.getItems().add(matchedSource);
			}
			if (matchedTarget != null) {
				this.targetPropList.getItems().add(matchedTarget);
			}
			final VBox row = (VBox) deleteRowButton.getParent().getParent();
			row.getChildren().remove(deleteRowButton.getParent());
		});
	}

	private void makeUnmatchedPropertyBox() {
		this.finishButton.setDisable(false);
		this.sourcePropertyUnmatched = false;
		this.unmatchedPropertyBox = new HBox();
		this.unmatchedPropertyBox.setSpacing(20);
		final VBox unmatchedProp1 = new VBox();
		final VBox unmatchedProp2 = new VBox();
		this.unmatchedPropertyBox.getChildren().addAll(unmatchedProp1, unmatchedProp2);
		this.matchedPropertiesBox.getChildren().add(this.unmatchedPropertyBox);
	}

}
