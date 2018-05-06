package org.aksw.limes.core.io.describe;

import org.aksw.jena_sparql_api.cache.core.QueryExecutionFactoryCacheEx;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontend;
import org.aksw.jena_sparql_api.core.FluentQueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.SparqlServiceReference;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.query.FileQueryModule;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.DatasetDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Descriptor {

    private ResourceDescriptorFactory factory;


    public Descriptor(QueryExecutionFactory qef){
        this.factory = new ResourceDescriptorFactory(qef);
    }

    public Descriptor(KBInfo kb, IConnectionConfig config, CacheFrontend frontend) {
        QueryExecutionFactory qef = initQueryExecution(kb, config);

        if(frontend != null){
            qef = wrapCachedQueryExecution(qef, frontend);
        }

        this.factory = new ResourceDescriptorFactory(qef);
    }

    public Descriptor(KBInfo kb, IConnectionConfig config){
        this(kb, config, null);
    }

    public Descriptor(KBInfo kb){
        this(kb, new DefaultConnetionConfig());
    }

    private FluentQueryExecutionFactory<?> initFactory(KBInfo info){
        if(info.getType().equalsIgnoreCase("N3")){
            new FileQueryModule(info);
            Model model = ModelRegistry.getInstance().getMap().get(info.getEndpoint());
            return FluentQueryExecutionFactory.from(model);
        }else{
            DatasetDescription dd = new DatasetDescription();
            if(info.getGraph() != null) {
                dd.addDefaultGraphURI(info.getGraph());
            }

            SparqlServiceReference ssr = new SparqlServiceReference(info.getEndpoint(), dd);

            return FluentQueryExecutionFactory.http(ssr);
        }
    }

    protected QueryExecutionFactory initQueryExecution(KBInfo kbInfo, IConnectionConfig config) {
        QueryExecutionFactory qef;

        qef =   initFactory(kbInfo)
                .config()
                    .withDelay(config.getRequestDelayInMs(), TimeUnit.MILLISECONDS)
                .end()
                .create();
        return qef;
    }

    protected QueryExecutionFactory wrapCachedQueryExecution(QueryExecutionFactory qef,
                                                             CacheFrontend frontend){
        return new QueryExecutionFactoryCacheEx(qef, frontend);

    }

    public IResourceDescriptor describe(String s){
        return this.factory.createDescriptor(s);
    }

    public List<IResourceDescriptor> describeAll(Iterable<String> uris){
        List<IResourceDescriptor> descriptors = new ArrayList<>();
        for(String uri: uris){
            descriptors.add(this.describe(uri));
        }
        return descriptors;
    }

    public Stream<IResourceDescriptor> describeAllStream(Iterable<String> uris){
        return StreamSupport.stream(uris.spliterator(), false).map(
           uri -> this.describe(uri)
        );
    }


}
