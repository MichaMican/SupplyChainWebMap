package com.thd.mapserver.domain.geom;

import com.thd.mapserver.helper.GeometryHelper;
import com.thd.mapserver.models.Coordinate;

import java.util.List;

public class Polygon extends Surface {
    private static final String TYPENAME_POINT = "Point";

    private final List<Coordinate> coordinates;

    public Polygon(List<Coordinate> coordinates, int srid){
        super(srid);
        this.coordinates = coordinates;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    @Override
    public String asText() {
        StringBuilder sb = new StringBuilder("[\n");
        for (Coordinate cor : coordinates) {
            sb.append(String.format("%s(%s), \n", TYPENAME_POINT.toUpperCase(), GeometryHelper.convertCoordinatesToWkt(cor)));
        }
        sb.append("]");


        return sb.toString();
    }

    @Override
    public String geometryType() {
        return TYPENAME_POINT;
    }
}
