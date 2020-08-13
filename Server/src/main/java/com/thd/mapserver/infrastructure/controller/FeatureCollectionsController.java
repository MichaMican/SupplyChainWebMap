package com.thd.mapserver.infrastructure.controller;

import com.thd.mapserver.Settings;
import com.thd.mapserver.helper.DbParseHelper;
import com.thd.mapserver.helper.ResponseHelper;
import com.thd.mapserver.models.Coordinate;
import com.thd.mapserver.models.DbModels.PoiTypeDbDto;
import com.thd.mapserver.models.responseDtos.CollectionDto;
import com.thd.mapserver.models.responseDtos.LinkDto;
import com.thd.mapserver.models.responseDtos.ResponseCollectionsDto;
import com.thd.mapserver.postsql.PostgresqlPoiRepository;
import org.geojson.FeatureCollection;
import org.geojson.Polygon;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class FeatureCollectionsController {

    private ResponseHelper rtb = new ResponseHelper();
    private PostgresqlPoiRepository dbConnect = new PostgresqlPoiRepository();
    private Settings settings = Settings.getInstance();

    //TODO: Remove before release
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
            featureLink.href = settings.getBaseLink() + "/collections/" + collection.typ + "/items.json";
            featureLink.rel = "items";
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
        link.href = settings.getBaseLink()+"/collections/"+collectionId+"/items.json";
        link.rel = "item";
        link.type = "application/geo+json";

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
    public HttpEntity<FeatureCollection> getItems(@PathVariable("collectionId") String collectionId,
                                                  @RequestParam(required = false) Integer limit,
                                                  @RequestParam(required = false) double[] bbox,
                                                  @RequestParam(required = false) String datetime) {
        if(dbConnect.getCollection(collectionId) == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(limit == null){
            limit = 10;
        }

        //variable validation
        if(limit < 1 || limit > 10000 || ( bbox != null && (bbox.length < 4 || bbox.length > 6))){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<PoiTypeDbDto> dbResRaw;

        if(bbox != null){
            List<Coordinate> bboxCors = new ArrayList<>();
            double x1, y1, x2, y2;
            switch (bbox.length){
                case 4:
                    x1 = bbox[0];
                    y1 = bbox[1];
                    x2 = bbox[2];
                    y2 = bbox[3];
                    break;
                case 5:
                    /* Fall through */
                case 6:
                    x1 = bbox[0];
                    y1 = bbox[1];
                    x2 = bbox[3];
                    y2 = bbox[4];
                    break;
                default:
                    //Will never get hit (because variable already was validated (line 110))
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            //Left lower
            bboxCors.add(new Coordinate(x1, y1));
            //Left upper
            bboxCors.add(new Coordinate(x1, y2));
            //Right upper
            bboxCors.add(new Coordinate(x2, y2));
            //Right lower
            bboxCors.add(new Coordinate(x2, y1));
            //Left lower - Yes this is really necessary because of definition the endpoint has to be specificly spacified as the firs
            bboxCors.add(new Coordinate(x1, y1));

            dbResRaw = dbConnect.getByBboxAndType(bboxCors, collectionId);

        } else {
            dbResRaw = dbConnect.getByType(collectionId);
        }

        var dbRes = dbResRaw;
        if(dbRes.size() > limit){
            dbRes = dbResRaw.subList(0, limit);
        }

        var response= DbParseHelper.parsePoisDescJoin(dbRes);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}/items/{featureId}")
    public HttpEntity<String> getFeatureLinks(@PathVariable("collectionId") String collectionId,
                                  @PathVariable("featureId") String featureId) {
        if(!isCollectionValid(collectionId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var resRaw = dbConnect.getFeatureById(featureId);
        if(resRaw == null || !resRaw.typ.equals(collectionId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(rtb.getFeatureLinkResponse(collectionId, featureId), HttpStatus.OK);
    }

    @GetMapping("/collections/{collectionId}/items/{featureId}.json")
    public HttpEntity<FeatureCollection> getFeatures(@PathVariable("collectionId") String collectionId,
                                         @PathVariable("featureId") String featureId) {
        if(!isCollectionValid(collectionId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        var resRaw = dbConnect.getFeatureById(featureId);
        if(resRaw == null || !resRaw.typ.equals(collectionId)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(DbParseHelper.parsePoisDescJoin(resRaw), HttpStatus.OK);
    }

    private boolean isCollectionValid(String collectionId){
        return dbConnect.getCollection(collectionId) != null;
    }

}
