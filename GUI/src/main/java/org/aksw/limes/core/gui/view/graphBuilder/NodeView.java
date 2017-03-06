package org.aksw.limes.core.gui.view.graphBuilder;

import java.util.List;
import java.util.Vector;

import org.aksw.limes.core.gui.model.metric.Node;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

/**
 * Graphical representation of a node
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class NodeView {

	/**
	 * Node-Shape Integer Metric
	 */
	public static final int METRIC = 1; // Metric

	/**
	 * Node-Shape Integer Output
	 */
	public static final int OUTPUT = 2; // Output

	/**
	 * Node-Shape Integer Operator
	 */
	public static final int OPERATOR = 3; // Operator

	/**
	 * Node-Shape Integer Source
	 */
	public static final int SOURCE = 4; // SourceProperty

	/**
	 * Node-Shape Integer Target
	 */
	public static final int TARGET = 5; // TargetProperty
	/**
	 * Image of Linkarrow
	 */
	private final Image arrow = new Image("gui/arrow.png", 10.0, 10.0, true, true);
	/**
	 * Position on x-Axis
	 */
	public int x;
	/**
	 * Position on y-Axis
	 */
	public int y;
	/**
	 * Shape int of Node
	 */
	public int nodeShape;
	/**
	 * Node Data Model
	 */
	public Node nodeData;
	/**
	 * Parent of Node
	 */
	public NodeView parent = null;
	/**
	 * Canvas to draw Node
	 */
	GraphBuildView gbv;
	/**
	 * Width of Nodes
	 */
	private int width = 200;
	/**
	 * Height of Nodes
	 */
	private int height = 80;
	/**
	 * Children of Node
	 */
	private List<NodeView> children = new Vector<NodeView>();
	/**
	 * X Position of middle of link
	 */
	private int midLinkX;

	/**
	 * Y Position of middle of link
	 */
	private int midLinkY;

	/**
	 * Constructor
	 *
	 * @param x
	 *            Position on x-Axis
	 * @param y
	 *            Position on y-Axis
	 * @param nodeShape
	 *            Shape int of Node
	 * @param label
	 *            Label to display
	 * @param gbv
	 *            Canvas to Draw Node
	 * @param node
	 *            Node-Datamodel
	 */
	public NodeView(int x, int y, int nodeShape, String label, GraphBuildView gbv, Node node) {
		this.x = x;
		this.y = y;
		this.nodeShape = nodeShape;
		if (nodeShape == NodeView.OUTPUT) {
			this.width = 230;
		} else {
			this.width = 200;
		}
		if (nodeShape != NodeView.METRIC) {
			this.height = 80;
		} else {
			this.height = 20;
		}
		this.gbv = gbv;
		this.nodeData = node;
	}

	/**
	 * Draw Node to GraphBuildView
	 */
	public void displayNode() {
		GraphicsContext gc = gbv.getGraphicsContext2D();
		NodeViewRectangle nvr = new NodeViewRectangle(this.x, this.y, this.nodeShape, this, this.nodeData);
		nvr.drawNodeViewRectangle(gc);
	}

	/**
	 * Set Position of NodeView on Canvas
	 *
	 * @param x
	 *            Position on x-Axis
	 * @param y
	 *            Position on y-Axis
	 */
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Proof if Point is in the Node
	 *
	 * @param x
	 *            Position on x-Axis to proof
	 * @param y
	 *            Position on y-Axis to proof
	 * @return True it Position is in drawed sector
	 */
	public boolean contains(int x, int y) {
		int minX = this.x;
		int maxX = this.x + this.width;
		int minY = this.y;
		int maxY = this.y + this.height;
		if (x >= minX && x <= maxX) {
			if (y >= minY && y <= maxY) {
				return true;
			}
		} else {
			return false;
		}
		return false;
	}

	/**
	 * Proof if Point is in the LinkMid
	 *
	 * @param x
	 *            Position on x-Axis to proof
	 * @param y
	 *            Position on y-Axis to proof
	 * @return True it Position is in drawed sector
	 */
	public boolean containsLinkMid(int x, int y) {
		int minX = this.midLinkX - 10;
		int maxX = this.midLinkX + 10;
		int minY = this.midLinkY - 10;
		int maxY = this.midLinkY + 10;
		if (x >= minX && x <= maxX) {
			if (y >= minY && y <= maxY) {
				return true;
			}
		} else {
			return false;
		}
		return false;
	}

	/**
	 * Adds a Parent to the NodeView, and links the Data Models
	 *
	 * @param parent
	 *            parent to add
	 * @return True if successful
	 */
	public boolean addParent(NodeView parent) {
		boolean test = parent.nodeData.addChild(nodeData);
		if (!test) {
			return false;
		}
		parent.children.add(this);
		this.parent = parent;
		return true;
	}

	/**
	 * Deletes Parent from NodeView and unlinks data models
	 *
	 * @param parent
	 *            parent node
	 */
	public void deleteParent(NodeView parent) {
		parent.nodeData.removeChild(nodeData);
		parent.children.remove(this);
		this.parent = null;
	}

	/**
	 * Adds a Child without Linking the Data Models
	 *
	 * @param child
	 *            Child to Added
	 */
	public void addChildWithOutDataLinking(NodeView child) {
		children.add(child);
		child.parent = this;
		child.nodeData.overwriteParent(this.nodeData);
	}

	/**
	 * Draw the Links on Canvas to the Childs
	 */
	public void drawLink() {
		GraphicsContext gc = gbv.getGraphicsContext2D();
		gc.setStroke(Color.BLACK);
		children.forEach(nodeView -> {
			int x1 = x + this.width / 2;
			int y1 = y + this.height / 2;
			int x2 = nodeView.x + nodeView.width / 2;
			int y2 = nodeView.y + nodeView.height / 2;
			gc.strokeLine(x1, y1, x2, y2);

			double linkMidX = (x1 + x2) / 2.0;
			nodeView.midLinkX = (int) linkMidX;
			double linkMidY = (y1 + y2) / 2.0;
			nodeView.midLinkY = (int) linkMidY;
			double rotate = Math.toDegrees(Math.atan2(y2 - y1, x2 - x1)) + 225;
			gc.setTransform(new Affine(new Rotate(rotate, linkMidX, linkMidY)));
			double arrowX = linkMidX - (arrow.getWidth() * 3 / 4);
			double arrowY = linkMidY - (arrow.getWidth() / 4);
			gc.drawImage(arrow, arrowX, arrowY);
			gc.setTransform(new Affine());
		});
	}

	/**
	 * Delete the Node unlink Children and Parent in Data and View Model
	 */
	public void deleteNode() {
		if (parent != null) {
			parent.nodeData.removeChild(this.nodeData);
			parent.children.remove(this);

		}
		children.forEach(e -> {
			e.nodeData.removeParent();
			e.parent = null;
		});
		this.parent = null;
		this.nodeData.removeParent();
	}

	public String toString() {
		String str = nodeData.id + "\n";
		switch (nodeShape) {
		case OUTPUT:
			str += "Acceptance threshold: " + nodeData.param1 + "\n";
			str += "Verification threshold: " + nodeData.param2 + "\n";
			break;
		case OPERATOR:
			if (this.nodeData.getChilds().isEmpty()) {
				str += "parent1 threshold: " + this.nodeData.param1 + "\n";
				str += "parent2 threshold: " + this.nodeData.param2 + "\n";
			} else if (this.nodeData.getChilds().size() == 1) {
				str += this.nodeData.getChilds().get(0).id + " threshold: " + this.nodeData.param1 + "\n";
				str += "parent2 threshold: " + this.nodeData.param2 + "\n";
			} else {
				str += this.nodeData.getChilds().get(0).id + " threshold: " + this.nodeData.param1 + "\n";
				str += this.nodeData.getChilds().get(1).id + " threshold: " + this.nodeData.param2 + "\n";
			}
			break;
		}
		return str;
	}

	/**
	 * returns width
	 * 
	 * @return width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * returns height
	 * 
	 * @return height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * returns midLinkX
	 * 
	 * @return midLinkX
	 */
	public int getMidLinkX() {
		return midLinkX;
	}

	/**
	 * returns midLinkY
	 * 
	 * @return midLinkY
	 */
	public int getMidLinkY() {
		return midLinkY;
	}
}
