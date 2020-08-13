package com.thd.mapserver.models.DbModels;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeatureTypeDbDto {
    public String typ;
    public String description;
    public String title;

    public FeatureTypeDbDto(String typ, String description, String title) {
        this.typ = typ;
        this.description = description;
        this.title = title;
    }

    public static List<FeatureTypeDbDto> parseDbResponse(ResultSet dbResponseToParse){
        List<FeatureTypeDbDto> rows = new ArrayList<>();

        try {
            while (dbResponseToParse.next()) {
                rows.add(new FeatureTypeDbDto(dbResponseToParse.getString("typ"),
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
