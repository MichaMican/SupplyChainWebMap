package com.thd.mapserver.postsql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.domain.geom.Point;
import com.thd.mapserver.interfaces.PoiRepository;

public class PostgresqlPoiRepository implements PoiRepository {
    private final String connectionString;

    public PostgresqlPoiRepository(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public void add(SFAFeature poi) {
        final var sqlUpdateStr = "INSERT INTO poi (srid, geometry, typ, description) VALUES (?, ST_GeomFromText(?, ?), ?, ?);";

        try (final var connection = DriverManager.getConnection(connectionString);
             var pstmt = connection.prepareStatement(sqlUpdateStr);) {
            final var point = (Point) poi.getGeometry();

            pstmt.setObject(1, UUID.fromString(poi.getId()));
            final var wkt = String.format(Locale.ROOT, "POINT(%f %f)", point.getCoordinate().getX(), point.getCoordinate().getY());
            pstmt.setString(2, wkt);
            pstmt.setInt(3, point.srid());
            pstmt.setString(4, (String) poi.getProperties().get("typ"));
            pstmt.setString(5, (String) poi.getProperties().get("description"));

            pstmt.executeUpdate();
        } catch (final SQLException e) {
            throw new PostgresqlException("Could not save the poi feature.", e);
        }
    }

}
