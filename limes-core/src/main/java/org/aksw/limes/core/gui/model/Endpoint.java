package org.aksw.limes.core.gui.model;

import org.aksw.limes.core.gui.util.sparql.PrefixHelper;
import org.aksw.limes.core.gui.view.TaskProgressView;
import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.cache.HybridCache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.aksw.limes.core.io.query.QueryModuleFactory;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Represents an LIMES endpoint
 * 
 * @author Manuel Jacob
 */
public class Endpoint {
	private KBInfo info;
	private Cache cache;
	private Model model;
	private ClassMatchingNode currentClass;

	public Endpoint(KBInfo info) {
		this.info = info;
		update();
	}

	public void update() {
		if (info.getEndpoint() == null) {
			return;
		}
		String fileType = info.getEndpoint().substring(info.getEndpoint()
				.lastIndexOf(".") + 1);
		QueryModuleFactory.getQueryModule(fileType, info);
		model = ModelRegistry.getInstance().getMap().get(info.getEndpoint());
	}

	public KBInfo getInfo() {
		return info;
	}

	public Cache getCache() {
		if (cache == null) {
			cache = HybridCache.getData(info);
		}
		return cache;
	}

	public GetClassesTask createGetClassesTask(TaskProgressView view) {
		return new GetClassesTask(info, model, view);
	}

	public ClassMatchingNode getCurrentClass() {
		return currentClass;
	}

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

		info.getPrefixes().put("rdf", PrefixHelper.getURI("rdf"));
		String classAbbr = PrefixHelper.abbreviate(currentClassAsString);
		info.getRestrictions().add(info.getVar() + " rdf:type " + classAbbr);
	}

	public GetPropertiesTask createGetPropertiesTask() {
		return new GetPropertiesTask(info, model, currentClass);
	}
}
