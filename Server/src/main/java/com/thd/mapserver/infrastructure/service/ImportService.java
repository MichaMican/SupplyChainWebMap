package com.thd.mapserver.infrastructure.service;

import com.thd.mapserver.Parser;
import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.domain.geom.SFAGeometry;
import com.thd.mapserver.models.featureTypeDto.FeatureTypeDto;
import com.thd.mapserver.postsql.PostgresqlPoiRepository;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;

import java.security.InvalidParameterException;
import java.util.List;

public class ImportService {
    private final PostgresqlPoiRepository dbConnect = new PostgresqlPoiRepository();

    public void parseGeoJsonObject(GeoJsonObject geoJsonObject) throws InvalidParameterException {
        if (geoJsonObject instanceof FeatureCollection) {
            List<Feature> features = ((FeatureCollection) geoJsonObject).getFeatures();
            for (var feature : features) {

                SFAGeometry parsedGeom = new Parser().parseGeometry(feature.getGeometry());

                if (parsedGeom != null) {
                    SFAFeature newSFAFeature = new SFAFeature(
                            feature.getId(),
                            parsedGeom,
                            feature.getProperties(),
                            parsedGeom.geometryType()
                    );
                    dbConnect.add(newSFAFeature);
                } else {
                    throw new InvalidParameterException("only Points and Polygons are currently supported");
                }
            }
        } else {
            throw new InvalidParameterException("only GeoJson featureCollections are supported");
        }
    }

    public void addFeatureTypes(FeatureTypeDto featureType) {
        PostgresqlPoiRepository dbConnect = new PostgresqlPoiRepository();
        dbConnect.addFeatureType(featureType);
    }
}
