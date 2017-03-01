package org.aksw.limes.core.gui;

import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

import java.util.Locale;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.util.CustomGuiTest;
import org.aksw.limes.core.gui.view.MainView;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ConfigurationWizardTest extends ApplicationTest{

	MainView mainView;
	MainController mainController;
	private static final int timeout = 15;

	@Override
	public void start(Stage stage) throws Exception {
		Locale.setDefault(new Locale("en", "US"));
		mainView = new MainView(stage);
		mainController = new MainController(mainView);
		mainView.setController(mainController);
	}
	
	@Before
	public void openWizard(){
		clickOn("#menuConfiguration");
		clickOn("New");
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
	public void walkThrough(){
		testEditEndpoint();
		testEditClassMatching();
		testEditPropertyMatching();
		FxRobot robo = new FxRobot();
		//Verify that properties were loaded
		ListView<String> tbsp = robo.lookup("#toolBoxSourceProperties").query();
		ListView<String> tbtp = robo.lookup("#toolBoxTargetProperties").query();
		assertTrue(tbsp.getItems().size() > 0);
		assertTrue(tbtp.getItems().size() > 0);
	}
	
	
	public void testEditEndpoint(){
		clickOn("#SOURCEendpointURLTextField").write("http://dbpedia.org/sparql");
		clickOn("#SOURCEidNamespaceTextField").write("dbpedia");
		clickOn("#TARGETendpointURLTextField").write("http://linkedgeodata.org/sparql");
		clickOn("#TARGETidNamespaceTextField").write("linkedgeodata");
		clickOn("Next");
	}
	
	public void testEditClassMatching(){
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		//Test if manual matching gets loaded
		clickOn("#switchModeButton");
		CustomGuiTest.waitUntilNodeIsVisible("#sourcePanel", timeout);
		verifyThat("#sourcePanel", NodeMatchers.hasText("dbpedia classes"));
		verifyThat("#targetPanel", NodeMatchers.hasText("linkedgeodata classes"));
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		//Continue with automated matching
		clickOn("#switchModeButton");
		CustomGuiTest.waitUntilNodeIsVisible("#tableView", timeout);
		clickOn("#tableView");
		clickOn("Next");
	}
	
	public void testEditPropertyMatching(){
		CustomGuiTest.waitUntilNodeIsVisible("#sourcePropColumn", timeout);
		verifyThat("#sourcePropColumn", NodeMatchers.isVisible());
		verifyThat("#targetPropColumn", NodeMatchers.isVisible());
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		clickOn("#switchModeButton");
		CustomGuiTest.waitUntilNodeIsVisible("#sourcePropList", timeout);
		verifyThat("#sourcePropList", NodeMatchers.isVisible());
		verifyThat("#targetPropList", NodeMatchers.isVisible());
		verifyThat("#addedSourcePropsList", NodeMatchers.isVisible());
		verifyThat("#addedTargetPropsList", NodeMatchers.isVisible());
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", timeout);
		clickOn("#switchModeButton");
		clickOn("rdfs:label");
		clickOn("Finish");
	}

}
