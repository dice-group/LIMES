package org.aksw.limes.core.measures.mapper.topology.im;

import com.vividsolutions.jts.geom.Geometry;

public class MBBIndex {

    public int lat1, lat2, lon1, lon2;
    public Geometry polygon;
    public String uri;
    public String origin_uri;

    public MBBIndex(int lat1, int lon1, int lat2, int lon2, Geometry polygon, String uri) {
        this.lat1 = lat1;
        this.lat2 = lat2;
        this.lon1 = lon1;
        this.lon2 = lon2;
        this.polygon = polygon;
        this.uri = uri;
        this.origin_uri = uri;
    }

    public MBBIndex(int lat1, int lon1, int lat2, int lon2, Geometry polygon, String uri, String origin_uri) {
        this.lat1 = lat1;
        this.lat2 = lat2;
        this.lon1 = lon1;
        this.lon2 = lon2;
        this.polygon = polygon;
        this.uri = uri;
        this.origin_uri = origin_uri;
    }

    public boolean contains(MBBIndex i) {
        return this.lat1 <= i.lat1 && this.lon1 <= i.lon1 && this.lon2 >= i.lon2 && this.lat2 >= i.lat2;
    }

    public boolean covers(MBBIndex i) {
        return this.lat1 <= i.lat1 && this.lon1 <= i.lon1 && this.lon2 >= i.lon2 && this.lat2 >= i.lat2;
    }

    public boolean intersects(MBBIndex i) {
        return !this.disjoint(i);
    }

    public boolean disjoint(MBBIndex i) {
        return this.lat2 < i.lat1 || this.lat1 > i.lat2 || this.lon2 < i.lon1 || this.lon1 > i.lon2;
    }

    public boolean equals(Object o) {
        if (!(o instanceof MBBIndex)) {
            return false;
        }
        MBBIndex i = ((MBBIndex) o);
        return lat1 == i.lat1 && lat2 == i.lat2 && lon1 == i.lon1 && lon2 == i.lon2;
    }

    
}
