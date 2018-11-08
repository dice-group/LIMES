package org.aksw.limes.core.gui.controller;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.metric.Measure;
import org.aksw.limes.core.gui.model.metric.MetricParser;
import org.aksw.limes.core.gui.model.metric.Node;
import org.aksw.limes.core.gui.model.metric.Operator;
import org.aksw.limes.core.gui.model.metric.Output;
import org.aksw.limes.core.gui.model.metric.Property;
import org.aksw.limes.core.gui.util.SourceOrTarget;
import org.aksw.limes.core.gui.view.ToolBox;
import org.aksw.limes.core.gui.view.graphBuilder.GraphBuildView;
import org.aksw.limes.core.gui.view.graphBuilder.NodeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller of GraphBuildView, controls drawing and moving of Node Elements
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class GraphBuildController {

	private static final Logger logger = LoggerFactory.getLogger(GraphBuildController.class);

	/**
	 * CurrentConfig of the Limes query
	 */
	private Config currentConfig;
	/**
	 * Corresponding GraphBuildView
	 */
	private final GraphBuildView graphBuildView;
	/**
	 * Toolbox to calculate offset of nodes
	 */
	private final ToolBox toolbox;

	/**
	 * Constructor
	 * 
	 * @param currentConfig
	 *            config
	 * @param view
	 *            corresponding view
	 * @param toolbox
	 *            toolbox of mainview
	 */
	public GraphBuildController(Config currentConfig, GraphBuildView view, ToolBox toolbox) {
		this.currentConfig = currentConfig;
		this.graphBuildView = view;
		this.toolbox = toolbox;
	}

	/**
	 * Constructor
	 * 
	 * @param view
	 *            view
	 * @param toolbox
	 *            toolbox
	 */
	public GraphBuildController(GraphBuildView view, ToolBox toolbox) {

		this.graphBuildView = view;
		this.toolbox = toolbox;
	}

	/**
	 * Set currentConfig
	 *
	 * @param currentConfig
	 *            CurrentConfig of Limes query
	 */
	public void setConfig(Config currentConfig) {
		this.currentConfig = currentConfig;
	}

	/**
	 * Takes the configuration and generates the Graph
	 */
	@SuppressWarnings("unchecked")
	public void generateGraphFromConfig() {
		this.graphBuildView.nodeList.clear();
		this.graphBuildView.edited = true;
		this.graphBuildView.draw();
		final Output out = this.currentConfig.getMetric();
		final ArrayList<NodeView> newNodeList = new ArrayList<>();
		final NodeView outView = new NodeView(200, 200, NodeView.OUTPUT, out.id, this.graphBuildView, out);
		newNodeList.add(outView);
		this.graphBuildView.nodeList = this.drawChildRek(outView, outView.nodeData.getChilds().get(0), newNodeList);
		this.graphBuildView.reversedNodeList = (ArrayList<NodeView>) this.graphBuildView.nodeList.clone();
		this.layoutGraph();
	}

	/**
	 * Takes the graph and writes the information to the config
	 */
	public void setConfigFromGraph() {
		// Get the output node manually, since it is not guaranteed to be the
		// first in the list
		final NodeView output = this.getOutputNode();
		this.currentConfig.setAcceptanceThreshold(output.nodeData.param1);
		this.currentConfig.setVerificationThreshold(output.nodeData.param2);
		this.currentConfig
				.setMetricExpression(MetricParser
						.parse(output.nodeData.toString(),
								this.currentConfig.getSourceInfo().getVar().replaceAll("\\?", ""), this.currentConfig)
						.toString());
	}

	/**
	 * Get output node of the graph
	 * 
	 * @return NodeView or null if output node cannot be found
	 */
	public NodeView getOutputNode() {
		for (int i = 0; i < this.graphBuildView.nodeList.size(); i++) {
			if (this.graphBuildView.nodeList.get(i).nodeShape == NodeView.OUTPUT) {
				return this.graphBuildView.nodeList.get(i);
			}
		}
		logger.error("Could not find output node!");
		return null;
	}

	/**
	 * Delete current graph
	 */
	public void deleteGraph() {
		this.graphBuildView.nodeList.clear();
		this.graphBuildView.edited = true;
		this.graphBuildView.addNode(300, 300, 2, new Output());
		this.graphBuildView.draw();
	}

	/**
	 * Refresh the layout of the graph
	 */
	public void layoutGraph() {
		final double h = this.graphBuildView.getHeight();
		final double w = this.graphBuildView.getWidth();
		final List<Integer> stages = new ArrayList<>();
		List<Integer> stages2;
		this.graphBuildView.nodeList.forEach(e -> {
			int i = 0;
			NodeView test = e;
			while (test.parent != null) {
				test = test.parent;
				i++;
			}
			try {
				stages.get(i);
			} catch (final IndexOutOfBoundsException exception) {
				try {
					stages.add(i, 0);
				} catch (final IndexOutOfBoundsException e2) {
					this.rekListAdder(i, stages);
				}
			}
			stages.set(i, Integer.sum(Integer.max(stages.get(i).intValue(), 0), 1));

		});
		stages2 = new ArrayList<>(stages);
		this.graphBuildView.nodeList.forEach(e -> {
			int i = 0;
			final int hInt = stages.size();
			NodeView test = e;
			while (test.parent != null) {
				test = test.parent;
				i++;
			}
			e.setXY((int) (w - w * stages2.get(i) / stages.get(i) + w / (2 * stages.get(i)) - e.getWidth()
					+ this.toolbox.getWidth() / 2), (int) (h + e.getHeight() - h * (i + 1) / hInt));
			stages2.set(i, stages2.get(i) - 1);
		});
		this.graphBuildView.draw();
	}

	/**
	 * Helper function for layout graph
	 *
	 * @param index
	 *            index to set to 0
	 * @param stages
	 *            List of nodes per generation
	 * @return modified Stages
	 */
	private List<Integer> rekListAdder(int index, List<Integer> stages) {
		try {
			stages.add(index - 1, 0);
			stages.add(index, 0);
			return stages;
		} catch (final IndexOutOfBoundsException e) {
			this.rekListAdder(index - 1, stages);
			stages.add(index, 0);
			return stages;
		}
	}

	/**
	 * Recursive function to link
	 * {@link org.aksw.core.gui.view.graphBuilder.NodeView} according to the
	 * underlying data model
	 *
	 * @param parent
	 *            Parent NodeView
	 * @param node
	 *            data model
	 * @param nodeList
	 *            NodeList to be modified
	 * @return modified NodeList
	 */
	private ArrayList<NodeView> drawChildRek(NodeView parent, Node node, ArrayList<NodeView> nodeList) {
		int nodeShape;
		if (new Measure("").identifiers().contains(node.id)) {
			nodeShape = NodeView.METRIC;
		} else if (Operator.identifiers.contains(node.id)) {
			nodeShape = NodeView.OPERATOR;
		} else {
			final Property castedNode = (Property) node;
			if (castedNode.getOrigin() == SourceOrTarget.SOURCE) {
				nodeShape = NodeView.SOURCE;
			} else {
				nodeShape = NodeView.TARGET;
			}
		}
		final NodeView thisNode = new NodeView(200, 200, nodeShape, node.id, this.graphBuildView, node);

		nodeList.add(thisNode);
		parent.addChildWithOutDataLinking(thisNode);
		if (node.getMaxChilds() == 0) {
			return nodeList;
		} else {
			this.drawChildRek(thisNode, node.getChilds().get(0), nodeList);
			this.drawChildRek(thisNode, node.getChilds().get(1), nodeList);
			return nodeList;
		}
	}

	public Config getConfig() {
		return this.currentConfig;
	}

}
