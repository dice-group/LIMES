package org.aksw.limes.core.measures.measure.customGraphs.description.impl;

import org.aksw.limes.core.io.describe.IResourceDescriptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Class to load the graph descriptor via hook
 *
 * @author Cedric Richter
 */
public class LoadingHook {

    static Log logger = LogFactory.getLog(LoadingHook.class);

    private boolean loaded = false;
    private boolean shouldLoad = false;
    private IResourceDescriptor descriptor;
    private IGraphLoaded loadTo;

    public LoadingHook(IResourceDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    private void executeLoad(){
        if(!shouldLoad)return;
        if(loadTo==null || loaded)return;
        Model description = descriptor.queryDescription();

        if(description.isEmpty()){
            logger.warn(descriptor.getURI()+" cannot be loaded!");
        }

        StmtIterator it = description.listStatements();

        while(it.hasNext())
            parseStatement(it.nextStatement());

        loaded = true;
    }

    public void load(){
        shouldLoad = true;
        executeLoad();
    }

    private void parseStatement(Statement statement){
        String src = statement.getSubject().getURI();
        String property = statement.getPredicate().getURI();
        loadTo.onStatement(src, property, statement.getObject());
    }

    public IResourceDescriptor getDescriptor() {
        return descriptor;
    }

    void injectStart(IGraphLoaded loaded){
        this.loadTo = loaded;
        executeLoad();
    }

}
