package com.thd.mapserver.domain.geom;

import com.thd.mapserver.models.Coordinate;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class LineString extends Curve {

    private static final String TYPENAME_LINESTRING = "LineString";

    private List<Point> points;

    public LineString(List<Point> points) {
        this.points = points;
    }

    public int numPoints() {
        return points.size();
    }

    public Point pointN(int n) {
        return points.get(n);
    }

    @Override
    public String asText() {
        return null;
    }

    @Override
    public String geometryType() {
        return TYPENAME_LINESTRING;
    }

    @Override
    public boolean isClosed() {
        return points.get(0).equals(points.get(numPoints() - 1));
    }

    @Override
    public Point startPoint() {
        return pointN(0);
    }

    @Override
    public Point endPoint() {
        return pointN(numPoints() - 1);
    }

    public List<Coordinate> getAllPointsAsCoordinates() {
        List<Coordinate> returnCoordinate = new ArrayList<>();

        for (Point point : points) {
            returnCoordinate.add(point.getCoordinate());
        }

        return returnCoordinate;
    }
}
