package com.thd.mapserver.postsql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.thd.mapserver.Settings;
import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.domain.geom.Point;
import com.thd.mapserver.domain.geom.Polygon;
import com.thd.mapserver.interfaces.PoiRepository;

public class PostgresqlPoiRepository implements PoiRepository {
    private final String connectionString;

    public PostgresqlPoiRepository(){
        Settings settings = Settings.getInstance();
        this.connectionString = settings.getDbConString();
    }

    @Override
    public void add(List<SFAFeature> poi) {
        final var sqlDescriptionString = "INSERT INTO descriptions (typ, description) VALUES (?, ?) ON DUPLICATE KEY UPDATE typ = ?;";
        final var sqlPoiString = "INSERT INTO pois (id, geometry, featureid, descriptiontype) VALUES (?, ST_GeomFromText(?), ?, ?);";
        if(!poi.isEmpty()) {
            try (final var connection = DriverManager.getConnection(connectionString);) {
                UUID featureId = UUID.randomUUID();
                for (SFAFeature feature : poi) {

                    var pstmtDesc = connection.prepareStatement(sqlDescriptionString);

                    pstmtDesc.setObject(1, feature.getProperties().get("typ"));
                    pstmtDesc.setObject(2, feature.getProperties().get("description"));
                    pstmtDesc.setObject(3, feature.getProperties().get("typ"));

                    pstmtDesc.executeUpdate();

                    var pstmtPoi = connection.prepareStatement(sqlPoiString);

                    pstmtPoi.setObject(1, UUID.randomUUID());
                    pstmtPoi.setObject(2, feature.getGeometry().asST_GeomText());
                    pstmtPoi.setObject(3, featureId);
                    pstmtPoi.setObject(4, feature.getProperties().get("typ"));

                    pstmtPoi.executeUpdate();
                }

            } catch (final SQLException e) {
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
}
