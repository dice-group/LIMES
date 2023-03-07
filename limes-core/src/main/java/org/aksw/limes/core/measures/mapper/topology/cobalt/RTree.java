package org.aksw.limes.core.measures.mapper.topology.cobalt;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RTree {

    public static class Entry {
        private final String uri;
        private final Envelope envelope;
        private final Geometry geometry;

        public Entry(String uri, Envelope envelope, Geometry geometry) {
            this.uri = uri;
            this.envelope = envelope;
            this.geometry = geometry;
        }

        public String getUri() {
            return uri;
        }

        public Envelope getEnvelope() {
            return envelope;
        }

        public Geometry getGeometry() {
            return geometry;
        }
    }


    private boolean leaf;
    private Envelope boundary;
    private List<RTree> children;
    private List<Entry> contents;

    private static int capacity = 4;

    private static RTree createLeaf(List<Entry> contents) {
        RTree tree = new RTree();
        tree.leaf = true;
        tree.contents = contents;
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (Entry content : contents) {
            if (content.envelope.getMinX() < minX) {
                minX = content.envelope.getMinX();
            }
            if (content.envelope.getMinY() < minY) {
                minY = content.envelope.getMinY();
            }
            if (content.envelope.getMaxX() > maxX) {
                maxX = content.envelope.getMaxX();
            }
            if (content.envelope.getMaxY() > maxY) {
                maxY = content.envelope.getMaxY();
            }
        }
        tree.boundary = new Envelope(minX, maxX, minY, maxY);
        return tree;
    }

    private static RTree createParent(List<RTree> children) {
        RTree tree = new RTree();
        tree.leaf = false;
        tree.children = new ArrayList<>(children.size());
        for (RTree child : children) {
            if (child.boundary.getMinX() != Double.POSITIVE_INFINITY) {
                tree.children.add(child);
            }else{
                throw new RuntimeException("");
            }
        }
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (RTree content : tree.children) {
            if (content.boundary.getMinX() < minX) {
                minX = content.boundary.getMinX();
            }
            if (content.boundary.getMinY() < minY) {
                minY = content.boundary.getMinY();
            }
            if (content.boundary.getMaxX() > maxX) {
                maxX = content.boundary.getMaxX();
            }
            if (content.boundary.getMaxY() > maxY) {
                maxY = content.boundary.getMaxY();
            }
        }
        tree.boundary = new Envelope(minX, maxX, minY, maxY);
        return tree;
    }

    public Envelope getBoundary() {
        return boundary;
    }

    public void setBoundary(Envelope boundary) {
        this.boundary = boundary;
    }

    public List<RTree> getChildren() {
        return children;
    }

    public void setChildren(List<RTree> children) {
        this.children = children;
    }

    public List<Entry> getContents() {
        return contents;
    }

    public void setContents(List<Entry> contents) {
        this.contents = contents;
    }

    /**
     * Builds the RTree using the sort tile recursive algorithm
     */
    public static RTree buildSTR(List<Entry> entries) {
        List<RTree> nodes = buildSTRBottomLayer(entries);
        return buildSTRRec(nodes);
    }

    /**
     * Builds the last layer of the RTree
     */
    private static List<RTree> buildSTRBottomLayer(List<Entry> entries) {
        int requiredNodeAmount = (int) Math.ceil((0.0 + entries.size()) / capacity);
        int sliceAmount = (int) Math.ceil(Math.sqrt(requiredNodeAmount));
        int entriesPerSlice = (int) Math.ceil((0.0 + entries.size()) / sliceAmount);
        int nodesPerSlice = (int) Math.ceil((0.0 +entriesPerSlice) / capacity);

        List<RTree> nodes = new ArrayList<>(requiredNodeAmount);

        entries.sort(Comparator.comparingDouble(o -> o.envelope.getMinX() + o.envelope.getMaxX()));
        for (int i = 0; i < sliceAmount; i++) {
            List<Entry> sliceEntries = entries.subList(i * entriesPerSlice, Math.min((i + 1) * entriesPerSlice, entries.size()));
            sliceEntries.sort(Comparator.comparingDouble(o -> o.envelope.getMinY() + o.envelope.getMaxY()));
            for (int j = 0; j < nodesPerSlice; j++) {
                List<Entry> nodeEntries = sliceEntries.subList(Math.min(j * capacity, sliceEntries.size()), Math.min((j + 1) * capacity, sliceEntries.size()));
                if(!nodeEntries.isEmpty()){
                    nodes.add(createLeaf(nodeEntries));
                }
            }
        }
        return nodes;
    }

    /**
     * Builds the upper layers of the RTree using the sort tile recursive algorithm
     */
    private static RTree buildSTRRec(List<RTree> entries) {
        int requiredNodeAmount = (int) Math.ceil((0.0 + entries.size()) / capacity);

        if(requiredNodeAmount == 0){
            return createLeaf(new ArrayList<>()); //Empty RTree
        }
        if(requiredNodeAmount == 1){
            return createParent(entries); //Create the root
        }

        int sliceAmount = (int) Math.ceil(Math.sqrt(requiredNodeAmount));
        int entriesPerSlice = (int) Math.ceil((0.0 + entries.size()) / sliceAmount);
        int nodesPerSlice = (int) Math.ceil((0.0 +entriesPerSlice) / capacity);

        List<RTree> nodes = new ArrayList<>(requiredNodeAmount);

        entries.sort(Comparator.comparingDouble(o -> o.boundary.getMinX() + o.boundary.getMaxX()));
        for (int i = 0; i < sliceAmount; i++) {
            List<RTree> sliceEntries = entries.subList(i * entriesPerSlice, Math.min((i + 1) * entriesPerSlice, entries.size()));
            sliceEntries.sort(Comparator.comparingDouble(o -> o.boundary.getMinY() + o.boundary.getMaxY()));
            for (int j = 0; j < nodesPerSlice; j++) {
                List<RTree> nodeEntries = sliceEntries.subList(Math.min(j * capacity, sliceEntries.size()), Math.min((j + 1) * capacity, sliceEntries.size()));
                if(!nodeEntries.isEmpty()){
                    nodes.add(createParent(nodeEntries));
                }
            }
        }
        return buildSTRRec(nodes);
    }

    /**
     * Queries all entries of the RTree whose MBB intersects the parameter envelope
     */
    public List<Entry> search(Envelope envelope) {
        ArrayList<Entry> result = new ArrayList<>();
        search(envelope, result);
        return result;
    }

    private void search(Envelope envelope, List<Entry> result) {
        if (leaf) {
            for (Entry content : contents) {
                if (content.envelope.intersects(envelope)) {
                    result.add(content);
                }
            }
        } else {
            for (RTree child : children) {
                if (child.boundary.intersects(envelope)) {
                    child.search(envelope, result);
                }
            }
        }
    }


}

