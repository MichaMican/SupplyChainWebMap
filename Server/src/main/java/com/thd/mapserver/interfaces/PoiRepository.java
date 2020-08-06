package com.thd.mapserver.interfaces;

import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.models.Coordinate;
import com.thd.mapserver.models.DbModels.FeatureTypeDbDto;
import com.thd.mapserver.models.DbModels.PoiTypeDbDto;
import com.thd.mapserver.models.featureTypeDto.CollectionDefinitionDto;
import com.thd.mapserver.models.featureTypeDto.FeatureTypeDto;

import java.util.List;

public interface PoiRepository {
    void add(List<SFAFeature> poi);
    void add(SFAFeature poi);
    void addFeatureType(FeatureTypeDto featureType);
    void addCollections(List<CollectionDefinitionDto> collections);
    List<PoiTypeDbDto> getAll();
    List<PoiTypeDbDto> getByType(String type);
    List<PoiTypeDbDto> getByType(List<String> types);
    List<PoiTypeDbDto> getByType(String... types);
    List<PoiTypeDbDto> getByBboxAndType(List<Coordinate> bbox, String type);
    List<FeatureTypeDbDto> getAllCollections();
    FeatureTypeDbDto getCollection(String collectionId);
    PoiTypeDbDto getFeatureById(String featurenId);
}
