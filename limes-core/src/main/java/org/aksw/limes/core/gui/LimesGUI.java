package org.aksw.limes.core.gui;

import java.util.Locale;

import org.aksw.limes.core.gui.controller.MainController;
import org.aksw.limes.core.gui.view.MainView;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Starts the LinkDiscovery Application, with Extra
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class LimesGUI extends Application {
	
	MainView mainView;
    /**
     * Main function Entry Point for the Application
     *
     * @param args
     *         optional arguments on StartUp, No Options implemented yet
     */
    public static void startGUI(String[] args) {
        launch(args);
    }
    
    /**
     * Opens a new Window for the Application
     * @param primaryStage View to initialize Application
     * @exception Exception Thrown if initialization didn't work properly
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Locale.setDefault(new Locale("en", "US"));
        mainView = new MainView(primaryStage);
        MainController mainController = new MainController(mainView);
        mainView.setController(mainController);
    }
}
