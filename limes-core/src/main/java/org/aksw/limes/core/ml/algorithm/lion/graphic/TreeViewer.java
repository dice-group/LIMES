package org.aksw.limes.core.ml.algorithm.lion.graphic;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.aksw.limes.core.ml.algorithm.lion.SearchTreeNode;

public class TreeViewer extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TreeViewer frame = new TreeViewer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TreeViewer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 584, 318);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		///////////////////////////
		//create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        //create the child nodes
        DefaultMutableTreeNode vegetableNode = new DefaultMutableTreeNode("Vegetables");
        DefaultMutableTreeNode fruitNode = new DefaultMutableTreeNode("Fruits");
 
        //add the child nodes to the root node
        root.add(vegetableNode);
        root.add(fruitNode);
         
        //create the tree by passing in the root node

        JTree tree = new JTree(root);
        
        getContentPane().add(tree);
         
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Refinements Tree");       
        this.pack();
        this.setVisible(true);
	}
	private DefaultMutableTreeNode createTreeNode(SearchTreeNode node)
	{
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(node.toString());
		return newNode;
	}
	private DefaultMutableTreeNode createGraphicalTree(TreeSet<SearchTreeNode> tree,DefaultMutableTreeNode graphRoot)
	{
		for (SearchTreeNode node : tree) {
			visitAllNodes(graphRoot,node);
		}
		return graphRoot;
	}
	private void visitAllNodes(DefaultMutableTreeNode graphRoot,SearchTreeNode dataNode) {
	    // node is visited exactly once
		if(dataNode.getParent().getSpec().toString().equals(graphRoot.toString()))
		{
			graphRoot.add(new DefaultMutableTreeNode(dataNode.getSpec().toString()));
			return;
		}
	    if (graphRoot.getChildCount() >= 0) {
	        for (Enumeration e=graphRoot.children(); e.hasMoreElements(); ) {
	        	DefaultMutableTreeNode n = (DefaultMutableTreeNode)e.nextElement();
	            visitAllNodes(n,dataNode);
	        }
	    }
	}
	private DefaultMutableTreeNode createTree2(DefaultMutableTreeNode graphRoot,SearchTreeNode dataRoot,TreeSet<SearchTreeNode> tree)
	{
		/*List<SearchTreeNode> checkedDataNodes =new ArrayList<SearchTreeNode>();// for the nodes already checked
		List<DefaultMutableTreeNode> checkedgraphNodes =new ArrayList<DefaultMutableTreeNode>();
		
		checkedDataNodes.add(dataRoot);
		checkedgraphNodes.add(graphRoot);
		
		for (SearchTreeNode checkedNode : checkedDataNodes) {
			for (SearchTreeNode treeNode : tree) {
				if(treeNode.getParent().getSpec().toString().equals(checkedNode.getSpec().toString())) // it is one of its children
				{	// create new graph tree node
					DefaultMutableTreeNode newGraphNode= new DefaultMutableTreeNode(treeNode.toString());
					graphRoot.add(newGraphNode);
				}
			}
			checkedDataNodes.remove(checkedNode);//remove it form the list
		}*/
		
		return graphRoot;
	}

}
