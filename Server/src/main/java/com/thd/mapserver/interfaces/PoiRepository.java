package com.thd.mapserver.interfaces;

import com.thd.mapserver.domain.SFAFeature;

import java.util.List;

public interface PoiRepository {
    void add(List<SFAFeature> poi);
    void add(SFAFeature poi);
}
