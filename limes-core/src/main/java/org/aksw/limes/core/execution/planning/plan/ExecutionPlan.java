package org.aksw.limes.core.execution.planning.plan;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JFrame;

import org.aksw.limes.core.execution.planning.plan.Instruction.Command;
import org.aksw.limes.core.measures.measure.MeasureProcessor;
import org.apache.log4j.Logger;

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
public class ExecutionPlan extends Plan {
    public double runtimeCost;
    public double mappingSize;
    public double selectivity;
    public List<ExecutionPlan> subPlans;
    public Command operator;
    public Instruction filteringInstruction;

    public ExecutionPlan() {
	instructionList = null;
	subPlans = null;
	filteringInstruction = null;
	runtimeCost = 0.0d;
	selectivity = 0.0d;
	mappingSize = 0.0d;
    }

    /**
     * Get all measure expressions from the execution plan, for example
     * "[trigrams(x.rdfs:label, y.rdfs:label),cosine(x.rdfs:title, y.rdfs:title)]"
     * .
     * 
     * @return results, list of measure expressions
     */
    public List<String> getAllMeasures() {
	List<String> result = new ArrayList<String>();

	if (isAtomic()) {
	    if (filteringInstruction != null) {
		result.addAll(MeasureProcessor.getMeasures(filteringInstruction.getMeasureExpression()));
	    }
	}
	if (!(subPlans == null)) {
	    if (!subPlans.isEmpty()) {
		for (ExecutionPlan p : subPlans) {
		    result.addAll(p.getAllMeasures());
		}
	    }
	}
	if (instructionList != null) {
	    for (Instruction i : instructionList) {
		if (i.getMeasureExpression() != null) {
		    result.addAll(MeasureProcessor.getMeasures(i.getMeasureExpression()));
		}
	    }
	}
	return result;
    }

