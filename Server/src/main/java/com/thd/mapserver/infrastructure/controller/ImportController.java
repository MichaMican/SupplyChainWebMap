package com.thd.mapserver.infrastructure.controller;

import com.thd.mapserver.Parser;
import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.domain.geom.SFAGeometry;
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

                SFAGeometry parsedGeom = new Parser().parseGeometry(feature.getGeometry());

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


}
