package com.thd.mapserver.models.responseDtos;

import java.util.List;

public class CollectionDto {

    /* Mandatory */
    public String id;
    public List<LinkDto> links;

    /* Optional */
    public String title;
    public String description;
    public String itemType;
    public List<String> crs;
}
