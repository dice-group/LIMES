package org.aksw.limes.core.gui.model;

import com.hp.hpl.jena.rdf.model.Model;
import javafx.concurrent.Task;
import org.aksw.limes.core.gui.util.sparql.PrefixHelper;
import org.aksw.limes.core.gui.util.sparql.SPARQLHelper;
import org.aksw.limes.core.io.config.KBInfo;

import java.util.ArrayList;
import java.util.List;

public class GetPropertiesTask extends Task<List<String>> {
    private KBInfo info;
    private Model model;
    private ClassMatchingNode class_;

    public GetPropertiesTask(KBInfo info, Model model, ClassMatchingNode class_) {
        this.info = info;
        this.model = model;
        this.class_ = class_;
    }

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
