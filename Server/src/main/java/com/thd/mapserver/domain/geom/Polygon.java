package com.thd.mapserver.domain.geom;

import com.thd.mapserver.helper.GeometryHelper;
import com.thd.mapserver.models.Coordinate;

import java.util.List;

public class Polygon extends Surface {
    private static final String TYPENAME_POINT = "Point";

    private final List<List<Coordinate>> coordinates;

    public Polygon(List<List<Coordinate>> coordinates, int srid) {
        super(srid);
        this.coordinates = coordinates;
    }

    public List<List<Coordinate>> getCoordinates() {
        return coordinates;
    }

    @Override
    public String asText() {
        StringBuilder sb = new StringBuilder("[\n");
        for (List<Coordinate> polCors : coordinates) {
            sb.append("[\n");
            for (Coordinate cor : polCors) {
                sb.append(String.format("[%s(%s)],\n", TYPENAME_POINT.toUpperCase(), GeometryHelper.convertCoordinatesToWkt(cor)));
            }
            sb.append("]\n");
        }
        sb.append("]");


        return sb.toString();
    }

    @Override
    public String geometryType() {
        return TYPENAME_POINT;
    }
}
