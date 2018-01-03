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
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class GetClassesTask extends Task<List<ClassMatchingNode>> {
    /**
     * config
     */
    private Config config;
    /**
     * info
     */
    private KBInfo info;
    /**
     * model
     */
    private Model model;
    /**
     * view for displaying progress of task
     */
    private TaskProgressView view;
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
	if(result != null){
	    Collections.sort(result, new ClassMatchingNodeComparator());
	    return result;
	}
        Set<String> rootClasses = SPARQLHelper.rootClassesUncached(info.getEndpoint(),
                info.getGraph(), model, config);
        counter = 0;
        progress = 0;
        result = getClassMatchingNodes(rootClasses);
        TaskResultSerializer.serializeTaskResult(this, result);
        Collections.sort(result, new ClassMatchingNodeComparator());
        return result;
    }

    /**
     * loads the classes and displays progress in progress bar
     * @param classes
     * @return
     */
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
                        .subClassesOfUncached(info.getEndpoint(), info.getGraph(), class_, model));
                result.add(new ClassMatchingNode(new URI(class_), children));
                double tmpProgress = (double) counter / maxSize;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        view.getInformationLabel().set("Getting: " + class_);
                        if (tmpProgress > progress) {
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
    
    /**
     * 
     * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
     *         studserv.uni-leipzig.de{@literal >}
     *	Helper class to sort ClassMatchingNodes by name
     */
    class ClassMatchingNodeComparator implements Comparator<ClassMatchingNode> {
	    @Override
	    public int compare(ClassMatchingNode a, ClassMatchingNode b) {
	        return a.getName().compareToIgnoreCase(b.getName());
	    }
	}

    public int hashCode() {
      return new HashCodeBuilder(17, 37).
        append(info.getEndpoint()).
        append(info.getGraph()).
        append(model).
        toHashCode();
    }
}
