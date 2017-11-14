package org.aksw.limes.core.gui;

import java.io.File;
import java.util.Locale;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.util.CustomGuiTest;
import org.aksw.limes.core.gui.view.MainView;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import com.sun.javafx.robot.FXRobot;

import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class EditLoadedConfigPropertiesTest extends ApplicationTest {

	private static final Logger logger = LoggerFactory.getLogger(EditLoadedConfigPropertiesTest.class);
	MainView mainView;
	MainController mainController;

	@Override
	public void start(Stage stage) throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		mainView = new MainView(stage);
		mainController = new MainController(mainView);
		mainView.setController(mainController);
	}

	@Before
	public void loadConfig() {
		mainController.loadConfig(
				new File(Thread.currentThread().getContextClassLoader().getResource("gui/testConfig.xml").getFile()));
	}

	@BeforeClass
	public static void setup() {
		System.setProperty("testfx.robot", "glass");
		System.setProperty("testfx.headless", "true");
		System.setProperty("prism.order", "sw");
		System.setProperty("prism.text", "t2k");
		System.setProperty("java.awt.headless", "true");
	}

	@Test
	public void testEditProperties() throws InterruptedException {
		logger.info("Clicking on Configuration");
		clickOn("Configuration");
		logger.info("Clicking on Edit");
		clickOn("Edit");
		// Necessary because otherwise the sub-menu vanishes
		logger.info("Moving to Edit Classes");
		moveTo("Edit Classes");
		logger.info("Clicking on Edit Properties");
		clickOn("Edit Properties");

		logger.info("Waiting for properties to finish loading");
		int sec = 0;
		while(listWindows().size() == 1){
			sleep(1000);
			sec++;
			if(sec % 100 == 0){
				logger.info("Waited: " + sec + " seconds");
			}
			//avoid infinite loop
			if(sec > 1000){
				break;
			}
		}
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", 180);
		clickOn("#switchModeButton");
		logger.info("Waiting for dbo:abbreviation");
		CustomGuiTest.waitUntilNodeIsVisible("dbo:abbreviation", 180);
		clickOn("dbo:abbreviation");
		clickOn("dbo:birthDate");
		clickOn("Finish");

		clickOn("dbo:abbreviation");
		clickOn("dbo:birthDate");
	}

}
