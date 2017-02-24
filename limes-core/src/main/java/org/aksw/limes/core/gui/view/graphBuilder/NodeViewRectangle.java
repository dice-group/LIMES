package org.aksw.limes.core.gui.view.graphBuilder;

import org.aksw.limes.core.gui.model.metric.Node;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Text;

/**
 * used for the shape of a
 * {@link org.aksw.limes.core.gui.view.graphBuilder.NodeView}
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class NodeViewRectangle {
	public static final Color targetCol = Color.rgb(23, 104, 19);
	public static final Color sourceCol = Color.rgb(128, 23, 26);
	public static final Color metricCol = Color.rgb(129, 70, 23);
	public static final Color operatorCol = Color.rgb(14, 78, 76);
	public static final Color outputCol = Color.rgb(1, 30, 0);
	public static final Color targetHeadTextCol = Color.rgb(236, 237, 236);
	public static final Color sourceHeadTextCol = Color.rgb(254, 242, 242);
	public static final Color metricHeadTextCol = Color.rgb(217, 218, 218);
	public static final Color operatorHeadtextCol = Color.rgb(255, 254, 253);
	public static final Color outputHeadTextCol = Color.rgb(243, 243, 243);
	public static double arch = 50;
	private NodeView node;
	private double x;
	private double y;
	private Color color;
	private Color HeadTextCol;
	private Node nodeData;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            x position
	 * @param y
	 *            y position
	 * @param nodeShape
	 *            shape
	 * @param node
	 *            nodeview
	 * @param nodeData
	 *            data model
	 */
	public NodeViewRectangle(double x, double y, int nodeShape, NodeView node, Node nodeData) {
		this.x = x;
		this.y = y;
		this.nodeData = nodeData;
		switch (nodeShape) {
		case NodeView.METRIC:
			this.color = metricCol;
			this.HeadTextCol = metricHeadTextCol;
			break;
		case NodeView.OUTPUT:
			this.color = outputCol;
			this.HeadTextCol = outputHeadTextCol;
			break;
		case NodeView.OPERATOR:
			this.color = operatorCol;
			this.HeadTextCol = operatorHeadtextCol;
			break;
		case NodeView.SOURCE:
			this.color = sourceCol;
			this.HeadTextCol = sourceHeadTextCol;
			break;
		case NodeView.TARGET:
			this.color = targetCol;
			this.HeadTextCol = targetHeadTextCol;
			break;
		}
		this.node = node;
		this.nodeData = nodeData;

	}

	/**
	 * draws the NodeViewRectangle object according to its variables in the
	 * {@link javafx.scene.canvas.GraphicsContext}
	 * 
	 * @param gc
	 *            GraphicsContext
	 */
	public void drawNodeViewRectangle(GraphicsContext gc) {
		if (this.color != metricCol) {
			gc.setFill(Color.rgb(243, 243, 243));
			gc.setStroke(this.color);
			gc.strokeRoundRect(this.x, this.y, this.node.getWidth(), this.node.getHeight(), NodeViewRectangle.arch,
					NodeViewRectangle.arch);
			gc.fillRoundRect(this.x, this.y, this.node.getWidth(), this.node.getHeight(), NodeViewRectangle.arch,
					NodeViewRectangle.arch);
			gc.setFill(this.color);
			gc.fillArc(this.x, this.y, NodeViewRectangle.arch, NodeViewRectangle.arch, 90.0, 90.0, ArcType.ROUND);
			gc.fillArc(this.x + this.node.getWidth() - NodeViewRectangle.arch, this.y, NodeViewRectangle.arch,
					NodeViewRectangle.arch, 0.0, 90.0, ArcType.ROUND);
			gc.fillRect(x + (arch / 2), y, this.node.getWidth() - arch, arch / 2);
		} else {
			gc.setFill(this.color);
			gc.fillRoundRect(this.x, this.y, this.node.getWidth(), this.node.getHeight(), NodeViewRectangle.arch,
					NodeViewRectangle.arch);
		}
		if (this.color != sourceCol && this.color != targetCol) {
			gc.setFill(this.HeadTextCol);
			fillText(gc, nodeData.id, node.getWidth(), 0, arch / 4, false);

			if (this.color == outputCol) {
				gc.setFill(operatorCol);
				fillText(gc, "Acceptance threshold: ", this.nodeData.param1, node.getWidth(), 4, arch * 0.75, true);
				fillText(gc, "Verification threshold: ", this.nodeData.param2, node.getWidth(), 4, arch * 1.25, true);
			} else if (this.color == operatorCol) {
				gc.setFill(operatorCol);
				if (this.nodeData.getChilds().isEmpty()) {
					fillText(gc, "parent1 threshold: ", this.nodeData.param1, node.getWidth(), 4, arch * 0.75, true);
					fillText(gc, "parent2 threshold: ", this.nodeData.param2, node.getWidth(), 4, arch * 1.25, true);
				} else if (this.nodeData.getChilds().size() == 1) {
					fillText(gc, this.nodeData.getChilds().get(0).id + " threshold: ", this.nodeData.param1,
							node.getWidth(), 4.0, arch * 0.75, true);
					fillText(gc, "parent2 threshold: ", this.nodeData.param2, node.getWidth(), 4, arch * 1.25, true);
				} else {
					fillText(gc, this.nodeData.getChilds().get(0).id + " threshold: ", this.nodeData.param1,
							node.getWidth(), 4.0, arch * 0.75, true);
					fillText(gc, this.nodeData.getChilds().get(1).id + " threshold: ", this.nodeData.param2,
							node.getWidth(), 4.0, arch * 1.25, true);
				}
			}
		} else if (this.color == sourceCol) {
			gc.setFill(this.HeadTextCol);
			fillText(gc, "source", node.getWidth(), 0.0, arch / 4, false);
			gc.setFill(operatorCol);
			fillText(gc, this.nodeData.id, node.getWidth(), 4, arch * 0.75, true);
		} else if (this.color == targetCol) {
			gc.setFill(this.HeadTextCol);
			fillText(gc, "target", node.getWidth(), 0.0, arch / 4, false);
			gc.setFill(operatorCol);
			fillText(gc, this.nodeData.id, node.getWidth(), 4, arch * 0.75, true);

		}
	}

	private void fillText(GraphicsContext gc, String text, int nodeWidth, double xoffset, double yoffset,
			boolean leftAligned) {
		fillText(gc, text, -1, nodeWidth, xoffset, yoffset, leftAligned);
	}

	private void fillText(GraphicsContext gc, String text, double thresholdValue, int nodeWidth, double xoffset,
			double yoffset, boolean leftAligned) {
		Text label = new Text(text);
		double labelWidth = label.getLayoutBounds().getWidth();

		// check if the label needs to be cutoff
		if (labelWidth > 0.85 * this.node.getWidth()) {
			double cutoff = 0.0;
			if (leftAligned) {
				cutoff = ((100 / (double) (this.node.getWidth() - 20)) * labelWidth - 100) / 100;
			} else {
				cutoff = ((100 / (double) (this.node.getWidth() - 30)) * labelWidth - 100) / 100;
			}
			if (thresholdValue == -1) {
				label = new Text(text.substring(0, (int) (text.length() - (text.length() * cutoff))) + "...");
			} else {
				label = new Text(text.substring(0, (int) (text.length() - (text.length() * cutoff))) + "... : ");
			}
		}
		if (!leftAligned) {
			xoffset += calculateOffset(label, nodeWidth);
		}
		if (thresholdValue == -1) {
			gc.fillText(label.getText(), x + xoffset, y + yoffset);
		} else {
			gc.fillText(label.getText() + thresholdValue, x + xoffset, y + yoffset);
		}

	}

	/**
	 * Calculate the offset, to get the text centered
	 */
	private int calculateOffset(Text label, int width) {
		double labelWidth = label.getLayoutBounds().getWidth();
		return (int) (width / 2.0 - labelWidth / 2.0);
	}

}
