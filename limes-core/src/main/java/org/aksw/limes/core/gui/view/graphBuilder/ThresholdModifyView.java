package org.aksw.limes.core.gui.view.graphBuilder;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class ThresholdModifyView {

    private Label acceptanceThresholdlabel = null;
    private TextField acceptanceThresholdinput = null;

    /**
     * Constructor builds the whole view
     * @param gbv GraphBuildView
     * @param node NodeView
     */
    public ThresholdModifyView(GraphBuildView gbv, NodeView node) {
        Stage stage = new Stage();
        VBox root = new VBox();

        HBox verThresh = new HBox(25);
        HBox accThresh = new HBox(23);
        HBox buttons = new HBox(100);

        String verThreshLabeltext = "";
        String accThreshLabeltext = "";

        if (node.nodeShape == NodeView.OPERATOR) {
            if(node.nodeData.getChilds().size() > 0){
            verThreshLabeltext = node.nodeData.getChilds().get(0).id
                    + " threshold: ";
            }else{
        	verThreshLabeltext = "parent 1 threshold: ";
            }
            if (node.nodeData.getChilds().size() > 1) {
                accThreshLabeltext = node.nodeData.getChilds().get(1).id
                        + " threshold: ";
            }else{
        	accThreshLabeltext = "parent 2 threshold: ";
            }
        } else {
            verThreshLabeltext = "Verification threshold: ";
            accThreshLabeltext = "Acceptance threshold: ";
        }
        int index = gbv.nodeList.indexOf(node);
        Label verificationThresholdlabel = new Label(verThreshLabeltext);
        TextField verificationThresholdinput = new TextField();
        verificationThresholdinput.setPromptText("value between 0 and 1");
        verThresh.getChildren().addAll(verificationThresholdlabel,
                verificationThresholdinput);
        if (!(node.nodeShape == NodeView.OPERATOR && node.nodeData.getChilds()
                .size() == 1)) {
            acceptanceThresholdlabel = new Label(accThreshLabeltext);
            acceptanceThresholdinput = new TextField();
            acceptanceThresholdinput.setPromptText("value between 0 and 1");
            accThresh.getChildren().addAll(acceptanceThresholdlabel,
                    acceptanceThresholdinput);
        }
        Button save = new Button("Save");
        save.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if (node.nodeShape == NodeView.OPERATOR
                        && node.nodeData.getChilds().size() == 1) {
                    if ((verificationThresholdinput.getText() != null && !verificationThresholdinput
                            .getText().isEmpty())) {

                        try {
                            String acc = verificationThresholdinput.getText()
                                    .replaceAll(",", ".");
                            gbv.nodeList.get(index).nodeData.param1 = Double
                                    .parseDouble(acc);
                        } catch (NumberFormatException exc) {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setContentText("Value is not legitimate!");
                            alert.showAndWait();
                        }

                    } else {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setContentText("You have not entered a value!");
                        alert.showAndWait();
                    }
                    if (gbv.nodeList.get(index).nodeData.param1 != null) {
                        stage.close();
                        gbv.draw();
                    }
                } else {
                    if ((acceptanceThresholdinput.getText() != null
                            && !acceptanceThresholdinput.getText().isEmpty()
                            && verificationThresholdinput.getText() != null && !verificationThresholdinput
                            .getText().isEmpty())) {
                        try {
                            String acc = acceptanceThresholdinput.getText()
                                    .replaceAll(",", ".");
                            gbv.nodeList.get(index).nodeData.param1 = Double
                                    .parseDouble(acc);
                            String ver = verificationThresholdinput.getText()
                                    .replaceAll(",", ".");
                            gbv.nodeList.get(index).nodeData.param2 = Double
                                    .parseDouble(ver);
                        } catch (NumberFormatException exc) {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setContentText("Values are not legitimate!");
                            alert.showAndWait();
                        }

                    } else {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setContentText("Entered values are incomplete!");
                        alert.showAndWait();
                    }

                    if (gbv.nodeList.get(index).nodeData.param1 != null
                            && gbv.nodeList.get(index).nodeData.param2 != null) {
                        stage.close();
                        gbv.draw();
                    }
                }
            }
        });

        Button cancel = new Button("Cancel");
        cancel.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                stage.close();
            }
        });

        buttons.getChildren().addAll(save, cancel);
        buttons.setPadding(new Insets(5, 50, 1, 50));
        buttons.setSpacing(120);

        if (!(node.nodeShape == NodeView.OPERATOR && node.nodeData.getChilds()
                .size() == 1)) {
            root.getChildren().addAll(verThresh, accThresh, buttons);
        } else {
            root.getChildren().addAll(verThresh, buttons);
        }
        root.setPadding(new Insets(5, 5, 5, 5));
        Scene scene = new Scene(root, 450, 130);
        scene.getStylesheets().add("gui/main.css");
        stage.setMinHeight(scene.getHeight());
        stage.setMinWidth(scene.getWidth());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

}
