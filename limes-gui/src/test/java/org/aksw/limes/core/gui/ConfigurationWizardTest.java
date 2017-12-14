package org.aksw.limes.core.gui;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
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

public class ConfigurationWizardTest extends ApplicationTest{

	MainView mainView;
	MainController mainController;
	private static final int timeout = 180;
	private static final Logger logger = LoggerFactory.getLogger(ConfigurationWizardTest.class);
	private String resourcesPath;
	

	@Override
	public void start(Stage stage) throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		mainView = new MainView(stage);
		mainController = new MainController(mainView);
		mainView.setController(mainController);
		resourcesPath = ProjectPropertiesGetter.getProperty(Thread.currentThread().getContextClassLoader().getResource("project.properties").getPath(), "limes-core.resources");
	}
	
	@Before
	public void openWizard(){
		clickOn("#menuConfiguration");
		clickOn("New");
	}
	
	@BeforeClass
	public static void setup(){
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
	}
	
	@Test
	public void walkThrough(){
		testEditEndpoint();
		testEditClassMatching();
		testEditPropertyMatching();
		FxRobot robo = new FxRobot();
		//Verify that properties were loaded
		ListView<String> tbsp = robo.lookup("#toolBoxSourceProperties").query();
		ListView<String> tbtp = robo.lookup("#toolBoxTargetProperties").query();
		logger.info("Verifying that properties got loaded");
		assertTrue(tbsp.getItems().size() > 0);
		assertTrue(tbtp.getItems().size() > 0);
	}
	
	
	public void testEditEndpoint(){
		logger.info("testEditEndpoint started");
		for(Window w: listWindows()){
			logger.info("Window open: " + ((Stage)w).getTitle());
		}
		clickOn("#SOURCEendpointURLTextField").write(resourcesPath + "datasets/Restaurants/restaurant1.nt");
		clickOn("#SOURCEidNamespaceTextField").write("Restaurant");
		clickOn("#TARGETendpointURLTextField").write(resourcesPath + "datasets/Persons2/person21.nt");
		clickOn("#TARGETidNamespaceTextField").write("Person");
		logger.info("Edited Endpoint clicking NEXT");
		clickOn("Next");
	}
	
	public void testEditClassMatching(){
		logger.info("testEditClassMatching started");
		CustomGuiTest.waitUntilLoadingWindowIsClosed("Get classes",500);
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		//Test if manual matching gets loaded
		logger.info("Clicking on Button to get Manual Matching");
		clickOn("#switchModeButton");
		logger.info("Waiting for #sourcePabel to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#sourcePanel", timeout);
		verifyThat("#sourcePanel", NodeMatchers.isVisible());
		verifyThat("#targetPanel", NodeMatchers.isVisible());
		logger.info("Waiting for #switchModeButton to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		logger.info("Clicking on Button to get automated Matching");
		//Continue with automated matching
		clickOn("#switchModeButton");
		logger.info("Waiting for #tableView to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#tableView", timeout);
		logger.info("Clicking on tableView");
		clickOn("#tableView");
		logger.info("Clicking on NEXT");
		clickOn("Next");
	}
	
	public void testEditPropertyMatching(){
		CustomGuiTest.waitUntilLoadingWindowIsClosed("Getting properties",500);
		logger.info("testEditPropertyMatching started");
		logger.info("Waiting for #sourcePropColumn to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#sourcePropColumn", timeout);
		verifyThat("#sourcePropColumn", NodeMatchers.isVisible());
		verifyThat("#targetPropColumn", NodeMatchers.isVisible());
		logger.info("Waiting for #switchModeButton to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		logger.info("Clicking on Button to get Manual Matching");
		clickOn("#switchModeButton");
		logger.info("Waiting for #sourcePropList to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#sourcePropList", timeout);
		verifyThat("#sourcePropList", NodeMatchers.isVisible());
		verifyThat("#targetPropList", NodeMatchers.isVisible());
		verifyThat("#addedSourcePropsList", NodeMatchers.isVisible());
		verifyThat("#addedTargetPropsList", NodeMatchers.isVisible());
		logger.info("Waiting for #switchModeButton to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		logger.info("Clicking on Button to get automated Matching");
		clickOn("#switchModeButton");
		logger.info("Waiting for #automatedPropList to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("#automatedPropList", timeout);
		logger.info("Clicking on first automatedPropList element");
		clickOn(CustomGuiTest.getFirstRowOfTableView("#automatedPropList"));
		logger.info("Clicking on FINISH");
		clickOn("Finish");
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
