package org.aksw.limes.core.gui.view.graphBuilder;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * used to represent a popup menu to set the thresholds in a link specification
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class ThresholdModifyView {

	private Label acceptanceThresholdlabel = null;
	private TextField acceptanceThresholdinput = null;

	/**
	 * Constructor builds the whole view
	 * 
	 * @param gbv
	 *            GraphBuildView
	 * @param node
	 *            NodeView
	 */
	public ThresholdModifyView(GraphBuildView gbv, NodeView node) {
		final Stage stage = new Stage();
		final VBox root = new VBox();

		final HBox verThresh = new HBox(25);
		final HBox accThresh = new HBox(23);
		final HBox buttons = new HBox(100);

		String verThreshLabeltext = "";
		String accThreshLabeltext = "";

		if (node.nodeShape == NodeView.OPERATOR) {
			if (node.nodeData.getChilds().size() > 0) {
				verThreshLabeltext = node.nodeData.getChilds().get(0).id + " threshold: ";
			} else {
				verThreshLabeltext = "parent 1 threshold: ";
			}
			if (node.nodeData.getChilds().size() > 1) {
				accThreshLabeltext = node.nodeData.getChilds().get(1).id + " threshold: ";
			} else {
				accThreshLabeltext = "parent 2 threshold: ";
			}
		} else {
			verThreshLabeltext = "Verification threshold: ";
			accThreshLabeltext = "Acceptance threshold: ";
		}
		final int index = gbv.nodeList.indexOf(node);
		final Label verificationThresholdlabel = new Label(verThreshLabeltext);
		final TextField verificationThresholdinput = new TextField();
		verificationThresholdinput.setPromptText("value between 0 and 1");
		verThresh.getChildren().addAll(verificationThresholdlabel, verificationThresholdinput);
		if (!(node.nodeShape == NodeView.OPERATOR && node.nodeData.getChilds().size() == 1)) {
			this.acceptanceThresholdlabel = new Label(accThreshLabeltext);
			this.acceptanceThresholdinput = new TextField();
			this.acceptanceThresholdinput.setPromptText("value between 0 and 1");
			accThresh.getChildren().addAll(this.acceptanceThresholdlabel, this.acceptanceThresholdinput);
		}
		final Button save = new Button("Save");
		save.setOnAction(e -> {
			if (node.nodeShape == NodeView.OPERATOR && node.nodeData.getChilds().size() == 1) {
				if (verificationThresholdinput.getText() != null && !verificationThresholdinput.getText().isEmpty()) {

					try {
						final String acc1 = verificationThresholdinput.getText().replaceAll(",", ".");
						gbv.nodeList.get(index).nodeData.param1 = Double.parseDouble(acc1);
					} catch (final NumberFormatException exc1) {
						final Alert alert1 = new Alert(AlertType.INFORMATION);
						alert1.setContentText("Value is not legitimate!");
						alert1.showAndWait();
					}

				} else {
					final Alert alert2 = new Alert(AlertType.INFORMATION);
					alert2.setContentText("You have not entered a value!");
					alert2.showAndWait();
				}
				if (gbv.nodeList.get(index).nodeData.param1 != null) {
					stage.close();
					gbv.draw();
				}
			} else {
				if (ThresholdModifyView.this.acceptanceThresholdinput.getText() != null
						&& !ThresholdModifyView.this.acceptanceThresholdinput.getText().isEmpty()
						&& verificationThresholdinput.getText() != null
						&& !verificationThresholdinput.getText().isEmpty()) {
					try {
						final String acc2 = ThresholdModifyView.this.acceptanceThresholdinput.getText().replaceAll(",",
								".");
						gbv.nodeList.get(index).nodeData.param1 = Double.parseDouble(acc2);
						final String ver = verificationThresholdinput.getText().replaceAll(",", ".");
						gbv.nodeList.get(index).nodeData.param2 = Double.parseDouble(ver);
					} catch (final NumberFormatException exc2) {
						final Alert alert3 = new Alert(AlertType.INFORMATION);
						alert3.setContentText("Values are not legitimate!");
						alert3.showAndWait();
					}

				} else {
					final Alert alert4 = new Alert(AlertType.INFORMATION);
					alert4.setContentText("Entered values are incomplete!");
					alert4.showAndWait();
				}

				if (gbv.nodeList.get(index).nodeData.param1 != null
						&& gbv.nodeList.get(index).nodeData.param2 != null) {
					stage.close();
					gbv.draw();
				}
			}
		});

		final Button cancel = new Button("Cancel");
		cancel.setOnAction(e -> stage.close());

		buttons.getChildren().addAll(save, cancel);
		buttons.setPadding(new Insets(5, 50, 1, 50));
		buttons.setSpacing(120);

		if (!(node.nodeShape == NodeView.OPERATOR && node.nodeData.getChilds().size() == 1)) {
			root.getChildren().addAll(verThresh, accThresh, buttons);
		} else {
			root.getChildren().addAll(verThresh, buttons);
		}
		root.setPadding(new Insets(5, 5, 5, 5));
		final Scene scene = new Scene(root, 450, 130);
		scene.getStylesheets().add("gui/main.css");
		stage.setMinHeight(scene.getHeight());
		stage.setMinWidth(scene.getWidth());
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();
	}

}
