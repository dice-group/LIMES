package org.aksw.limes.core.gui.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testfx.api.FxRobot;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class CustomGuiTest {

	private static final Logger logger = LoggerFactory.getLogger(CustomGuiTest.class);

	public static boolean waitUntilNodeIsNotNull(String nodeId, int timeout) {
		Node node = new FxRobot().lookup(nodeId).query();
		while (node == null && timeout != 0) {
			try {
				node = new FxRobot().lookup(nodeId).query();
				timeout--;
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("Interrupted while waiting for Node to be not null!");
				e.printStackTrace();
			} catch (NullPointerException e) {
				logger.error("Maximum timeout reached, while waiting for Node to be not null!");
				e.printStackTrace();
			}
		}
		if (node != null) {
			return true;
		}
		logger.error("Maximum timeout reached, while waiting for Node to be not null!");
		return false;
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
		boolean found = false;
		if (node == null) {
			found = waitUntilNodeIsNotNull(nodeId, timeout);
		}
		if (!found) {
			logger.error("Maximum timeout reached, while waiting for Node to be visible!");
			return;
		}
		node = new FxRobot().lookup(nodeId).query();
		while (!node.isVisible() && timeout != 0) {
			try {
				timeout--;
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("Interrupted while waiting for Node to be visible!");
				e.printStackTrace();
			}
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
