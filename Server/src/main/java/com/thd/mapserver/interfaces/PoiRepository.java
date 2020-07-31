package com.thd.mapserver.interfaces;

import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.models.PoiDescDbDto;
import org.geojson.FeatureCollection;

import java.util.List;

public interface PoiRepository {
    void add(List<SFAFeature> poi);
    void add(SFAFeature poi);
    List<PoiDescDbDto> getAll();
    List<PoiDescDbDto> getByType(String type);
    List<PoiDescDbDto> getByType(List<String> types);
    List<PoiDescDbDto> getByType(String... types);
}
