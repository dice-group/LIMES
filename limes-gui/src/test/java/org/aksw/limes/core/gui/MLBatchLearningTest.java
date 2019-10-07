//package org.aksw.limes.core.gui;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.File;
//import java.util.Locale;
//
//import org.aksw.limes.core.gui.controller.MainController;
//import org.aksw.limes.core.gui.util.CustomGuiTest;
//import org.aksw.limes.core.gui.util.ProjectPropertiesGetter;
//import org.aksw.limes.core.gui.view.MainView;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.testfx.api.FxRobot;
//import org.testfx.framework.junit.ApplicationTest;
//
//import javafx.application.Platform;
//import javafx.stage.Stage;
//import javafx.stage.Window;
//
//public class MLBatchLearningTest extends ApplicationTest {
//
//	MainView mainView;
//	MainController mainController;
//	private static final int timeout = 180;
//	private String resourcesPath;
//
//	@Override
//	public void start(Stage stage) throws Exception {
//		Locale.setDefault(new Locale("en", "US"));
//		this.mainView = new MainView(stage);
//		this.mainController = new MainController(this.mainView);
//		this.mainView.setController(this.mainController);
//		this.resourcesPath = ProjectPropertiesGetter.getProperty(
//				Thread.currentThread().getContextClassLoader().getResource("project.properties").getPath(),
//				"limes-core.resources");
//	}
//
//	@Before
//	public void loadConfig() {
//		this.mainController.loadConfig(new File(this.resourcesPath + "datasets/Amazon-GoogleProducts.xml"));
//	}
//
//	@BeforeClass
//	public static void setup() {
//		System.setProperty("testfx.robot", "glass");
//		System.setProperty("testfx.headless", "true");
//		System.setProperty("prism.order", "sw");
//		System.setProperty("prism.text", "t2k");
//		System.setProperty("java.awt.headless", "true");
//	}
//
//	@Test
//	public void walkThrough() {
//		final FxRobot robo = new FxRobot();
//		this.clickOn("#menuLearn");
//		this.clickOn("Batch Learning");
//		this.clickOn("choose algorithm");
//		this.clickOn("Dragon");
//		Platform.runLater(() -> ((Stage) robo.window("LIMES - Property Matching")).setFullScreen(true));
//		this.moveTo("available Source Properties:");
//		this.clickOn("title");
//		this.clickOn("name");
//		this.clickOn("description");
//		this.clickOn("description");
//		this.clickOn("manufacturer");
//		this.clickOn("manufacturer");
//		this.clickOn("price");
//		this.clickOn("price");
//		CustomGuiTest.waitUntilNodeIsVisible("finish", timeout);
//		this.clickOn("finish");
//		this.clickOn("learn");
//		CustomGuiTest.waitUntilNodeIsVisible("#filePathField", timeout);
//		this.clickOn("#filePathField")
//				.write(this.resourcesPath + "datasets/Amazon-GoogleProducts/Amzon_GoogleProducts_perfectMapping.csv");
//		this.clickOn("Save");
//		CustomGuiTest.waitUntilLoadingWindowIsClosed("Learning", timeout * 10);
//	}
//
//	@AfterClass
//	public static void cleanup() {
//		final FxRobot rob = new FxRobot();
//		for (final Window w : rob.listWindows()) {
//			final int currentsize = rob.listWindows().size();
//			System.out.println(((Stage) w).getTitle());
//			// Avoid not on fx application thread error
//			Platform.runLater(() -> ((Stage) w).close());
//			CustomGuiTest.waitUntilWindowIsClosed(currentsize - 1, 200);
//		}
//		assertEquals(0, rob.listWindows().size());
//	}
//
//}
