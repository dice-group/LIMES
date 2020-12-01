/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.limes.core.measures.mapper.pointsets;

import org.aksw.limes.core.datastrutures.Point;

/**
 * @author Axel-C. Ngonga Ngomo (ngonga@informatik.uni-leipzig.de)
 */
public class OrthodromicDistance {

    public static double R = 6371f;

    /**
     * Computes and returns distance between two points.
     *
     * @param x,
     *         first point
     * @param y,
     *         second point
     * @return the distance between x and y
     */
    public static double getDistanceInDegrees(Point x, Point y) {
        return getDistanceInDegrees(x.coordinates.get(0), x.coordinates.get(1), y.coordinates.get(0),
                y.coordinates.get(1));
    }

    /**
     * Computes the distance between two points on earth Input
     * latitudes/longitudes by converting their latitude and longitude into
     * radians.
     *
     * @param lat1,
     *         Latitude of first point
     * @param long1,
     *         Longitude of first point
     * @param lat2,
     *         Latitude of second point
     * @param long2,
     *         Longitude of second point
     * @return Distance between both points
     */
    public static double getDistanceInDegrees(double lat1, double long1, double lat2, double long2) {
        double la1 = (double) Math.toRadians(lat1);
        double lo1 = (double) Math.toRadians(long1);
        double la2 = (double) Math.toRadians(lat2);
        double lo2 = (double) Math.toRadians(long2);
        return getDistance(la1, lo1, la2, lo2);
    }

    /**
     * Computes the distance between two points on earth Input
     * latitudes/longitudes are in Radians
     *
     * @param lat1,
     *         Latitude of first point
     * @param long1,
     *         Longitude of first point
     * @param lat2,
     *         Latitude of second point
     * @param long2,
     *         Longitude of second point
     * @return Distance between both points
     */
    public static double getDistance(double lat1, double long1, double lat2, double long2) {
        double dLat = lat2 - lat1;
        double dLon = long2 - long1;
        double sinLat = (double) Math.sin(dLat / 2);
        double sinLon = (double) Math.sin(dLon / 2);

        double a = (double) (sinLat * sinLat + sinLon * sinLon * Math.cos(lat1) * Math.cos(lat2));
        double c = (double) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        return R * c;
    }

}
