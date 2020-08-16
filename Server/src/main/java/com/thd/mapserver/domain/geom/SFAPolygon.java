package com.thd.mapserver.domain.geom;

import com.thd.mapserver.helper.GeometryHelper;
import com.thd.mapserver.models.Coordinate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SFAPolygon extends SFASurface {
    private static final String TYPENAME_POLYGON = "Point";

    private final SFALinearRing outerRing;
    private final List<SFALinearRing> innerRings;

    //nur outer
    public SFAPolygon(SFALinearRing outerRing) {
        this.outerRing = outerRing;
        this.innerRings = new ArrayList<>();
    }

    //ein outer linear ring => mehrere inner linear ring
    public SFAPolygon(SFALinearRing outerRing, List<SFALinearRing> innerRings) {
        if(innerRings == null || innerRings.isEmpty()){
            this.outerRing = outerRing;
            this.innerRings = new ArrayList<>();
        }else{
            this.outerRing = outerRing;
            this.innerRings = innerRings;
        }

    }

    //This typo is part of the SFA Model
    public SFALineString exterorRing() {
        return outerRing;
    }

    public SFALineString interiorRingN(int n) {
        return innerRings.get(n);
    }

    public int numInteriorRing() {
        return innerRings.size();
    }

    @Override
    public String asText() {
        StringBuilder sb;
        sb = new StringBuilder("POLYGON(");

        List<List<Coordinate>> coordinates = new ArrayList<>();
        coordinates.add(outerRing.getAllPointsAsCoordinates());
        if (numInteriorRing() <= 0) {
            for (SFALinearRing innerRing : innerRings) {
                coordinates.add(innerRing.getAllPointsAsCoordinates());
            }
        }

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
