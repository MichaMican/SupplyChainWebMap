package com.thd.mapserver.infrastructure.service;

import com.thd.mapserver.Parser;
import com.thd.mapserver.Settings;
import com.thd.mapserver.models.Coordinate;
import com.thd.mapserver.models.DbModels.DbLimitResponse;
import com.thd.mapserver.models.DbModels.FeatureTypeDbDto;
import com.thd.mapserver.models.responseDtos.CollectionDto;
import com.thd.mapserver.models.responseDtos.FeatureCollectionWithLimitDto;
import com.thd.mapserver.models.responseDtos.LinkDto;
import com.thd.mapserver.models.responseDtos.ResponseCollectionsDto;
import com.thd.mapserver.postsql.PostgresqlPoiRepository;


import java.util.ArrayList;
import java.util.List;

public class FeatureCollectionsService {
    private final PostgresqlPoiRepository dbConnect = new PostgresqlPoiRepository();
    private final Settings settings = Settings.getInstance();

    public ResponseCollectionsDto getAllCollectionsResponse(List<FeatureTypeDbDto> res) {

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
            featureLink = new LinkDto();
            featureLink.href = settings.getBaseLink() + "/collections/" + collection.typ
            ;
            featureLink.rel = "self";
            featureLink.type = "application/json";
            collectionInfo.links.add(featureLink);

            returnResponse.collections.add(collectionInfo);
        }

        var collectionLink = new LinkDto();
        collectionLink.href = settings.getBaseLink() + "/collections";
        collectionLink.rel = "self";
        returnResponse.links.add(collectionLink);

        return returnResponse;
    }

    public CollectionDto getCollectionResponse(String collectionId, FeatureTypeDbDto res) {
        var returnResponse = new CollectionDto();
        returnResponse.title = res.title;
        returnResponse.id = res.typ;
        returnResponse.description = res.description;

        var link = new LinkDto();
        link.href = settings.getBaseLink()+"/collections/"+collectionId+"/items.json";
        link.rel = "item";
        link.type = "application/geo+json";
        returnResponse.links.add(link);

        link = new LinkDto();
        link.href = settings.getBaseLink()+"/collections/"+collectionId;
        link.rel = "self";
        link.type = "application/json";
        returnResponse.links.add(link);

        return returnResponse;
    }

    public String getItemLinkResponse(String collection){
        return String.format("Link: <%s/collections/%s/items.json>; " +
                "rel=\"self\"; type=\"application/geo+json\"", settings.getBaseLink() , collection);
    }

    public String getFeatureLinkResponse(String collection, String featureId){
        return String.format("Link: <%s/collections/%s/items/%s.json>; " +
                "rel=\"self\"; type=\"application/geo+json\"", settings.getBaseLink(), collection, featureId) + "\n" +
                String.format("Link: <%s/collections/%s.json>; " +
                        "rel=\"collection\"; type=\"application/json\"", settings.getBaseLink(), collection);
    }

    public FeatureCollectionWithLimitDto getAllItemsResponse(String collectionId, Integer limit, Integer offset, double[] bbox) {
        DbLimitResponse dbResRaw;

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
                    return null;
            }

            //Left lower
            bboxCors.add(new Coordinate(x1, y1));
            //Left upper
            bboxCors.add(new Coordinate(x1, y2));
            //Right upper
            bboxCors.add(new Coordinate(x2, y2));
            //Right lower
            bboxCors.add(new Coordinate(x2, y1));
            //Left lower - Yes this is really necessary because of definition the endpoint has to be specifically specified as the firs
            bboxCors.add(new Coordinate(x1, y1));

            dbResRaw = dbConnect.getByBboxAndType(bboxCors, collectionId, limit, offset);

        } else {

            dbResRaw = dbConnect.getByType(collectionId, limit, offset);
        }

        return new Parser().parsePoisDescJoin(dbResRaw);
    }
}
