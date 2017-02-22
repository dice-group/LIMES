package org.aksw.limes.core.gui;

import java.io.File;
import java.util.Locale;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.model.ClassMatchingNode;
import org.aksw.limes.core.gui.view.MainView;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import static org.junit.Assert.assertNotNull;

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

        //Verbose options
//        System.setProperty("prism.verbose", "true");
//        System.setProperty("quantum.verbose", "true");
//        System.setProperty("javafx.verbose", "true");
	}
	
	
	@Test
	public void testEditClassMatching(){
		clickOn("Configuration");
		clickOn("Edit");
		clickOn("Edit Classes");
		GuiTest.waitUntil("#sourceTreeView", Matchers.notNullValue());
		TreeView<ClassMatchingNode> tv = new FxRobot().lookup("#sourceTreeView").query();
		assertNotNull(tv);
		GuiTest.waitUntil(tv, t -> t.isVisible());
		clickOn("#sourceTreeView");
		clickOn("#targetTreeView");
		clickOn("Next");
	}
}
