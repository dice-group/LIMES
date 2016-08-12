package org.aksw.limes.core.gui.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.limes.core.gui.util.sparql.SPARQLHelper;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.io.config.KBInfo;
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
                        .subclassesOf(info.getEndpoint(), info.getGraph(), class_, model));
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
}
