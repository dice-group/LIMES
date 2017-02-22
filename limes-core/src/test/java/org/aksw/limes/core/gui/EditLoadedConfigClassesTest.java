package org.aksw.limes.core.gui;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.view.MainView;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import static org.loadui.testfx.controls.Commons.hasText;
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
		//Ensure loading is finished
		//FIXME not the best method to do this
		sleep(1, TimeUnit.SECONDS);
		clickOn("#sourceTreeView");
		clickOn("#targetTreeView");
		clickOn("Next");
	}
}
