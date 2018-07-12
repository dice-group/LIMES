package org.aksw.limes.core.io.describe;

import org.aksw.limes.core.io.cache.Instance;
import org.aksw.limes.core.io.cache.MemoryCache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.query.CsvQueryModule;
import org.aksw.limes.core.io.query.FileQueryModule;
import org.aksw.limes.core.io.query.ModelRegistry;
import org.apache.jena.rdf.model.*;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BunchDescriptor implements IDescriptor {

    private IDescriptor backend;
    private int cachedRecursion;
    private Set<String> bunch;
    private KBInfo info;
    private Map<String, Model> cache;

    public BunchDescriptor(KBInfo info, Set<String> bunch, int recursion){
        this(info, new Descriptor(info), bunch, recursion);
    }


    public BunchDescriptor(KBInfo info, IDescriptor backend, Set<String> bunch, int recursion){
        this.backend = backend;
        this.cachedRecursion = recursion;
        this.bunch = bunch;
        this.info = info;
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

    private Model loadComplete(){
        String name = info.getType();
        if(name.equalsIgnoreCase("N3") || name.toLowerCase().startsWith("nt") ||
                name.toLowerCase().startsWith("n-triple") ||
                name.toLowerCase().startsWith("turtle") || name.toLowerCase().startsWith("ttl") ||
                name.toLowerCase().startsWith("rdf") || name.toLowerCase().startsWith("xml")) {
            new FileQueryModule(info);
            Model model = ModelRegistry.getInstance().getMap().get(info.getEndpoint());
            return model;
        }else if(name.equalsIgnoreCase("csv")){
            Model model = processCSV(info);
            return model;
        }else{
            return null;
        }
    }


    private void initCache(){
        cache = new HashMap<>(bunch.size());

        Model complete = loadComplete();
        if(complete == null)return;

        Stack<Discover> stack = new Stack<>();

        for(String s: bunch){
            Resource r = complete.getResource(s);

            if(r == null)continue;

            stack.push(new Discover(s, r, 0));
            cache.put(s, ModelFactory.createDefaultModel());
        }

        while (!stack.isEmpty()){
            Discover discover = stack.pop();

            if(discover.depth > cachedRecursion){
                continue;
            }

            Resource r = discover.resource;

            Model m = cache.get(discover.root);
            StmtIterator iterator = r.listProperties();

            while(iterator.hasNext()){
                Statement stmt = iterator.nextStatement();
                m.add(stmt);

                if(stmt.getObject().isResource()){
                    stack.push(new Discover(discover.root, stmt.getObject().asResource(), discover.depth+1));
                }
            }


        }



    }


    private Model load(String s, int recursion){
        if(cache == null)initCache();

        if(recursion == cachedRecursion && cache.containsKey(s)){
            return cache.get(s);
        }

        return backend.describe(s, recursion).queryDescription();
    }


    @Override
    public IResourceDescriptor describe(String s) {
        return describe(s, 0);
    }

    @Override
    public IResourceDescriptor describe(String s, int recursion) {
        return new IResourceDescriptor() {
            @Override
            public String getURI() {
                return s;
            }

            @Override
            public Model queryDescription() {
                return load(s, recursion);
            }
        };
    }

    public List<IResourceDescriptor> describeAll(Iterable<String> uris, int recursion){
        List<IResourceDescriptor> descriptors = new ArrayList<>();
        for(String uri: uris){
            descriptors.add(this.describe(uri, recursion));
        }
        return descriptors;
    }

    public List<IResourceDescriptor> describeAll(Iterable<String> uris){
        return describeAll(uris, 0);
    }

    public Stream<IResourceDescriptor> describeAllStream(Iterable<String> uris, int recursion){
        return StreamSupport.stream(uris.spliterator(), false).map(
                uri -> this.describe(uri, recursion)
        );
    }

    public Stream<IResourceDescriptor> describeAllStream(Iterable<String> uris){
        return describeAllStream(uris, 0);
    }


    private class Discover{

        private String root;
        private Resource resource;
        private int depth;

        public Discover(String root, Resource resource, int depth) {
            this.root = root;
            this.resource = resource;
            this.depth = depth;
        }
    }

}
