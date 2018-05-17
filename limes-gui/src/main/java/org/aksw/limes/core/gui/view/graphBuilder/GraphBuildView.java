package org.aksw.limes.core.gui.view.graphBuilder;

import java.util.ArrayList;
import java.util.Collections;

import org.aksw.limes.core.gui.controller.GraphBuildController;
import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.metric.Node;
import org.aksw.limes.core.gui.model.metric.Output;
import org.aksw.limes.core.gui.model.metric.Property;
import org.aksw.limes.core.gui.util.SourceOrTarget;
import org.aksw.limes.core.gui.view.ToolBox;
import org.aksw.limes.core.io.config.KBInfo;

import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Class to graphically represent link specifications as linked
 * {@link org.aksw.limes.core.gui.view.graphBuilder.NodeView}
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class GraphBuildView extends Canvas {

	/**
	 * Corresponding GraphBuildController
	 */
	public GraphBuildController graphBuildController;

	/**
	 * List of nodes in the Canvas
	 */
	public ArrayList<NodeView> nodeList;

	/**
	 * Reversed List of nodes in Canvas to correctly display dragging
	 */
	public ArrayList<NodeView> reversedNodeList;
	/**
	 * True if Linking Process is running
	 */
	public boolean isLinking;
	/**
	 * Node to Link in linking Process
	 */
	public NodeView linkNode;
	/**
	 * Boolean to check if Graph was edited
	 */
	public boolean edited = false;
	/**
	 * Boolean to check if another ContextMenu is open
	 */
	public boolean contextMenuIsShown = false;
	/**
	 * True if Node was clicked
	 */
	private boolean nodeClicked;
	/**
	 * Index of clicked Node
	 */
	private NodeView clickedNode;
	/**
	 * Context Menu to show on secondary MouseClick
	 */
	private NodeContextMenu contextMenu;
	/**
	 * Mouseposition on Canvas
	 */
	private final double[] mouseCanvasPosition = { 0, 0 };

	private static final int HOVER_TIME_UNTIL_NODE_TOOLTIP_IS_DISPLAYED = 1500;

	private final double[] mouseScreenPosition = { 0, 0 };

	/**
	 * Tooltip over graphical node
	 */
	private Tooltip nodeTooltip = null;

	private Task<Void> checkMouseMovementThread = null;

	/**
	 * Constructor initializes nodeList and adds a new output node. Creates the
	 * corresponding controller with config
	 *
	 * @param currentConfig
	 *            Current used Configmodel
	 * @param toolbox
	 *            toolbox of the main view
	 */
	@SuppressWarnings("unchecked")
	public GraphBuildView(Config currentConfig, ToolBox toolbox) {
		this.widthProperty().addListener(evt -> this.draw());
		this.heightProperty().addListener(evt -> this.draw());
		this.nodeList = new ArrayList<>();
		this.reversedNodeList = (ArrayList<NodeView>) this.nodeList.clone();
		Collections.reverse(this.reversedNodeList);
		this.nodeClicked = false;
		this.isLinking = false;
		this.addNode(300, 300, 2, new Output());
		this.graphBuildController = new GraphBuildController(currentConfig, this, toolbox);
	}

	/**
	 * Constructor initializes nodeList and adds a new output node. Creates the
	 * corresponding controller with config
	 *
	 * @param toolbox
	 *            toolbox of the main view
	 */
	@SuppressWarnings("unchecked")
	public GraphBuildView(ToolBox toolbox) {
		this.widthProperty().addListener(evt -> this.draw());
		this.heightProperty().addListener(evt -> this.draw());
		this.nodeList = new ArrayList<>();
		this.reversedNodeList = (ArrayList<NodeView>) this.nodeList.clone();
		Collections.reverse(this.reversedNodeList);
		this.nodeClicked = false;
		this.isLinking = false;
		this.addNode(300, 300, 2, new Output());
		this.graphBuildController = new GraphBuildController(this, toolbox);
	}

	/**
	 * Set Resizablity to true,overwrite default Canvas Property
	 */
	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	/**
	 * Set Width of Canvas
	 *
	 * @param height
	 *            height
	 */
	public double prefWidth(double width) {
		return this.getWidth();
	}

	/**
	 * Set Height of Canvas
	 *
	 * @param height
	 *            height
	 */
	@Override
	public double prefHeight(double height) {
		return this.getHeight();
	}

	/**
	 * Set Current Config model
	 *
	 * @param config
	 *            Currently used Config
	 */
	public void setCurrentConfig(Config config) {
		this.graphBuildController.setConfig(config);
	}

	/**
	 * Add eventlisteners Begin drawing
	 */
	@SuppressWarnings("unchecked")
	public void start() {
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if (this.isLinking) {
				for (final NodeView node : this.nodeList) {
					if (node.contains((int) e.getX(), (int) e.getY())) {
						this.isLinking = false;
						if (this.linkNode.addParent(node)) {
						} else {
							final Alert alert = new Alert(AlertType.INFORMATION);
							alert.setContentText("Clicked Node is no valid Parent!");
							alert.showAndWait();
						}
						this.edited = true;
						this.draw();
						break;
					}
				}
			}
		});
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			if (this.contextMenuIsShown) {
				this.contextMenu.hide();
				this.contextMenuIsShown = false;
			}
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) {
					int index = 0;
					// boolean clickedOutput = false;
					for (final NodeView node : this.nodeList) {
						if (node.contains((int) e.getX(), (int) e.getY())) {
							this.clickedNode = this.nodeList.get(index);
							break;
						}
						index++;
					}
					if (this.clickedNode.nodeShape == NodeView.OPERATOR
							|| this.clickedNode.nodeShape == NodeView.OUTPUT) {
						new ThresholdModifyView(this, this.clickedNode);
					}
				}
			}
		});

		this.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			int index = 0;
			for (final NodeView node : this.reversedNodeList) {
				if (node.contains((int) e.getX(), (int) e.getY())) {
					this.nodeClicked = true;
					this.clickedNode = this.reversedNodeList.get(index);
					this.nodeList.remove(this.clickedNode);
					this.nodeList.add(this.clickedNode);
					this.reversedNodeList = (ArrayList<NodeView>) this.nodeList.clone();
					break;
				}
				index++;
			}

		});
		this.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
			if (this.nodeTooltip != null) {
				this.nodeTooltip.hide();
				this.nodeTooltip = null;
				if (this.checkMouseMovementThread != null) {
					this.checkMouseMovementThread.cancel();
					this.checkMouseMovementThread = null;
				}
			}

			if (this.nodeClicked) {
				this.clickedNode.setXY((int) e.getX() - this.clickedNode.getWidth() / 2,
						(int) e.getY() - this.clickedNode.getHeight() / 2);

				this.draw();
			}

		});
		this.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
			this.nodeClicked = false;
		});
		this.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				int index = 0;
				boolean NodeClickedBySecondary = false;
				for (final NodeView node : this.nodeList) {
					if (node.contains((int) e.getX(), (int) e.getY())) {
						this.clickedNode = this.nodeList.get(index);
						NodeClickedBySecondary = true;
						break;
					}
					if (node.containsLinkMid((int) e.getX(), (int) e.getY())) {
						node.deleteParent(node.parent);
						this.draw();
					}
					index++;
				}
				if (NodeClickedBySecondary) {
					if (this.clickedNode.nodeShape != NodeView.OUTPUT) {
						if (!this.contextMenuIsShown) {
							this.contextMenu = new NodeContextMenu(this, this.clickedNode);
							this.contextMenu.show(this, e.getScreenX(), e.getScreenY());
						}
					}
				}
			}
		});
		this.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
			this.mouseScreenPosition[0] = e.getScreenX();
			this.mouseScreenPosition[1] = e.getScreenY();
			this.mouseCanvasPosition[0] = e.getX();
			this.mouseCanvasPosition[1] = e.getY();
			if (this.isLinking) {
				this.draw();
			}
		});
		this.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
			this.showNodeTooltip(e);
			// If the mouse was moved delete the tooltip
			if (this.nodeTooltip != null && this.nodeTooltip.isShowing()) {
				this.nodeTooltip.hide();
				this.nodeTooltip = null;
			}
		});

		this.draw();
	}

	private void showNodeTooltip(MouseEvent e) {
		boolean insideNode = false;
		for (final NodeView node : this.nodeList) {
			if (node.contains((int) e.getX(), (int) e.getY())) {
				insideNode = true;
				if (this.nodeTooltip == null) {
					this.nodeTooltip = new Tooltip(node.toString());

					// =========== WAIT UNTIL MOUSE HAS HOVERED OVER THIS NODE
					// FOR A WHILE ================
					this.checkMouseMovementThread = new Task<Void>() {
						@Override
						protected Void call() throws Exception {
							try {
								Thread.sleep(HOVER_TIME_UNTIL_NODE_TOOLTIP_IS_DISPLAYED);
							} catch (final InterruptedException e) {
							}
							return null;
						}
					};
					// IF TOOLTIP IS NOT NULL THE MOUSE IS STILL OVER THE NODE
					this.checkMouseMovementThread.setOnSucceeded(event -> {
						if (GraphBuildView.this.nodeTooltip != null) {
							if (!GraphBuildView.this.contextMenuIsShown && !GraphBuildView.this.isLinking
									&& node.contains((int) GraphBuildView.this.mouseCanvasPosition[0],
											(int) GraphBuildView.this.mouseCanvasPosition[1])) {
								GraphBuildView.this.nodeTooltip.show(GraphBuildView.this.getParent(),
										GraphBuildView.this.mouseScreenPosition[0],
										GraphBuildView.this.mouseScreenPosition[1]);
							} else {
								GraphBuildView.this.nodeTooltip = null;
							}
						}
					});
					new Thread(this.checkMouseMovementThread).start();
				}
			}
		}
		if (this.nodeTooltip != null && !insideNode) {
			this.nodeTooltip.hide();
			this.nodeTooltip = null;
			if (this.checkMouseMovementThread != null) {
				this.checkMouseMovementThread.cancel();
				this.checkMouseMovementThread = null;
			}
		}
	}

	/**
	 * Draw Nodes and Links to the Canvas
	 */
	public void draw() {
		final GraphicsContext gc = this.getGraphicsContext2D();
		gc.clearRect(0, 0, this.getWidth(), this.getHeight());
		if (this.isLinking) {
			gc.strokeLine(this.linkNode.x + this.linkNode.getWidth() / 2,
					this.linkNode.y + this.linkNode.getHeight() / 2, this.mouseCanvasPosition[0],
					this.mouseCanvasPosition[1]);
		}
		this.nodeList.forEach(e -> {
			e.drawLink();
		});
		this.nodeList.forEach(e -> {
			if (e.nodeShape == NodeView.SOURCE || e.nodeShape == NodeView.TARGET) {
				final Config c = this.graphBuildController.getConfig();
				final String propString = c.removeVar(e.nodeData.id, ((Property) e.nodeData).getOrigin());
				final SourceOrTarget sot = ((Property) e.nodeData).getOrigin();
				final KBInfo info = sot == SourceOrTarget.SOURCE ? c.getSourceInfo() : c.getTargetInfo();
				if (info.getOptionalProperties().contains(propString)
						|| info.getOptionalProperties().contains(c.reverseRename(propString, sot))) {
					((Property) e.nodeData).setOptional(true);
				} else {
					((Property) e.nodeData).setOptional(false);
				}
			}
			e.displayNode();
		});

	}

	/**
	 * Adds a Node to the Canvas
	 *
	 * @param x
	 *            Position on x-Axis
	 * @param y
	 *            Position on y-Axis
	 * @param shape
	 *            Shape of Node
	 * @param node
	 *            Node Data Model
	 */
	@SuppressWarnings("unchecked")
	public void addNode(int x, int y, int shape, Node node) {
		final int[] xy = this.findFreePlace(x, y);
		if (xy != null) {
			final int new_x = xy[0];
			final int new_y = xy[1];
			final NodeView nv = new NodeView(new_x, new_y, shape, "test", this, node);
			nv.displayNode();
			this.nodeList.add(nv);
			this.reversedNodeList = (ArrayList<NodeView>) this.nodeList.clone();
			Collections.reverse(this.reversedNodeList);
		}
	}

	/**
	 * Calculates a place for the new node, which is not already taken
	 *
	 * @param x
	 *            Proposed position on x-Axis
	 * @param y
	 *            Proposed position on y-Axis
	 * @return int[] containing a free coordinate
	 */
	private int[] findFreePlace(int x, int y) {
		for (int i = 0; i < this.nodeList.size(); i++) {
			if (this.nodeList.get(i).x == x || this.nodeList.get(i).y == y) {
				x += 10;
				y += 10;
				i = -1; // Because it needs to start from 0 and the for-loop
				// does i++
				if (x >= (int) this.widthProperty().doubleValue() || y >= (int) this.heightProperty().doubleValue()) {
					final Alert alert = new Alert(AlertType.INFORMATION);
					alert.setContentText("Cannot add more nodes! Please move or delete some nodes!");
					alert.showAndWait();
					return null;
				}
			}
		}
		return new int[] { x, y };
	}

	/**
	 * Remove Node From Canvas
	 *
	 * @param node
	 *            Node to be removed
	 */
	public void removeNodeView(NodeView node) {
		this.edited = true;
		boolean remove = false;
		for (final NodeView item : this.nodeList) {
			if (node.nodeData.id.equals(item.nodeData.id)) {
				remove = true;
			}
		}
		this.reversedNodeList = this.nodeList;
		Collections.reverse(this.reversedNodeList);
		if (remove) {
			node.deleteNode();
			this.nodeList.remove(node);
			this.draw();
		}
	}
}
