package org.aksw.limes.core.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

import java.util.Locale;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.util.CustomGuiTest;
import org.aksw.limes.core.gui.util.ProjectPropertiesGetter;
import org.aksw.limes.core.gui.view.MainView;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ConfigurationWizardTest extends ApplicationTest {

	MainView mainView;
	MainController mainController;
	private static final int timeout = 180;
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationWizardTest.class);
	private String resourcesPath;

	@Override
	public void start(Stage stage) throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		this.mainView = new MainView(stage);
		this.mainController = new MainController(this.mainView);
		this.mainView.setController(this.mainController);
		this.resourcesPath = ProjectPropertiesGetter.getProperty(
				Thread.currentThread().getContextClassLoader().getResource("project.properties").getPath(),
				"limes-core.resources");
	}

	@Before
	public void openWizard() {
		this.clickOn("#menuConfiguration");
		this.clickOn("New");
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
	public void walkThrough() {
		this.testEditEndpoint();
		this.testEditClassMatching();
		this.testEditPropertyMatching();
		final FxRobot robo = new FxRobot();
		// Verify that properties were loaded
		final ListView<String> tbsp = robo.lookup("#toolBoxSourceProperties").query();
		final ListView<String> tbtp = robo.lookup("#toolBoxTargetProperties").query();
		logger.info("Verifying that properties got loaded");
		assertTrue(tbsp.getItems().size() > 0);
		assertTrue(tbtp.getItems().size() > 0);
	}

	public void testEditEndpoint() {
		logger.info("testEditEndpoint started");
		for (final Window w : this.listWindows()) {
			logger.info("Window open: " + ((Stage) w).getTitle());
		}
		this.clickOn("#SOURCEendpointURLTextField").write(this.resourcesPath + "datasets/Restaurants/restaurant1.nt");
		this.clickOn("#SOURCEidNamespaceTextField").write("Restaurant");
		this.clickOn("#TARGETendpointURLTextField").write(this.resourcesPath + "datasets/Persons2/person21.nt");
		this.clickOn("#TARGETidNamespaceTextField").write("Person");
		logger.info("Edited Endpoint clicking NEXT");
		this.clickOn("Next");
	}

	public void testEditClassMatching() {
		logger.info("testEditClassMatching started");
		CustomGuiTest.waitUntilLoadingWindowIsClosed("Get classes", 500);
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		// Test if manual matching gets loaded
		logger.info("Clicking on Button to get Manual Matching");
		this.clickOn("#switchModeButton");
		logger.info("Waiting for #sourcePabel to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#sourcePanel", timeout);
		verifyThat("#sourcePanel", NodeMatchers.isVisible());
		verifyThat("#targetPanel", NodeMatchers.isVisible());
		logger.info("Waiting for #switchModeButton to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		logger.info("Clicking on Button to get automated Matching");
		// Continue with automated matching
		this.clickOn("#switchModeButton");
		logger.info("Waiting for #tableView to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#tableView", timeout);
		logger.info("Clicking on tableView");
		this.clickOn("#tableView");
		logger.info("Clicking on NEXT");
		this.clickOn("Next");
	}

	public void testEditPropertyMatching() {
		CustomGuiTest.waitUntilLoadingWindowIsClosed("Getting properties", 500);
		logger.info("testEditPropertyMatching started");
		logger.info("Waiting for #sourcePropColumn to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#sourcePropColumn", timeout);
		verifyThat("#sourcePropColumn", NodeMatchers.isVisible());
		verifyThat("#targetPropColumn", NodeMatchers.isVisible());
		logger.info("Waiting for #switchModeButton to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		logger.info("Clicking on Button to get Manual Matching");
		this.clickOn("#switchModeButton");
		logger.info("Waiting for #sourcePropList to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#sourcePropList", timeout);
		verifyThat("#sourcePropList", NodeMatchers.isVisible());
		verifyThat("#targetPropList", NodeMatchers.isVisible());
		verifyThat("#addedSourcePropsList", NodeMatchers.isVisible());
		verifyThat("#addedTargetPropsList", NodeMatchers.isVisible());
		logger.info("Waiting for #switchModeButton to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		logger.info("Clicking on Button to get automated Matching");
		this.clickOn("#switchModeButton");
		logger.info("Waiting for #automatedPropList to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#automatedPropList", timeout);
		logger.info("Clicking on first automatedPropList element");
		this.clickOn(CustomGuiTest.getFirstRowOfTableView("#automatedPropList"));
		logger.info("Clicking on FINISH");
		this.clickOn("Finish");
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
