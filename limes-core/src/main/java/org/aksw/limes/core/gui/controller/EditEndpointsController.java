package org.aksw.limes.core.gui.controller;


import static org.aksw.limes.core.gui.util.SourceOrTarget.SOURCE;
import static org.aksw.limes.core.gui.util.SourceOrTarget.TARGET;

import org.aksw.limes.core.gui.model.Config;
import org.aksw.limes.core.gui.model.Endpoint;
import org.aksw.limes.core.gui.util.SourceOrTarget;
import org.aksw.limes.core.gui.view.EditEndpointsView;
import org.aksw.limes.core.gui.view.IEditView;
import org.aksw.limes.core.io.config.KBInfo;

/**
 * Controls EditEndpointsView
 *
 * @author Manuel Jacob, Felix Brei
 */
public class EditEndpointsController implements IEditController {
    /**
     * Config of the LIMES Query
     */
    private Config config;

    private EditEndpointsView view;

    /**
     * Constructor
     *
     * @param config
     *         Config of Limes Query
     * @param view
     *         corresponding EditEndpointsView
     */
    EditEndpointsController(Config config, EditEndpointsView view) {
        this.config = config;
        this.view = view;
        view.setController(this);
    }

    @Override
    public void load() {
        KBInfo sourceEndpoint = config.getSourceInfo();
        view.setFields(SOURCE, sourceEndpoint.getEndpoint(), sourceEndpoint.getId(),
                sourceEndpoint.getGraph(), Integer.toString(sourceEndpoint.getPageSize()));
        KBInfo targetEndpoint = config.getTargetInfo();
        view.setFields(TARGET, targetEndpoint.getEndpoint(), targetEndpoint.getId(),
                targetEndpoint.getGraph(), Integer.toString(targetEndpoint.getPageSize()));
    }

    /**
     * Saves edited Endpoint
     *
     * @param source
     *         if True Source else Target
     * @param idNamespace
     *         Namespace of Endpoint
     * @param graph
     *         TODO
     * @param pageSize
     *         length of Query
     * @param.getEndpoint()URL URL of the Endpoint
     */
    public void save(SourceOrTarget sourceOrTarget, String endpointURL,
                     String idNamespace, String graph, String pageSize) {
        Endpoint endpoint = sourceOrTarget == SOURCE ? config
                .getSourceEndpoint() : config.getTargetEndpoint();
        KBInfo info = endpoint.getInfo();
        info.setEndpoint(endpointURL);
        info.setId(idNamespace);
        info.setGraph(graph);
        // TODO: Validierung
        info.setPageSize(Integer.parseInt(pageSize));
        endpoint.update();
    }

    @Override
    public IEditView getView() {
        return view;
    }
}
