package org.aksw.limes.core.execution.planning.plan;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFrame;

import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.measures.measure.MeasureProcessor;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

/**
 * Implements execution plan that are given to an execution engine.
 * 
 * @author ngonga
 * @author kleanthi
 */
public class NestedPlan extends Plan {

    
    public NestedPlan() {
	super();
	
    }

    @Override
    public boolean isEmpty() {
	return (getInstructionList() == null && getSubPlans() == null && getFilteringInstruction() == null);
    }

    @Override
    public List<Instruction> getInstructionList() {
	List<Instruction> instructions = super.getInstructionList();
	if (!isFlat()) {
	    for (NestedPlan np : getSubPlans()) {
		instructions.addAll(np.getInstructionList());
	    }
	}
	return instructions;
    }

    /**
     * Adds a subplan to the current list of subplans, if there is no list one
     * will be created
     * 
     * @param subplan
     *            to be added
     */
    public void addSubplan(NestedPlan subplan) {
	if (getSubPlans() == null)
	    setSubPlans(new ArrayList<NestedPlan>());
	getSubPlans().add(subplan);
    }

    /**
     * Get all the metric expressions of the current NestedPlan
     * 
     * @return List of all metric expressions
     */
    public List<String> getAllMeasures() {
	List<String> result = new ArrayList<String>();

	if (isFlat()) {
	    if (getFilteringInstruction() != null) {
		result.addAll(MeasureProcessor.getMeasures(getFilteringInstruction().getMeasureExpression()));
	    }
	}
	if (!(getSubPlans() == null)) {
	    if (!getSubPlans().isEmpty()) {
		for (NestedPlan p : getSubPlans()) {
		    result.addAll(p.getAllMeasures());
		}
	    }
	}
	if (getInstructionList() != null) {
	    for (Instruction i : getInstructionList()) {
		if (i.getMeasureExpression() != null) {
		    result.addAll(MeasureProcessor.getMeasures(i.getMeasureExpression()));
		}
	    }
	}
	return result;
    }

    /**
     * Checks whether the current NestedPlan is atomic
     * 
     * @return true, if current NestedPlan is atomic. false, if otherwise
     */
    public boolean isFlat() {
	if (getSubPlans() == null) {
	    return true;
	} else {
	    if (getSubPlans().isEmpty()) {
		return true;
	    }
	}
	return false;
    }

    /**
     * String representation of NestedPlan
     * 
     * @return NestedPlan as string
     */
    public String toString() {
	String pre = ("Selectivity = " + getSelectivity());
	if (isEmpty()) {
	    return "Empty plan";
	}
	if (isFlat()) {

	    if (getInstructionList() != null) {
		return "\n\nBEGIN\n" + pre + "\n-----\nNULL\n" + getInstructionList() + "\nEND\n-----";
	    } else {
		return "\nBEGIN\n" + pre + "-----\nNULL\n" + getFilteringInstruction() + "\nEND\n-----";
	    }
	} else {
	    return "\nBEGIN\n" + pre + "-----\n" + getFilteringInstruction() + "\nSubplans\n" + getOperator() + "\n" + getSubPlans()
		    + "\nEND\n-----";
	}
    }

    /**
     * Returns the filterInstruction of a NestedPlan
     *
     * @return filterInstruction as string
     */
    public String getFilterString(String filter) {
	String[] parts = filter.split("\t");
	String result = parts[0];
	if (!parts[1].equals("null")) {
	    result = result + "\n" + parts[1];
	}
	result = result + "\n" + parts[2];
	return result;
    }

    /**
     * Returns the measure that is equivalent to this plan. Mostly for planning
     * purposes
     *
     * @return Measure as string
     */
    public String getEquivalentMeasure() {
	String result;
	if (isFlat()) {
	    result = getInstructionList().get(0).getMeasureExpression();
	} else {
	    // filtering node with one
	    if (getSubPlans().size() == 1) {
		if (getFilteringInstruction() != null) {
		    if (getFilteringInstruction().getMeasureExpression() == null) {
			return getSubPlans().get(0).getEquivalentMeasure();
		    } else {
			return "AND(" + getFilteringInstruction().getMeasureExpression() + "|"
				+ getFilteringInstruction().getThreshold() + "," + getSubPlans().get(0).getEquivalentMeasure()
				+ "|" + getSubPlans().get(0).getInstructionList().get(0).getThreshold() + ")";
		    }
		}
	    }

	    if (getOperator() == Command.INTERSECTION) {
		result = "AND(";
	    } else if (getOperator() == Command.UNION) {
		result = "OR(";
	    } else if (getOperator() == Command.XOR) {
		result = "XOR(";
	    } else if (getOperator() == Command.DIFF) {
		result = "MINUS(";
	    } else {
		result = "";
	    }

	    for (NestedPlan p : getSubPlans()) {
		result = result + p.getEquivalentMeasure() + "|";
		if (p.isFlat()) {
		    result = result + p.getInstructionList().get(0).getThreshold() + ",";
		} else {
		    if (p.getFilteringInstruction() != null) {
			result = result + p.getFilteringInstruction().getThreshold() + ",";
		    }
		}
	    }
	    result = result.substring(0, result.length() - 1);
	    result = result + ")";

	    if (!(getInstructionList() == null)) {
		if (!getInstructionList().isEmpty()) {
		    for (Instruction i : getInstructionList()) {
			result = "AND(" + i.getMeasureExpression() + "|" + i.getThreshold() + "," + result + ")";
		    }
		}
	    }
	}
	if (getFilteringInstruction() != null) {
	    if (getFilteringInstruction().getMeasureExpression() != null) {
		result = "AND(" + getFilteringInstruction().getMeasureExpression() + "|"
			+ getFilteringInstruction().getThreshold() + "," + result + "|" + getFilteringInstruction().getThreshold()
			+ ")";
	    }
	}

	return result;
    }

