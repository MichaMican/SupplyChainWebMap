package com.thd.mapserver.models.responseDtos;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ResponseCollectionDto {
    @NotNull
    public List<LinkDto> links = new ArrayList<>();
    @NotNull
    public CollectionDto collections;
}
