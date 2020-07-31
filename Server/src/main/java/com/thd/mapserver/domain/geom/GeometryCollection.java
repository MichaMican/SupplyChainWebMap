package com.thd.mapserver.domain.geom;

import java.util.Iterator;
import java.util.List;

public class GeometryCollection extends Geometry {
    private static final String TYPENAME_GEOMETRY_COLLECTION = "GeometryCollection";
    List<Geometry> geometries;

    public GeometryCollection(List<Geometry> geometries, int srid) {
        super(srid);
        this.geometries = geometries;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (var geometry : geometries) {
            sb.append(geometry.toString());
            sb.append(",");
        }

        return String.format("%s(%s)", TYPENAME_GEOMETRY_COLLECTION, sb.toString());
    }

    @Override
    public String asText() {
        StringBuilder sb = new StringBuilder("GEOMETRYCOLLECTION(");

        Iterator<Geometry> geomIter = geometries.iterator();
        while(geomIter.hasNext()){
            Geometry geometry = geomIter.next();
            sb.append(geometry.asText());
            if(geomIter.hasNext()){
                sb.append(",");
            }
        }

        sb.append(")");

        return sb.toString();
    }

    @Override
    public String geometryType() {
        return TYPENAME_GEOMETRY_COLLECTION;
    }
}
