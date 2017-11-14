package org.aksw.limes.core.gui;

import java.io.File;
import java.util.Locale;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.util.CustomGuiTest;
import org.aksw.limes.core.gui.view.MainView;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.framework.junit.ApplicationTest;

import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class EditLoadedConfigPropertiesTest extends ApplicationTest {

	private static final Logger logger = LoggerFactory.getLogger(EditLoadedConfigPropertiesTest.class);
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
	public void loadConfig() {
		mainController.loadConfig(
				new File(Thread.currentThread().getContextClassLoader().getResource("gui/testConfig.xml").getFile()));
	}

	@BeforeClass
	public static void setup() {
		System.setProperty("testfx.robot", "glass");
		System.setProperty("testfx.headless", "true");
		System.setProperty("prism.order", "sw");
		System.setProperty("prism.text", "t2k");
		System.setProperty("java.awt.headless", "true");
	}

	@Test
	public void testEditProperties() throws InterruptedException {
		logger.info("Clicking on Configuration");
		clickOn("Configuration");
		logger.info("Clicking on Edit");
		clickOn("Edit");
		// Necessary because otherwise the sub-menu vanishes
		logger.info("Moving to Edit Classes");
		moveTo("Edit Classes");
		logger.info("Clicking on Edit Properties");
		clickOn("Edit Properties");

		logger.info("Waiting for #switchModeButton");
		for (Window w : listWindows()) {
			System.out.println("window: " + ((Stage)w).getTitle());
			for (Node n : w.getScene().getRoot().getChildrenUnmodifiable()) {
				if (n instanceof ScrollPane) {
					System.out.println("line 71 " + n +  " -> " + n.getId());
					for (Node cN : ((ScrollPane) n).getChildrenUnmodifiable()) {
						if (cN instanceof StackPane) {
							System.out.println("line 74 " + cN +  " -> " + cN.getId());
							for (Node cN2 : ((StackPane) cN).getChildrenUnmodifiable()) {
								if (cN2 instanceof StackPane) {
									System.out.println("line 77 " + cN2 +  " -> " + cN2.getId());
									for (Node cN3 : ((StackPane) cN2).getChildrenUnmodifiable()) {
										if(cN3 instanceof BorderPane){
											System.out.println("line 80 " + cN3 +  " -> " + cN3.getId());
                                            for(Node cN4: ((BorderPane) cN3).getChildrenUnmodifiable()){
                                            	if(cN4 instanceof VBox){
                                                    System.out.println("line 83 " + cN4 +  " -> " + cN4.getId());
                                            		for(Node cN5: ((VBox)cN4).getChildrenUnmodifiable()){
                                            			System.out.println(cN5 + " -> " + cN5.getId());
                                            		}
                                            	}
                                            	if(cN4 instanceof HBox){
                                                    System.out.println("line 89 " + cN4 +  " -> " + cN4.getId());
                                            		for(Node cN5: ((HBox)cN4).getChildrenUnmodifiable()){
                                            			System.out.println(cN5 + " -> " + cN5.getId());
                                            		}
                                            	}
                                            }
										}
									}
								}
							}
						}
					}
				}
			}
		}
		CustomGuiTest.waitUntilNodeIsVisible("#switchModeButton", 180);
		clickOn("#switchModeButton");
		logger.info("Waiting for dbo:abbreviation");
		CustomGuiTest.waitUntilNodeIsVisible("dbo:abbreviation", 180);
		clickOn("dbo:abbreviation");
		clickOn("dbo:birthDate");
		clickOn("Finish");

		clickOn("dbo:abbreviation");
		clickOn("dbo:birthDate");
	}

}
