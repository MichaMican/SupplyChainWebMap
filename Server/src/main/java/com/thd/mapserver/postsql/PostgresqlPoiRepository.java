package com.thd.mapserver.postsql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import com.thd.mapserver.Settings;
import com.thd.mapserver.domain.SFAFeature;
import com.thd.mapserver.helper.DbParseHelper;
import com.thd.mapserver.interfaces.PoiRepository;
import com.thd.mapserver.models.PoiDescDbDto;
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
        final var sqlPoiString = "INSERT INTO pois (geometry, descriptiontype) VALUES (ST_GeomFromText(?), ?) ON CONFLICT (geometry, descriptiontype) DO NOTHING;";
        if(!poi.isEmpty()) {
            try (final var connection = DriverManager.getConnection(connectionString)) {
                for (SFAFeature feature : poi) {

                    var pstmtDesc = connection.prepareStatement(sqlDescriptionString);

                    pstmtDesc.setObject(1, feature.getProperties().get("typ").toString());
                    pstmtDesc.setObject(2, feature.getProperties().get("description").toString());
                    pstmtDesc.setObject(3, feature.getProperties().get("description").toString());

                    pstmtDesc.executeUpdate();

                    var pstmtPoi = connection.prepareStatement(sqlPoiString);

                    pstmtPoi.setObject(1, feature.getGeometry().asText());
                    pstmtPoi.setObject(2, feature.getProperties().get("typ").toString());

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
    public List<PoiDescDbDto> getAll(){
        final var sqlQuery = "SELECT p.id, ST_AsGeoJSON(p.geometry) as geometry_asgeojson, d.typ, d.description " +
                "FROM pois p LEFT JOIN descriptions d ON p.descriptiontype = d.typ;";

        try(final var connection = DriverManager.getConnection(connectionString)){
            var pstmt = connection.prepareStatement(sqlQuery);
            var res = pstmt.executeQuery();
            return PoiDescDbDto.parseDbResponse(res);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;

    }

    @Override
    public List<PoiDescDbDto> getByType(String type){
        var list = new ArrayList<String>();
        list.add(type);
        return getByType(list);
    }

    @Override
    public List<PoiDescDbDto> getByType(List<String> types){
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
            return PoiDescDbDto.parseDbResponse(res);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public List<PoiDescDbDto> getByType(String... types){
        List<String> list = Arrays.asList(types);
        return getByType(list);
    }
}
