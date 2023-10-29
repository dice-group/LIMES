package org.aksw.limes.core.measures.mapper.topology.cobalt.splitting;

import org.locationtech.jts.geom.*;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CobaltSplitter implements ICobaltSplitter {

    public GeometryFactory factory = new GeometryFactory();

    public Geometry emptyGeo;
    public Geometry[][] empty2x2Geo;

    public CobaltSplitter() {
        this.emptyGeo = factory.createPolygon();
        this.empty2x2Geo = new Geometry[][]{
                new Geometry[]{
                        emptyGeo, emptyGeo
                },
                new Geometry[]{
                        emptyGeo, emptyGeo
                }
        };
    }

    public Geometry[][] getSplitGeo(Geometry geo, double splitX, double splitY) {
        if (geo.isEmpty()) {
            return empty2x2Geo;
        }

        try {
            Geometry[] geometries = splitLeftRight(geo, splitX);

            Geometry geoLeft = geometries[0];
            Geometry geoRight = geometries[1];

            Geometry[] leftGeos = splitDownUp(geoLeft, splitY);
            Geometry geoBottomLeft = leftGeos[0];
            Geometry geoTopLeft = leftGeos[1];

            Geometry[] rightGeos = splitDownUp(geoRight, splitY);
            Geometry geoBottomRight = rightGeos[0];
            Geometry geoTopRight = rightGeos[1];

            Geometry[][] split = new Geometry[][]{
                    new Geometry[]{
                            geoBottomLeft,
                            geoTopLeft
                    },
                    new Geometry[]{
                            geoBottomRight,
                            geoTopRight
                    }
            };
            return split;
        } catch (TopologyException e) {
            e.printStackTrace();
            return empty2x2Geo;
        }
    }

    public Geometry[] splitLeftRight(Geometry geo, double splitX) {
        if (geo instanceof Polygon) {
            List<Polygon>[] lists = splitPolygonLeftRight((Polygon) geo, splitX);
            Geometry g1;
            if (lists[0].size() == 0) {
                g1 = emptyGeo;
            } else if (lists[0].size() == 1) {
                g1 = lists[0].get(0);
            } else {
                g1 = factory.createMultiPolygon(lists[0].toArray(Polygon[]::new));
            }
            Geometry g2;
            if (lists[1].size() == 0) {
                g2 = emptyGeo;
            } else if (lists[1].size() == 1) {
                g2 = lists[1].get(0);
            } else {
                g2 = factory.createMultiPolygon(lists[1].toArray(Polygon[]::new));
            }
            return new Geometry[]{g1, g2};
        } else if (geo instanceof MultiPolygon) {
            return splitMultiPolygonLeftRight((MultiPolygon) geo, splitX);
        } else {
            throw new RuntimeException();
        }
    }

    public Geometry[] splitDownUp(Geometry geo, double splitY) {
        if (geo instanceof Polygon) {
            List<Polygon>[] lists = splitPolygonDownUp((Polygon) geo, splitY);
            Geometry g1;
            if (lists[0].size() == 0) {
                g1 = emptyGeo;
            } else if (lists[0].size() == 1) {
                g1 = lists[0].get(0);
            } else {
                g1 = factory.createMultiPolygon(lists[0].toArray(Polygon[]::new));
            }
            Geometry g2;
            if (lists[1].size() == 0) {
                g2 = emptyGeo;
            } else if (lists[1].size() == 1) {
                g2 = lists[1].get(0);
            } else {
                g2 = factory.createMultiPolygon(lists[1].toArray(Polygon[]::new));
            }
            return new Geometry[]{g1, g2};
        } else if (geo instanceof MultiPolygon) {
            return splitMultiPolygonDownUp((MultiPolygon) geo, splitY);
        } else {
            throw new RuntimeException();
        }
    }


    public Geometry[] splitMultiPolygonLeftRight(MultiPolygon geo, double splitX) {
        ArrayList<Polygon> polygonsLeft = new ArrayList<>();
        ArrayList<Polygon> polygonsRight = new ArrayList<>();
        for (int i = 0; i < geo.getNumGeometries(); i++) {
            List<Polygon>[] leftRight = splitPolygonLeftRight((Polygon) geo.getGeometryN(i), splitX);
            if (leftRight[0] != null) {
                polygonsLeft.addAll(leftRight[0]);
            }
            if (leftRight[1] != null) {
                polygonsRight.addAll(leftRight[1]);
            }
        }

        MultiPolygon poly1 = null;
        MultiPolygon poly2 = null;

        if (polygonsLeft.size() > 0) {
            poly1 = factory.createMultiPolygon(polygonsLeft.toArray(Polygon[]::new));
        } else {
            poly1 = factory.createMultiPolygon();
        }
        if (polygonsRight.size() > 0) {
            poly2 = factory.createMultiPolygon(polygonsRight.toArray(Polygon[]::new));
        } else {
            poly2 = factory.createMultiPolygon();
        }
        return new MultiPolygon[]{poly1, poly2};
    }

    public Geometry[] splitMultiPolygonDownUp(MultiPolygon geo, double splitY) {
        ArrayList<Polygon> polygonsDown = new ArrayList<>();
        ArrayList<Polygon> polygonsUp = new ArrayList<>();
        for (int i = 0; i < geo.getNumGeometries(); i++) {
            List<Polygon>[] leftRight = splitPolygonDownUp((Polygon) geo.getGeometryN(i), splitY);
            if (leftRight[0] != null) {
                polygonsDown.addAll(leftRight[0]);
            }
            if (leftRight[1] != null) {
                polygonsUp.addAll(leftRight[1]);
            }
        }

        MultiPolygon poly1 = null;
        MultiPolygon poly2 = null;

        if (polygonsDown.size() > 0) {
            poly1 = factory.createMultiPolygon(polygonsDown.toArray(Polygon[]::new));
        } else {
            poly1 = factory.createMultiPolygon();
        }
        if (polygonsUp.size() > 0) {
            poly2 = factory.createMultiPolygon(polygonsUp.toArray(Polygon[]::new));
        } else {
            poly2 = factory.createMultiPolygon();
        }
        return new MultiPolygon[]{poly1, poly2};
    }


    public List<Polygon>[] splitPolygonLeftRight(Polygon geo, double splitX) {
        Polygon polygon = geo;
        Coordinate[] exteriorRingCoordinates = polygon.getExteriorRing().getCoordinates();

        List<LinearRing>[] exteriorRings = splitRingLeftRight(splitX, exteriorRingCoordinates);

        List<LinearRing> leftHoleList = new ArrayList<>();
        List<LinearRing> rightHoleList = new ArrayList<>();
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            LineString interiorRingN = polygon.getInteriorRingN(i);
            List<LinearRing>[] splitRingLeftRight = splitRingLeftRight(splitX, interiorRingN.getCoordinates());
            fixHolesLeftRight(splitX, exteriorRings[0], leftHoleList, splitRingLeftRight[0]);
            fixHolesLeftRight(splitX, exteriorRings[1], rightHoleList, splitRingLeftRight[1]);
        }

        LinearRing[] holesLeft = leftHoleList.toArray(LinearRing[]::new);
        LinearRing[] holesRight = rightHoleList.toArray(LinearRing[]::new);

        List<Polygon> poly1 = new ArrayList<>();
        List<Polygon> poly2 = new ArrayList<>();
        if (exteriorRings[0] != null) {
            for (LinearRing linearRing : exteriorRings[0]) {
                Polygon x = factory.createPolygon(linearRing, holesLeft);
                if (!x.isValid()) {
                    x = factory.createPolygon(linearRing);
                }
                poly1.add(x);
            }
        }
        if (exteriorRings[1] != null) {
            for (LinearRing linearRing : exteriorRings[1]) {
                Polygon x = factory.createPolygon(linearRing, holesRight);
                if (!x.isValid()) {
                    x = factory.createPolygon(linearRing);
                }
                poly2.add(x);
            }
        }


        return new List[]{poly1, poly2};
    }

    public List<Polygon>[] splitPolygonDownUp(Polygon geo, double splitY) {
        Polygon polygon = geo;
        Coordinate[] exteriorRingCoordinates = polygon.getExteriorRing().getCoordinates();

        List<LinearRing>[] exteriorRings = splitRingDownUp(splitY, exteriorRingCoordinates);

        List<LinearRing> downHoleList = new ArrayList<>();
        List<LinearRing> upHoleList = new ArrayList<>();
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            LineString interiorRingN = polygon.getInteriorRingN(i);
            List<LinearRing>[] splitRingLeftRight = splitRingDownUp(splitY, interiorRingN.getCoordinates());
            fixHolesDownUp(splitY, exteriorRings[0], downHoleList, splitRingLeftRight[0]);
            fixHolesDownUp(splitY, exteriorRings[1], upHoleList, splitRingLeftRight[1]);
        }

        LinearRing[] holesDown = downHoleList.toArray(LinearRing[]::new);
        LinearRing[] holesUp = upHoleList.toArray(LinearRing[]::new);

        List<Polygon> poly1 = new ArrayList<>();
        List<Polygon> poly2 = new ArrayList<>();
        if (exteriorRings[0] != null) {
            for (LinearRing linearRing : exteriorRings[0]) {
                Polygon x = factory.createPolygon(linearRing, holesDown);
                if (!x.isValid()) {
                    x = factory.createPolygon(linearRing);
                }
                poly1.add(x);
            }
        }
        if (exteriorRings[1] != null) {
            for (LinearRing linearRing : exteriorRings[1]) {
                Polygon x = factory.createPolygon(linearRing, holesUp);
                if (!x.isValid()) {
                    x = factory.createPolygon(linearRing);
                }
                poly2.add(x);
            }
        }


        return new List[]{poly1, poly2};
    }


    private void fixHolesLeftRight(double splitX, List<LinearRing> exteriorRings, List<LinearRing> holeList, List<LinearRing> splitRingLeftRight) {
        if (splitRingLeftRight != null) {
            for (LinearRing holeRing : splitRingLeftRight) {
                if (holeRing.getCoordinates()[0].x == splitX) {
                    //Is a hole at splitLine -> No longer a hole but part of exteriorRing
                    Coordinate a = holeRing.getCoordinates()[0];
                    Coordinate b = holeRing.getCoordinates()[holeRing.getCoordinates().length - 2];

                    Coordinate holeTopCoord = a.getY() > b.getY() ? a : b;
                    Coordinate holeBottomCoord = a.getY() > b.getY() ? b : a;

                    for (int j = 0; j < exteriorRings.size(); j++) {
                        LinearRing exteriorRing = exteriorRings.get(j);
                        List<Coordinate> exteriorCoordinatesAtSplittingLineSorted = Arrays.stream(exteriorRing.getCoordinates())
                                .filter(coordinate -> coordinate.getX() == splitX)
                                .sorted(Comparator.comparingDouble(Coordinate::getY)).collect(Collectors.toList());
                        Coordinate exteriorBot = exteriorCoordinatesAtSplittingLineSorted.get(0);
                        Coordinate exteriorTop = exteriorCoordinatesAtSplittingLineSorted.get(exteriorCoordinatesAtSplittingLineSorted.size() - 1);
                        if (holeTopCoord.getY() >= exteriorBot.getY() && holeTopCoord.getY() <= exteriorTop.getY()) {
                            //Insert hole
                            Coordinate[] combinedResultRingCoordinates = new Coordinate[exteriorRing.getCoordinates().length + holeRing.getCoordinates().length - 1];
                            int index = 0;
                            for (Coordinate currentCoordinate : exteriorRing.getCoordinates()) {
                                combinedResultRingCoordinates[index] = currentCoordinate;
                                index++;
                            }
                            index--; //Skip last

                            Coordinate coordinate = exteriorRing.getCoordinates()[exteriorRing.getCoordinates().length - 2];
                            if (coordinate.getY() == exteriorTop.getY()) { //start with the top part of the hole
                                combinedResultRingCoordinates[index] = holeTopCoord;
                                index++;
                                if (holeRing.getCoordinates()[0] == holeTopCoord) {
                                    for (int k = 1; k < holeRing.getCoordinates().length - 2; k++) {
                                        combinedResultRingCoordinates[index] = holeRing.getCoordinates()[k];
                                        index++;
                                    }
                                } else {
                                    for (int k = holeRing.getCoordinates().length - 3; k >= 1; k--) {
                                        combinedResultRingCoordinates[index] = holeRing.getCoordinates()[k];
                                        index++;
                                    }
                                }
                                combinedResultRingCoordinates[index] = holeBottomCoord;
                                index++;
                            } else { //start from bottom
                                combinedResultRingCoordinates[index] = holeBottomCoord;
                                index++;
                                if (holeRing.getCoordinates()[0] == holeBottomCoord) {
                                    for (int k = 1; k < holeRing.getCoordinates().length - 2; k++) {
                                        combinedResultRingCoordinates[index] = holeRing.getCoordinates()[k];
                                        index++;
                                    }
                                } else {
                                    for (int k = holeRing.getCoordinates().length - 3; k >= 1; k--) {
                                        combinedResultRingCoordinates[index] = holeRing.getCoordinates()[k];
                                        index++;
                                    }
                                }
                                combinedResultRingCoordinates[index] = holeTopCoord;
                                index++;
                            }


                            combinedResultRingCoordinates[combinedResultRingCoordinates.length - 1] = combinedResultRingCoordinates[0];
                            exteriorRings.set(j, factory.createLinearRing(combinedResultRingCoordinates));
                        }
                    }
                } else {
                    holeList.add(holeRing);
                }
            }
        }
    }

    private void fixHolesDownUp(double splitY, List<LinearRing> exteriorRings, List<LinearRing> holeList, List<LinearRing> splitRingLeftRight) {
        if (splitRingLeftRight != null) {
            for (LinearRing holeRing : splitRingLeftRight) {
                if (holeRing.getCoordinates()[0].y == splitY) {
                    //Is a hole at splitLine -> No longer a hole but part of exteriorRing
                    Coordinate a = holeRing.getCoordinates()[0];
                    Coordinate b = holeRing.getCoordinates()[holeRing.getCoordinates().length - 2];

                    Coordinate holeRightCoord = a.getX() > b.getX() ? a : b;
                    Coordinate holeLeftCoord = a.getX() > b.getX() ? b : a;

                    for (int j = 0; j < exteriorRings.size(); j++) {
                        LinearRing exteriorRing = exteriorRings.get(j);
                        List<Coordinate> exteriorCoordinatesAtSplittingLineSorted = Arrays.stream(exteriorRing.getCoordinates())
                                .filter(coordinate -> coordinate.getY() == splitY)
                                .sorted(Comparator.comparingDouble(Coordinate::getX)).collect(Collectors.toList());
                        Coordinate exteriorLeft = exteriorCoordinatesAtSplittingLineSorted.get(0);
                        Coordinate exteriorRight = exteriorCoordinatesAtSplittingLineSorted.get(exteriorCoordinatesAtSplittingLineSorted.size() - 1);
                        if (holeRightCoord.getX() >= exteriorLeft.getX() && holeRightCoord.getX() <= exteriorRight.getX()) {
                            //Insert hole
                            Coordinate[] combinedResultRingCoordinates = new Coordinate[exteriorRing.getCoordinates().length + holeRing.getCoordinates().length - 1];
                            int index = 0;
                            for (Coordinate currentCoordinate : exteriorRing.getCoordinates()) {
                                combinedResultRingCoordinates[index] = currentCoordinate;
                                index++;
                            }
                            index--; //Skip last

                            Coordinate coordinate = exteriorRing.getCoordinates()[exteriorRing.getCoordinates().length - 2];
                            if (coordinate.getX() == exteriorRight.getX()) { //start with the top part of the hole
                                combinedResultRingCoordinates[index] = holeRightCoord;
                                index++;
                                if (holeRing.getCoordinates()[0] == holeRightCoord) {
                                    for (int k = 1; k < holeRing.getCoordinates().length - 2; k++) {
                                        combinedResultRingCoordinates[index] = holeRing.getCoordinates()[k];
                                        index++;
                                    }
                                } else {
                                    for (int k = holeRing.getCoordinates().length - 3; k >= 1; k--) {
                                        combinedResultRingCoordinates[index] = holeRing.getCoordinates()[k];
                                        index++;
                                    }
                                }
                                combinedResultRingCoordinates[index] = holeLeftCoord;
                                index++;
                            } else { //start from bottom
                                combinedResultRingCoordinates[index] = holeLeftCoord;
                                index++;
                                if (holeRing.getCoordinates()[0] == holeLeftCoord) {
                                    for (int k = 1; k < holeRing.getCoordinates().length - 2; k++) {
                                        combinedResultRingCoordinates[index] = holeRing.getCoordinates()[k];
                                        index++;
                                    }
                                } else {
                                    for (int k = holeRing.getCoordinates().length - 3; k >= 1; k--) {
                                        combinedResultRingCoordinates[index] = holeRing.getCoordinates()[k];
                                        index++;
                                    }
                                }
                                combinedResultRingCoordinates[index] = holeRightCoord;
                                index++;
                            }


                            combinedResultRingCoordinates[combinedResultRingCoordinates.length - 1] = combinedResultRingCoordinates[0];
                            exteriorRings.set(j, factory.createLinearRing(combinedResultRingCoordinates));
                        }
                    }
                } else {
                    holeList.add(holeRing);
                }
            }
        }
    }

    public List<LinearRing>[] splitRingLeftRight(double splitX, Coordinate[] exteriorRingCoordinates) {
        ArrayList<Coordinate> leftRing = new ArrayList<>();
        ArrayList<Coordinate> rightRing = new ArrayList<>();

        List<List<Coordinate>> leftRings = new ArrayList<>();
        List<List<Coordinate>> rightRings = new ArrayList<>();


        boolean lastLeft = false;
        boolean lastRight = false;
        Coordinate lastCoordinate = null;

        for (Coordinate exteriorRingCoordinate : exteriorRingCoordinates) {
            if (!lastLeft && !lastRight) {
                //First one
                if (exteriorRingCoordinate.x < splitX) {
                    leftRing.add(exteriorRingCoordinate);
                    lastLeft = true;
                } else {
                    rightRing.add(exteriorRingCoordinate);
                    lastRight = true;
                }
            } else if (lastLeft) {
                if (exteriorRingCoordinate.x > splitX) {
                    //Create new point at border
                    double ydiff = exteriorRingCoordinate.y - lastCoordinate.y;
                    double xdiff = exteriorRingCoordinate.x - lastCoordinate.x;
                    double currentSlope = ydiff / xdiff;

                    double xDiffToSplitLine = splitX - lastCoordinate.x;
                    double yDiffToSplitLine = currentSlope * xDiffToSplitLine;

                    Coordinate pointOnSplitLine = new Coordinate(lastCoordinate.x + xDiffToSplitLine, lastCoordinate.y + yDiffToSplitLine);
                    leftRing.add(pointOnSplitLine);
                    leftRings.add(leftRing);
                    leftRing = new ArrayList<>();

                    rightRing.add(pointOnSplitLine);
                    rightRing.add(exteriorRingCoordinate);
                    lastRight = true;
                    lastLeft = false;
                } else {
                    leftRing.add(exteriorRingCoordinate);
                }
            } else if (lastRight) {
                if (exteriorRingCoordinate.x > splitX) {
                    rightRing.add(exteriorRingCoordinate);
                } else {
                    //Create new point at border
                    double ydiff = lastCoordinate.y - exteriorRingCoordinate.y;
                    double xdiff = lastCoordinate.x - exteriorRingCoordinate.x;
                    double currentSlope = ydiff / xdiff;

                    double xDiffToSplitLine = splitX - exteriorRingCoordinate.x;
                    double yDiffToSplitLine = currentSlope * xDiffToSplitLine;

                    Coordinate pointOnSplitLine = new Coordinate(exteriorRingCoordinate.x + xDiffToSplitLine, exteriorRingCoordinate.y + yDiffToSplitLine);
                    rightRing.add(pointOnSplitLine);
                    rightRings.add(rightRing);
                    rightRing = new ArrayList<>();

                    leftRing.add(pointOnSplitLine);
                    leftRing.add(exteriorRingCoordinate);
                    lastLeft = true;
                    lastRight = false;
                }
            }
            lastCoordinate = exteriorRingCoordinate;
        }
        //Connect first and last part. Everything now starts and ends with x=splitX
        if (leftRings.size() > 0 && rightRings.size() > 0) {
            if (leftRing.size() > 0) {
                putTogetherStartringX(splitX, leftRing, leftRings);
            }
            if (rightRing.size() > 0) {
                putTogetherStartringX(splitX, rightRing, rightRings);
            }
        } else {
            if (leftRing.size() > 0) {
                leftRings.add(leftRing);
            }
            if (rightRing.size() > 0) {
                rightRings.add(rightRing);
            }
        }

        fixDoubledCoordinates(leftRings);
        fixDoubledCoordinates(rightRings);

        return new List[]{
                fixLineStringsLeftRight(splitX, leftRings),
                fixLineStringsLeftRight(splitX, rightRings)
        };
    }

    private static void putTogetherStartringX(double splitX, ArrayList<Coordinate> lastRing, List<List<Coordinate>> correctSideRings) {
        List<Coordinate> startRing = correctSideRings.get(0);
        lastRing.remove(lastRing.get(lastRing.size() - 1)); //Remove first/last point so it is only once in the result
        startRing.addAll(lastRing);
        int max = startRing.size();
        while (!(startRing.get(0).x == splitX && startRing.get(startRing.size() - 1).x == splitX)) {
            if (max-- == 0) { //Fix double precision errors
                Map<Double, List<Integer>> closestIndex = new TreeMap<>(Double::compareTo);
                for (int i = 0; i < startRing.size(); i++) {
                    double diff = Math.abs(startRing.get(i).x - splitX);
                    if (!closestIndex.containsKey(diff)) {
                        closestIndex.put(diff, new ArrayList<>());
                    }
                    closestIndex.get(diff).add(i);
                }
                int count = 0;
                outer:
                for (Double aDouble : closestIndex.keySet()) {
                    for (Integer index : closestIndex.get(aDouble)) {
                        if (count++ == 2) {
                            break outer;
                        }
                        startRing.set(index, new Coordinate(splitX, startRing.get(index).getY()));
                    }
                }
            }
            Coordinate coordinate = startRing.remove(0);
            startRing.add(coordinate);
        }
    }

    private static void putTogetherStartringY(double splitY, ArrayList<Coordinate> lastRing, List<List<Coordinate>> correctSideRings) {
        List<Coordinate> startRing = correctSideRings.get(0);
        lastRing.remove(lastRing.get(lastRing.size() - 1)); //Remove first/last point so it is only once in the result
        startRing.addAll(lastRing);
        int max = startRing.size();
        while (!(startRing.get(0).y == splitY && startRing.get(startRing.size() - 1).y == splitY)) {
            if (max-- == 0) { //Fix double precision errors
                Map<Double, List<Integer>> closestIndex = new TreeMap<>(Double::compareTo);
                for (int i = 0; i < startRing.size(); i++) {
                    double diff = Math.abs(startRing.get(i).y - splitY);
                    if (!closestIndex.containsKey(diff)) {
                        closestIndex.put(diff, new ArrayList<>());
                    }
                    closestIndex.get(diff).add(i);
                }
                int count = 0;
                outer:
                for (Double aDouble : closestIndex.keySet()) {
                    for (Integer index : closestIndex.get(aDouble)) {
                        if (count++ == 2) {
                            break outer;
                        }
                        startRing.set(index, new Coordinate(startRing.get(index).getX(), splitY));
                    }
                }
            }
            Coordinate coordinate = startRing.remove(0);
            startRing.add(coordinate);
        }
    }


    public List<LinearRing>[] splitRingDownUp(double splitY, Coordinate[] exteriorRingCoordinates) {
        ArrayList<Coordinate> downRing = new ArrayList<>();
        ArrayList<Coordinate> upRing = new ArrayList<>();

        List<List<Coordinate>> downRings = new ArrayList<>();
        List<List<Coordinate>> upRings = new ArrayList<>();


        boolean lastDown = false;
        boolean lastUp = false;
        Coordinate lastCoordinate = null;

        for (Coordinate exteriorRingCoordinate : exteriorRingCoordinates) {
            if (!lastDown && !lastUp) {
                //First one
                if (exteriorRingCoordinate.y < splitY) {
                    downRing.add(exteriorRingCoordinate);
                    lastDown = true;
                } else {
                    upRing.add(exteriorRingCoordinate);
                    lastUp = true;
                }
            } else if (lastDown) {
                if (exteriorRingCoordinate.y > splitY) {
                    //Create new point at border
                    double ydiff = exteriorRingCoordinate.y - lastCoordinate.y;
                    double xdiff = exteriorRingCoordinate.x - lastCoordinate.x;
                    double currentSlope = ydiff / xdiff;

                    double yDiffToSplitLine = splitY - lastCoordinate.y;
                    double xDiffToSplitLine = (xdiff / ydiff) * yDiffToSplitLine;

                    Coordinate pointOnSplitLine = new Coordinate(lastCoordinate.x + xDiffToSplitLine, lastCoordinate.y + yDiffToSplitLine);
                    downRing.add(pointOnSplitLine);
                    downRings.add(downRing);
                    downRing = new ArrayList<>();

                    upRing.add(pointOnSplitLine);
                    upRing.add(exteriorRingCoordinate);
                    lastUp = true;
                    lastDown = false;
                } else {
                    downRing.add(exteriorRingCoordinate);
                }
            } else if (lastUp) {
                if (exteriorRingCoordinate.y > splitY) {
                    upRing.add(exteriorRingCoordinate);
                } else {
                    //Create new point at border
                    double ydiff = lastCoordinate.y - exteriorRingCoordinate.y;
                    double xdiff = lastCoordinate.x - exteriorRingCoordinate.x;
                    double currentSlope = ydiff / xdiff;

                    double yDiffToSplitLine = splitY - exteriorRingCoordinate.y;
                    double xDiffToSplitLine = (xdiff / ydiff) * yDiffToSplitLine;

                    Coordinate pointOnSplitLine = new Coordinate(exteriorRingCoordinate.x + xDiffToSplitLine, exteriorRingCoordinate.y + yDiffToSplitLine);
                    upRing.add(pointOnSplitLine);
                    upRings.add(upRing);
                    upRing = new ArrayList<>();

                    downRing.add(pointOnSplitLine);
                    downRing.add(exteriorRingCoordinate);
                    lastDown = true;
                    lastUp = false;
                }
            }
            lastCoordinate = exteriorRingCoordinate;
        }
        //Connect first and last part. Everything now starts and ends with x=splitX
        if (downRings.size() > 0 && upRings.size() > 0) {
            if (downRing.size() > 0) {
                putTogetherStartringY(splitY, downRing, downRings);
            }
            if (upRing.size() > 0) {
                putTogetherStartringY(splitY, upRing, upRings);
            }
        } else {
            if (downRing.size() > 0) {
                downRings.add(downRing);
            }
            if (upRing.size() > 0) {
                upRings.add(upRing);
            }
        }

        fixDoubledCoordinates(downRings);
        fixDoubledCoordinates(upRings);

        return new List[]{
                fixLineStringsDownUp(splitY, downRings),
                fixLineStringsDownUp(splitY, upRings)
        };
    }

    private static void fixDoubledCoordinates(List<List<Coordinate>> downRings) {
        for (int i = 0; i < downRings.size(); i++) {
            List<Coordinate> coordinates = downRings.get(i);
            for (int j = 0; j < coordinates.size() - 2; j++) {
                if (coordinates.get(j).equals(coordinates.get(j + 1))) {
                    coordinates.remove(j);
                    j--;
                }
            }
        }
    }


    public List<LinearRing> fixLineStringsLeftRight(double splitX, List<List<Coordinate>> rings) {
        Map<Coordinate, List<Coordinate>> map = new TreeMap<>(Comparator.comparingDouble(Coordinate::getY));
        List<List<Coordinate>> result = new ArrayList<>();
        if (rings.size() == 1) {
            List<Coordinate> coordinates = rings.get(0);
            coordinates.add(coordinates.get(0));
            result.add(coordinates);
        } else if (rings.size() > 1) {
            for (List<Coordinate> ring : rings) {
                if (ring.get(0).x == splitX) {
                    map.put(ring.get(0), ring);
                }
                if (ring.get(ring.size() - 1).x == splitX) {
                    map.put(ring.get(ring.size() - 1), ring);
                }
            }
            boolean snap = false;
            Coordinate lastCoord = null;
            for (Coordinate c : map.keySet()) {
                if (!snap) {
                    snap = true;
                } else {
                    snap = false;
                    List<Coordinate> coordinates = map.get(c);
                    if (coordinates.contains(lastCoord)) {
                        coordinates.add(coordinates.get(0)); //Is a full geo
                        result.add(coordinates);
                    } else {
                        //Merge both
                        List<Coordinate> lastCoords = map.get(lastCoord);
                        //Check which one is the last in their list
                        if (coordinates.get(coordinates.size() - 1).equals(c)) {
                            //Put the other list behind this one
                            if (!coordinates.contains(lastCoord)) {
                                coordinates.addAll(lastCoords);
                                map.replace(lastCoords.get(0), coordinates);
                                map.replace(lastCoords.get(lastCoords.size() - 1), coordinates);
                            } else {
                                //Is full geo
                                coordinates.add(coordinates.get(0));
                                result.add(coordinates);
                            }
                        } else {
                            if (!lastCoords.contains(c)) {
                                lastCoords.addAll(coordinates);
                                map.replace(coordinates.get(0), lastCoords);
                                map.replace(coordinates.get(coordinates.size() - 1), lastCoords);
                            } else {
                                lastCoords.add(lastCoords.get(0));
                                result.add(lastCoords);
                            }
                        }
                    }
                }
                lastCoord = c;
            }
        }


        return result.stream()
                .filter(coordinates -> coordinates.stream().noneMatch(coordinate -> Double.isNaN(coordinate.x) || Double.isNaN(coordinate.y)))
                .filter(coordinates -> coordinates.size() >= 4)
                .map(coordinates -> factory.createLinearRing(coordinates.toArray(Coordinate[]::new)))
                .collect(Collectors.toList());
    }

    public List<LinearRing> fixLineStringsDownUp(double splitY, List<List<Coordinate>> rings) {
        Map<Coordinate, List<Coordinate>> map = new TreeMap<>(Comparator.comparingDouble(Coordinate::getX));
        List<List<Coordinate>> result = new ArrayList<>();
        if (rings.size() == 1) {
            List<Coordinate> coordinates = rings.get(0);
            coordinates.add(coordinates.get(0));
            result.add(coordinates);
        } else if (rings.size() > 1) {
            for (List<Coordinate> ring : rings) {
                if (ring.get(0).y == splitY) {
                    map.put(ring.get(0), ring);
                }
                if (ring.get(ring.size() - 1).y == splitY) {
                    map.put(ring.get(ring.size() - 1), ring);
                }
            }
            boolean snap = false;
            Coordinate lastCoord = null;
            for (Coordinate c : map.keySet()) {
                if (!snap) {
                    snap = true;
                } else {
                    snap = false;
                    List<Coordinate> coordinates = map.get(c);
                    if (coordinates.contains(lastCoord)) {
                        coordinates.add(coordinates.get(0)); //Is a full geo
                        result.add(coordinates);
                    } else {
                        //Merge both
                        List<Coordinate> lastCoords = map.get(lastCoord);
                        //Check which one is the last in their list
                        if (coordinates.get(coordinates.size() - 1).equals(c)) {
                            //Put the other list behind this one
                            if (!coordinates.contains(lastCoord)) {
                                coordinates.addAll(lastCoords);
                                map.put(lastCoords.get(0), coordinates);
                                map.put(lastCoords.get(lastCoords.size() - 1), coordinates);
                            } else {
                                //Is full geo
                                coordinates.add(coordinates.get(0));
                                result.add(coordinates);
                            }
                        } else {
                            if (!lastCoords.contains(c)) {
                                lastCoords.addAll(coordinates);
                                map.put(coordinates.get(0), lastCoords);
                                map.put(coordinates.get(coordinates.size() - 1), lastCoords);
                            } else {
                                lastCoords.add(lastCoords.get(0));
                                result.add(lastCoords);
                            }
                        }
                    }
                }
                lastCoord = c;
            }
        }
        return result.stream()
                .filter(coordinates -> coordinates.stream().noneMatch(coordinate -> Double.isNaN(coordinate.x) || Double.isNaN(coordinate.y)))
                .filter(coordinates -> coordinates.size() >= 4)
                .map(coordinates -> factory.createLinearRing(coordinates.toArray(Coordinate[]::new)))
                .collect(Collectors.toList());
    }


}
