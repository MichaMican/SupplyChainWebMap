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
    public static FeatureCollection parsePoisDescJoin(ResultSet dbResponseToParse){
        var featureCollection = new FeatureCollection();

        List<PoiDescDbDto> rows = new ArrayList<>();

        try {
            while (dbResponseToParse.next()) {
                rows.add(new PoiDescDbDto(UUID.fromString(dbResponseToParse.getString(1)),
                        dbResponseToParse.getString(2),
                        dbResponseToParse.getString(3),
                        dbResponseToParse.getString(4))
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }
}
