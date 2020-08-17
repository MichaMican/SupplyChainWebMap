package com.thd.mapserver.models.DbModels;

public class FeatureTypeDbDto {
    public String typ;
    public String description;
    public String title;

    public FeatureTypeDbDto(String typ, String description, String title) {
        this.typ = typ;
        this.description = description;
        this.title = title;
    }
}
