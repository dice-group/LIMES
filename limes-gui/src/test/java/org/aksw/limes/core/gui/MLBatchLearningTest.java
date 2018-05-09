package org.aksw.limes.core.gui;

import static org.junit.Assert.assertEquals;

import java.io.File;
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

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MLBatchLearningTest extends ApplicationTest{

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
	public void loadConfig() {
		mainController.loadConfig(new File(resourcesPath + "datasets/Amazon-GoogleProducts.xml"));
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
		FxRobot robo = new FxRobot();
		clickOn("#menuLearn");
		clickOn("Batch Learning");
		clickOn("choose algorithm");
		clickOn("Dragon");
        Platform.runLater(new Runnable() {
            @Override public void run() {
            	((Stage)robo.window("LIMES - Property Matching")).setFullScreen(true);
            }
        });
		moveTo("available Source Properties:");
		clickOn("title");
		clickOn("name");
		clickOn("description");
		clickOn("description");
		clickOn("manufacturer");
		clickOn("manufacturer");
		clickOn("price");
		clickOn("price");
		CustomGuiTest.waitUntilNodeIsVisible("finish", timeout);
		clickOn("finish");
		clickOn("learn");
		CustomGuiTest.waitUntilNodeIsVisible("#filePathField", timeout);
		clickOn("#filePathField").write(resourcesPath + "datasets/Amazon-GoogleProducts/Amzon_GoogleProducts_perfectMapping.csv");
		clickOn("Save");
		CustomGuiTest.waitUntilLoadingWindowIsClosed("Learning", timeout*10);
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
