package org.aksw.limes.core.gui.view;

import org.aksw.limes.core.gui.controller.TaskProgressController;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * popup window to show progress to the user
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class TaskProgressView {
	private Stage stage;
	private TaskProgressController controller;
	/**
	 * displays what is happening at the moment
	 */
	private StringProperty informationLabel;
	private ProgressBar progressBar;
	private final BooleanProperty finishedSuccessfully;
	private final BooleanProperty cancelled;

	/**
	 * Constructor builds the window with the given title
	 * 
	 * @param title
	 *            title of window
	 */
	public TaskProgressView(String title) {
		this.finishedSuccessfully = new SimpleBooleanProperty(false);
		this.cancelled = new SimpleBooleanProperty(false);
		this.showWindow(title);
	}

	/**
	 * sets the controller
	 * 
	 * @param controller
	 *            controller
	 */
	public void setController(TaskProgressController controller) {
		this.controller = controller;
	}

	/**
	 * builds the window
	 * 
	 * @param title
	 */
	private void showWindow(String title) {
		final BorderPane mainPane = new BorderPane();

		final Label label = new Label(title);
		final Label information = new Label("");
		this.informationLabel = new SimpleStringProperty(" ");
		information.textProperty().bind(this.informationLabel);

		this.progressBar = new ProgressBar();

		final HBox hb = new HBox();
		hb.setSpacing(5);
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(label, this.progressBar);
		final VBox vb = new VBox();
		vb.getChildren().addAll(hb, information);
		vb.setAlignment(Pos.CENTER);
		mainPane.setTop(vb);

		final Button cancelButton = new Button("Cancel");

		final HBox hb2 = new HBox();
		hb2.setSpacing(5);
		hb2.setAlignment(Pos.CENTER);
		hb2.getChildren().addAll(cancelButton);
		mainPane.setBottom(hb2);

		// cancels all tasks and closes the window
		cancelButton.setOnAction(event -> {
			this.controller.cancel();
		});

		final Scene scene = new Scene(mainPane, 250, 100, Color.WHITE);
		scene.getStylesheets().add("gui/main.css");
		this.stage = new Stage();
		this.stage.setMinHeight(scene.getHeight());
		this.stage.setMinWidth(scene.getWidth());
		this.stage.setAlwaysOnTop(true);
		this.stage.setTitle(title);
		this.stage.setScene(scene);
		this.stage.show();
	}

	/**
	 * closes the window
	 */
	public void close() {
		this.stage.close();
	}

	/**
	 * returns the informationLabel
	 * 
	 * @return label
	 */
	public StringProperty getInformationLabel() {
		return this.informationLabel;
	}

	/**
	 * returns the progress bar
	 * 
	 * @return progress bar
	 */
	public ProgressBar getProgressBar() {
		return this.progressBar;
	}

	public BooleanProperty getFinishedSuccessfully() {
		return this.finishedSuccessfully;
	}

	public void setFinishedSuccessfully(boolean finishedSuccessfully) {
		this.finishedSuccessfully.set(finishedSuccessfully);
	}

	public BooleanProperty getCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled.set(cancelled);
	}

}
