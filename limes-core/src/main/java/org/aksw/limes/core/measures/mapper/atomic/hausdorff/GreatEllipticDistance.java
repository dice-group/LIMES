
package org.aksw.limes.core.measures.mapper.atomic.hausdorff;

import org.aksw.limes.core.datastrutures.Point;

/**
 * implementation of https://en.wikipedia.org/wiki/Vincenty's_formulae Solve the
 * inverse problem on the ellipsoid using a great elliptic section The inverse
 * problem is: Given latitudes and longitudes of P1 and P2 on the ellipsoid
 * compute the azimuth a_12 of the great elliptic section P1 P2 and the arc
 * length s of the great elliptic curve. With the ellipsoid constants a, f, e^2
 * and e'^2
 *
 * @author sherif
 */
public class GreatEllipticDistance {

    /**
     * Computes and returns distance between two points.
     * 
     * @param x,
     *            first point
     * @param y,
     *            second point
     * @return the distance between x and y
     * 
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
     *            Latitude of first point
     * @param long1,
     *            Longitude of first point
     * @param lat2,
     *            Latitude of second point
     * @param long2,
     *            Longitude of second point
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
     *            Latitude of first point
     * @param long1,
     *            Longitude of first point
     * @param lat2,
     *            Latitude of second point
     * @param long2,
     *            Longitude of second point
     * @return Distance between both points
     */
    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
	double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563;
	double L = lon2 - lon1;
	double U1 = Math.atan((1 - f) * Math.tan(lat1));
	double U2 = Math.atan((1 - f) * Math.tan(lat2));
	double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
	double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
	double cosSqAlpha;
	double sinSigma;
	double cos2SigmaM;
	double cosSigma;
	double sigma;

	double lambda = L, lambdaP, iterLimit = 100;
	do {
	    double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
	    sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
		    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
	    if (sinSigma == 0) {
		return 0;
	    }

	    cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
	    sigma = Math.atan2(sinSigma, cosSigma);
	    double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
	    cosSqAlpha = 1 - sinAlpha * sinAlpha;
	    cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

	    double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
	    lambdaP = lambda;
	    lambda = L + (1 - C) * f * sinAlpha
		    * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));

	} while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

	if (iterLimit == 0) {
	    return 0;
	}

	double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
	double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
	double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
	double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)
		- B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));

	double s = b * A * (sigma - deltaSigma);

	return s / 1000;
    }

}
