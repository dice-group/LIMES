package org.aksw.limes.core.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
		this.mainView = new MainView(stage);
		this.mainController = new MainController(this.mainView);
		this.mainView.setController(this.mainController);
	}

	@BeforeClass
	public static void setup() {
		System.setProperty("testfx.robot", "glass");
		System.setProperty("testfx.headless", "true");
		System.setProperty("prism.order", "sw");
		System.setProperty("prism.text", "t2k");
		System.setProperty("java.awt.headless", "true");
	}

	@Before
	public void getMenus() {

		for (final Menu m : ((MenuBar) this.mainView.getScene().lookup("#menuBar")).getMenus()) {
			switch (m.getText()) {
			case "Configuration":
				this.menuConfiguration = m;
				break;
			case "Layout":
				this.menuLayout = m;
				break;
			case "Learn":
				this.menuLearn = m;
				break;
			default:
				assertTrue(false);
				break;
			}
		}

		for (final MenuItem m : this.menuConfiguration.getItems()) {
			// in case it is a seperator item
			if (m.getText() != null) {
				switch (m.getText()) {
				case "New":
					this.itemNew = m;
					break;
				case "Edit":
					this.itemEdit = m;
					break;
				case "Load Configuration":
					this.itemLoad = m;
					break;
				case "Save Configuration":
					this.itemSave = m;
					break;
				case "Exit":
					this.itemExit = m;
					break;
				default:
					assertTrue(false);
					break;
				}
			}
		}

		for (final MenuItem m : this.menuLayout.getItems()) {
			// in case it is a seperator item
			if (m.getText() != null) {
				switch (m.getText()) {
				case "Refresh Layout":
					this.itemLayout = m;
					break;
				case "Delete Graph":
					this.itemDelete = m;
					break;
				default:
					assertTrue(false);
					break;
				}
			}
		}

		for (final MenuItem m : this.menuLearn.getItems()) {
			// in case it is a seperator item
			if (m.getText() != null) {
				switch (m.getText()) {
				case "Active Learning":
					this.itemActiveLearning = m;
					break;
				case "Batch Learning":
					this.itemBatchLearning = m;
					break;
				case "Unsupervised Learning":
					this.itemUnsupervisedLearning = m;
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
		assertNotNull(this.mainView.getGraphBuild());
		assertNotNull(this.mainView.toolBox);

		verifyThat("#toolBarRunButton", NodeMatchers.isDisabled());
		verifyThat("#toolBarNewConfigButton", NodeMatchers.isEnabled());
		verifyThat("#toolBarLoadConfigButton", NodeMatchers.isEnabled());
		verifyThat("#toolBarSaveConfigButton", NodeMatchers.isDisabled());

		assertFalse(this.itemNew.isDisable());
		assertFalse(this.itemLoad.isDisable());
		assertFalse(this.itemExit.isDisable());
		assertFalse(this.itemLayout.isDisable());
		assertFalse(this.itemDelete.isDisable());

		assertTrue(this.itemEdit.isDisable());
		assertTrue(this.itemSave.isDisable());
		assertTrue(this.itemActiveLearning.isDisable());
		assertTrue(this.itemBatchLearning.isDisable());
		assertTrue(this.itemUnsupervisedLearning.isDisable());
	}

	@AfterClass
	public static void cleanup() {
		final FxRobot rob = new FxRobot();
		for (final Window w : rob.listWindows()) {
			final int currentsize = rob.listWindows().size();
			System.out.println(((Stage) w).getTitle());
			// Avoid not on fx application thread error
			Platform.runLater(() -> ((Stage) w).close());
			CustomGuiTest.waitUntilWindowIsClosed(currentsize - 1, 200);
		}
		assertEquals(0, rob.listWindows().size());
	}
}
