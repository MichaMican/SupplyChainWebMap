package com.thd.mapserver.models.DbModels;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PoiTypeDbDto {
    public String id;
    public String geometry_asgeojson;
    public String typ;
    public String description;
    public String title;

    public PoiTypeDbDto(String id, String geometry_asGeoJson, String typ, String description, String title) {
        this.id = id;
        this.geometry_asgeojson = geometry_asGeoJson;
        this.typ = typ;
        this.description = description;
        this.title = title;
    }

    public static List<PoiTypeDbDto> parseDbResponse(ResultSet dbResponseToParse){
        List<PoiTypeDbDto> rows = new ArrayList<>();

        try {
            while (dbResponseToParse.next()) {
                rows.add(new PoiTypeDbDto(dbResponseToParse.getString("id"),
                        dbResponseToParse.getString("geometry_asgeojson"),
                        dbResponseToParse.getString("typ"),
                        dbResponseToParse.getString("description"),
                        dbResponseToParse.getString("title"))
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return rows;
    }
}
