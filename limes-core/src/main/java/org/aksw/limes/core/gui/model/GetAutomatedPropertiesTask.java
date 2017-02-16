package org.aksw.limes.core.gui.model;

import org.aksw.limes.core.gui.util.TaskResultSerializer;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.matching.DefaultPropertyMapper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.jena.rdf.model.Model;

import javafx.concurrent.Task;

public class GetAutomatedPropertiesTask extends Task<AMapping> {
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
     * constructor
     * @param sinfo
     * @param tinfo
     * @param smodel
     * @param tmodel
     * @param sourceClass
     * @param targetClass
     */
    public GetAutomatedPropertiesTask(KBInfo sinfo, KBInfo tinfo, Model smodel, Model tmodel, String sourceClass, String targetClass) {
        this.sinfo = sinfo;
        this.tinfo = tinfo;
        this.smodel = smodel;
        this.tmodel = tmodel;
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
    }

    /**
     * Get classes for matching.
     */
    @Override
    protected AMapping call() throws Exception {
    Object serializedResult = TaskResultSerializer.getTaskResult(this);
	AMapping result = null; 
	if(serializedResult instanceof AMapping && serializedResult != null){
		result = (AMapping) serializedResult;
	    return result;
	}
        result = getAutomatedPropertyMatching();
        TaskResultSerializer.serializeTaskResult(this, result);
        return result;
    }

    /**
     * loads the classes and displays progress in progress bar
     * @param classes
     * @return
     */
    private AMapping getAutomatedPropertyMatching() {
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

        return result;
    }
    
    public int hashCode() {
      return new HashCodeBuilder(13, 37).
        append(sinfo.getEndpoint()).
        append(tinfo.getEndpoint()).
        append(smodel).
        append(tmodel).
        append(sourceClass).
        append(targetClass).
        toHashCode();
    }
    
}
