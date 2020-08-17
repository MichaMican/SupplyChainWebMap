package com.thd.mapserver.models.responseDtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

public class LinkDto {
    /* mandatory */
    @NotNull
    public String href;

    /* optional */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String rel;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String type;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String hreflang;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer length;
}
