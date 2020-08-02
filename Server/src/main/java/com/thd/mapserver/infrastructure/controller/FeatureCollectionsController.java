package com.thd.mapserver.infrastructure.controller;

import com.thd.mapserver.helper.DbParseHelper;
import com.thd.mapserver.postsql.PostgresqlPoiRepository;
import org.geojson.FeatureCollection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FeatureCollectionsController {
    @GetMapping("/test")
    public FeatureCollection test() {
        var dbCon = new PostgresqlPoiRepository();
        return DbParseHelper.parsePoisDescJoin(dbCon.getAll());
    }
}
