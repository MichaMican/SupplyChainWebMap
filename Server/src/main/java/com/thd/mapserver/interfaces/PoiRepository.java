package com.thd.mapserver.interfaces;

import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.models.Coordinate;
import com.thd.mapserver.models.DbModels.DbLimitResponse;
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
    DbLimitResponse getByType(String type, Integer limit, int offset);
    DbLimitResponse getByType(String type, Integer limit);
    DbLimitResponse getByType(String type);
    DbLimitResponse getByType(List<String> types, Integer limit, int offset);
    DbLimitResponse getByType(List<String> types, Integer limit);
    DbLimitResponse getByType(List<String> types);
    DbLimitResponse getByBboxAndType(List<Coordinate> bbox, String type, Integer limit, int offset);
    DbLimitResponse getByBboxAndType(List<Coordinate> bbox, String type, Integer limit);
    DbLimitResponse getByBboxAndType(List<Coordinate> bbox, String type);
    List<FeatureTypeDbDto> getAllCollections();
    FeatureTypeDbDto getCollection(String collectionId);
    PoiTypeDbDto getFeatureById(String featurenId);
    public PoiTypeDbDto getFeatureByIdAndType(String featurenId, String collectionId);
}
