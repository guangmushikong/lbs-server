package com.guangmushikong.lbi.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guangmushikong.lbi.model.*;

import com.guangmushikong.lbi.util.TileSystem;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class TileDao extends CommonDao{
    final GeometryFactory GEO_FACTORY=new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING),4326);

    public List<FeatureVO> getFeatureListByTile(String tableName,String[] fields,JSONObject prop,Tile tile){
        Envelope enve= TileSystem.TileXYToBounds(tile);
        Geometry grid= GEO_FACTORY.toGeometry(enve);
        String field;
        if(fields!=null){
            field= StringUtils.join(fields,",");
        }else {
            field= "*";
        }
        String enveSql=String.format("ST_MakeEnvelope(%f,%f,%f,%f,4326)",enve.getMinX(),enve.getMinY(),enve.getMaxX(),enve.getMaxY());
        field+=",ST_AsGeoJSON(st_clipbybox2d(geom,"+enveSql+")) as geojson";
        String sql=String.format("select %s from data.%s where st_intersects(st_geomfromtext('%s',4326),ST_Transform(geom,4326))",field,tableName,grid.toText());
        log.info("【sql】{}",sql);
        return jdbcTemplate.query(
                sql,
                (rs,rowNum)->{
                    FeatureVO u=new FeatureVO();
                    u.setType("Feature");

                    JSONObject properties=new JSONObject();
                    if(prop!=null){
                        properties=prop;
                    }
                    if(fields!=null){
                        for(String key:fields){
                            String val=rs.getString(key);
                            properties.put(key,val);
                        }
                    }
                    if(!properties.isEmpty()){
                        u.setProperties(properties);
                    }

                    String geojsonStr=rs.getString("geojson");
                    JSONObject geojson=JSONObject.parseObject(geojsonStr);
                    GeometryVO geometryVO=new GeometryVO();
                    geometryVO.setType(geojson.getString("type"));
                    JSONArray coordinates=geojson.getJSONArray("coordinates");
                    geometryVO.setCoordinates(coordinates);
                    u.setGeometry(geometryVO);

                    return u;
                });
    }
}
