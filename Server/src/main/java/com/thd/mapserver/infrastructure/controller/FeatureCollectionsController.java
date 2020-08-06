package com.thd.mapserver.infrastructure.controller;

import com.thd.mapserver.helper.DbParseHelper;
import com.thd.mapserver.helper.ResponseTextBuilder;
import com.thd.mapserver.models.responseDtos.CollectionDto;
import com.thd.mapserver.models.responseDtos.ResponseCollectionDto;
import com.thd.mapserver.models.responseDtos.ResponseCollectionsDto;
import com.thd.mapserver.postsql.PostgresqlPoiRepository;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FeatureCollectionsController {

    private ResponseTextBuilder rtb = new ResponseTextBuilder();

    @GetMapping("/test")
    public FeatureCollection test() {
        var dbCon = new PostgresqlPoiRepository();
        return DbParseHelper.parsePoisDescJoin(dbCon.getAll());
    }

    @GetMapping("/collections")
    public HttpEntity<ResponseCollectionsDto> getCollections() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/collections/{collectionId}")
    public HttpEntity<ResponseCollectionDto> getCollection(@PathVariable("collectionId") String collectionId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/collections/{collectionId}/items")
    public HttpEntity<String> getItemsLink(@PathVariable("collectionId") String collectionId) {
        //TODO Test if collection exists
        return new ResponseEntity<>(rtb.getItemLinkResponse(collectionId), HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}/items.json")
    public HttpEntity<FeatureCollection> getItems(@PathVariable("collectionId") String collectionId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/collections/{collectionId}/items/{featureId}")
    public HttpEntity<String> getFeatureLinks(@PathVariable("collectionId") String collectionId,
                                  @PathVariable("featureId") String featureId) {
        //TODO Test if collection exists
        return new ResponseEntity<>(rtb.getFeatureLinkResponse(collectionId, featureId), HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}/items/{featureId}.json")
    public HttpEntity<FeatureCollection> getFeatures(@PathVariable("collectionId") String collectionId,
                                         @PathVariable("featureId") String featureId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
