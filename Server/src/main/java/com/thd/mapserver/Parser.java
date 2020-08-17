package com.thd.mapserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thd.mapserver.domain.geom.*;
import com.thd.mapserver.models.DbModels.DbLimitResponse;
import com.thd.mapserver.models.DbModels.FeatureTypeDbDto;
import com.thd.mapserver.models.DbModels.PoiTypeDbDto;
import com.thd.mapserver.models.responseDtos.FeatureCollectionWithLimitDto;
import org.geojson.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    public SFAGeometry parseGeometry(GeoJsonObject geometry) {

        if (geometry instanceof Point) {
            LngLatAlt cor = ((Point) geometry).getCoordinates();

            return (
                    new SFAPoint(cor.getLongitude(), cor.getLatitude(), cor.getAltitude())
            );

        } else if (geometry instanceof Polygon) {

            Polygon polygon = (Polygon) geometry;
            SFALinearRing outerRing;
            List<SFALinearRing> innerRings = new ArrayList<>();

            List<SFAPoint> SFAPoints = new ArrayList<>();
            for (var coordinates : polygon.getExteriorRing()) {
                SFAPoints.add(new SFAPoint(coordinates.getLongitude(), coordinates.getLatitude(), coordinates.getAltitude()));
            }
            outerRing = new SFALinearRing(SFAPoints);

            if (!polygon.getInteriorRings().isEmpty()) {
                for (var ringList : polygon.getInteriorRings()) {
                    SFAPoints = new ArrayList<>();
                    for (var coordinates : ringList) {
                        SFAPoints.add(new SFAPoint(coordinates.getLongitude(), coordinates.getLatitude(), coordinates.getAltitude()));
                    }
                    innerRings.add(new SFALinearRing(SFAPoints));
                }
                outerRing = new SFALinearRing(SFAPoints);
            }

            return new SFAPolygon(outerRing, innerRings);

        } else if (geometry instanceof GeometryCollection) {

            List<SFAGeometry> geometries = new ArrayList<>();
            for (var subGeometry : ((GeometryCollection) geometry).getGeometries()) {
                geometries.add(parseGeometry(subGeometry));
            }
            return new SFASFAGeometryCollection(geometries);

        } else {
            return null;
        }
    }

    private static Feature parseFeature(PoiTypeDbDto rowToParse) throws JsonProcessingException {
        //prepare properties for parsing
        Map<String, Object> properties = new HashMap<>();

        properties.put("type", rowToParse.typ);
        properties.put("title", rowToParse.title);
        properties.put("description", rowToParse.description);

        //prepare geometry for parsing
        GeoJsonObject rawObject = new ObjectMapper().readValue(rowToParse.geometry_asgeojson, GeoJsonObject.class);

        Feature feature = new Feature();
        if (rawObject instanceof GeometryCollection || rawObject instanceof Point ||
                rawObject instanceof Polygon || rawObject instanceof MultiPolygon) {
            feature.setGeometry(rawObject);
        } else {
            return null;
        }

        feature.setProperties(properties);
        feature.setId(rowToParse.id);

        return feature;
    }

    public FeatureCollectionWithLimitDto parsePoisDescJoin(DbLimitResponse elementsToParse) {
        var returnCollection = new FeatureCollectionWithLimitDto();

        for (var element : elementsToParse.data) {

            try {
                Feature feature = parseFeature(element);
                if (feature != null) {
                    returnCollection.add(feature);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        returnCollection.numMatched = elementsToParse.numMatched;
        returnCollection.numReturned = elementsToParse.numReturned;

        return returnCollection;

    }

    public FeatureCollection parsePoisDescJoin(List<PoiTypeDbDto> elementsToParse) {
        var featureCollection = new FeatureCollection();

        for (var element : elementsToParse) {

            try {
                Feature feature = parseFeature(element);
                if (feature != null) {
                    featureCollection.add(feature);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return featureCollection;

    }

    public FeatureCollection parsePoisDescJoin(PoiTypeDbDto elementsToParse) {
        var list = new ArrayList<PoiTypeDbDto>();
        list.add(elementsToParse);
        return parsePoisDescJoin(list);

    }

    public List<FeatureTypeDbDto> parseDbResponseFeatureType(ResultSet dbResponseToParse) {
        List<FeatureTypeDbDto> rows = new ArrayList<>();

        try {
            while (dbResponseToParse.next()) {
                rows.add(new FeatureTypeDbDto(dbResponseToParse.getString("typ"),
                        dbResponseToParse.getString("description"),
                        dbResponseToParse.getString("title"))
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return rows;
    }

    public List<PoiTypeDbDto> parseDbResponsePoiType(ResultSet dbResponseToParse) {
        List<PoiTypeDbDto> rows = new ArrayList<>();

        try {
            while (dbResponseToParse.next()) {
                rows.add(new PoiTypeDbDto(dbResponseToParse.getString("id"),
                        dbResponseToParse.getString("geometry_asgeojson"),
                        dbResponseToParse.getString("typ"),
                        dbResponseToParse.getString("description"),
                        dbResponseToParse.getString("title"))
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return rows;
    }

}
