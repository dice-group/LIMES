package org.aksw.limes.core.measures.mapper.graphs;

import com.google.common.collect.Multiset;
import no.uib.cipr.matrix.*;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import org.aksw.limes.core.io.mapping.AMapping;
import org.aksw.limes.core.io.mapping.MappingFactory;
import org.aksw.limes.core.measures.measure.graphs.representation.WLModelRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class GraphMapperCallable implements Callable<AMapping> {

    static Logger logger = LoggerFactory.getLogger("GraphMapper");

    private int iteration;
    private List<WLModelRepresentation> sourceModel;
    private List<WLModelRepresentation> targetModel;
    private double threshold;

    private Map<String, Integer> index;
    private int counter = 0;

    public GraphMapperCallable(int iteration, List<WLModelRepresentation> sourceModel,
                               List<WLModelRepresentation> targetModel, double threshold) {
        this.iteration = iteration;
        this.sourceModel = sourceModel;
        this.targetModel = targetModel;
        this.threshold = threshold;
    }

    @Override
    public AMapping call() throws Exception {

        logger.info(String.format("Build source WL representation for %d instances.", sourceModel.size()));
        List<AbstractMatrix> sourceMat = matrices(sourceModel, iteration);

        Vector sourceDot = selfDot(sourceMat);

        logger.info(String.format("Build target WL representation for %d instances.", targetModel.size()));
        List<AbstractMatrix> targetMat = matrices(targetModel, iteration);

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

        logger.info(String.format(
                "Compare %d tuples.", matrix.numRows()*matrix.numColumns()
        ));
        for(int i = 0; i < matrix.numRows(); i++){
            for(int j = 0; j < matrix.numColumns(); j++){

                printProcess("Compared %d percent of source and target entities.",
                              i*matrix.numRows() + j, matrix.numRows()*matrix.numColumns());

                double norm = Math.sqrt(sourceDot.get(i) * targetDot.get(j));
                double sim = matrix.get(i,j) / norm;

                if(sim >= threshold){
                    mapping.add(sourceModel.get(i).getModelDescriptor().getURI(),
                                targetModel.get(j).getModelDescriptor().getURI(), sim);
                }

            }
        }


        return mapping;
    }

    private void printProcess(String format, int process, int max){
        int units = (int)Math.round((double)max/10);

        if(process % units == 0){
            logger.info(
                    String.format(format, (int)(((double)process)/max*100))
            );
        }

    }

    private List<AbstractMatrix> matrices(List<WLModelRepresentation> list, int iteration){
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
            printProcess("Created %d percent of entries", row, list.size());
        }

        List<AbstractMatrix> matrices = new ArrayList<>();
        for(int i = 0; i <= iteration; i++){
            matrices.add(new FlexCompRowMatrix(list.size(), counter));
        }

        for(WLQuad quad: quads){
            AbstractMatrix matrixSparse = matrices.get(quad.iteration);
            matrixSparse.set(quad.row, quad.col, quad.entry);
        }


        return matrices;
    }


    private int index(String s){
        if(index == null)
            index = new HashMap<>();
        if(!index.containsKey(s))
            index.put(s, counter++);
        return index.get(s);
    }

    private List<WLQuad> process(WLModelRepresentation representation, int row, int iteration, Map<String, Integer> index){
        List<WLQuad> out = new ArrayList<>();

        for(int it = 0; it <= iteration; it++){
            Multiset<String> wlIt = representation.getIteration(it).getRepresentation();
            for(String label: wlIt.elementSet()){
                int count = wlIt.count(label);
                out.add(new WLQuad(it, row, index(label), count));
            }
        }

        return out;
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
