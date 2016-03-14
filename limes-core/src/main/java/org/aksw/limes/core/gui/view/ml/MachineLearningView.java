package org.aksw.limes.core.gui.view.ml;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.aksw.limes.core.gui.controller.ml.MachineLearningController;
import org.aksw.limes.core.gui.view.MainView;

public abstract class MachineLearningView {

	protected MainView mainView;

	protected MachineLearningController mlController;

	public static final String[] mlAlgorithms = { "Eagle", "Euclid", "Lion",
			"Wombat", "Raven", "Ukulele", "Coala", "Acids", "Mandolin",
			"Cluster" };

	public MachineLearningView(MainView mainView, MachineLearningController mlController) {
		this.mainView = mainView;
		this.mlController = mlController;
		this.mlController.setMlView(this);
		createMLAlgorithmsRootPane();
	}

	//TODO finish
	/**
	 * checks if the MLAlgorithm is implemented for this LearningSetting
	 * corresponding to the subclass of MachineLearningView
	 * 
	 * @return ComboBox with corresponding options
	 */
	public void createMLAlgorithmsRootPane() {
		BorderPane root = new BorderPane();
		HBox content = new HBox();

		ObservableList<String> mloptions = FXCollections.observableArrayList();
		//FIXME Temporary Solution
		mloptions.add("Lion");
		if (this instanceof ActiveLearningView){
			
		}else if(this instanceof BatchLearningView){
			
		}else if(this instanceof UnsupervisedLearningView){
			
		}else{
			System.err.println("Unknown subclass of MachineLearningView");
		}
		ComboBox<String>mlOptionsChooser = new ComboBox<String>(mloptions);
		mlOptionsChooser.setPromptText("choose algorithm");
		mlOptionsChooser.setOnAction(e -> {
			mlController.setMLAlgorithmToModel(mlOptionsChooser.getValue());
		});

		content.getChildren().add(mlOptionsChooser);
		VBox selfConfigWrapper = new VBox();
		selfConfigWrapper.setFillWidth(false);
		root.setTop(content);
		root.setCenter(selfConfigWrapper);
		Scene scene = new Scene(root, 300, 400);
		scene.getStylesheets().add("gui/main.css");

		Stage stage = new Stage();
		stage.setTitle("LIMES - Machine Learning");
		stage.setScene(scene);
		stage.show();

	}

	//public abstract void createRootPane(HashMap<String, ?> params);

}
