package org.aksw.limes.core.gui;

import static org.testfx.api.FxAssert.verifyThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Locale;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.Endpoint;
import org.aksw.limes.core.gui.util.CustomGuiTest;
import org.aksw.limes.core.gui.util.ProjectPropertiesGetter;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.io.config.KBInfo;
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
import javafx.stage.Stage;
import javafx.stage.Window;

public class EditLoadedConfigClassesTest extends ApplicationTest {

	private static final Logger logger = LoggerFactory.getLogger(EditLoadedConfigClassesTest.class);
	MainView mainView;
	MainController mainController;
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
	public void loadConfig() {
		mainController.loadConfig(
				new File(Thread.currentThread().getContextClassLoader().getResource("gui/testConfig.xml").getFile()));
		Config c = mainController.getCurrentConfig();
		KBInfo sinfo = new KBInfo();
		KBInfo tinfo = new KBInfo();
		String restaurantsEndpoint = resourcesPath + "datasets/Restaurants/restaurant1.nt";
		String personsEndpoint = resourcesPath + "datasets/Persons2/person21.nt";
		sinfo.setEndpoint(restaurantsEndpoint);
		sinfo.setId("Restaurants");
		Endpoint sendpoint = new Endpoint(sinfo, c);
		sendpoint.update();
		c.setSourceEndpoint(sendpoint);

		tinfo.setEndpoint(personsEndpoint);
		tinfo.setId("Restaurants");
		Endpoint tendpoint = new Endpoint(tinfo, c);
		tendpoint.update();
		c.setTargetEndpoint(tendpoint);
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
	public void testEditClassMatching() {
		logger.info("Clicking on Configuration");
		clickOn("Configuration");
		logger.info("Clicking on Edit");
		clickOn("Edit");
		logger.info("Clicking on Classes");
		clickOn("Edit Classes");

		logger.info("Waiting for classes to be visible");
		CustomGuiTest.waitUntilLoadingWindowIsClosed("Get classes",500);
		CustomGuiTest.waitUntilNodeIsVisible("Restaurant", 150);
		CustomGuiTest.waitUntilNodeIsVisible("Person", 15);
		verifyThat("Restaurant", NodeMatchers.isVisible());
		verifyThat("Person", NodeMatchers.isVisible());
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
