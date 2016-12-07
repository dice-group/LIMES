package org.aksw.limes.core.gui.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.aksw.limes.core.gui.util.TaskResultSerializer;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.ml.algorithm.matching.DefaultPropertyMapper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.jena.rdf.model.Model;

import javafx.concurrent.Task;

public class GetAutomatedPropertiesTask extends Task<AMapping> {
    /**
     * config
     */
    private Config config;
    /**
     * source info
     */
    private KBInfo sinfo;
    /**
     * target info
     */
    private KBInfo tinfo;
    /**
     * source model
     */
    private Model smodel;
    /**
     * target model
     */
    private Model tmodel;
    
    private String sourceClass;
    
    private String targetClass;
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
    public GetAutomatedPropertiesTask(KBInfo sinfo, KBInfo tinfo, Model smodel, Model tmodel, String sourceClass, String targetClass, TaskProgressView view, Config config) {
        this.sinfo = sinfo;
        this.tinfo = tinfo;
        this.smodel = smodel;
        this.tmodel = tmodel;
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.view = view;
        this.config = config;
    }

    /**
     * Get classes for matching.
     */
    @Override
    protected AMapping call() throws Exception {
    Object serializedResult = TaskResultSerializer.getTaskResult(this);
	AMapping result = null; 
	if(serializedResult instanceof AMapping && serializedResult != null){
	System.out.println("res: " + serializedResult.toString());
		result = (AMapping) serializedResult;
	    return result;
	}
        counter = 0;
        progress = 0;
        result = getAutomatedPropertyMatching();
        System.out.println("res before serializing: " + result.toString());
        TaskResultSerializer.serializeTaskResult(this, result);
        System.out.println("res after serializing: " + TaskResultSerializer.getTaskResult(this).getClass());
//        Collections.sort(result, new AutomatedClassMatchingNodeComparator());
        return result;
    }

    /**
     * loads the classes and displays progress in progress bar
     * @param classes
     * @return
     */
    private AMapping getAutomatedPropertyMatching() {
//        maxSize += classes.size(); 
        if (isCancelled()) {
            return null;
        }
        DefaultPropertyMapper mapper;
        if(smodel != null && tmodel != null){
        	mapper = new DefaultPropertyMapper(smodel, tmodel);
        }else{
        	mapper = new DefaultPropertyMapper();
        }
        AMapping result = mapper.getPropertyMapping(sinfo.getEndpoint(), tinfo.getEndpoint(), sourceClass, targetClass);
//                counter++;
//                double tmpProgress = (double) counter / maxSize;
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        view.getInformationLabel().set("Getting: " + class_);
//                        if (tmpProgress > progress) {
//                            progress = tmpProgress;
//                            view.getProgressBar().setProgress(progress);
//                        }
//                    }
//                });

        return result;
    }
    
//    /**
//     * 
//     * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
//     *         studserv.uni-leipzig.de{@literal >}
//     *	Helper class to sort AutomatedClassMatchingNodes by name
//     */
//    class AutomatedClassMatchingNodeComparator implements Comparator<AutomatedClassMatchingNode> {
//	    @Override
//	    public int compare(AutomatedClassMatchingNode a, AutomatedClassMatchingNode b) {
//	        return a.getName().compareToIgnoreCase(b.getName());
//	    }
//	}

    public int hashCode() {
      return new HashCodeBuilder(13, 37).
        append(sinfo.getEndpoint()).
        append(tinfo.getEndpoint()).
        append(sinfo.getGraph()).
        append(tinfo.getGraph()).
        append(smodel).
        append(tmodel).
        append(sourceClass).
        append(targetClass).
        toHashCode();
    }
    
}
