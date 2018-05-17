package org.aksw.limes.core.gui.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.gui.util.TaskResultSerializer;
import org.aksw.limes.core.gui.util.sparql.SPARQLHelper;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.io.config.KBInfo;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.jena.rdf.model.Model;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Task for loading classes in {@link org.aksw.limes.core.gui.view.WizardView}
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class GetClassesTask extends Task<List<ClassMatchingNode>> {
	/**
	 * config
	 */
	private final Config config;
	/**
	 * info
	 */
	private final KBInfo info;
	/**
	 * model
	 */
	private final Model model;
	/**
	 * view for displaying progress of task
	 */
	private final TaskProgressView view;
	/**
	 * used for progress
	 */
	private int counter;
	/**
	 * used for progress
	 */
	private int maxSize;
	/**
	 * progress
	 */
	private double progress;

	/**
	 * Constructor
	 * 
	 * @param info
	 * @param model
	 * @param view
	 */
	public GetClassesTask(KBInfo info, Model model, TaskProgressView view, Config config) {
		this.info = info;
		this.model = model;
		this.view = view;
		this.config = config;
	}

	/**
	 * Get classes for matching.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected List<ClassMatchingNode> call() throws Exception {
		List<ClassMatchingNode> result = (List<ClassMatchingNode>) TaskResultSerializer.getTaskResult(this);
		if (result != null) {
			Collections.sort(result, new ClassMatchingNodeComparator());
			return result;
		}
		final Set<String> rootClasses = SPARQLHelper.rootClassesUncached(this.info.getEndpoint(), this.info.getGraph(),
				this.model, this.config);
		this.counter = 0;
		this.progress = 0;
		result = this.getClassMatchingNodes(rootClasses);
		TaskResultSerializer.serializeTaskResult(this, result);
		Collections.sort(result, new ClassMatchingNodeComparator());
		return result;
	}

	/**
	 * loads the classes and displays progress in progress bar
	 * 
	 * @param classes
	 * @return
	 */
	private List<ClassMatchingNode> getClassMatchingNodes(Set<String> classes) {
		this.maxSize += classes.size();
		if (this.isCancelled()) {
			return null;
		}
		final List<ClassMatchingNode> result = new ArrayList<>();
		for (final String class_ : classes) {
			try {
				this.counter++;
				final List<ClassMatchingNode> children = this.getClassMatchingNodes(SPARQLHelper
						.subClassesOfUncached(this.info.getEndpoint(), this.info.getGraph(), class_, this.model));
				result.add(new ClassMatchingNode(new URI(class_), children));
				final double tmpProgress = (double) this.counter / this.maxSize;
				Platform.runLater(() -> {
					GetClassesTask.this.view.getInformationLabel().set("Getting: " + class_);
					if (tmpProgress > GetClassesTask.this.progress) {
						GetClassesTask.this.progress = tmpProgress;
						GetClassesTask.this.view.getProgressBar().setProgress(GetClassesTask.this.progress);
					}
				});
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 
	 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
	 *         studserv.uni-leipzig.de{@literal >} Helper class to sort
	 *         ClassMatchingNodes by name
	 */
	class ClassMatchingNodeComparator implements Comparator<ClassMatchingNode> {
		@Override
		public int compare(ClassMatchingNode a, ClassMatchingNode b) {
			return a.getName().compareToIgnoreCase(b.getName());
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.info.getEndpoint()).append(this.info.getGraph())
				.append(this.model).toHashCode();
	}
}
