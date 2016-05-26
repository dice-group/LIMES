package org.aksw.limes.core.gui.view.graphBuilder;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Text;
import org.aksw.limes.core.gui.model.metric.Node;

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

    public NodeViewRectangle(double x, double y, int nodeShape, NodeView node,
                             Node nodeData) {
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

    public void drawNodeViewRectangle(GraphicsContext gc) {
        if (this.color != metricCol) {
            gc.setFill(Color.rgb(243, 243, 243));
            gc.setStroke(this.color);
            gc.strokeRoundRect(this.x, this.y, this.node.getWidth(),
                    this.node.getHeight(), NodeViewRectangle.arch,
                    NodeViewRectangle.arch);
            gc.fillRoundRect(this.x, this.y, this.node.getWidth(),
                    this.node.getHeight(), NodeViewRectangle.arch,
                    NodeViewRectangle.arch);
            gc.setFill(this.color);
            gc.fillArc(this.x, this.y, NodeViewRectangle.arch,
                    NodeViewRectangle.arch, 90.0, 90.0, ArcType.ROUND);
            gc.fillArc(this.x + this.node.getWidth() - NodeViewRectangle.arch,
                    this.y, NodeViewRectangle.arch, NodeViewRectangle.arch,
                    0.0, 90.0, ArcType.ROUND);
            gc.fillRect(x + (arch / 2), y, this.node.getWidth() - arch,
                    arch / 2);
        } else {
            gc.setFill(this.color);
            gc.fillRoundRect(this.x, this.y, this.node.getWidth(),
                    this.node.getHeight(), NodeViewRectangle.arch,
                    NodeViewRectangle.arch);
        }
        if (this.color != sourceCol && this.color != targetCol) {
            gc.setFill(this.HeadTextCol);
            gc.fillText(this.nodeData.id, x + calculateOffset(), y + arch / 4);

            if (this.color == outputCol) {
                gc.setFill(operatorCol);
                gc.fillText("Acceptance threshold: " + this.nodeData.param1,
                        x + 4, y + arch * 0.75);
                gc.fillText("Verification threshold: " + this.nodeData.param2,
                        x + 4, y + arch * 1.25);
            } else if (this.color == operatorCol) {
                gc.setFill(operatorCol);
                if (this.nodeData.getChilds().isEmpty()) {
                    gc.fillText("parent 1 threshold: " + this.nodeData.param1,
                            x + 4, y + arch * 0.75);
                    gc.fillText("parent 2 threshold: " + this.nodeData.param2,
                            x + 4, y + arch * 1.25);
                } else if (this.nodeData.getChilds().size() == 1) {
                    gc.fillText(this.nodeData.getChilds().get(0).id
                            + " threshold: " + this.nodeData.param1, x + 4, y
                            + arch * 0.75);
                    gc.fillText("parent 2 threshold: " + this.nodeData.param2,
                            x + 4, y + arch * 1.25);
                } else {
                    gc.fillText(this.nodeData.getChilds().get(0).id
                            + " threshold: " + this.nodeData.param1, x + 4, y
                            + arch * 0.75);
                    gc.fillText(this.nodeData.getChilds().get(1).id
                            + " threshold: " + this.nodeData.param2, x + 4, y
                            + arch * 1.25);
                }
            }
        } else if (this.color == sourceCol) {
            gc.setFill(this.HeadTextCol);
            gc.fillText("source", x + calculateOffset(), y + arch / 4);
            gc.setFill(operatorCol);
            gc.fillText(this.nodeData.id, x + 4, y + arch * 0.75);
        } else if (this.color == targetCol) {
            gc.setFill(this.HeadTextCol);
            gc.fillText("target", x + calculateOffset(), y + arch / 4);
            gc.setFill(operatorCol);
            gc.fillText(this.nodeData.id, x + 4, y + arch * 0.75);

        }
    }

    /**
     * Calculate the offset, to get the text centered
     */
    private int calculateOffset() {
        Text label = new Text(nodeData.id);
        double labelWidth = label.getLayoutBounds().getWidth();
        return (int) (this.node.getWidth() / 2.0 - labelWidth / 2.0);
    }

}