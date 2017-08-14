package org.aksw.limes.core.gui.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.aksw.limes.core.gui.util.TaskResultSerializer;
import org.aksw.limes.core.gui.view.TaskProgressView;
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
	/**
	 * constructor
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
		Object serializedResult = TaskResultSerializer.getTaskResult(this);
		ObservableList<AutomatedClassMatchingNode> result = null;
		if (serializedResult instanceof ArrayList && serializedResult != null) {
			// Creating tmpRes is necessary to guarantee typesafety
			ArrayList<AutomatedClassMatchingNode> tmpRes = (ArrayList<AutomatedClassMatchingNode>) serializedResult;
			result = FXCollections.observableArrayList();
			result.addAll(tmpRes);
			return result;
		}
		result = getAutomatedClassMatchingNodes();
		// Converting to ArrayList is necessary because ObservableList is not
		// serializable
		TaskResultSerializer.serializeTaskResult(this, new ArrayList<AutomatedClassMatchingNode>(result));
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
		if (isCancelled()) {
			return null;
		}
		DefaultClassMapper mapper;
		ObservableList<AutomatedClassMatchingNode> result = FXCollections.observableArrayList();
		if (smodel != null && tmodel != null) {
			mapper = new DefaultClassMapper(smodel, tmodel);
		} else {
			mapper = new DefaultClassMapper();
		}
		AMapping classMapping = mapper.getEntityMapping(sinfo.getEndpoint(), tinfo.getEndpoint(), sinfo.getId(),
				tinfo.getId());
		for (String classTarget : classMapping.getMap().keySet()) {
			for (String classSource : classMapping.getMap().get(classTarget).keySet()) {
				try {
					result.add(new AutomatedClassMatchingNode(new URI(classSource), new URI(classTarget)));
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(sinfo.getEndpoint()).append(tinfo.getEndpoint()).append(sinfo.getId())
				.append(tinfo.getId()).append(smodel).append(tmodel).toHashCode();
	}
}
