package org.aksw.limes.core.gui.model;

import org.aksw.limes.core.gui.util.sparql.PrefixHelper;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.aksw.limes.core.io.query.QueryModuleFactory;
import org.apache.jena.rdf.model.Model;

/**
 * Represents an endpoint for graphical representation
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 */
public class Endpoint {
    /**
     * info about knowledgebase
     */
    private KBInfo info;
    /**
     * cache
     */
    private ACache cache;
    /**
     * model
     */
    private Model model;
    /**
     * config
     */
    private Config config;
    /**
     * current class
     */
    private ClassMatchingNode currentClass;

    /**
     * constructor
     * 
     * @param info
     *            info
     */
    public Endpoint(KBInfo info, Config config) {
	this.info = info;
	this.config = config;
	update();
    }

    /**
     * updates the model
     */
    public void update() {
	if (info.getEndpoint() == null) {
	    return;
	}
	String fileType = info.getEndpoint().substring(info.getEndpoint().lastIndexOf(".") + 1);
	//Sometimes local endpoints start with "file://" which can lead to errors
	if(info.getEndpoint().startsWith("file:")){
	    //6 because "file://" is characters long
	    info.setEndpoint(info.getEndpoint().substring(6));
	}
	QueryModuleFactory.getQueryModule(fileType, info);
	model = ModelRegistry.getInstance().getMap().get(info.getEndpoint());
    }

    /**
     * returns kbinfo
     * 
     * @return kbinfo
     */
    public KBInfo getInfo() {
	return info;
    }

    /**
     * returns cache
     * 
     * @return cache
     */
    public ACache getCache() {
	if (cache == null) {
	    for (String key : config.getPrefixes().keySet()) {
		info.getPrefixes().put(key, config.getPrefixes().get(key));
	    }
	    cache = HybridCache.getData(info);
	}
	return cache;
    }

    /**
     * creates a new {@link GetClassesTask}
     * 
     * @param view
     *            TaskProgressView
     * @return task
     */
    public GetClassesTask createGetClassesTask(TaskProgressView view) {
	return new GetClassesTask(info, model, view, config);
    }

    /**
     * returns current class
     * 
     * @return current class
     */
    public ClassMatchingNode getCurrentClass() {
	return currentClass;
    }

    /**
     * sets current class
     * 
     * @param currentClass
     *            currentClass
     */
    public void setCurrentClass(ClassMatchingNode currentClass) {
	this.currentClass = currentClass;
	info.getPrefixes().clear();
	info.getRestrictions().clear();

	if (currentClass == null) {
	    return;
	}

	String currentClassAsString = currentClass.getUri().toString();
	 String[] abbr = PrefixHelper.generatePrefix(currentClassAsString);
	 info.getPrefixes().put(abbr[0], abbr[1]);
	//
	// info.getPrefixes().put("rdf", PrefixHelper.getURI("rdf"));
	String classAbbr = PrefixHelper.abbreviate(currentClassAsString);
	// info.getRestrictions().add(info.getVar() + " rdf:type " + classAbbr);
	info.getRestrictions().add(info.getVar() + " a " + classAbbr);
    }

    /**
     * creates a new {@link GetPropertiesTask}
     * 
     * @return task
     */
    public GetPropertiesTask createGetPropertiesTask() {
	return new GetPropertiesTask(info, model, currentClass);
    }

    public Model getModel() {
	return model;
    }

    public void setModel(Model model) {
	this.model = model;
    }
}
