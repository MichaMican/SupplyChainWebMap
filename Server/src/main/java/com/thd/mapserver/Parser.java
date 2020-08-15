package com.thd.mapserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thd.mapserver.domain.geom.LinearRing;
import com.thd.mapserver.helper.GeometryHelper;
import com.thd.mapserver.models.Coordinate;
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
    public com.thd.mapserver.domain.geom.Geometry parseGeometry(GeoJsonObject geometry) {

        if (geometry instanceof Point) {
            LngLatAlt cor = ((Point) geometry).getCoordinates();

            return (
                    new com.thd.mapserver.domain.geom.Point(cor.getLongitude(), cor.getLatitude(), cor.getAltitude())
            );

        } else if (geometry instanceof Polygon) {
            List<List<LngLatAlt>> coordinatesRaw = ((Polygon) geometry).getCoordinates();
            List<List<Coordinate>> coordinates = new ArrayList<>();

            for (var polygonPartRaw : coordinatesRaw) {
                List<Coordinate> polygonPart = new ArrayList<>();
                for (var polygonPartCor : polygonPartRaw) {
                    polygonPart.add(new Coordinate(
                            polygonPartCor.getLongitude(), polygonPartCor.getLatitude(), polygonPartCor.getAltitude()
                    ));
                }
                coordinates.add(polygonPart);
            }

            LinearRing innerRing = new LinearRing(GeometryHelper.convertCoordinateListToPointList(coordinates.get(0)));
            List<LinearRing> outerRing = new ArrayList<>();

            if(coordinates.size() > 1){
                for(int i = 1; i < coordinates.size(); i++){
                    outerRing.add(new LinearRing(GeometryHelper.convertCoordinateListToPointList(coordinates.get(i))));
                }
            }

            return new com.thd.mapserver.domain.geom.Polygon(innerRing, outerRing);
        } else if (geometry instanceof GeometryCollection) {

            List<com.thd.mapserver.domain.geom.Geometry> geometries = new ArrayList<>();
            for (var subGeometry : ((GeometryCollection) geometry).getGeometries()) {
                geometries.add(parseGeometry(subGeometry));
            }
            return new com.thd.mapserver.domain.geom.GeometryCollection(geometries);

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
        if(rawObject instanceof GeometryCollection || rawObject instanceof Point ||
                rawObject instanceof Polygon || rawObject instanceof  MultiPolygon){
            feature.setGeometry(rawObject);
        }else {
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
                if(feature != null){
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
                if(feature != null){
                    featureCollection.add(feature);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return featureCollection;

    }

    public FeatureCollection parsePoisDescJoin(PoiTypeDbDto elementsToParse){
        var list = new ArrayList<PoiTypeDbDto>();
        list.add(elementsToParse);
        return parsePoisDescJoin(list);

    }

    public List<FeatureTypeDbDto> parseDbResponseFeatureType(ResultSet dbResponseToParse){
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

    public List<PoiTypeDbDto> parseDbResponsePoiType(ResultSet dbResponseToParse){
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
