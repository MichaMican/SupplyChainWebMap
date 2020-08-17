package com.thd.mapserver.domain.geom;

import java.util.Iterator;
import java.util.List;

public class SFASFAGeometryCollection extends SFAGeometry {
    private static final String TYPENAME_GEOMETRY_COLLECTION = "GeometryCollection";
    List<SFAGeometry> geometries;

    public SFASFAGeometryCollection(List<SFAGeometry> geometries) {
        super(4326);
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

        Iterator<SFAGeometry> geomIter = geometries.iterator();
        while(geomIter.hasNext()){
            SFAGeometry SFAGeometry = geomIter.next();
            sb.append(SFAGeometry.asText());
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
