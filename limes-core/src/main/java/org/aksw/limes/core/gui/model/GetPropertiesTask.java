package org.aksw.limes.core.gui.model;

import java.util.ArrayList;
import java.util.List;

import org.aksw.limes.core.gui.util.sparql.PrefixHelper;
import org.aksw.limes.core.gui.util.sparql.SPARQLHelper;
import org.aksw.limes.core.io.config.KBInfo;
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
    private ClassMatchingNode class_;

    /**
     * Constructor
     * @param info info
     * @param model model
     * @param class_ class_
     */
    public GetPropertiesTask(KBInfo info, Model model, ClassMatchingNode class_) {
        this.info = info;
        this.model = model;
        this.class_ = class_;
    }

    /**
     * calls the task and loads the properties
     */
    @Override
    protected List<String> call() throws Exception {
        List<String> result = new ArrayList<String>();
        for (String property : SPARQLHelper.properties(info.getEndpoint(),
                info.getGraph(), class_.getUri().toString(), model)) {
            result.add(PrefixHelper.abbreviate(property));
        }
        return result;
    }
}
