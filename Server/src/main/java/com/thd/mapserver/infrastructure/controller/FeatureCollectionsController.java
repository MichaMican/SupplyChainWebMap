package com.thd.mapserver.infrastructure.controller;

import com.thd.mapserver.models.PoiDescDbDto;
import com.thd.mapserver.postsql.PostgresqlPoiRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FeatureCollectionsController {
    @GetMapping("/test")
    public List<PoiDescDbDto> test() {
        var dbCon = new PostgresqlPoiRepository();
        return dbCon.getByType("Gold", "Zinn");
    }
}
