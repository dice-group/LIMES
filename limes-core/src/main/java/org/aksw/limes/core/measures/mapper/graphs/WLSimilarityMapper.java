package org.aksw.limes.core.measures.mapper.graphs;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.LinkedSparseMatrix;
import org.aksw.limes.core.io.cache.ACache;
import org.aksw.limes.core.io.config.KBInfo;
import org.aksw.limes.core.io.describe.Descriptor;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.io.parser.Parser;
import org.aksw.limes.core.measures.mapper.AMapper;
import org.aksw.limes.core.measures.measure.MeasureFactory;
import org.aksw.limes.core.measures.measure.MeasureType;
import org.aksw.limes.core.measures.measure.graphs.AGraphSimilarityMeasure;
import org.aksw.limes.core.measures.measure.graphs.gouping.INodeLabelGrouper;
import org.aksw.limes.core.measures.measure.graphs.gouping.NodeLabelGrouperFactory;
import org.aksw.limes.core.measures.measure.graphs.representation.WLModelIteration;
import org.aksw.limes.core.measures.measure.graphs.representation.WLModelRepresentation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WLSimilarityMapper extends AMapper {
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


    private int index(String name, Map<String, Integer> index){
        if(index.containsKey(name)){
            return index.get(name);
        }
        int count = index.get("#counter#");
        index.put("#counter#", count+1);
        index.put(name, count);
        return count;
    }

    private List<WLQuad> process(WLModelRepresentation representation,int row, int iteration, Map<String, Integer> index){
        List<WLQuad> out = new ArrayList<>();

        for(int it = 0; it <= iteration; it++){
            Multiset<String> wlIt = representation.getIteration(it).getRepresentation();
            for(String label: wlIt.elementSet()){
                int count = wlIt.count(label);
                out.add(new WLQuad(it, row, index(label, index), count));
            }
        }

        return out;
    }



    private List<AbstractMatrix> mapMatrix(
            KBInfo info,
            List<String> uris,
            int iteration,
            INodeLabelGrouper grouper,
            Map<String, Integer> index
    ){

        List<WLModelRepresentation> list = new Descriptor(info)
                                            .describeAllStream(uris)
                                            .map(res -> new WLModelRepresentation(res.queryDescription(), grouper))
                                            .collect(Collectors.toList());

        List<WLQuad> quads = new ArrayList<>();
        for(int row = 0; row < list.size(); row++){
            quads.addAll(
                    process(
                            list.get(row),
                            row,
                            iteration,
                            index
                    )
            );
        }

        List<AbstractMatrix> matrices = new ArrayList<>();
        for(int i = 0; i <= iteration; i++){
            matrices.add(new FlexCompRowMatrix(list.size(), index.get("#counter#")));
        }

        for(WLQuad quad: quads){
            AbstractMatrix matrixSparse = matrices.get(quad.iteration);
            matrixSparse.set(quad.row, quad.col, quad.entry);
        }


        return matrices;

    }

    private FlexCompRowMatrix reshapeCol(FlexCompRowMatrix A, AbstractMatrix B){
        if(A.numColumns() < B.numColumns()){
            FlexCompRowMatrix out = new FlexCompRowMatrix(A.numRows(), B.numColumns());
            for(int r = 0; r < A.numRows(); r++){
                for(VectorEntry e: A.getRow(r)){
                    out.set(r, e.index(), e.get());
                }
            }
            return out;
        }
        return A;
    }

    private FlexCompRowMatrix castOrTransform(AbstractMatrix matrix){
        if(matrix instanceof FlexCompRowMatrix){
            return (FlexCompRowMatrix)matrix;
        }else{
            return new FlexCompRowMatrix(matrix);
        }
    }

    private AbstractMatrix dotProduct(AbstractMatrix A, AbstractMatrix B){
        DenseMatrix out = new DenseMatrix(A.numRows(), B.numRows());
        FlexCompRowMatrix rA = reshapeCol(castOrTransform(A), B);
        FlexCompRowMatrix rB = reshapeCol(castOrTransform(B), A);

        for(int b = 0; b < rB.numRows(); b++){
            Vector bVec = rB.getRow(b);
            Vector oVec = new DenseVector(rA.numRows());
            oVec = rA.multAdd(1, bVec, oVec);

            for(VectorEntry e: oVec){
                out.set(e.index(), b, e.get());
            }
        }
        return out;
    }

    private Vector selfDot(List<AbstractMatrix> AList){
        Vector o = null;
        for(AbstractMatrix A: AList) {
            DenseVector vector = new DenseVector(A.numRows());

            FlexCompRowMatrix rA = castOrTransform(A);

            for (int a = 0; a < rA.numRows(); a++) {
                vector.set(a, rA.getRow(a).dot(new DenseVector(rA.getRow(a))));
            }

            if(o == null){
                o = vector;
            }else{
                o.add(vector);
            }
        }

        return o;
    }


    private AMapping mapWLSimilarity(ACache source, ACache target, double threshold){

        INodeLabelGrouper grouper = new NodeLabelGrouperFactory().create();

        Map<String, Integer> index = new HashMap<>();
        index.put("#counter#", 0);

        List<String> sourceURIs = source.getAllUris();

        List<AbstractMatrix> sourceMat = mapMatrix(
                source.getKbInfo(),
                sourceURIs,
                2,
                grouper,
                index
        );

        Vector sourceDot = selfDot(sourceMat);


        List<String> targetURIs = source.getAllUris();

        List<AbstractMatrix> targetMat = mapMatrix(
                target.getKbInfo(),
                targetURIs,
                2,
                grouper,
                index
        );

        Vector targetDot = selfDot(targetMat);

        AbstractMatrix matrix = null;
        for(int i = 0; i < sourceMat.size(); i++){
            AbstractMatrix dot = dotProduct(sourceMat.get(i), targetMat.get(i));
            if(matrix == null){
                matrix = dot;
            }else{
                matrix.add(dot);
            }
        }

        AMapping mapping = MappingFactory.createDefaultMapping();

        for(int i = 0; i < matrix.numRows(); i++){
            for(int j = 0; j < matrix.numColumns(); j++){

                double norm = Math.sqrt(sourceDot.get(i) * targetDot.get(j));
                double sim = matrix.get(i,j) / norm;

                if(sim >= threshold){
                    mapping.add(sourceURIs.get(i), targetURIs.get(j), sim);
                }

            }
        }



        return mapping;
    }

    private class WLQuad{

        private int iteration;
        private int row;
        private int col;
        private int entry;

        WLQuad(int iteration, int row, int col, int entry) {
            this.iteration = iteration;
            this.row = row;
            this.col = col;
            this.entry = entry;
        }

    }
}
