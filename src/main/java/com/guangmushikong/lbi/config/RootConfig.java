
package com.guangmushikong.lbi.config;

import com.guangmushikong.lbi.model.TileMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Configuration
@Slf4j
public class RootConfig {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Bean(name = "tileMapConfig")
    public TileMapConfig getTileMapConfig(){
        List<TileMap> tileMaps=loadTileMapList();
        TileMapConfig tileMapConfig=new TileMapConfig();
        tileMapConfig.setTileMaps(tileMaps);
        return tileMapConfig;
    }

    private List<TileMap> loadTileMapList(){
        List<TileMap> list=null;
        try{
            String sql="select * from t_tilemap where service_id in (1,2) order by id";
            list=jdbcTemplate.query(
                    sql,
                    new RowMapper<TileMap>() {
                        public TileMap mapRow(ResultSet rs, int i) throws SQLException {
                            TileMap u=new TileMap();
                            u.setId(rs.getLong("id"));
                            u.setServiceId(rs.getLong("service_id"));
                            u.setTitle(rs.getString("title"));
                            u.setRecordDate(rs.getString("record_date"));
                            u.setAbstract(rs.getString("abstract"));
                            u.setSrs(rs.getString("srs"));
                            u.setProfile(rs.getString("profile"));
                            u.setHref(rs.getString("href"));

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
                            u.setFileExtension(rs.getString("file_extension"));
                            return u;
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return list;
    }
}
