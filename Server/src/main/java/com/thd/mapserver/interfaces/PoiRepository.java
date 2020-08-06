package com.thd.mapserver.interfaces;

import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.models.Coordinate;
import com.thd.mapserver.models.PoiDescDbDto;
import com.thd.mapserver.models.featureTypeDto.CollectionDefinitionDto;
import com.thd.mapserver.models.featureTypeDto.FeatureTypeDto;

import java.util.List;

public interface PoiRepository {
    void add(List<SFAFeature> poi);
    void add(SFAFeature poi);
    void addFeatureType(FeatureTypeDto featureType);
    void addCollections(List<CollectionDefinitionDto> collections);
    List<PoiDescDbDto> getAll();
    List<PoiDescDbDto> getByType(String type);
    List<PoiDescDbDto> getByType(List<String> types);
    List<PoiDescDbDto> getByType(String... types);
    List<PoiDescDbDto> getByBboxAndType(List<Coordinate> bbox, String type);
}