    /**
     * Generates a clone of the current NestedPlan
     * 
     * @return Clone of current NestedPlan
     */
    public NestedPlan clone() {
	NestedPlan clone = new NestedPlan();
	clone.setFilteringInstruction(this.getFilteringInstruction());
	clone.setInstructionList(this.getInstructionList());
	clone.setMappingSize(this.getMappingSize());
	clone.setOperator(this.getOperator());
	clone.setRuntimeCost(this.getRuntimeCost());
	clone.setSelectivity(this.getSelectivity());
	List<NestedPlan> l = new ArrayList<NestedPlan>();
	NestedPlan subPlanCopy;
	if (this.getSubPlans() != null)
	    for (NestedPlan c : this.getSubPlans()) {
		subPlanCopy = c.clone();
		clone.addSubplan(subPlanCopy);
		l.add(subPlanCopy);
	    }

	return clone;
    }

    /**
     * Returns the threshold to be used when reconstructing the metric that led
     * to the current NestedPlan
     *
     * @return Threshold as string
     */
    public String getThreshold() {
	if (getFilteringInstruction() != null) {
	    return getFilteringInstruction().getThreshold();
	} else {
	    return "0";
	}
    }

    /**
     * Returns the size of a NestedPlan
     * 
     * @param size
     *            as int
     */
    public int getSize(String s) {
	int size = 0;
	if (s.contains("\n")) {
	    String[] parts = s.split("\n");
	    for (int i = 0; i < parts.length; i++) {
		size = Math.max(size, parts[i].length());
	    }
	    return size;
	}
	return s.length();
    }

    /**
     * Returns the string representation of the instruction of an atomic
     * NestedPlan
     * 
     * @return Instruction as a string
     */
    public String getInstructionString(List<Instruction> list) {
	Instruction i = list.get(0);
	String result = i.getCommand() + "\n";
	result = result + i.getMeasureExpression() + "\n";
	result = result + i.getThreshold();
	return result;
    }

    /**
     * Graphic representation of the current NestedPlan
     * 
     */
    public void draw(mxGraph graph, Object root) {
	int charsize = 8;
	Object parent = graph.getDefaultParent();
	if (isFlat()) {
	    Object v;
	    if (getInstructionList() != null && !getInstructionList().isEmpty()) {
		String inst = getInstructionString(getInstructionList());
		v = graph.insertVertex(parent, null, inst, 20, 40, getSize(inst) * charsize, 45, "ROUNDED");
	    } else {
		String filter = getFilterString(getFilteringInstruction().toString());
		v = graph.insertVertex(parent, null, filter, 20, 40, getSize(filter) * charsize, 45, "ROUNDED");
	    }
	    if (root != null) {
		graph.insertEdge(parent, null, "", root, v);
	    }
	} else {
	    Object v1, v2;
	    String filter;
	    if (getFilteringInstruction() != null) {
		filter = getFilterString(getFilteringInstruction().toString());
	    } else {
		filter = "NULL";
	    }
	    // String inst = getInstructionString(instructionList);
	    v1 = graph.insertVertex(parent, null, filter, 20, 40, getSize(filter) * charsize, 45, "ROUNDED");
	    v2 = graph.insertVertex(parent, null, getOperator(), 20, 40, (getOperator() + "").length() * charsize, 45,
		    "RECTANGLE");
	    graph.insertEdge(parent, null, "", root, v1);
	    graph.insertEdge(parent, null, "", v1, v2);
	    for (NestedPlan p : getSubPlans()) {
		p.draw(graph, v2);
	    }
	}
    }

    /**
     * Representation of the current NestedPlan as a Graph
     * 
     * @return NestedPlan as a Graph
     */
    public mxGraph getGraph() {
	mxGraph graph = new mxGraph();

	mxStylesheet stylesheet = graph.getStylesheet();
	Hashtable<String, Object> rounded = new Hashtable<String, Object>();
	rounded.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
	rounded.put(mxConstants.STYLE_OPACITY, 50);
	rounded.put(mxConstants.STYLE_FILLCOLOR, "#FF5240");
	rounded.put(mxConstants.STYLE_FONTCOLOR, "#000000");
	stylesheet.putCellStyle("ROUNDED", rounded);

	Hashtable<String, Object> rectangle = new Hashtable<String, Object>();
	rectangle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
	rectangle.put(mxConstants.STYLE_OPACITY, 50);
	rectangle.put(mxConstants.STYLE_FILLCOLOR, "#5FEB3B");
	rectangle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
	stylesheet.putCellStyle("RECTANGLE", rectangle);

	@SuppressWarnings("unused")
	Object parent = graph.getDefaultParent();
	graph.getModel().beginUpdate();
	try {

	    draw(graph, null);
	} finally {
	    graph.getModel().endUpdate();
	}
	mxCompactTreeLayout layout = new mxCompactTreeLayout(graph);
	layout.setHorizontal(false);
	layout.execute(graph.getDefaultParent());
	return graph;
    }

    /**
     * Drawing of the current NestedPlan
     * 
     */
    public void draw() {
	mxGraph graph = getGraph();
	mxGraphComponent graphComponent = new mxGraphComponent(graph);
	graphComponent.getViewport().setOpaque(false);
	graphComponent.setBackground(Color.WHITE);

	JFrame frame = new JFrame();

	frame.setSize(500, 500);
	frame.setLocation(300, 200);
	frame.setBackground(Color.white);

	frame.add(graphComponent);
	frame.pack();
	frame.setVisible(true);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
