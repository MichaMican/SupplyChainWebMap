package com.thd.mapserver.models.responseDtos;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.geojson.FeatureCollection;

@JsonTypeName("FeatureCollection")
public class FeatureCollectionWithLimitDto extends FeatureCollection {
    public int numMatched;
    public int numReturned;
}
