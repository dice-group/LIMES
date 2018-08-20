package org.aksw.limes.core.gui;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Locale;

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
		this.mainView = new MainView(stage);
		this.mainController = new MainController(this.mainView);
		this.mainView.setController(this.mainController);
	}

	@Before
	public void loadConfig() {
		this.mainController.loadConfig(
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
		this.clickOn("Configuration");
		logger.info("Clicking on Edit");
		this.clickOn("Edit");
		// Necessary because otherwise the sub-menu vanishes
		logger.info("Moving to Edit Classes");
		this.moveTo("Edit Classes");
		logger.info("Clicking on Edit Properties");
		this.clickOn("Edit Properties");

		logger.info("Waiting for properties to finish loading");
		CustomGuiTest.waitUntilLoadingWindowIsClosed("Getting properties", 500);
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", 180);
		this.clickOn("#switchModeButton");
		logger.info("Waiting for dbo:abbreviation");
		CustomGuiTest.waitUntilNodeIsVisible("dbo:abbreviation", 180);
		this.clickOn("dbo:abbreviation");
		this.clickOn("dbo:birthDate");
		this.clickOn("Finish");

		this.clickOn("dbo:abbreviation");
		this.clickOn("dbo:birthDate");
	}

	@AfterClass
	public static void cleanup() {
		final FxRobot rob = new FxRobot();
		for (final Window w : rob.listWindows()) {
			final int currentsize = rob.listWindows().size();
			System.out.println(((Stage) w).getTitle());
			// Avoid not on fx application thread error
			Platform.runLater(() -> ((Stage) w).close());
			CustomGuiTest.waitUntilWindowIsClosed(currentsize - 1, 200);
		}
		assertEquals(0, rob.listWindows().size());
	}

}
