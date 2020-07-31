package com.thd.mapserver.models;

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
}
