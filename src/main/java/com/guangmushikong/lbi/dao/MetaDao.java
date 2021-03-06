package com.guangmushikong.lbi.dao;

import com.guangmushikong.lbi.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository
@Slf4j
public class MetaDao extends CommonDao{
    @Value("${service.mapserver}")
    String mapserver;
    @Value("${spring.table.t_tilemapservice}")
    String t_tilemapservice;
    @Value("${spring.table.t_tilemap}")
    String t_tilemap;
    @Value("${spring.table.t_tileset}")
    String t_tileset;
    @Value("${spring.table.t_project}")
    String t_project;
    @Value("${spring.table.t_dataset}")
    String t_dataset;

    public List<TileMapService> getTileMapServiceList(){
        String sql=String.format("select * from %s order by id",t_tilemapservice);
        return jdbcTemplate.query(
                sql,
                (rs,rowNum)->toTileMapService(rs));
    }

    public TileMapService getTileMapServiceById(long serviceId){
        String sql=String.format("select * from %s where id=?",t_tilemapservice);
        List<TileMapService> list=jdbcTemplate.query(
                sql,
                new Object[]{serviceId},
                new int[]{Types.BIGINT},
                (rs,rowNum)->toTileMapService(rs));

        if(list.isEmpty()){
            return null;
        }else {
            return list.get(0);
        }
    }

    public List<TileMap> getTileMapList(long serviceId){
        StringBuilder sb=new StringBuilder();
        sb.append("select t1.*,t2.min_zoom,t2.max_zoom from "+t_tilemap+" t1 left join");
        sb.append(" (select map_id,min(sort_order) as min_zoom,max(sort_order) as max_zoom from "+t_tileset+" group by map_id) t2");
        sb.append(" on t1.id=t2.map_id");
        sb.append(" where t1.service_id=?");
        sb.append(" order by t1.name,t1.layer_group");
        //log.info("【sql】{}",sb.toString());
        return jdbcTemplate.query(
                sb.toString(),
                new Object[]{serviceId},
                new int[]{Types.BIGINT},
                (rs,rowNum)->toTileMap(rs));

    }

    public List<TileMap> getTileMapList(List<Long> ids){
        StringBuilder sb=new StringBuilder();
        sb.append("select t1.*,t2.min_zoom,t2.max_zoom from "+t_tilemap+" t1 left join");
        sb.append(" (select map_id,min(sort_order) as min_zoom,max(sort_order) as max_zoom from "+t_tileset+" group by map_id) t2");
        sb.append(" on t1.id=t2.map_id");
        sb.append(" where t1.id in ("+ StringUtils.join(ids,",")+")");
        sb.append(" order by t1.name,t1.layer_group");
        //log.info("【sql】{}",sb.toString());
        return jdbcTemplate.query(
                sb.toString(),
                (rs,rowNum)->toTileMap(rs));
    }

    public TileMap getTileMapById(long id){
        String sql=String.format("select * from %s where id=?",t_tilemap);
        List<TileMap> list=jdbcTemplate.query(
                sql,
                new Object[]{id},
                new int[]{Types.BIGINT},
                (rs,rowNum)->toTileMap(rs));
        if(list.isEmpty()){
            return null;
        }else {
            return list.get(0);
        }
    }

    @Cacheable(value = "tileMapCache",key = "'tileMap_'+#serviceId+#title+#srs+#extension", unless="#result == null")
    public TileMap getTileMapById(
            long serviceId,
            String title,
            String srs,
            String extension){
        long epsg=Long.parseLong(srs.replace("EPSG:",""));
        String sql=String.format("select * from %s where service_id=? and name=? and epsg=? and tile_type=?",t_tilemap);
        List<TileMap> list=jdbcTemplate.query(
                sql,
                new Object[]{
                        serviceId,
                        title,
                        epsg,
                        extension
                },
                new int[]{
                        Types.BIGINT,
                        Types.VARCHAR,
                        Types.BIGINT,
                        Types.VARCHAR,
                },
                (rs,rowNum)->toTileMap(rs));
        if(list.isEmpty()){
            return null;
        }else {
            return list.get(0);
        }
    }
    public List<TileSet> getTileSetList(long mapId){
        String sql=String.format("select * from %s where map_id=? order by sort_order",t_tileset);
        return jdbcTemplate.query(
                sql,
                new Object[]{mapId},
                new int[]{Types.INTEGER},
                (rs,rowNum)->toTileSet(rs));
    }

    public List<ProjectDO> getProjectList(){
        String sql=String.format("select * from %s order by id",t_project);
        return jdbcTemplate.query(sql, (rs,rowNum)->toProjectDO(rs));
    }

    public List<ProjectDO> getProjectList(String ids){
        String sql=String.format("select * from %s where id in (%s) order by id",t_project,ids);
        return jdbcTemplate.query(sql,(rs,rowNum)->toProjectDO(rs));
    }

    public ProjectDO getProjectById(long id){
        String sql=String.format("select * from %s where id=?",t_project);
        List<ProjectDO> list=jdbcTemplate.query(
                sql,
                new Object[]{id},
                new int[]{Types.BIGINT},
                (rs,rowNum)->toProjectDO(rs));

        if(list.isEmpty()){
            return null;
        }else {
            return list.get(0);
        }
    }

