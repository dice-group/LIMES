package org.aksw.limes.core.gui.model;

import org.aksw.limes.core.gui.util.TaskResultSerializer;
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
	private final KBInfo sinfo;
	/**
	 * target info
	 */
	private final KBInfo tinfo;
	/**
	 * source model
	 */
	private final Model smodel;
	/**
	 * target model
	 */
	private final Model tmodel;

	private final String sourceClass;

	private final String targetClass;

	/**
	 * constructor
	 * 
	 * @param sinfo
	 * @param tinfo
	 * @param smodel
	 * @param tmodel
	 * @param sourceClass
	 * @param targetClass
	 */
	public GetAutomatedPropertiesTask(KBInfo sinfo, KBInfo tinfo, Model smodel, Model tmodel, String sourceClass,
			String targetClass) {
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
		final Object serializedResult = TaskResultSerializer.getTaskResult(this);
		AMapping result = null;
		if (serializedResult instanceof AMapping && serializedResult != null) {
			result = (AMapping) serializedResult;
			return result;
		}
		result = this.getAutomatedPropertyMatching();
		TaskResultSerializer.serializeTaskResult(this, result);
		return result;
	}

	/**
	 * loads the classes and displays progress in progress bar
	 * 
	 * @param classes
	 * @return
	 */
	private AMapping getAutomatedPropertyMatching() {
		if (this.isCancelled()) {
			return null;
		}
		DefaultPropertyMapper mapper;
		if (this.smodel != null && this.tmodel != null) {
			mapper = new DefaultPropertyMapper(this.smodel, this.tmodel);
		} else {
			mapper = new DefaultPropertyMapper();
		}
		final AMapping result = mapper.getPropertyMapping(this.sinfo.getEndpoint(), this.tinfo.getEndpoint(),
				this.sourceClass, this.targetClass);

		return result;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 37).append(this.sinfo.getEndpoint()).append(this.tinfo.getEndpoint())
				.append(this.smodel).append(this.tmodel).append(this.sourceClass).append(this.targetClass).toHashCode();
	}

}
