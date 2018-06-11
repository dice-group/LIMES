package org.aksw.limes.core.io.describe;

import org.aksw.jena_sparql_api.cache.core.QueryExecutionFactoryCacheEx;
import org.aksw.jena_sparql_api.cache.extra.CacheFrontend;
import org.aksw.jena_sparql_api.core.FluentQueryExecutionFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.core.SparqlServiceReference;
import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.query.CsvQueryModule;
import org.aksw.limes.core.io.query.FileQueryModule;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.core.DatasetDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
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


    private Model processCSV(KBInfo info){
        CsvQueryModule module = new CsvQueryModule(info);
        MemoryCache cache = new MemoryCache();
        module.fillAllInCache(cache);

        Model model = ModelFactory.createDefaultModel();

        for(Instance instance: cache.getAllInstances()){
            Resource resource = model.createResource(instance.getUri());
            for(String prop: instance.getAllProperties()) {
                Property property = model.createProperty("", prop);
                TreeSet<String> properties = instance.getProperty(prop);

                if(properties.size() == 1){
                    for(String s: properties) {
                        Literal literal = model.createLiteral(s, false);
                        model.add(
                                model.createStatement(resource, property, literal)
                        );
                    }
                }else{
                    List<RDFNode> list = new ArrayList<>();
                    for(String s: properties){
                        list.add(model.createLiteral(s, false));
                    }
                    model.add(
                      model.createStatement(resource, property, model.createList(list.iterator()))
                    );
                }

            }
        }


        return model;
    }


    private FluentQueryExecutionFactory<?> initFactory(KBInfo info){
        String name = info.getType();
        if(name.equalsIgnoreCase("N3") || name.toLowerCase().startsWith("nt") ||
            name.toLowerCase().startsWith("n-triple") ||
                name.toLowerCase().startsWith("turtle") || name.toLowerCase().startsWith("ttl") ||
                name.toLowerCase().startsWith("rdf") || name.toLowerCase().startsWith("xml")) {
            new FileQueryModule(info);
            Model model = ModelRegistry.getInstance().getMap().get(info.getEndpoint());
            return FluentQueryExecutionFactory.from(model);
        }else if(name.equalsIgnoreCase("csv")){
            Model model = processCSV(info);
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
