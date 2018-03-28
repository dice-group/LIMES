package org.aksw.limes.core.gui;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.metric.MetricParser;
import org.aksw.limes.core.gui.model.metric.Output;
import org.aksw.limes.core.gui.util.CustomGuiTest;
import org.aksw.limes.core.gui.view.MainView;
import org.aksw.limes.core.io.config.KBInfo;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.Window;

public class OptionalPropertiesTest extends ApplicationTest{

	private static final Logger logger = LoggerFactory.getLogger(OptionalPropertiesTest.class);
	MainView mainView;
	MainController mainController;
	Config c;
	KBInfo sourceInfo;
	KBInfo targetInfo;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
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
	public void loadConfig() throws IOException {
		File sourceFile = writeTemporarySourceEndpoints();
		File targetFile = writeTemporaryTargetEndpoints();
		Map<String,String> prefixes = new HashMap<>();
		prefixes.put("owl","http://www.w3.org/2002/07/owl#");
		prefixes.put("test","http://www.test.org/ont#");
		prefixes.put("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        Map<String, Map<String,String>> functions = new HashMap<>();
        HashMap<String, String> f = new HashMap<>();
        f.put("test:sp1", null);
        functions.put("test:sp1", f);
        HashMap<String, String> f1 = new HashMap<>();
        f1.put("test:tp1", null);
        functions.put("test:tp1", f1);
        HashMap<String, String> f2 = new HashMap<>();
        f2.put("test:tp2", null);
        functions.put("test:tp2", f2);
        HashMap<String, String> f3 = new HashMap<>();
        f3.put("test:sp2", null);
        functions.put("test:sp2", f3);
		List<String> sourceproperties = new ArrayList<>();
		sourceproperties.add("test:sp2");
		List<String> targetproperties = new ArrayList<>();
		targetproperties.add("test:tp2");
		List<String> sourceoptionalProperties = new ArrayList<>();
		sourceoptionalProperties.add("test:sp1");
		List<String> targetoptionalProperties = new ArrayList<>();
		targetoptionalProperties.add("test:tp1");
		ArrayList<String> classString = new ArrayList<>();
		classString.add("?x rdf:type test:Person");
		sourceInfo = new KBInfo("source", sourceFile.getAbsolutePath(), "", "?x", sourceproperties, sourceoptionalProperties, classString, functions, prefixes, -1, "NT");
		targetInfo = new KBInfo("target", targetFile.getAbsolutePath(), "", "?x", targetproperties, targetoptionalProperties, classString, functions, prefixes, -1, "NT");
        c = new Config(sourceInfo, targetInfo, "cosine(x.test:sp2,y.test:tp2)", "owl:sameAs", "owl:sameAs", 0.9, "",0.7, "", prefixes, "TAB", "default", "default", "default", 2, "",null, null, "", null);
        Output out = MetricParser.parse(c.getMetricExpression(), c .getSourceInfo().getVar().replaceAll("\\?", ""),c);
        out.param1 = 0.9;
        out.param2 = 0.7;
	    c.setMetric(out);
        mainController.setCurrentConfig(c);
        mainView.showLoadedConfig(true);
	}
	
	private File writeTemporarySourceEndpoints() throws IOException{
		String sourceString = 
        "<http://www.test.org/s1> <http://www.test.org/ont#sp1> \"property1\" ."
    +	"<http://www.test.org/s1> <http://www.test.org/ont#sp2> \"property2\" ."
    +	"<http://www.test.org/s1> <http://www.test.org/ont#sp3> \"property3\" ."
    +	"<http://www.test.org/s1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.test.org/ont#Person> ."
    +	"<http://www.test.org/s2> <http://www.test.org/ont#sp2> \"property22\" ."
    +	"<http://www.test.org/s2> <http://www.test.org/ont#sp3> \"property33\" ." 
    +	"<http://www.test.org/s2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.test.org/ont#Person> ." ;
		final File sourceFile = tempFolder.newFile("source.nt");
		FileUtils.writeStringToFile(sourceFile, sourceString);
		return sourceFile;
	}

	private File writeTemporaryTargetEndpoints() throws IOException{
		String targetString = 
        "<http://www.test.org/t1> <http://www.test.org/ont#tp1> \"property1\" ."
    +	"<http://www.test.org/t1> <http://www.test.org/ont#tp2> \"property2\" ."
    +	"<http://www.test.org/t1> <http://www.test.org/ont#tp3> \"property3\" ."
    +	"<http://www.test.org/t1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.test.org/ont#Person> ."
    +	"<http://www.test.org/t2> <http://www.test.org/ont#tp1> \"property12\" ."
    +	"<http://www.test.org/t2> <http://www.test.org/ont#tp2> \"property22\" ."
    +	"<http://www.test.org/t2> <http://www.test.org/ont#tp3> \"property32\" ." 
    +	"<http://www.test.org/t2> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.test.org/ont#Person> ." ;
		final File targetFile = tempFolder.newFile("target.nt");
		FileUtils.writeStringToFile(targetFile, targetString);
		return targetFile;
	}
	
	@Test
	public void testSwitchingOptionalProperties(){
		logger.info("INITIAL CACHES: " + 
                    mainController.getCurrentConfig().getSourceEndpoint().getCache() + "\n" +
                    mainController.getCurrentConfig().getTargetEndpoint().getCache());
		clickOn("#toolBarRunButton");
		logger.info("Clicked on run");
		CustomGuiTest.waitUntilLoadingWindowIsClosed("Mapping",200);
		assertEquals(2,c.getMapping().size());
		assertEquals(2,mainController.getCurrentConfig().getSourceEndpoint().getCache().size());
		assertEquals(2,mainController.getCurrentConfig().getTargetEndpoint().getCache().size());
		logger.info("AFTER RUN CACHES: " + 
                    mainController.getCurrentConfig().getSourceEndpoint().getCache() + "\n" +
                    mainController.getCurrentConfig().getTargetEndpoint().getCache());

		clickOn("test:sp1",MouseButton.SECONDARY);
		logger.info("Set test:sp1 to be obligatory");
		
		logger.info("AFTER SETTING SP1 CACHES: " + 
                    mainController.getCurrentConfig().getSourceEndpoint().getCache() + "\n" +
                    mainController.getCurrentConfig().getTargetEndpoint().getCache());
		clickOn("#toolBarRunButton");
		logger.info("Clicked on run again");
		CustomGuiTest.waitUntilLoadingWindowIsClosed("Mapping",200);
		logger.info("AFTER SECOND RUN CACHES: " + 
                    mainController.getCurrentConfig().getSourceEndpoint().getCache() + "\n" +
                    mainController.getCurrentConfig().getTargetEndpoint().getCache());
		assertEquals(1,c.getMapping().size());
		assertEquals(1,mainController.getCurrentConfig().getSourceEndpoint().getCache().size());
		assertEquals(2,mainController.getCurrentConfig().getTargetEndpoint().getCache().size());
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
