package org.aksw.limes.core.gui;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Locale;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.util.CustomGuiTest;
import org.aksw.limes.core.gui.view.MainView;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SaveAndLoadConfigTest extends ApplicationTest{
	
	
	MainView mainView;
	MainController mainController;
	private static final String metricExpression = "or(jaccard(x.rdfs:label,y.rdfs:label)|0.8,cosine(x.rdfs:label,y.rdfs:label)|0.9)";
	
	private static final File changedTestConfig = new File("src/test/resources/gui/changedTestConfig.ttl");
	
	@Override
	public void start(Stage stage) throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		mainView = new MainView(stage);
		mainController = new MainController(mainView);
		mainView.setController(mainController);
	}

	@Before
	public void loadInitialConfig(){
		mainController.loadConfig(new File(Thread.currentThread().getContextClassLoader().getResource("gui/testConfig.xml").getFile()));
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
	public void changeSaveAndLoadConfig(){
		changeAndSaveConfig();
		loadNewConfig();
	}
	
	
	public void changeAndSaveConfig(){
		mainController.getCurrentConfig().setMetricExpression(metricExpression);
		mainView.getGraphBuild().graphBuildController.generateGraphFromConfig();
		CustomGuiTest.waitUntilNodeIsVisible("Agent properties", 180);
//		GuiTest.waitUntil("Drug properties", Matchers.notNullValue()); 

		mainController.saveConfig(changedTestConfig);
	}
	
	public void loadNewConfig(){
		mainController.loadConfig(changedTestConfig);
		assertEquals(metricExpression, mainController.getCurrentConfig().getMetricExpression());
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
