package com.thd.mapserver.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thd.mapserver.models.PoiDescDbDto;
import org.apache.commons.lang3.NotImplementedException;
import org.geojson.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public final class DbParseHelper {
    public static FeatureCollection parsePoisDescJoin(List<PoiDescDbDto> elementsToParse) {
        var featureCollection = new FeatureCollection();

        for (var element : elementsToParse) {

            try {
                //prepare properties for parsing
                Map<String, Object> properties = new HashMap<>();

                properties.put("typ", element.typ);
                properties.put("description", element.description);

                //prepare geometry for parsing
                GeoJsonObject rawObject = new ObjectMapper().readValue(element.geometry_asGeoJson, GeoJsonObject.class);

                Feature feature = new Feature();
                if(rawObject instanceof GeometryCollection || rawObject instanceof Point ||
                        rawObject instanceof Polygon || rawObject instanceof  MultiPolygon){
                    feature.setGeometry(rawObject);
                }else {
                    continue;
                }

                feature.setProperties(properties);

                featureCollection.add(feature);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return featureCollection;

    }
}
