package com.thd.mapserver.models.DbModels;

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


}
