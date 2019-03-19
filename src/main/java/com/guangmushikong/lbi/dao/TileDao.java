package com.guangmushikong.lbi.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lbi.model.Tile;
import com.guangmushikong.lbi.model.*;
import com.lbi.util.TileSystem;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository(value="tileDao")
public class TileDao extends CommonDao{
    @Value("${spring.table.china_city_polygon}")
    String china_city_polygon;
    final GeometryFactory GEO_FACTORY=new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING),4326);

    public List<Admin_Region> getCityRegionList(Tile tile){
        List<Admin_Region> list=null;
        try{
            Envelope enve= TileSystem.TileXYToBounds(tile);
            Geometry grid= GEO_FACTORY.toGeometry(enve);
            StringBuilder sb=new StringBuilder();
            String[] fields={"adcode","name","st_astext(geom) as wkt"};
            sb.append("select ").append(StringUtils.join(fields,',')).append(" from "+china_city_polygon);
            sb.append(" where st_intersects(st_geomfromtext(?,4326),geom)");
            list=jdbcTemplate.query(
                    sb.toString(),
                    new Object[]{grid.toText()},
                    new int[]{Types.VARCHAR},
                    new RowMapper<Admin_Region>() {
                        public Admin_Region mapRow(ResultSet rs, int i) throws SQLException {
                            Admin_Region u=new Admin_Region();
                            u.setCode(rs.getString("adcode"));
                            u.setName(rs.getString("name"));
                            u.setWkt(rs.getString("wkt"));
                            return u;
                        }
                    });
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    public List<JSONObject> getGeojsonListByTile(String tableName, Tile tile){
        List<JSONObject> list=null;
        try{
            Envelope enve= TileSystem.TileXYToBounds(tile);
            Geometry grid= GEO_FACTORY.toGeometry(enve);
            StringBuilder sb=new StringBuilder();
            sb.append("select json_build_object('type','Feature','id',id,'geometry',ST_AsGeoJSON(ST_Transform(geom,4326))::json");
            sb.append(",'properties',json_build_object('contour',contour))");
            sb.append(" as geojson from "+tableName);
            sb.append(" where st_intersects(st_geomfromtext('"+grid.toText()+"',4326),ST_Transform(geom,4326))");

            //System.out.println(sb.toString());

            list=jdbcTemplate.query(
                    sb.toString(),
                    new RowMapper<JSONObject>() {
                        public JSONObject mapRow(ResultSet rs, int i) throws SQLException {
                            String geojson=rs.getString("geojson");
                            return JSONObject.parseObject(geojson);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public List<JSONObject> getLPSListByTile(String tableName, Tile tile){
        List<JSONObject> list=null;
        try{
            Envelope enve= TileSystem.TileXYToBounds(tile);
            Geometry grid= GEO_FACTORY.toGeometry(enve);
            StringBuilder sb=new StringBuilder();
            sb.append("select json_build_object('type','Feature','id',id,'geometry',ST_AsGeoJSON(ST_Transform(geom,4326))::json,'properties',json_build_object('name','liupanshui')) as geojson");
            sb.append(" from "+tableName);
            sb.append(" where st_intersects(st_geomfromtext('"+grid.toText()+"',4326),ST_Transform(geom,4326))");

            list=jdbcTemplate.query(
                    sb.toString(),
                    new RowMapper<JSONObject>() {
                        public JSONObject mapRow(ResultSet rs, int i) throws SQLException {
                            String geojson=rs.getString("geojson");
                            return JSONObject.parseObject(geojson);
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public List<JSONObject> getContourList(String tableName,Tile tile){
        List<JSONObject> list=null;
        try{
            Envelope enve= TileSystem.TileXYToBounds(tile);

            StringBuilder sb=new StringBuilder();
            String enveSql="ST_MakeEnvelope("+enve.getMinX()+","+enve.getMinY()+","+enve.getMaxX()+","+enve.getMaxY()+",4326)";

            sb.append("select json_build_object('features',array_to_json(array(");
            sb.append("select json_build_object('geometry',ST_AsGeoJSON(geom)::json,'properties',json_build_object('id',id,'contour',contour),'type','Feature') as geometry");
            sb.append(" from (");
            sb.append("select id,contour");
            sb.append(",st_clipbybox2d(geom,"+enveSql+") as geom");
            sb.append(" from "+tableName);
            sb.append(" where st_intersects("+enveSql+",geom)");
            sb.append(") t))");
            sb.append(") as features");
            list=jdbcTemplate.query(
                    sb.toString(),
                    new RowMapper<JSONObject>() {
                        public JSONObject mapRow(ResultSet rs, int i) throws SQLException {
                            String geojson=rs.getString("features");
                            return JSONObject.parseObject(geojson);
                        }
                    });
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return list;
    }

    public  List<FeatureVO> getContour(String tableName,Tile tile){
        List<FeatureVO> list=null;
        try{
            Envelope enve= TileSystem.TileXYToBounds(tile);

            StringBuilder sb=new StringBuilder();
            String enveSql="ST_MakeEnvelope("+enve.getMinX()+","+enve.getMinY()+","+enve.getMaxX()+","+enve.getMaxY()+",4326)";

            sb.append("select id,contour");
            sb.append(",ST_AsGeoJSON(st_clipbybox2d(geom,"+enveSql+")) as geojson");
            sb.append(" from "+tableName);
            sb.append(" where st_intersects("+enveSql+",geom)");
            list=jdbcTemplate.query(
                    sb.toString(),
                    new RowMapper<FeatureVO>() {
                        public FeatureVO mapRow(ResultSet rs, int i) throws SQLException {
                            FeatureVO u=new FeatureVO();
                            u.setType("Feature");

                            String geojsonStr=rs.getString("geojson");
                            JSONObject geojson=JSONObject.parseObject(geojsonStr);
                            GeometryVO geometryVO=new GeometryVO();
                            geometryVO.setType(geojson.getString("type"));
                            JSONArray coordinates=geojson.getJSONArray("coordinates");
                            geometryVO.setCoordinates(coordinates);
                            u.setGeometry(geometryVO);

                            JSONObject properties=new JSONObject();
                            properties.put("id",rs.getLong("id"));
                            properties.put("contour",rs.getLong("contour"));
                            u.setProperties(properties);
                            return u;
                        }
                    });

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
}
