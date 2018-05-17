package org.aksw.limes.core.gui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aksw.limes.core.gui.util.TaskResultSerializer;
import org.aksw.limes.core.gui.util.sparql.PrefixHelper;
import org.aksw.limes.core.gui.util.sparql.SPARQLHelper;
import org.aksw.limes.core.io.config.KBInfo;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.jena.rdf.model.Model;

import javafx.concurrent.Task;

/**
 * Task for loading properties in
 * {@link org.aksw.limes.core.gui.view.WizardView}
 * 
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class GetPropertiesTask extends Task<List<String>> {
	/**
	 * info
	 */
	private final KBInfo info;
	/**
	 * model
	 */
	private final Model model;
	/**
	 * class
	 */
	private final String class_;

	/**
	 * Constructor
	 * 
	 * @param info
	 *            info
	 * @param model
	 *            model
	 * @param class_
	 *            class_
	 */
	public GetPropertiesTask(KBInfo info, Model model, String class_) {
		this.info = info;
		this.model = model;
		this.class_ = class_;
	}

	/**
	 * calls the task and loads the properties
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected List<String> call() throws Exception {
		List<String> result = (List<String>) TaskResultSerializer.getTaskResult(this);
		if (result != null) {
			Collections.sort(result);
			return result;
		}
		result = new ArrayList<>();
		for (final String property : SPARQLHelper.propertiesUncached(this.info.getEndpoint(), this.info.getGraph(),
				this.class_, this.model)) {
			result.add(PrefixHelper.abbreviate(property));
		}
		TaskResultSerializer.serializeTaskResult(this, result);
		Collections.sort(result);
		return result;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(15, 37).append(this.info.getEndpoint()).append(this.info.getGraph())
				.append(this.model).append(this.class_).toHashCode();
	}
}
