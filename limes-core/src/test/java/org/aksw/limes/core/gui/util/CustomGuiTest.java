package org.aksw.limes.core.gui.util;

import org.testfx.api.FxRobot;

import javafx.scene.Node;

public class CustomGuiTest {
	
	public static void waitUntilNodeIsNotNull(String nodeId, int timeout){
		Node node = new FxRobot().lookup(nodeId).query();
		while(node == null && timeout != 0)
		{
		  try {
			  node = new FxRobot().lookup(nodeId).query();
			  timeout --;
			  Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	/**
	 * Uses FxRobot from TestFX to lookup the node 
	 * @param nodeId ID of the node
	 * @param timeout in seconds
	 */
	public static void waitUntilNodeIsVisible(String nodeId, int timeout){
		Node node = new FxRobot().lookup(nodeId).query();
		if(node == null){
			waitUntilNodeIsNotNull(nodeId, timeout);
		}
		node = new FxRobot().lookup(nodeId).query();
		while(!node.isVisible() && timeout != 0)
		{
		  try {
			  timeout --;
			  Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
	}

}
