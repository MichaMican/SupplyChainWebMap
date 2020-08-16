package com.thd.mapserver.infrastructure.controller;

import com.thd.mapserver.Parser;
import com.thd.mapserver.infrastructure.service.FeatureCollectionsService;
import com.thd.mapserver.models.responseDtos.CollectionDto;
import com.thd.mapserver.models.responseDtos.FeatureCollectionWithLimitDto;
import com.thd.mapserver.models.responseDtos.ResponseCollectionsDto;
import com.thd.mapserver.postsql.PostgresqlPoiRepository;
import org.geojson.FeatureCollection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class FeatureCollectionsController {

    private final PostgresqlPoiRepository dbConnect = new PostgresqlPoiRepository();

    private final FeatureCollectionsService service = new FeatureCollectionsService();

    @GetMapping("/collections")
    public HttpEntity<ResponseCollectionsDto> getCollections() {
        var res = dbConnect.getAllCollections();

        return new ResponseEntity<>(service.getAllCollectionsResponse(res), HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}")
    public HttpEntity<CollectionDto> getCollection(@PathVariable("collectionId") String collectionId) {

        var res = dbConnect.getCollection(collectionId);
        if(res == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(service.getCollectionResponse(collectionId, res), HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}/items")
    public HttpEntity<String> getItemsLink(@PathVariable("collectionId") String collectionId) {
        if(dbConnect.getCollection(collectionId) == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(service.getItemLinkResponse(collectionId), HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}/items.json")
    public HttpEntity<FeatureCollectionWithLimitDto> getItems(@PathVariable("collectionId") String collectionId,
                                                              @RequestParam(required = false) Integer limit,
                                                              @RequestParam(required = false) Integer offset,
                                                              @RequestParam(required = false) double[] bbox,
                                                              @RequestParam(required = false) String datetime) {
        if(dbConnect.getCollection(collectionId) == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(limit == null){
            limit = 10;
        }
        if(offset == null){
            offset = 0;
        }
        //variable validation
        if(limit < 1 || limit > 10000 || ( bbox != null && (bbox.length < 4 || bbox.length > 6))){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(service.getAllItemsResponse(collectionId, limit, offset, bbox), HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}/items/{featureId}")
    public HttpEntity<String> getFeatureLinks(@PathVariable("collectionId") String collectionId,
                                  @PathVariable("featureId") String featureId) {
        if(!isCollectionValid(collectionId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var resRaw = dbConnect.getFeatureByIdAndType(featureId, collectionId);
        if(resRaw == null || !resRaw.typ.equals(collectionId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(service.getFeatureLinkResponse(collectionId, featureId), HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}/items/{featureId}.json")
    public HttpEntity<FeatureCollection> getFeatures(@PathVariable("collectionId") String collectionId,
                                         @PathVariable("featureId") String featureId) {
        if(!isCollectionValid(collectionId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        var resRaw = dbConnect.getFeatureByIdAndType(featureId, collectionId);
        if(resRaw == null || !resRaw.typ.equals(collectionId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new Parser().parsePoisDescJoin(resRaw), HttpStatus.OK);
    }

    private boolean isCollectionValid(String collectionId){
        return dbConnect.getCollection(collectionId) != null;
    }

}
