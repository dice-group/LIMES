package org.aksw.limes.core.gui;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.view.MainView;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;

public class SaveAndLoadConfigTest extends ApplicationTest{
	
	
	MainView mainView;
	MainController mainController;
	private static final String newMetricExpression = "or(jaccard(x.rdfs:label,y.name)|0.8,cosine(x.rdfs:label,y.name)|0.9)";
	/**
	 * There is a rename operation in the xml config that cannot be represented in ttl therefore this has to have rdfs:label instead of name
	 */
	private static final String testMetricExpression = "or(jaccard(x.rdfs:label,y.rdfs:label)|0.8,cosine(x.rdfs:label,y.rdfs:label)|0.9)";
	
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
        
        //Verbose options
        System.setProperty("prism.verbose", "true");
        System.setProperty("quantum.verbose", "true");
        System.setProperty("javafx.verbose", "true");
	}
	
	@Test
	public void changeSaveAndLoadConfig(){
		changeAndSaveConfig();
		//FIXME maybe not the ideal way to wait until everything is finished
		sleep(1, TimeUnit.SECONDS);
		loadNewConfig();
	}
	
	
	public void changeAndSaveConfig(){
		mainController.getCurrentConfig().setMetricExpression(newMetricExpression);
		mainView.graphBuild.graphBuildController.generateGraphFromConfig();
		//FIXME maybe not the ideal way to wait until everything is finished
		sleep(1, TimeUnit.SECONDS);
		mainController.saveConfig(new File("src/test/resources/gui/changedTestConfig.ttl"));
	}
	
	public void loadNewConfig(){
		mainController.loadConfig(new File(Thread.currentThread().getContextClassLoader().getResource("gui/changedTestConfig.ttl").getFile()));
		assertEquals(testMetricExpression, mainController.getCurrentConfig().getMetricExpression());
	}
}
