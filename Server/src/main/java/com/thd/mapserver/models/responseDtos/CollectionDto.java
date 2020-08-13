package com.thd.mapserver.models.responseDtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CollectionDto {

    /* Mandatory */
    @NotNull
    public String id;
    @NotNull
    public List<LinkDto> links = new ArrayList<>();

    /* Optional */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String itemType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> crs;
}
