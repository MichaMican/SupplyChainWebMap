package com.thd.mapserver.infrastructure.controller;

import com.thd.mapserver.Settings;
import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.models.Coordinate;
import com.thd.mapserver.postsql.PostgresqlPoiRepository;
import org.geojson.*;
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
    @PostMapping(path = "")
    public ResponseEntity importFromGeoJson(@RequestBody GeoJsonObject geoJsonObject) {
        PostgresqlPoiRepository dbConnect = new PostgresqlPoiRepository();

        if (geoJsonObject instanceof FeatureCollection) {
            List<Feature> features = ((FeatureCollection) geoJsonObject).getFeatures();
            for (var feature : features) {
                if (feature.getGeometry() instanceof GeometryCollection) {
                    GeometryCollection geometryCollection = (GeometryCollection) feature.getGeometry();
                    List<SFAFeature> sfaFeatures = new ArrayList<>();
                    for (var geometry : geometryCollection) {
                        SFAFeature newSFAFeature = parseGeometry(geometry, feature);
                        if (newSFAFeature != null) {
                            sfaFeatures.add(newSFAFeature);
                        } else {
                            return new ResponseEntity("only Points and Polygons are currently supported", HttpStatus.BAD_REQUEST);
                        }
                    }
                    dbConnect.add(sfaFeatures);
                } else {
                    SFAFeature newSFAFeature = parseGeometry(feature.getGeometry(), feature);
                    if (newSFAFeature != null) {
                        dbConnect.add(newSFAFeature);
                    } else {
                        return new ResponseEntity("only Points and Polygons are currently supported", HttpStatus.BAD_REQUEST);
                    }
                }
            }
        } else {
            return new ResponseEntity("only Geojson featureCollections are supported", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    private SFAFeature parseGeometry(GeoJsonObject geometry, Feature feature) {

        int srid = 0;

        if (geometry instanceof Point) {
            LngLatAlt cor = ((Point) geometry).getCoordinates();

            return (
                    new SFAFeature(
                        feature.getId(),
                        new com.thd.mapserver.domain.geom.Point(
                                cor.getLongitude(), cor.getLatitude(), cor.getAltitude(), srid
                        ),
                        feature.getProperties(),
                        "Point"
                    )
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

            return (
                    new SFAFeature(
                        feature.getId(),
                        new com.thd.mapserver.domain.geom.Polygon(
                                coordinates, srid
                        ),
                        feature.getProperties(),
                        "Polygon"
                    )
            );
        } else {
            return null;
        }
    }

}