    /**
     * Check if plan consists of one instruction.
     * 
     * @return true, if plan is atomic or false, otherwise
     */
    public boolean isAtomic() {
	if (subPlans == null) {
	    return true;
	} else {
	    if (subPlans.isEmpty()) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean isEmpty() {
	return (instructionList == null && subPlans == null && filteringInstruction == null);
    }

    /**
     * String representation of execution plan.
     * 
     * 
     * @return pre, string representation of plan
     */
    public String toString() {
	String pre = ("Selectivity = " + selectivity);
	if (isEmpty()) {
	    return "Empty plan";
	}
	if (isAtomic()) {

	    if (instructionList != null) {
		return "\n\nBEGIN\n" + pre + "\n-----\nNULL\n" + instructionList + "\nEND\n-----";
	    } else {
		return "\nBEGIN\n" + pre + "-----\nNULL\n" + filteringInstruction + "\nEND\n-----";
	    }
	} else {
	    return "\nBEGIN\n" + pre + "-----\n" + filteringInstruction + "\nSubplans\n" + operator + "\n" + subPlans
		    + "\nEND\n-----";
	}
    }

    /**
     * TODO: add description
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
     * @return result, the string representation of equivalent measure
     */
    public String getEquivalentMeasure() {
	String result;
	if (isAtomic()) {
	    result = instructionList.get(0).getMeasureExpression();
	} else {

	    // filtering node with one
	    if (subPlans.size() == 1) {
		if (filteringInstruction != null) {
		    if (filteringInstruction.getMeasureExpression() == null) {
			return subPlans.get(0).getEquivalentMeasure();
		    } else {
			return "AND(" + filteringInstruction.getMeasureExpression() + "|"
				+ filteringInstruction.getThreshold() + "," + subPlans.get(0).getEquivalentMeasure()
				+ "|" + subPlans.get(0).instructionList.get(0).getThreshold() + ")";
		    }
		}
	    }

	    if (operator == Command.INTERSECTION) {
		result = "AND(";
	    } else if (operator == Command.UNION) {
		result = "OR(";
	    } else if (operator == Command.XOR) {
		result = "XOR(";
	    } else if (operator == Command.DIFF) {
		result = "MINUS(";
	    } else {
		result = "";
	    }

	    for (ExecutionPlan p : subPlans) {
		result = result + p.getEquivalentMeasure() + "|";
		if (p.isAtomic()) {
		    result = result + p.instructionList.get(0).getThreshold() + ",";
		} else {
		    if (p.filteringInstruction != null) {

			result = result + p.filteringInstruction.getThreshold() + ",";

		    }
		}
	    }
	    result = result.substring(0, result.length() - 1);
	    result = result + ")";

	    if (!(instructionList == null)) {
		if (!instructionList.isEmpty()) {
		    for (Instruction i : instructionList) {
			result = "AND(" + i.getMeasureExpression() + "|" + i.getThreshold() + "," + result + ")";
		    }
		}
	    }
	}
	if (filteringInstruction != null) {
	    if (filteringInstruction.getMeasureExpression() != null) {
		result = "AND(" + filteringInstruction.getMeasureExpression() + "|"
			+ filteringInstruction.getThreshold() + "," + result + "|" + filteringInstruction.getThreshold()
			+ ")";
	    }
	}

	return result;
    }

    /**
     * Returns the threshold to be used when reconstructing the metric that led
     * to this plans
     *
     * @return Threshold as string
     */
    public String getThreshold() {
	if (filteringInstruction != null) {
	    return filteringInstruction.getThreshold();
	} else {
	    return "0";
	}
    }
    
    /**
     * Get size of biggest instruction.
     *
     * @return size, size of biggest instruction
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
     * Get string representation of  instruction
     *
     * @return result, instruction as string
     */
    public String getInstructionString(List<Instruction> list) {
	Instruction i = list.get(0);
	String result = i.getCommand() + "\n";
	result = result + i.getMeasureExpression() + "\n";
	result = result + i.getThreshold();
	return result;
    }

    @Override
    public List<Instruction> getInstructionList() {
	List<Instruction> instructions = super.getInstructionList();
	if (!isAtomic()) {
	    for (ExecutionPlan np : subPlans) {
		instructions.addAll(np.getInstructionList());
	    }
	}
	return instructions;
    }
    /**
     * Get graphical representation of execution plan
     *
     */
    public void draw(mxGraph graph, Object root) {
	int charsize = 8;
	Object parent = graph.getDefaultParent();
	if (isAtomic()) {
	    Object v;
	    if (instructionList != null && !instructionList.isEmpty()) {
		String inst = getInstructionString(instructionList);
		v = graph.insertVertex(parent, null, inst, 20, 40, getSize(inst) * charsize, 45, "ROUNDED");
	    } else {
		String filter = getFilterString(filteringInstruction.toString());
		v = graph.insertVertex(parent, null, filter, 20, 40, getSize(filter) * charsize, 45, "ROUNDED");
	    }
	    if (root != null) {
		graph.insertEdge(parent, null, "", root, v);
	    }
	} else {
	    Object v1, v2;
	    String filter;
	    if (filteringInstruction != null) {
		filter = getFilterString(filteringInstruction.toString());
	    } else {
		filter = "NULL";
	    }
	    v1 = graph.insertVertex(parent, null, filter, 20, 40, getSize(filter) * charsize, 45, "ROUNDED");
	    v2 = graph.insertVertex(parent, null, operator, 20, 40, (operator + "").length() * charsize, 45,
		    "RECTANGLE");
	    graph.insertEdge(parent, null, "", root, v1);
	    graph.insertEdge(parent, null, "", v1, v2);
	    for (ExecutionPlan p : subPlans) {
		p.draw(graph, v2);
	    }
	}
    }
    /**
     * Get graph of execution plan
     *
     *@return graph, graph of execution plan
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

	Object parent = graph.getDefaultParent();
	graph.getModel().beginUpdate();
	try {
	    // Object root = graph.insertVertex(parent, null, "ROOT", 20, 40,
	    // 80, 30, "RECTANGLE");
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
     * Draw execution plan
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
