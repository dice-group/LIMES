package org.aksw.limes.core.gui.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.aksw.limes.core.gui.util.TaskResultSerializer;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.ml.algorithm.matching.DefaultClassMapper;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.jena.rdf.model.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * Task for loading classes in {@link org.aksw.limes.core.gui.view.WizardView}
 *
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class GetAutomatedClassMatchingTask extends Task<ObservableList<AutomatedClassMatchingNode>> {
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

	/**
	 * constructor
	 * 
	 * @param sinfo
	 * @param tinfo
	 * @param smodel
	 * @param tmodel
	 */
	public GetAutomatedClassMatchingTask(KBInfo sinfo, KBInfo tinfo, Model smodel, Model tmodel) {
		this.sinfo = sinfo;
		this.tinfo = tinfo;
		this.smodel = smodel;
		this.tmodel = tmodel;
	}

	/**
	 * Get classes for matching.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected ObservableList<AutomatedClassMatchingNode> call() throws Exception {
		final Object serializedResult = TaskResultSerializer.getTaskResult(this);
		ObservableList<AutomatedClassMatchingNode> result = null;
		if (serializedResult instanceof ArrayList && serializedResult != null) {
			// Creating tmpRes is necessary to guarantee typesafety
			final ArrayList<AutomatedClassMatchingNode> tmpRes = (ArrayList<AutomatedClassMatchingNode>) serializedResult;
			result = FXCollections.observableArrayList();
			result.addAll(tmpRes);
			return result;
		}
		result = this.getAutomatedClassMatchingNodes();
		// Converting to ArrayList is necessary because ObservableList is not
		// serializable
		TaskResultSerializer.serializeTaskResult(this, new ArrayList<>(result));
		return result;
	}

	/**
	 * loads the classes and displays progress in progress bar
	 *
	 * @param classes
	 * @return
	 */
	private ObservableList<AutomatedClassMatchingNode> getAutomatedClassMatchingNodes() {
		// maxSize += classes.size();
		if (this.isCancelled()) {
			return null;
		}
		DefaultClassMapper mapper;
		final ObservableList<AutomatedClassMatchingNode> result = FXCollections.observableArrayList();
		if (this.smodel != null && this.tmodel != null) {
			mapper = new DefaultClassMapper(this.smodel, this.tmodel);
		} else {
			mapper = new DefaultClassMapper();
		}
		final AMapping classMapping = mapper.getEntityMapping(this.sinfo.getEndpoint(), this.tinfo.getEndpoint(),
				this.sinfo.getId(), this.tinfo.getId());
		for (final String classTarget : classMapping.getMap().keySet()) {
			for (final String classSource : classMapping.getMap().get(classTarget).keySet()) {
				try {
					result.add(new AutomatedClassMatchingNode(new URI(classSource), new URI(classTarget)));
				} catch (final URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.sinfo.getEndpoint()).append(this.tinfo.getEndpoint())
				.append(this.sinfo.getId()).append(this.tinfo.getId()).append(this.smodel).append(this.tmodel)
				.toHashCode();
	}
}
