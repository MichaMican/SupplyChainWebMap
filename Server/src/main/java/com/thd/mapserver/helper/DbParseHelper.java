package com.thd.mapserver.helper;

import com.thd.mapserver.models.PoiDescDbDto;
import org.apache.commons.lang3.NotImplementedException;
import org.geojson.FeatureCollection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DbParseHelper {
    public static FeatureCollection parsePoisDescJoin(List<PoiDescDbDto> elementsToParse) {
        var featureCollection = new FeatureCollection();

        for (var element : elementsToParse) {

        }

        return null;

    }
}
