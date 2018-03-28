package org.aksw.limes.core.gui.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CustomGuiTest {

	private static final Logger logger = LoggerFactory.getLogger(CustomGuiTest.class);

	public static boolean waitUntilNodeIsNotNull(String nodeId, int timeout) {
		Node node = new FxRobot().lookup(nodeId).query();
		while (node == null && timeout != 0) {
			try {
				node = new FxRobot().lookup(nodeId).query();
				timeout--;
				logger.info("Timeoutremaining for "+ nodeId + " : " + timeout);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("Interrupted while waiting for Node " + nodeId + " to be not null!");
				e.printStackTrace();
			}
		}
		if (node != null) {
			logger.info(nodeId + " not null");
			return true;
		}
		logger.error("Maximum timeout reached, while waiting for Node " + nodeId + " to be not null!");
		return false;
	}

	/**
	 * Waits until windows is closed or timeout in seconds is reached
	 * @param name of window
	 * @param timeout
	 */
	public static void waitUntilLoadingWindowIsClosed(String windowname, int timeout){
		FxRobot rob = new FxRobot();
		int sec = 0;
		logger.info("Wait until " + windowname + " is closed");
		logger.info("Currently open windows: ");
		for(Window w: rob.listWindows()){
			logger.info(((Stage)w).getTitle());
		}
		do{
			boolean closed = true;
            for(Window w: rob.listWindows()){
            	if(((Stage)w).getTitle().trim().equals(windowname.trim())){
            		closed = false;
            	}
            }
            if(closed){
            	break;
            }
			rob.sleep(1000);
			sec++;
			if(sec % 100 == 0){
				logger.info("Waited: " + sec + " seconds");
			}
			//avoid infinite loop
			if(sec > timeout){
				break;
			}
		}while(true);
	}

	/**
	 * Waits until number of windows is reduced to n or timeout in seconds is reached
	 * @param n number of desired windows
	 * @param timeout
	 */
	public static void waitUntilWindowIsClosed(int n, int timeout){
		FxRobot rob = new FxRobot();
		int sec = 0;
		logger.info("Wait until " + n + " windows are left open");
		logger.info("Currently open windows: ");
		for(Window w: rob.listWindows()){
			logger.info(((Stage)w).getTitle());
		}
		do{
			rob.sleep(1000);
			sec++;
			if(sec % 100 == 0){
				logger.info("Waited: " + sec + " seconds");
			}
			//avoid infinite loop
			if(sec > timeout){
				break;
			}
		}while(rob.listWindows().size() > n);
	}

	/**
	 * Uses FxRobot from TestFX to lookup the node
	 * 
	 * @param nodeId
	 *            ID of the node
	 * @param timeout
	 *            in seconds
	 */
	public static void waitUntilNodeIsVisible(String nodeId, int timeout) {
		Node node = new FxRobot().lookup(nodeId).query();
		boolean found;
		if (node == null) {
			found = waitUntilNodeIsNotNull(nodeId, timeout);
		}else{
			found = true;
		}
		if (!found) {
			logger.error("Maximum timeout reached, while waiting for Node " + nodeId + " to be visible!");
			return;
		}
		node = new FxRobot().lookup(nodeId).query();
		while (!node.isVisible() && timeout != 0) {
			try {
				timeout--;
				if(timeout % 10 == 0){
                    logger.info("Timeoutremaining for "+ nodeId + " : " + timeout);
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("Interrupted while waiting for Node " + nodeId + " to be visible!");
				e.printStackTrace();
			}
		}
		if(node.isVisible()){
			logger.info(nodeId +  " is visible"); 
		}else{
			logger.info(nodeId +  " is NOT visible"); 
		}

	}

	public static TableRow<?> getFirstRowOfTableView(String tableSelector) {
		TableView<?> tableView = new FxRobot().lookup(tableSelector).query();

		List<Node> current = tableView.getChildrenUnmodifiable();
		while (current.size() == 1) {
			current = ((Parent) current.get(0)).getChildrenUnmodifiable();
		}

		current = ((Parent) current.get(1)).getChildrenUnmodifiable();
		while (!(current.get(0) instanceof TableRow)) {
			current = ((Parent) current.get(0)).getChildrenUnmodifiable();
		}

		Node node = current.get(0);
		if (node instanceof TableRow) {
			return (TableRow<?>) node;
		} else {
			throw new RuntimeException("Expected Group with only TableRows as children");
		}
	}

}
