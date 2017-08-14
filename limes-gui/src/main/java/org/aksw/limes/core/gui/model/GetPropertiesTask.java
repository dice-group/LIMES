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
 * Task for loading properties in {@link org.aksw.limes.core.gui.view.WizardView}   
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class GetPropertiesTask extends Task<List<String>> {
    /**
     * info
     */
    private KBInfo info;
    /**
     * model
     */
    private Model model;
    /**
     * class
     */
    private String class_;

    /**
     * Constructor
     * @param info info
     * @param model model
     * @param class_ class_
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
	if(result != null){
	    Collections.sort(result);
	    return result;
	}
        result = new ArrayList<String>();
        for (String property : SPARQLHelper.propertiesUncached(info.getEndpoint(),
                info.getGraph(), class_, model)) {
            result.add(PrefixHelper.abbreviate(property));
        }
        TaskResultSerializer.serializeTaskResult(this, result);
        Collections.sort(result);
        return result;
    }

    public int hashCode() {
      return new HashCodeBuilder(15, 37).
        append(info.getEndpoint()).
        append(info.getGraph()).
        append(model).
        append(class_).
        toHashCode();
    }
}
