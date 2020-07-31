package com.thd.mapserver.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PoiDescDbDto {
    public UUID id;
    public String geometry_astext;
    public String typ;
    public String description;

    public PoiDescDbDto(UUID id, String geometry_astext, String typ, String description) {
        this.id = id;
        this.geometry_astext = geometry_astext;
        this.typ = typ;
        this.description = description;
    }

    public static List<PoiDescDbDto> parseDbResponse(ResultSet dbResponseToParse){
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

        return rows;
    }
}
