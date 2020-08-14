package com.thd.mapserver.models.responseDtos;

import org.geojson.FeatureCollection;

public class FeatureCollectionWithLimitDto extends FeatureCollection {
    public int numMatched;
    public int numReturned;
}
