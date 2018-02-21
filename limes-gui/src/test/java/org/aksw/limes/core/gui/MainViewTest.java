package org.aksw.limes.core.gui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

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
import org.testfx.matcher.base.NodeMatchers;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainViewTest extends ApplicationTest {

	MainView mainView;
	MainController mainController;

	Menu menuConfiguration = null;
	Menu menuLayout = null;
	Menu menuLearn = null;

	MenuItem itemNew = null;
	MenuItem itemEdit = null;
	MenuItem itemLoad = null;
	MenuItem itemSave = null;
	MenuItem itemExit = null;

	MenuItem itemLayout = null;
	MenuItem itemDelete = null;
	
	MenuItem itemBatchLearning = null;
	MenuItem itemUnsupervisedLearning = null;
	MenuItem itemActiveLearning = null;
	
	@Override
	public void start(Stage stage) {
		Locale.setDefault(new Locale("en", "US"));
		mainView = new MainView(stage);
		mainController = new MainController(mainView);
		mainView.setController(mainController);
	}
	
	
	@BeforeClass
	public static void setup(){
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
	}
	
	@Before
	public void getMenus(){

		for (Menu m : ((MenuBar) mainView.getScene().lookup("#menuBar")).getMenus()) {
			switch (m.getText()) {
			case "Configuration":
				menuConfiguration = m;
				break;
			case "Layout":
				menuLayout = m;
				break;
			case "Learn":
				menuLearn = m;
				break;
			default:
				assertTrue(false);
				break;
			}
		}

		for (MenuItem m : menuConfiguration.getItems()) {
			// in case it is a seperator item
			if (m.getText() != null) {
				switch (m.getText()) {
				case "New":
					itemNew = m;
					break;
				case "Edit":
					itemEdit = m;
					break;
				case "Load Configuration":
					itemLoad = m;
					break;
				case "Save Configuration":
					itemSave = m;
					break;
				case "Exit":
					itemExit = m;
					break;
				default:
					assertTrue(false);
					break;
				}
			}
		}

		for (MenuItem m : menuLayout.getItems()) {
			// in case it is a seperator item
			if (m.getText() != null) {
				switch (m.getText()) {
				case "Refresh Layout":
					itemLayout = m;
					break;
				case "Delete Graph":
					itemDelete = m;
					break;
				default:
					assertTrue(false);
					break;
				}
			}
		}

		for (MenuItem m : menuLearn.getItems()) {
			// in case it is a seperator item
			if (m.getText() != null) {
				switch (m.getText()) {
				case "Active Learning":
					itemActiveLearning = m;
					break;
				case "Batch Learning":
					itemBatchLearning = m;
					break;
				case "Unsupervised Learning":
					itemUnsupervisedLearning = m;
					break;
				default:
					assertTrue(false);
					break;
				}
			}
		}
	}

	@Test
	public void testVisibility() {
		assertNotNull(mainView.getGraphBuild());
		assertNotNull(mainView.toolBox);

		verifyThat("#toolBarRunButton", NodeMatchers.isDisabled());
		verifyThat("#toolBarNewConfigButton", NodeMatchers.isEnabled());
		verifyThat("#toolBarLoadConfigButton", NodeMatchers.isEnabled());
		verifyThat("#toolBarSaveConfigButton", NodeMatchers.isDisabled());

		assertFalse(itemNew.isDisable());
		assertFalse(itemLoad.isDisable());
		assertFalse(itemExit.isDisable());
		assertFalse(itemLayout.isDisable());
		assertFalse(itemDelete.isDisable());

		assertTrue(itemEdit.isDisable());
		assertTrue(itemSave.isDisable());
		assertTrue(itemActiveLearning.isDisable());
		assertTrue(itemBatchLearning.isDisable());
		assertTrue(itemUnsupervisedLearning.isDisable());
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