    public List<DataSetDO> getDataSetList(){
        String sql=String.format("select * from %s order by name",t_dataset);
        return jdbcTemplate.query(sql, (rs,rowNum)->toDataSetDO(rs));
    }

    public List<DataSetDO> getDataSetList(String datasetIds){
        String sql=String.format("select * from %s where id in (%s) order by layer_group,record_date",t_dataset,datasetIds);
        return jdbcTemplate.query(sql, (rs,rowNum)->toDataSetDO(rs));
    }

    public DataSetDO getDataSetById(long id){
        String sql=String.format("select * from %s where id=?",t_dataset);
        List<DataSetDO> list=jdbcTemplate.query(
                sql,
                new Object[]{id},
                new int[]{Types.BIGINT},
                (rs,rowNum)->toDataSetDO(rs));
        if(list.isEmpty()){
            return null;
        }else {
            return list.get(0);
        }
    }

    private TileMapService toTileMapService(ResultSet rs)throws SQLException{
        TileMapService u=new TileMapService();
        u.setId(rs.getLong("id"));
        u.setTitle(rs.getString("title"));
        u.setAbstract(rs.getString("abstract"));
        u.setVersion(rs.getString("version"));
        String href=rs.getString("href");
        href=href.replace("${mapserver}",mapserver);
        u.setHref(href);
        u.setKind(rs.getInt("kind"));
        return u;
    }

    private TileMap toTileMap(ResultSet rs)throws SQLException{
        TileMap u=new TileMap();
        u.setId(rs.getLong("id"));

        u.setServiceId(rs.getLong("service_id"));
        u.setTitle(rs.getString("name"));
        u.set_abstract(rs.getString("memo"));
        u.setKind(rs.getInt("kind"));
        u.setGroup(rs.getString("layer_group"));

        long epsg=rs.getLong("epsg");
        String tileType=rs.getString("tile_type");
        u.setSrs(String.format("EPSG:%d",epsg));
        if(900913==epsg){
            u.setProfile("mercator");
        }else if(4326==epsg){
            u.setProfile("geodetic");
        }
        String href;
        if(u.getServiceId()==1){
            href=String.format("http://%s/xyz/1.0.0/%s@EPSG:%d@%s",mapserver,u.getTitle(),epsg,tileType);
        }else {
            href=String.format("http://%s/tms/1.0.0/%s@EPSG:%d@%s",mapserver,u.getTitle(),epsg,tileType);
        }

        u.setHref(href);
        u.setMinX(rs.getDouble("minx"));
        u.setMinY(rs.getDouble("miny"));
        u.setMaxX(rs.getDouble("maxx"));
        u.setMaxY(rs.getDouble("maxy"));
        u.setOriginX(rs.getDouble("origin_x"));
        u.setOriginY(rs.getDouble("origin_y"));

        u.setWidth(rs.getInt("width"));
        u.setHeight(rs.getInt("height"));

        u.setExtension(tileType);
        u.setFileExtension(rs.getString("suffix"));
        if("jpeg".equals(tileType)){
            u.setMimeType("image/jpeg");
        }else if("png".equals(tileType)){
            u.setMimeType("image/png");
        }else if("tif".equals(tileType)){
            u.setMimeType("image/tif");
        }else if("geojson".equals(tileType)){
            u.setMimeType("application/json");
        }else if("terrain".equals(tileType)){
            u.setMimeType("application/terrain");
        }

        u.setRecordDate(rs.getString("record_date"));
        u.setProp(rs.getString("prop"));

        if(isExistColumn(rs,"min_zoom")){
            u.setMinZoom(rs.getInt("min_zoom"));
        }
        if(isExistColumn(rs,"max_zoom")){
            u.setMaxZoom(rs.getInt("max_zoom"));
        }
        return u;
    }

    private TileSet toTileSet(ResultSet rs)throws SQLException{
        TileSet u=new TileSet();
        u.setId(rs.getLong("id"));
        u.setMapId(rs.getLong("map_id"));
        String href=rs.getString("href");
        href=href.replace("${mapserver}",mapserver);
        u.setHref(href);
        u.setUnitsPerPixel(rs.getString("units_per_pixel"));
        u.setSortOrder(rs.getString("sort_order"));
        return u;
    }

    private ProjectDO toProjectDO(ResultSet rs)throws SQLException{
        ProjectDO u=new ProjectDO();
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        u.setMemo(rs.getString("memo"));
        u.setDatasetIds(rs.getString("dataset_ids"));
        u.setCreateTime(rs.getTimestamp("create_time"));
        u.setModifyTime(rs.getTimestamp("modify_time"));
        return u;
    }

    private DataSetDO toDataSetDO(ResultSet rs)throws SQLException{
        DataSetDO u=new DataSetDO();
        u.setId(rs.getLong("id"));
        u.setMapId(rs.getLong("map_id"));
        u.setName(rs.getString("name"));
        u.setMemo(rs.getString("memo"));
        u.setRecordDate(rs.getString("record_date"));
        u.setType(rs.getInt("type"));
        u.setGroup(rs.getString("layer_group"));
        u.setKind(rs.getInt("kind"));
        u.setCreateTime(rs.getTimestamp("create_time"));
        u.setModifyTime(rs.getTimestamp("modify_time"));
        return u;
    }
}
