package org.aksw.limes.core.measures.measure.graphs;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;
import org.aksw.limes.core.datastrutures.GoldStandard;
import org.aksw.limes.core.evaluation.evaluationDataLoader.DataSetChooser;
import org.aksw.limes.core.evaluation.evaluationDataLoader.EvaluationData;
import org.aksw.limes.core.evaluation.evaluator.EvaluatorType;
import org.aksw.limes.core.evaluation.qualititativeMeasures.QualitativeMeasuresEvaluator;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.measures.mapper.IMapper;
import org.junit.Test;

import java.util.*;

public class SimilarityOptimizer {

    public IMapper createMapper(){
        return new SimilarityTest().createMapper();
    }

    public EvaluationData getData(){
        return DataSetChooser.getData("OAEI2014BOOKS");
    }

    @Test
    public void optimize(){

        EvaluationData data = getData();
        IMapper mapper = createMapper();

        AMapping mapping = mapper.getMapping(
                data.getSourceCache(), data.getTargetCache(),
                "x", "y", "graph_wsl(x, y)", 0.1
        );

        AMapping standard = data.getReferenceMapping();

        double maxNon = Double.NEGATIVE_INFINITY;
        double minIs = Double.POSITIVE_INFINITY;

        for(String s: mapping.getMap().keySet()){

            for(String t: mapping.getMap().get(s).keySet()){
                if(standard.contains(s, t)){
                    minIs = Math.min(minIs, mapping.getConfidence(s, t));
                }else{
                    maxNon = Math.max(maxNon, mapping.getConfidence(s, t));
                }
            }
        }

        List<Entity> real = new ArrayList<>();
        SortedMultiset<Entity> entities = TreeMultiset.create();

        for(String s: mapping.getMap().keySet()){

            for(String t: mapping.getMap().get(s).keySet()){
                double sim = mapping.getConfidence(s, t);
                real.add(new Entity(
                        standard.contains(s, t), sim
                ));

                if(sim >= minIs && sim <= maxNon){
                    entities.add(new Entity(
                            standard.contains(s, t), sim
                    ));
                }

            }
        }

        double threshold = Math.max(minIs-0.01, maxNon);
        double f = Double.NEGATIVE_INFINITY;


        for(Entity e: entities.descendingMultiset().elementSet()){

            int fp = 0;
            int tp = 0;
            int fn = 0;

            for(Entity t: real){
                if(t.sim > e.sim || (e.pos && t.sim == e.sim)){
                    if(e.pos)
                        tp++;
                    else
                        fp++;
                }else{
                    if(e.pos)
                        fn++;
                }
            }

            double g = fmeasure(tp, fp, fn);
            if(g > f){
                f = g;
                if(e.pos)
                    threshold = e.sim;
                else
                    threshold = e.sim + 0.001;
            }


        }

        threshold = (double)Math.ceil(threshold*1000)/1000;

        mapping = mapper.getMapping(
                data.getSourceCache(), data.getTargetCache(),
                "x", "y", "graph_wsl(x, y)", threshold
        );

        GoldStandard gold = new GoldStandard(standard,
                data.getSourceCache().getAllUris(),
                data.getTargetCache().getAllUris());

        QualitativeMeasuresEvaluator evaluator = new QualitativeMeasuresEvaluator();

        Set<EvaluatorType> evalTypes = new HashSet<>();
        evalTypes.add(EvaluatorType.ACCURACY);
        evalTypes.add(EvaluatorType.PRECISION);
        evalTypes.add(EvaluatorType.RECALL);
        evalTypes.add(EvaluatorType.F_MEASURE);

        Map<EvaluatorType, Double> quality = evaluator.evaluate(mapping, gold, evalTypes);

        System.out.println("Threshold: "+threshold);
        for (Map.Entry<EvaluatorType, Double> e : quality.entrySet()) {
            System.out.println(String.format("\t%s: %f", e.getKey().name(), e.getValue()));
        }





    }

    private double fmeasure(int tp, int fp, int fn){
        double precision = (double)tp/(tp + fp);
        double recall = (double)tp/(tp+fn);
        return 2*(precision*recall)/(precision+recall);
    }

    private class Entity implements Comparable<Entity>{

        private boolean pos;
        private double sim;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entity entity = (Entity) o;
            return Double.compare(entity.sim, sim) == 0;
        }

        @Override
        public int hashCode() {

            return Objects.hash(sim);
        }

        public Entity(boolean pos, double sim) {
            this.pos = pos;
            this.sim = sim;
        }

        @Override
        public int compareTo(Entity o) {
            return new Double(sim).compareTo(new Double(o.sim));
        }
    }

}
