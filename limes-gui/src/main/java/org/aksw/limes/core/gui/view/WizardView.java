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
		this.createWindow();
	}

	/**
	 * sets the controller
	 * 
	 * @param controller
	 *            controller
	 */
	public void setController(WizardController controller) {
		this.controller = controller;
	}

	/**
	 * creates the window
	 */
	private void createWindow() {
		final ButtonBar buttonBar = new ButtonBar();
		this.buttonBack = new Button("Back");
		this.buttonBack.setOnAction(e -> this.controller.back());
		buttonBar.getButtons().add(this.buttonBack);
		this.buttonNext = new Button("???");
		this.buttonNext.setOnAction(e -> this.controller.nextOrFinish());
		buttonBar.getButtons().add(this.buttonNext);
		final Button buttonCancel = new Button("Cancel");
		buttonBar.getButtons().add(buttonCancel);

		this.rootPane = new BorderPane();
		this.rootPane.setCenter(null);
		this.rootPane.setBottom(buttonBar);

		final Scene scene = new Scene(this.rootPane, 1200, 600);
		scene.getStylesheets().add("gui/main.css");
		this.stage = new Stage();
		this.stage.setMinWidth(1200);
		this.stage.setMinHeight(600);
		buttonCancel.setOnAction(e -> {
			this.stage.close();
		});
		this.stage.setTitle("LIMES - New Configuration");
		this.stage.setScene(scene);
		this.stage.show();
	}

	/**
	 * corresponding to the step the buttons are changed
	 * 
	 * @param editView
	 *            view of the step
	 * @param canGoBack
	 *            true if there is a step before this one
	 * @param canGoNext
	 *            true if there is a step after this one
	 */
	public void setEditView(IEditView editView, boolean canGoBack, boolean canGoNext) {
		this.buttonBack.setDisable(!canGoBack);
		this.buttonNext.setText(canGoNext ? "Next" : "Finish");
		this.rootPane.setCenter(editView.getPane());
	}

	/**
	 * closes the window
	 */
	public void close() {
		this.stage.close();
	}

	public Stage getStage() {
		return this.stage;
	}

	public void setToRootPane(Parent pane) {
		this.rootPane.setCenter(pane);
	}
}
