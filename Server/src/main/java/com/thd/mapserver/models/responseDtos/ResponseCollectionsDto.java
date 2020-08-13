package com.thd.mapserver.models.responseDtos;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ResponseCollectionsDto {
    @NotNull
    public List<LinkDto> links = new ArrayList<>();
    @NotNull
    public List<CollectionDto> collections = new ArrayList<>();
}
