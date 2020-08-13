package com.thd.mapserver.infrastructure.controller;

import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.domain.geom.LinearRing;
import com.thd.mapserver.helper.GeometryHelper;
import com.thd.mapserver.models.Coordinate;
import com.thd.mapserver.models.featureTypeDto.FeatureTypeDto;
import com.thd.mapserver.postsql.PostgresqlPoiRepository;
import org.geojson.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/import")
public class ImportController {

    @PostMapping(path = "/featureTypes")
    public HttpEntity<String> importFeatureTypes(@RequestBody FeatureTypeDto featureType){

        PostgresqlPoiRepository dbConnect = new PostgresqlPoiRepository();
        dbConnect.addFeatureType(featureType);

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping(path = "/features")
    public HttpEntity<String> importFromGeoJson(@RequestBody GeoJsonObject geoJsonObject) {
        PostgresqlPoiRepository dbConnect = new PostgresqlPoiRepository();

        if (geoJsonObject instanceof FeatureCollection) {
            List<Feature> features = ((FeatureCollection) geoJsonObject).getFeatures();
            for (var feature : features) {

                com.thd.mapserver.domain.geom.Geometry parsedGeom = parseGeometry(feature.getGeometry());

                if (parsedGeom != null) {
                    SFAFeature newSFAFeature = new SFAFeature(
                            feature.getId(),
                            parsedGeom,
                            feature.getProperties(),
                            parsedGeom.geometryType()
                    );
                    dbConnect.add(newSFAFeature);
                } else {
                    return new ResponseEntity<>("only Points and Polygons are currently supported", HttpStatus.BAD_REQUEST);
                }
            }
        } else {
            return new ResponseEntity<>("only Geojson featureCollections are supported", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }
    private com.thd.mapserver.domain.geom.Geometry parseGeometry(GeoJsonObject geometry) {

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

}
