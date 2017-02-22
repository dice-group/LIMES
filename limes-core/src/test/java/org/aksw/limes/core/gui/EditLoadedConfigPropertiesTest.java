package org.aksw.limes.core.gui;

import java.io.File;
import java.util.Locale;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.view.MainView;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.control.ListView;
import javafx.stage.Stage;

import static org.junit.Assert.assertNotNull;

public class EditLoadedConfigPropertiesTest extends ApplicationTest{

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
	public void testEditProperties(){
		clickOn("Configuration");
		clickOn("Edit");
		//Necessary because otherwise the sub-menu vanishes
		moveTo("Edit Classes");
		clickOn("Edit Properties");
		int timeoutCounter = 15;
		ListView<String> tv = new FxRobot().lookup("#sourcePropList").query();
		while(tv == null && timeoutCounter != 0)
		{
		  try {
			  tv = new FxRobot().lookup("#sourcePropList").query();
			  timeoutCounter --;
			  Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		clickOn("#sourcePropList");
		clickOn("#targetPropList");
		clickOn("Finish");
	}
	
}
