package com.thd.mapserver.postsql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import com.thd.mapserver.Settings;
import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.domain.geom.Polygon;
import com.thd.mapserver.interfaces.PoiRepository;
import com.thd.mapserver.models.Coordinate;
import com.thd.mapserver.models.DbModels.FeatureTypeDbDto;
import com.thd.mapserver.models.DbModels.PoiTypeDbDto;
import com.thd.mapserver.models.featureTypeDto.CollectionDefinitionDto;
import com.thd.mapserver.models.featureTypeDto.FeatureTypeDto;

@SuppressWarnings("SqlNoDataSourceInspection")
public class PostgresqlPoiRepository implements PoiRepository {
    private final String connectionString;

    public PostgresqlPoiRepository(){
        Settings settings = Settings.getInstance();
        this.connectionString = settings.getDbConString();
    }

    @Override
    public void add(List<SFAFeature> poi) {
        final var sqlDescriptionString = "INSERT INTO collections (typ, description, title) VALUES (?, ?, ?) " +
                "ON CONFLICT (typ) DO NOTHING;";
        final var sqlPoiString = "INSERT INTO pois (geometry, descriptiontype) VALUES (ST_GeomFromText(?), ?) " +
                "ON CONFLICT (geometry, descriptiontype) DO NOTHING;";
        if(!poi.isEmpty()) {
            try (final var connection = DriverManager.getConnection(connectionString)) {
                for (SFAFeature feature : poi) {

                    var pstmtDesc = connection.prepareStatement(sqlDescriptionString);

                    pstmtDesc.setObject(1, feature.getProperties().get("typ").toString().toLowerCase());
                    pstmtDesc.setObject(2, feature.getProperties().get("description").toString());
                    pstmtDesc.setObject(3, feature.getProperties().get("typ").toString());

                    pstmtDesc.executeUpdate();

                    var pstmtPoi = connection.prepareStatement(sqlPoiString);

                    pstmtPoi.setObject(1, feature.getGeometry().asText());
                    pstmtPoi.setObject(2, feature.getProperties().get("typ").toString().toLowerCase());

                    pstmtPoi.executeUpdate();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                throw new PostgresqlException("Could not save the poi feature.", e);
            }
        }
    }

    @Override
    public void add(SFAFeature poi) {
        var list = new ArrayList<SFAFeature>();
        list.add(poi);
        add(list);
    }

    @Override
    public void addFeatureType(FeatureTypeDto featureTypes) {
        addCollections(featureTypes.collections);
    }

    @Override
    public void addCollections(List<CollectionDefinitionDto> collections) {
        final var sqlQuery = "INSERT INTO collections (typ, description, title) VALUES (?, ?, ?) " +
                "ON CONFLICT (typ) DO UPDATE SET description = ?, title = ?;";
        if(!collections.isEmpty()) {
            try (final var connection = DriverManager.getConnection(connectionString)) {
                for (CollectionDefinitionDto collection : collections) {
                    var pstmt = connection.prepareStatement(sqlQuery);

                    pstmt.setObject(1, collection.id);
                    pstmt.setObject(2, collection.description);
                    pstmt.setObject(3, collection.title);
                    pstmt.setObject(4, collection.description);
                    pstmt.setObject(5, collection.title);

                    pstmt.executeUpdate();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                throw new PostgresqlException("Could not save the poi feature.", e);
            }
        }
    }

    @Override
    public List<PoiTypeDbDto> getAll(){
        final var sqlQuery = "SELECT p.id, ST_AsGeoJSON(p.geometry) as geometry_asgeojson, d.typ, d.description " +
                "FROM pois p LEFT JOIN descriptions d ON p.descriptiontype = d.typ;";

        try(final var connection = DriverManager.getConnection(connectionString)){
            var pstmt = connection.prepareStatement(sqlQuery);
            var res = pstmt.executeQuery();
            return PoiTypeDbDto.parseDbResponse(res);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

    @Override
    public List<PoiTypeDbDto> getByType(String type){
        var list = new ArrayList<String>();
        list.add(type);
        return getByType(list);
    }

    @Override
    public List<PoiTypeDbDto> getByType(List<String> types){
        String sqlQuery;

        if(types.isEmpty()){
            return null;
        } else {
            StringBuilder sb = new StringBuilder("SELECT p.id, ST_AsText(p.geometry) as geometry_astext, d.typ, d.description " +
                    "FROM pois p LEFT JOIN descriptions d ON p.descriptiontype = d.typ WHERE ");

            Iterator<String> typIter = types.iterator();
            while (typIter.hasNext()){
                typIter.next();

                sb.append("typ = ?");

                if(typIter.hasNext()){
                    sb.append(" OR ");
                }
            }

            sqlQuery = sb.toString();
        }

        try(final var connection = DriverManager.getConnection(connectionString)){
            var pstmt = connection.prepareStatement(sqlQuery);

            for(int i = 0; i < types.size(); i++){
                pstmt.setObject(i+1, types.get(i));
            }

            var res = pstmt.executeQuery();
            return PoiTypeDbDto.parseDbResponse(res);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public List<PoiTypeDbDto> getByType(String... types){
        List<String> list = Arrays.asList(types);
        return getByType(list);
    }

    @Override
    public List<PoiTypeDbDto> getByBboxAndType(List<Coordinate> bbox, String type) {
        String sqlQuery = "SELECT p.id, ST_AsText(p.geometry) as geometry_astext, d.typ, d.description " +
                    "FROM pois p LEFT JOIN descriptions d ON p.descriptiontype = d.typ WHERE " +
                "d.typ = ? AND ST_Overlaps(p.geometry, ST_GeomFromText(?))";

        try(final var connection = DriverManager.getConnection(connectionString)){
            var pstmt = connection.prepareStatement(sqlQuery);

            pstmt.setObject(1, type);
            pstmt.setObject(2, new Polygon(bbox, null, 0).asText());

            var res = pstmt.executeQuery();
            return PoiTypeDbDto.parseDbResponse(res);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    @Override
    public List<FeatureTypeDbDto> getAllCollections() {
        final var sqlQuery = "SELECT * FROM collections;";

        try(final var connection = DriverManager.getConnection(connectionString)){
            var pstmt = connection.prepareStatement(sqlQuery);
            var res = pstmt.executeQuery();
            return FeatureTypeDbDto.parseDbResponse(res);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }
}
