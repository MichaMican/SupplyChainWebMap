package com.thd.mapserver.domain.geom;

import com.thd.mapserver.helper.GeometryHelper;
import com.thd.mapserver.models.Coordinate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Polygon extends Surface {
    private static final String TYPENAME_POLYGON = "Point";

    private final List<List<Coordinate>> coordinates;

    public Polygon(List<List<Coordinate>> coordinates, int srid) {
        super(srid);
        this.coordinates = coordinates;
    }

    public Polygon(List<Coordinate> outerCoordinates, List<Coordinate> innerCoordinates, int srid) {
        super(srid);
        var list = new ArrayList<List<Coordinate>>();
        //TODO Throw exception when outer coordinates are empty
        if(outerCoordinates != null && !outerCoordinates.isEmpty()){
            list.add(outerCoordinates);
        }
        if(innerCoordinates != null && !innerCoordinates.isEmpty()){
            list.add(innerCoordinates);
        }

        this.coordinates = list;
    }

    public List<List<Coordinate>> getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[\n");
        for (List<Coordinate> polCors : coordinates) {
            sb.append("[\n");
            for (Coordinate cor : polCors) {
                sb.append(String.format("[%s(%s)],\n", TYPENAME_POLYGON.toUpperCase(), GeometryHelper.convertCoordinatesToWkt(cor)));
            }
            sb.append("]\n");
        }
        sb.append("]");


        return sb.toString();
    }

    @Override
    public String asText() {
        StringBuilder sb;
        sb = new StringBuilder("POLYGON(");

        Iterator<List<Coordinate>> polCorsIter = coordinates.iterator();
        while (polCorsIter.hasNext()) {
            var polCors = polCorsIter.next();
            sb.append("(");

            appendCoors(sb, polCors);

            sb.append(")");
            if (polCorsIter.hasNext()) {
                sb.append(",");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    private void appendCoors(StringBuilder sb, List<Coordinate> coordinates) {
        Iterator<Coordinate> corIter = coordinates.iterator();
        while (corIter.hasNext()) {
            Coordinate cor = corIter.next();
            sb.append(String.format("%s", GeometryHelper.convertCoordinatesToWkt(cor)));
            //this checks if there is another coordinate in the list
            if (corIter.hasNext()) {
                sb.append(",");
            }
        }
    }

    @Override
    public String geometryType() {
        return TYPENAME_POLYGON;
    }
}
