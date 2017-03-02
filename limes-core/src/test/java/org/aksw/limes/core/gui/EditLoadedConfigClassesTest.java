package org.aksw.limes.core.gui;

import static org.testfx.api.FxAssert.verifyThat;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.Endpoint;
import org.aksw.limes.core.gui.util.CustomGuiTest;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.io.config.KBInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import javafx.stage.Stage;

public class EditLoadedConfigClassesTest extends ApplicationTest {

	private static final Logger logger = LoggerFactory.getLogger(EditLoadedConfigClassesTest.class);
	MainView mainView;
	MainController mainController;

	@Override
	public void start(Stage stage) throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		mainView = new MainView(stage);
		mainController = new MainController(mainView);
		mainView.setController(mainController);
	}

	/**
	 * Since loading classes might take long we load the testConfig but change the endpoints to local ones
	 * We cannot do this in the config because relative paths for endpoints are not implemented
	 */
	@Before
	public void loadConfig() {
		mainController.loadConfig(
				new File(Thread.currentThread().getContextClassLoader().getResource("gui/testConfig.xml").getFile()));
		Config c = mainController.getCurrentConfig();
		KBInfo sinfo = new KBInfo();
		KBInfo tinfo = new KBInfo();
		String restaurantsEndpoint = Thread.currentThread().getContextClassLoader()
				.getResource("datasets/Restaurants/restaurant1.nt").toString();
		String personsEndpoint = Thread.currentThread().getContextClassLoader()
				.getResource("datasets/Persons2/person21.nt").toString();
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

	/**
	 * This checks if editing of classes works on a previously loaded config
	 */
	@Test
	public void testEditClassMatching() {
		logger.debug("Clicking on Configuration");
		clickOn("Configuration");
		logger.debug("Clicking on Edit");
		clickOn("Edit");
		logger.debug("Clicking on Classes");
		clickOn("Edit Classes");

		logger.debug("Waiting for classes to be visible");
		CustomGuiTest.waitUntilNodeIsVisible("Restaurant", 15);
		CustomGuiTest.waitUntilNodeIsVisible("Person", 15);
		verifyThat("Restaurant", NodeMatchers.isVisible());
		verifyThat("Person", NodeMatchers.isVisible());
	}
}
