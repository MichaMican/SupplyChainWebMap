package com.thd.mapserver.infrastructure.controller;

import com.thd.mapserver.Settings;
import com.thd.mapserver.helper.DbParseHelper;
import com.thd.mapserver.helper.ResponseHelper;
import com.thd.mapserver.models.responseDtos.CollectionDto;
import com.thd.mapserver.models.responseDtos.LinkDto;
import com.thd.mapserver.models.responseDtos.ResponseCollectionsDto;
import com.thd.mapserver.postsql.PostgresqlPoiRepository;
import org.geojson.FeatureCollection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeatureCollectionsController {

    private ResponseHelper rtb = new ResponseHelper();
    private PostgresqlPoiRepository dbConnect = new PostgresqlPoiRepository();
    private Settings settings = Settings.getInstance();

    @GetMapping("/test")
    public FeatureCollection test() {
        var dbCon = new PostgresqlPoiRepository();
        return DbParseHelper.parsePoisDescJoin(dbCon.getAll());
    }

    @GetMapping("/collections")
    public HttpEntity<ResponseCollectionsDto> getCollections() {

        var res = dbConnect.getAllCollections();

        var returnResponse = new ResponseCollectionsDto();
        for (var collection : res) {
            var collectionInfo = new CollectionDto();

            collectionInfo.id = collection.typ;
            collectionInfo.description = collection.description;
            collectionInfo.title = collection.title;
            var featureLink = new LinkDto();
            featureLink.href = settings.getBaseLink() + "/collections/" + collection.typ;
            featureLink.rel = "self";
            featureLink.type = "application/geo+json";
            collectionInfo.links.add(featureLink);

            returnResponse.collections.add(collectionInfo);
        }

        var collectionLink = new LinkDto();
        collectionLink.href = settings.getBaseLink() + "/collections";
        collectionLink.rel = "self";
        returnResponse.links.add(collectionLink);

        return new ResponseEntity<>(returnResponse, HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}")
    public HttpEntity<CollectionDto> getCollection(@PathVariable("collectionId") String collectionId) {

        var res = dbConnect.getCollection(collectionId);
        if(res == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        var returnResponse = new CollectionDto();
        returnResponse.title = res.title;
        returnResponse.id = res.typ;
        returnResponse.description = res.description;

        var link = new LinkDto();
        link.href = settings.getBaseLink()+"/collections/"+collectionId;
        link.rel = "self";

        returnResponse.links.add(link);

        return new ResponseEntity<>(returnResponse, HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}/items")
    public HttpEntity<String> getItemsLink(@PathVariable("collectionId") String collectionId) {
        if(dbConnect.getCollection(collectionId) == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(rtb.getItemLinkResponse(collectionId), HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}/items.json")
    public HttpEntity<FeatureCollection> getItems(@PathVariable("collectionId") String collectionId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/collections/{collectionId}/items/{featureId}")
    public HttpEntity<String> getFeatureLinks(@PathVariable("collectionId") String collectionId,
                                  @PathVariable("featureId") String featureId) {
        if(dbConnect.getCollection(collectionId) == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(rtb.getFeatureLinkResponse(collectionId, featureId), HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}/items/{featureId}.json")
    public HttpEntity<FeatureCollection> getFeatures(@PathVariable("collectionId") String collectionId,
                                         @PathVariable("featureId") String featureId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}
