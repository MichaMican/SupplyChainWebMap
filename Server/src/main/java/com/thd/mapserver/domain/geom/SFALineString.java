package com.thd.mapserver.domain.geom;

import com.thd.mapserver.helper.GeometryHelper;
import com.thd.mapserver.models.Coordinate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SFALineString extends SFACurve {

    private static final String TYPENAME_LINESTRING = "LineString";

    private List<SFAPoint> points;

    public SFALineString(List<SFAPoint> points) {
        this.points = points;
    }

    public int numPoints() {
        return points.size();
    }

    public SFAPoint pointN(int n) {
        return points.get(n);
    }

    @Override
    public String asText() {
        StringBuilder sb = new StringBuilder("LINESTRING(");

        Iterator<SFAPoint> pointIter = points.iterator();

        while (pointIter.hasNext()){
            var point = pointIter.next();
            sb.append(GeometryHelper.convertCoordinatesToWkt(point));

            if(pointIter.hasNext()){
                sb.append(",");
            }
        }

        sb.append(")");
        return sb.toString();
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
    public SFAPoint startPoint() {
        return pointN(0);
    }

    @Override
    public SFAPoint endPoint() {
        return pointN(numPoints() - 1);
    }

    public List<Coordinate> getAllPointsAsCoordinates() {
        List<Coordinate> returnCoordinate = new ArrayList<>();

        for (SFAPoint SFAPoint : points) {
            returnCoordinate.add(SFAPoint.getCoordinate());
        }

        return returnCoordinate;
    }
}
