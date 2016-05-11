package org.aksw.limes.core.measures.measure.space;

import org.aksw.limes.core.measures.measure.pointsets.GeoDistance;
import org.apache.log4j.Logger;



/**
*
* @author ngonga
*/
public class SpaceMeasureFactory {

   static Logger logger = Logger.getLogger("LIMES");

   public static ISpaceMeasure getMeasure(String name, int dimension) {
//   	System.out.println("SpaceMesure.getMeasure("+name+")");
   	if (name.toLowerCase().startsWith("geo")) {
           if (dimension != 2) {
               logger.warn("Erroneous dimension settings for GeoDistance (" + dimension + ").");
           }
           return new GeoDistance();
       } 
       else {
           EuclideanMetric measure = new EuclideanMetric();
           measure.setDimension(dimension);
           return measure;
       }
   }
}

