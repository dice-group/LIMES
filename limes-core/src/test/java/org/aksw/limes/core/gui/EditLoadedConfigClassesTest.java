package org.aksw.limes.core.gui;

import java.io.File;
import java.util.Locale;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.view.MainView;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;

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
		mainController.loadConfig(new File(System.getProperty("user.dir") + "/resources/lgd-lgd.ttl"));
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
	public void testEditClassMatching(){
		clickOn("Configuration");
		clickOn("Edit");
		clickOn("Edit Classes");
		clickOn("#sourcePanel");
		clickOn("#targetPanel");
		clickOn("Next");
	}
}
