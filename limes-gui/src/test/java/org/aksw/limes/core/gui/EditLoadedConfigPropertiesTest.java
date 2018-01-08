package org.aksw.limes.core.gui;

import java.io.File;
import java.util.Locale;
import static org.junit.Assert.assertEquals;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.util.CustomGuiTest;
import org.aksw.limes.core.gui.view.MainView;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
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
		CustomGuiTest.waitUntilLoadingWindowIsClosed("Getting properties",500);
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

	@AfterClass
	public static void cleanup(){
		FxRobot rob = new FxRobot();
		for(Window w : rob.listWindows()){
			int currentsize = rob.listWindows().size();
			System.out.println(((Stage)w).getTitle());
			//Avoid not on fx application thread error
            Platform.runLater(new Runnable() {
                @Override public void run() {
                	((Stage)w).close();
                }
            });
            CustomGuiTest.waitUntilWindowIsClosed(currentsize - 1, 200);
		}
		assertEquals(0,rob.listWindows().size());
	}

}
