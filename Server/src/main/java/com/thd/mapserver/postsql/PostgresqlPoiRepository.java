package com.thd.mapserver.postsql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.thd.mapserver.Settings;
import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.helper.DbParseHelper;
import com.thd.mapserver.interfaces.PoiRepository;
import org.apache.commons.lang3.NotImplementedException;
import org.geojson.FeatureCollection;

public class PostgresqlPoiRepository implements PoiRepository {
    private final String connectionString;

    public PostgresqlPoiRepository(){
        Settings settings = Settings.getInstance();
        this.connectionString = settings.getDbConString();
    }

    @Override
    public void add(List<SFAFeature> poi) {
        final var sqlDescriptionString = "INSERT INTO descriptions (typ, description) VALUES (?, ?) ON CONFLICT (typ) DO UPDATE SET description=?;";
        final var sqlPoiString = "INSERT INTO pois (id, geometry, descriptiontype) VALUES (?, ST_GeomFromText(?), ?) ON CONFLICT (id) DO NOTHING;";
        if(!poi.isEmpty()) {
            try (final var connection = DriverManager.getConnection(connectionString)) {
                UUID featureId = UUID.randomUUID();
                for (SFAFeature feature : poi) {

                    var pstmtDesc = connection.prepareStatement(sqlDescriptionString);

                    pstmtDesc.setObject(1, feature.getProperties().get("typ"));
                    pstmtDesc.setObject(2, feature.getProperties().get("description"));
                    pstmtDesc.setObject(3, feature.getProperties().get("description"));

                    pstmtDesc.executeUpdate();

                    var pstmtPoi = connection.prepareStatement(sqlPoiString);

                    pstmtPoi.setObject(1, UUID.randomUUID());
                    pstmtPoi.setObject(2, feature.getGeometry().asText());
                    pstmtPoi.setObject(3, feature.getProperties().get("typ"));

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

    public FeatureCollection getAll(){
        final var sqlQuery = "SELECT p.id, ST_AsText(p.geometry) as geometry_astext, d.typ, d.description " +
                "FROM pois p LEFT JOIN descriptions d ON p.descriptiontype = d.typ;";

        try(final var connection = DriverManager.getConnection(connectionString)){
            var pstmt = connection.prepareStatement(sqlQuery);
            var res = pstmt.executeQuery();
            return DbParseHelper.parsePoisDescJoin(res);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

    public FeatureCollection getByType(String type){
        var list = new ArrayList<String>();
        list.add(type);
        return getByType(list);
    }

    public FeatureCollection getByType(List<String> types){
        throw new NotImplementedException();
    }
}
