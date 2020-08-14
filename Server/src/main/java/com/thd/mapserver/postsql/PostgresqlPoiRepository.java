package com.thd.mapserver.postsql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import com.thd.mapserver.Settings;
import com.thd.mapserver.domain.Parser;
import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.domain.geom.LinearRing;
import com.thd.mapserver.domain.geom.Polygon;
import com.thd.mapserver.helper.GeometryHelper;
import com.thd.mapserver.interfaces.PoiRepository;
import com.thd.mapserver.models.Coordinate;
import com.thd.mapserver.models.DbModels.DbLimitResponse;
import com.thd.mapserver.models.DbModels.FeatureTypeDbDto;
import com.thd.mapserver.models.DbModels.PoiTypeDbDto;
import com.thd.mapserver.models.featureTypeDto.CollectionDefinitionDto;
import com.thd.mapserver.models.featureTypeDto.FeatureTypeDto;

@SuppressWarnings("SqlNoDataSourceInspection")
public class PostgresqlPoiRepository implements PoiRepository {
    private final String connectionString;

    public PostgresqlPoiRepository() {
        Settings settings = Settings.getInstance();
        this.connectionString = settings.getDbConString();
    }

    @Override
    public void add(List<SFAFeature> poi) {
        final var sqlDescriptionString = "INSERT INTO collections (typ, description, title) VALUES (?, ?, ?) " +
                "ON CONFLICT (typ) DO NOTHING;";
        final var sqlPoiString = "INSERT INTO pois (id, geometry, descriptiontype) VALUES (?, ST_GeomFromText(?, 4326), ?) " +
                "ON CONFLICT (geometry, descriptiontype) DO NOTHING;";
        if (!poi.isEmpty()) {
            try (final var connection = DriverManager.getConnection(connectionString)) {
                for (SFAFeature feature : poi) {

                    var pstmtDesc = connection.prepareStatement(sqlDescriptionString);

                    pstmtDesc.setObject(1, feature.getProperties().get("typ").toString().toLowerCase());
                    pstmtDesc.setObject(2, feature.getProperties().get("description").toString());
                    pstmtDesc.setObject(3, feature.getProperties().get("typ").toString());

                    pstmtDesc.executeUpdate();

                    var pstmtPoi = connection.prepareStatement(sqlPoiString);

                    String poiId = feature.getId();

                    if (poiId == null || poiId.isEmpty()) {
                        poiId = UUID.randomUUID().toString();
                    }

                    pstmtPoi.setObject(1, poiId);
                    pstmtPoi.setObject(2, feature.getGeometry().asText());
                    pstmtPoi.setObject(3, feature.getProperties().get("typ").toString().toLowerCase());

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
        if (!collections.isEmpty()) {
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
    public List<PoiTypeDbDto> getAll() {
        final var sqlQuery = "SELECT p.id, ST_AsGeoJSON(p.geometry) as geometry_asgeojson, d.typ, d.description, d.title " +
                "FROM pois p LEFT JOIN collections d ON p.descriptiontype = d.typ;";

        try (final var connection = DriverManager.getConnection(connectionString)) {
            var pstmt = connection.prepareStatement(sqlQuery);
            var res = pstmt.executeQuery();
            return new Parser().parseDbResponsePoiType(res);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

    @Override
    public DbLimitResponse getByType(String type, Integer limit) {
        return getByType(type, limit, 0);
    }

    @Override
    public DbLimitResponse getByType(String type) {
        return getByType(type, null, 0);
    }

    @Override
    public DbLimitResponse getByType(String type, Integer limit, int offset) {
        var list = new ArrayList<String>();
        list.add(type);
        return getByType(list, limit, offset);
    }

    @Override
    public DbLimitResponse getByType(List<String> types, Integer limit) {
        return getByType(types, limit, 0);
    }

    @Override
    public DbLimitResponse getByType(List<String> types) {
        return getByType(types, null, 0);
    }

    @Override
    public DbLimitResponse getByType(List<String> types, Integer limit, int offset) {
        String sqlQuery;
        String sqlCountQuery;

        if (types.isEmpty()) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder("SELECT p.id, ST_AsGeoJSON(p.geometry) as geometry_asgeojson, d.typ, d.description, d.title " +
                    "FROM pois p LEFT JOIN collections d ON p.descriptiontype = d.typ WHERE ");

            StringBuilder sbC = new StringBuilder("SELECT COUNT(*) FROM pois p LEFT JOIN collections d ON p.descriptiontype = d.typ WHERE ");

            Iterator<String> typIter = types.iterator();
            while (typIter.hasNext()) {
                typIter.next();

                sb.append("typ = ?");
                sbC.append("typ = ?");

                if (typIter.hasNext()) {
                    sb.append(" OR ");
                    sbC.append(" OR ");
                }
            }

            if (limit != null) {
                sb.append("LIMIT ? OFFSET ?");
            }

            sqlQuery = sb.toString();
            sqlCountQuery = sbC.toString();
        }

        try (final var connection = DriverManager.getConnection(connectionString)) {
            var pstmt = connection.prepareStatement(sqlQuery);
            var pstmtC = connection.prepareStatement(sqlCountQuery);

            for (int i = 0; i < types.size(); i++) {
                pstmt.setObject(i + 1, types.get(i));
                pstmtC.setObject(i + 1, types.get(i));
            }

            if (limit != null) {
                pstmt.setObject(types.size() + 1, limit);
                pstmt.setObject(types.size() + 2, offset);
            }

            var countRes = pstmtC.executeQuery();
            countRes.next();
            int numMatched = countRes.getInt("count");
            var resRaw = pstmt.executeQuery();
            var res = new Parser().parseDbResponsePoiType(resRaw);


            return new DbLimitResponse(res, res.size(), numMatched);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    @Override
    public DbLimitResponse getByBboxAndType(List<Coordinate> bbox, String type, Integer limit, int offset) {
        String sqlQuery = "SELECT p.id, ST_AsGeoJSON(p.geometry) as geometry_asgeojson, d.typ, d.description, d.title " +
                "FROM pois p LEFT JOIN collections d ON p.descriptiontype = d.typ WHERE " +
                "d.typ = ? AND ST_Intersects(p.geometry, ST_GeomFromText(?, 4326)) LIMIT ? OFFSET ?";

        String sqlCountQuery = "SELECT COUNT(*) " +
                "FROM pois p LEFT JOIN collections d ON p.descriptiontype = d.typ WHERE " +
                "d.typ = ? AND ST_Intersects(p.geometry, ST_GeomFromText(?, 4326))";

        try (final var connection = DriverManager.getConnection(connectionString)) {
            var pstmt = connection.prepareStatement(sqlQuery);
            var pstmtC = connection.prepareStatement(sqlCountQuery);

            pstmt.setObject(1, type);
            pstmtC.setObject(1, type);
            pstmt.setObject(2, new Polygon(new LinearRing(GeometryHelper.convertCoordinateListToPointList(bbox))).asText());
            pstmtC.setObject(2, new Polygon(new LinearRing(GeometryHelper.convertCoordinateListToPointList(bbox))).asText());
            pstmt.setObject(3, limit);
            pstmt.setObject(4, offset);

            var countRes = pstmtC.executeQuery();
            countRes.next();
            int numMatched = countRes.getInt("count");


            var resRaw = pstmt.executeQuery();
            var res = new Parser().parseDbResponsePoiType(resRaw);
            return new DbLimitResponse(res, res.size(), numMatched);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    @Override
    public DbLimitResponse getByBboxAndType(List<Coordinate> bbox, String type, Integer limit) {
        return getByBboxAndType(bbox, type, limit, 0);
    }

    @Override
    public DbLimitResponse getByBboxAndType(List<Coordinate> bbox, String type) {
        return getByBboxAndType(bbox, type, null);
    }

    @Override
    public List<FeatureTypeDbDto> getAllCollections() {
        final var sqlQuery = "SELECT * FROM collections;";

        try (final var connection = DriverManager.getConnection(connectionString)) {
            var pstmt = connection.prepareStatement(sqlQuery);
            var res = pstmt.executeQuery();
            return new Parser().parseDbResponseFeatureType(res);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    @Override
    public FeatureTypeDbDto getCollection(String collectionId) {
        final var sqlQuery = "SELECT * FROM collections WHERE typ = ?;";

        try (final var connection = DriverManager.getConnection(connectionString)) {
            var pstmt = connection.prepareStatement(sqlQuery);
            pstmt.setObject(1, collectionId);
            var resRaw = pstmt.executeQuery();
            var res = new Parser().parseDbResponseFeatureType(resRaw);
            if (!res.isEmpty()) {
                return res.get(0);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    @Override
    public PoiTypeDbDto getFeatureById(String featurenId) {

        final var sqlQuery = "SELECT p.id, ST_AsGeoJSON(p.geometry) as geometry_asgeojson, d.typ, d.description, d.title " +
                "FROM pois p LEFT JOIN collections d ON p.descriptiontype = d.typ WHERE p.id = ?;";

        try (final var connection = DriverManager.getConnection(connectionString)) {
            var pstmt = connection.prepareStatement(sqlQuery);

            pstmt.setObject(1, featurenId);

            var resRaw = pstmt.executeQuery();
            var res = new Parser().parseDbResponsePoiType(resRaw);
            if (!res.isEmpty()) {
                return res.get(0);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }


}
