package com.thd.mapserver.infrastructure.controller;

import com.thd.mapserver.infrastructure.service.ImportService;
import com.thd.mapserver.models.featureTypeDto.FeatureTypeDto;
import org.geojson.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidParameterException;

@RestController
@RequestMapping("/import")
public class ImportController {

    ImportService service = new ImportService();

    @PostMapping(path = "/featureTypes")
    public HttpEntity<String> importFeatureTypes(@RequestBody FeatureTypeDto featureType){
        service.addFeatureTypes(featureType);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @PostMapping(path = "/features")
    public HttpEntity<String> importFromGeoJson(@RequestBody GeoJsonObject geoJsonObject) {

        try{
            service.parseGeoJsonObject(geoJsonObject);
        } catch (InvalidParameterException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }


}
