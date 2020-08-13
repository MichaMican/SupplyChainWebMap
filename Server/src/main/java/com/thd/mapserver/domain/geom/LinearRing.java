package com.thd.mapserver.domain.geom;

import com.thd.mapserver.models.Coordinate;

import java.security.InvalidParameterException;
import java.util.List;

public class LinearRing extends LineString {

    private static final String TYPENAME_LINEARRING = "LinearRing";

    public LinearRing(List<Point> points) {
        super(points);
        if(!isClosed()){
            throw new InvalidParameterException("LinearRing must be closed");
        }
    }

    @Override
    public String geometryType() {
        return TYPENAME_LINEARRING;
    }
}
