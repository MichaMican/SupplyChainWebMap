package com.thd.mapserver.models.DbModels;

import java.util.List;

public class DbLimitResponse {
    public int numMatched;
    public int numReturned;
    public List<PoiTypeDbDto> data;

    public DbLimitResponse(List<PoiTypeDbDto> data, int numReturned, int numMatched) {
        this.numMatched = numMatched;
        this.numReturned = numReturned;
        this.data = data;
    }
}
