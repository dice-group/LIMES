package org.aksw.limes.core.gui.view;

import org.aksw.limes.core.gui.controller.WizardController;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * view used for creating a new configuration in three steps going
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class WizardView {
    private WizardController controller;
    private Button buttonBack;
    private Button buttonNext;
    private BorderPane rootPane;
    private Stage stage;

    /**
     * Constructor creates the view
     */
    WizardView() {
        createWindow();
    }

    /**
     * sets the controller
     * @param controller controller
     */
    public void setController(WizardController controller) {
        this.controller = controller;
    }

    /**
     * creates the window
     */
    private void createWindow() {
        ButtonBar buttonBar = new ButtonBar();
        buttonBack = new Button("Back");
        buttonBack.setOnAction(e -> controller.back());
        buttonBar.getButtons().add(buttonBack);
        buttonNext = new Button("???");
        buttonNext.setOnAction(e -> controller.nextOrFinish());
        buttonBar.getButtons().add(buttonNext);
        Button buttonCancel = new Button("Cancel");
        buttonBar.getButtons().add(buttonCancel);

        rootPane = new BorderPane();
        rootPane.setCenter(null);
        rootPane.setBottom(buttonBar);

        Scene scene = new Scene(rootPane, 1200, 600);
        scene.getStylesheets().add("gui/main.css");
        stage = new Stage();
        stage.setMinWidth(1200);
        stage.setMinHeight(600);
        buttonCancel.setOnAction(e -> {
            stage.close();
        });
        stage.setTitle("LIMES - New Configuration");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * corresponding to the step the buttons are changed 
     * @param editView view of the step
     * @param canGoBack true if there is a step before this one
     * @param canGoNext true if there is a step after this one
     */
    public void setEditView(IEditView editView, boolean canGoBack,
                            boolean canGoNext) {
        buttonBack.setDisable(!canGoBack);
        buttonNext.setText(canGoNext ? "Next" : "Finish");
        rootPane.setCenter(editView.getPane());
    }

    /**
     * closes the window
     */
    public void close() {
        stage.close();
    }
    
    public Stage getStage(){
	return this.stage;
    }
    
    public void setToRootPane(Parent pane){
    	rootPane.setCenter(pane);
    }
}
