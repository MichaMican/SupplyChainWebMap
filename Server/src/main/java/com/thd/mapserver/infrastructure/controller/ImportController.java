package com.thd.mapserver.infrastructure.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/import")
public class ImportController {
//    @PostMapping(path = "")
//    public ResponseEntity importFromGeoJson(@RequestBody GeoJsonObject geoJsonObject){
//
//        List<SFAFeature> parsedFeatures = new ArrayList<>();
//
//        System.out.println("Parsing");
//
//        if(geoJsonObject instanceof FeatureCollection){
//            List<Feature> features = ((FeatureCollection) geoJsonObject).getFeatures();
//
//            for (var feature : features) {
//                GeoJsonObject geometry = feature.getGeometry();
//                if(geometry instanceof Point){
//                    parsedFeatures.add(new SFAFeature(feature.getId(), new com.thd.mapserver.domain.geom.Point(((Point) geometry).getCoordinates())))
//                }
//            }
//
//        } else {
//            return new ResponseEntity("only Geojson featureCollections are supported", HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity(HttpStatus.OK);
//    }
}
