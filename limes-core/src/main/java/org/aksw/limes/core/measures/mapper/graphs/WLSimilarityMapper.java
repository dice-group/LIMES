package org.aksw.limes.core.measures.mapper.graphs;


import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.describe.Descriptor;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.measures.measure.graphs.AGraphSimilarityMeasure;
import org.aksw.limes.core.measures.measure.graphs.gouping.*;
import org.aksw.limes.core.measures.measure.graphs.gouping.labels.*;
import org.aksw.limes.core.measures.measure.graphs.representation.WLDescriptionFactory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WLSimilarityMapper extends AMapper {

    public static final int MEM_SPACE = 5000;

    @Override
    public AMapping getMapping(ACache source, ACache target, String sourceVar, String targetVar, String expression, double threshold) {

        Parser parser = new Parser(expression, threshold);

        MeasureType type = MeasureFactory.getMeasureType(parser.getOperator());
        AGraphSimilarityMeasure measure;

        switch(type){
            case GRAPH_WLS:
                return mapWLSimilarity(source, target, threshold);
            default:
                measure = (AGraphSimilarityMeasure)MeasureFactory.createMeasure(type);
                break;

        }

        measure.setDescriptor1(
                new Descriptor(source.getKbInfo())
        );
        measure.setDescriptor2(
                new Descriptor(target.getKbInfo())
        );

        AMapping mapping = MappingFactory.createDefaultMapping();

        for(String s: source.getAllUris()){
            for(String t: target.getAllUris()){
                double sim = measure.getSimilarity(s, t);
                if(sim >= threshold){
                    mapping.add(s, t, sim);
                }
            }
        }


        return mapping;
    }

    @Override
    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
        return 1000d;
    }

    @Override
    public String getName() {
        return "WLSimilarityMapper";
    }


    protected Future<AMapping> execute(GraphMapperCallable callable){
        FutureTask task = new FutureTask<>(callable);
        task.run();
        return task;
    }

    private List<List<String>> split(List<String> list){
        List<List<String>> out = new ArrayList<>();

        if(list.size() <= MEM_SPACE){
            out.add(list);
        }else{
            List<String> tmp;

            int pos = 0;

            while(pos < list.size()){
                int i = 0;
                tmp = new ArrayList<>(Math.min(list.size() - pos, MEM_SPACE));
                while(i < MEM_SPACE && pos < list.size()){
                    tmp.add(list.get(pos++));
                    i++;
                }
                if(!tmp.isEmpty())
                    out.add(tmp);
            }

        }
        return out;
    }

    private AMapping merge(AMapping A, AMapping B){
        if(A == null)return B;
        if(B == null)return A;

        if(A.size() < B.size()){
            AMapping tmp = A;
            A = B;
            B = tmp;
        }

        for(Map.Entry<String, HashMap<String, Double>> e: B.getMap().entrySet()){
            A.add(e.getKey(), e.getValue());
        }

        return A;
    }

    private AMapping join(List<Future<AMapping>> mappings){
        AMapping mapping = null;

        for(Future<AMapping> mappingFuture: mappings) {
            try {
                mapping = merge(mapping, mappingFuture.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return mapping;
    }

    private IDependendNodeLabelGrouper initDependentGrouper(){
        List<SimDefinition> definitions = new ArrayList<>();
        definitions.add(new SimDefinition(new JaccardSimilarity(), 0.62));
        definitions.add(new SimDefinition(new LevenshteinSimilarity(), 0.4));
        definitions.add(new SimDefinition(new CosineSimilarity(), 0.55));
        return new MaxSimGrouper(definitions, new ExactNodeLabelGrouper());
    }


    private AMapping mapWLSimilarity(ACache source, ACache target, double threshold){
        List<List<String>> sourceUris = split(source.getAllUris());
        List<List<String>> targetUris = split(target.getAllUris());

        ICollectingNodeLabelGrouper sourceGrouper = new CollectingGrouperWrapper(new ExactNodeLabelGrouper());
        IDependendNodeLabelGrouper targetGrouper = initDependentGrouper();
        sourceGrouper.collectOn(targetGrouper.getDependLabelConsumer());

        WLDescriptionFactory sourceFactory = new WLDescriptionFactory(source.getKbInfo(), sourceGrouper);
        WLDescriptionFactory targetFactory = new WLDescriptionFactory(target.getKbInfo(), targetGrouper);

        List<Future<AMapping>> mappings = new ArrayList<>();

        for(List<String> sourceUri: sourceUris){
            for(List<String> targetUri: targetUris){
                mappings.add(
                        execute(
                                new GraphMapperCallable(
                                        2,
                                        sourceFactory.createWLDescriptor(sourceUri),
                                        targetFactory.createWLDescriptor(targetUri),
                                        threshold
                                )
                        )
                );
            }
        }

        return join(mappings);
    }

}
