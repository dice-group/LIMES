package org.aksw.limes.core.gui;

import java.io.File;
import java.util.Locale;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.util.CustomGuiTest;
import org.aksw.limes.core.gui.view.MainView;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import javafx.stage.Stage;
import static org.testfx.api.FxAssert.verifyThat;

public class EditLoadedConfigClassesTest extends ApplicationTest{

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
	public void loadConfig(){
		mainController.loadConfig(new File(Thread.currentThread().getContextClassLoader().getResource("gui/testConfig.xml").getFile()));
	}
	
	@BeforeClass
	public static void setup(){
//        System.setProperty("testfx.robot", "glass");
//        System.setProperty("testfx.headless", "true");
//        System.setProperty("prism.order", "sw");
//        System.setProperty("prism.text", "t2k");
//        System.setProperty("java.awt.headless", "true");
	}
	
	
	@Test
	public void testEditClassMatching(){
		clickOn("Configuration");
		clickOn("Edit");
		clickOn("Edit Classes");

		CustomGuiTest.waitUntilNodeIsVisible("Thing", 15);

		verifyThat("Thing", NodeMatchers.isVisible());
	}
}
