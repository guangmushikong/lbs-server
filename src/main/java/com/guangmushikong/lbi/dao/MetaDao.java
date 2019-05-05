package com.guangmushikong.lbi.dao;

import com.guangmushikong.lbi.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository(value="metaDao")
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
        String sql="select * from "+t_tilemapservice+" order by id";
        return jdbcTemplate.query(
                sql,
                (rs,rowNum)->toTileMapService(rs));
    }

    public TileMapService getTileMapServiceById(long serviceId){
        String sql="select * from "+t_tilemapservice+" where id=?";
        return DataAccessUtils.requiredSingleResult(
                jdbcTemplate.query(
                        sql,
                        new Object[]{serviceId},
                        new int[]{Types.BIGINT},
                        (rs,rowNum)->toTileMapService(rs))
        );
    }

    public List<TileMap> getTileMapList(long serviceId){
        StringBuilder sb=new StringBuilder();
        sb.append("select t1.*,t2.min_zoom,t2.max_zoom from "+t_tilemap+" t1 left join");
        sb.append(" (select map_id,min(sort_order) as min_zoom,max(sort_order) as max_zoom from "+t_tileset+" group by map_id) t2");
        sb.append(" on t1.id=t2.map_id");
        sb.append(" where t1.service_id=?");
        sb.append(" order by t1.title,t1.layer_group");
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
        sb.append(" order by t1.title,t1.layer_group");
        return jdbcTemplate.query(
                sb.toString(),
                (rs,rowNum)->toTileMap(rs));
    }

    public TileMap getTileMapById(long id){
        String sql="select * from "+t_tilemap+" where id=?";
        return DataAccessUtils.requiredSingleResult(
                jdbcTemplate.query(
                        sql,
                        new Object[]{id},
                        new int[]{Types.BIGINT},
                        (rs,rowNum)->toTileMap(rs))
        );
    }

    @Cacheable(value = "tileMapCache",key = "'tileMap_'+#serviceId+#title+#srs+#extension")
    public TileMap getTileMapById(
            long serviceId,
            String title,
            String srs,
            String extension){
        String sql="select * from "+t_tilemap+" where service_id=? and title=? and srs=? and extension=?";
        return DataAccessUtils.requiredSingleResult(
                jdbcTemplate.query(
                        sql,
                        new Object[]{
                                serviceId,
                                title,
                                srs,
                                extension
                        },
                        new int[]{
                                Types.BIGINT,
                                Types.VARCHAR,
                                Types.VARCHAR,
                                Types.VARCHAR,
                        },
                        (rs,rowNum)->toTileMap(rs))
        );
    }
    public List<TileSet> getTileSetList(long mapId){
        String sql="select * from "+t_tileset+" where map_id=? order by sort_order";
        return jdbcTemplate.query(
                sql,
                new Object[]{mapId},
                new int[]{Types.INTEGER},
                (rs,rowNum)->toTileSet(rs));
    }

    public List<ProjectDO> getProjectList(){
        StringBuilder sb=new StringBuilder();
        sb.append("select * from "+t_project);
        sb.append(" order by id");
        return jdbcTemplate.query(
                sb.toString(),
                (rs,rowNum)->toProjectDO(rs));
    }

    public ProjectDO getProjectById(long id){
        String sql="select * from "+t_project+" where id=?";
        return DataAccessUtils.requiredSingleResult(
                jdbcTemplate.query(
                        sql,
                        new Object[]{id},
                        new int[]{Types.BIGINT},
                        (rs,rowNum)->toProjectDO(rs))
        );
    }

    public List<DataSetDO> getDataSetList(long projectId){
        StringBuilder sb=new StringBuilder();
        sb.append("select * from "+t_dataset);
        sb.append(" where id in (select dataset_id from r_project_dataset where project_id=?)");
        sb.append(" order by layer_group,record_date");
        return jdbcTemplate.query(
                sb.toString(),
                new Object[]{projectId},
                new int[]{Types.BIGINT},
                (rs,rowNum)->toDataSetDO(rs));
    }

    public DataSetDO getDataSetById(long id){
        String sql="select * from "+t_dataset+" where id=?";
        return DataAccessUtils.requiredSingleResult(
                jdbcTemplate.query(
                        sql,
                        new Object[]{id},
                        new int[]{Types.BIGINT},
                        (rs,rowNum)->toDataSetDO(rs))
        );
    }

    private TileMapService toTileMapService(ResultSet rs)throws SQLException{
        TileMapService u=new TileMapService();
        u.setId(rs.getLong("id"));
        u.setTitle(rs.getString("title"));
        u.setAbstract(rs.getString("abstract"));
        u.setVersion(rs.getString("version"));
        String href=rs.getString("href");
        href=href.replace("${mapserver}",mapserver+"/service");
        u.setHref(href);
        u.setKind(rs.getInt("kind"));
        return u;
    }

    private TileMap toTileMap(ResultSet rs)throws SQLException{
        TileMap u=new TileMap();
        u.setId(rs.getLong("id"));
        u.setServiceId(rs.getLong("service_id"));
        u.setTitle(rs.getString("title"));
        u.setRecordDate(rs.getString("record_date"));
        u.setAbstract(rs.getString("abstract"));
        u.setSrs(rs.getString("srs"));
        u.setProfile(rs.getString("profile"));
        String href=rs.getString("href");
        href=href.replace("${mapserver}",mapserver+"/service");
        u.setHref(href);

        u.setMinX(rs.getDouble("minx"));
        u.setMinY(rs.getDouble("miny"));
        u.setMaxX(rs.getDouble("maxx"));
        u.setMaxY(rs.getDouble("maxy"));
        u.setOriginX(rs.getDouble("origin_x"));
        u.setOriginY(rs.getDouble("origin_y"));

        u.setWidth(rs.getInt("width"));
        u.setHeight(rs.getInt("height"));
        u.setMimeType(rs.getString("mime_type"));
        u.setExtension(rs.getString("extension"));

        u.setKind(rs.getInt("kind"));
        u.setGroup(rs.getString("layer_group"));
        u.setSource(rs.getString("source"));
        u.setProp(rs.getString("prop"));
        u.setFileExtension(rs.getString("file_extension"));
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
