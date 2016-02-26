package org.aksw.limes.core.gui.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javafx.application.Platform;
import javafx.concurrent.Task;

import org.aksw.limes.core.gui.util.sparql.SPARQLHelper;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.io.config.KBInfo;

import com.hp.hpl.jena.rdf.model.Model;

public class GetClassesTask extends Task<List<ClassMatchingNode>> {
	private KBInfo info;
	private Model model;
	private TaskProgressView view;
	private int counter;
	private int maxSize;
	private double progress;

	GetClassesTask(KBInfo info, Model model, TaskProgressView view) {
		this.info = info;
		this.model = model;
		this.view = view;
	}

	/**
	 * Get classes for matching.
	 */
	@Override
	protected List<ClassMatchingNode> call() throws Exception {
		Set<String> rootClasses = SPARQLHelper.rootClasses(info.getEndpoint(),
				info.getGraph(), model);
		counter = 0;
		progress = 0;
		List<ClassMatchingNode> result = getClassMatchingNodes(rootClasses);
		return result;
	}

	private List<ClassMatchingNode> getClassMatchingNodes(Set<String> classes) {
		maxSize += classes.size();
		if (isCancelled()) {
			return null;
		}
		List<ClassMatchingNode> result = new ArrayList<ClassMatchingNode>();
		for (String class_ : classes) {
			try {
				counter++;
				List<ClassMatchingNode> children = getClassMatchingNodes(SPARQLHelper
						.subclassesOf(info.getEndpoint(), info.getGraph(), class_, model));
				result.add(new ClassMatchingNode(new URI(class_), children));
				double tmpProgress = (double)counter/maxSize;
				Platform.runLater(new Runnable(){
					@Override
					public void run(){
						view.getInformationLabel().set("Getting: " + class_);
						if(tmpProgress > progress){
							progress = tmpProgress;
							view.getProgressBar().setProgress(progress);
						}
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
}
