package com.thd.mapserver.helper;

import com.thd.mapserver.Settings;

public class ResponseTextBuilder {

    private Settings settings = Settings.getInstance();

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
}
