package org.aksw.limes.core.gui.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

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
	 * matches properties of the form " prefix:property "
	 */
	public static final String propertyRegex = "\\s+\\w+:\\w+\\s+";
	/**
	 * info about knowledgebase
	 */
	private final KBInfo info;
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
	private final Config config;
	/**
	 * current class
	 */
	private ClassMatchingNode currentClass;

	/**
	 * constructor
	 *
	 * @param info
	 *            info
	 * @param config
	 *            config
	 */
	public Endpoint(KBInfo info, Config config) {
		this.info = info;
		this.config = config;
		this.update();
	}

	/**
	 * updates the model
	 */
	public void update() {
		if (this.info.getEndpoint() == null) {
			return;
		}
		// TODO fix this for sparql endpoints
		final String fileType = this.info.getEndpoint().substring(this.info.getEndpoint().lastIndexOf(".") + 1);
		// Sometimes local endpoints start with "file://" which can lead to
		// errors
		if (this.info.getEndpoint().startsWith("file://")) {
			// 6 because "file://" is characters long
			this.info.setEndpoint(this.info.getEndpoint().substring(6));
		} else if (this.info.getEndpoint().startsWith("file:")) {
			// 5 because "file:" is characters long
			this.info.setEndpoint(this.info.getEndpoint().substring(5));
		}
		System.err.println("Endpoint: " + this.info.getEndpoint());
		QueryModuleFactory.getQueryModule(fileType, this.info);
		this.model = ModelRegistry.getInstance().getMap().get(this.info.getEndpoint());
	}

	/**
	 * returns kbinfo
	 *
	 * @return kbinfo
	 */
	public KBInfo getInfo() {
		return this.info;
	}

	/**
	 * returns cache
	 *
	 * @return cache
	 */
	public ACache getCache() {
		for (final String key : this.config.getPrefixes().keySet()) {
			this.info.getPrefixes().put(key, this.config.getPrefixes().get(key));
		}
		this.cache = HybridCache.getData(this.info);
		return this.cache;
	}

	/**
	 * creates a new {@link GetClassesTask}
	 *
	 * @param view
	 *            TaskProgressView
	 * @return task
	 */
	public GetClassesTask createGetClassesTask(TaskProgressView view) {
		return new GetClassesTask(this.info, this.model, view, this.config);
	}

	/**
	 * returns current class
	 *
	 * @return current class
	 */
	public ClassMatchingNode getCurrentClass() {
		return this.currentClass;
	}

	/**
	 * sets current class
	 *
	 * @param currentClass
	 *            currentClass
	 */
	public void setCurrentClass(ClassMatchingNode currentClass) {
		this.currentClass = currentClass;
		if (currentClass == null) {
			return;
		}
		final String currentClassAsString = currentClass.getUri().toString();
		this.setCurrentClassAsString(currentClassAsString);
	}

	public void setCurrentClassAsString(String currentClass) {
		try {
			this.currentClass = new ClassMatchingNode(new URI(currentClass), null);
		} catch (final URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final String[] abbr = PrefixHelper.generatePrefix(currentClass);
		this.info.getPrefixes().put(abbr[0], abbr[1]);
		// info.getPrefixes().put("rdf", PrefixHelper.getURI("rdf"));
		final String classAbbr = PrefixHelper.abbreviate(currentClass);
		// info.getRestrictions().add(info.getVar() + " rdf:type " + classAbbr);

		// Else an unneccessary second more abstract restriction gets added
		boolean alreadyContains = false;
		for (final String restr : this.info.getRestrictions()) {
			if (restr.matches(Pattern.quote(this.info.getVar()) + propertyRegex + classAbbr)) {
				alreadyContains = true;
			}
		}
		// This usually only gets called when the ConfigurationWizard is used
		if (!alreadyContains) {
			this.info.getRestrictions().add(this.info.getVar() + " a " + classAbbr);
		}
		final String[] abbrSource = PrefixHelper.generatePrefix(currentClass);
		this.config.getPrefixes().put(abbrSource[0], abbrSource[1]);
	}

	/**
	 * creates a new {@link GetPropertiesTask}
	 *
	 * @return task
	 */
	public GetPropertiesTask createGetPropertiesTask() {
		return new GetPropertiesTask(this.info, this.model, this.currentClass.getUri().toString());
	}

	public Model getModel() {
		return this.model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
