
package com.guangmushikong.lbi.dao;


import com.guangmushikong.lbi.model.KmlDO;
import com.guangmushikong.lbi.model.UserDataDO;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/*************************************
 * Class Name: CustomDataSetDao
 * Description:〈自定义数据Dao〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@Repository
@Slf4j
public class UserDataDao extends CommonDao{
    @Value("${spring.table.t_custom_data}")
    String t_custom_data;
    @Value("${spring.table.t_kml_data}")
    String t_kml_data;

    public List<UserDataDO> listUserData(long projectId){
        String sql=String.format("select uuid,name,type,project_id,user_name,prop,st_astext(geom) as wkt from %s where project_id=%d",t_custom_data,projectId);
        return jdbcTemplate.query(sql, (rs,rowNum)->toUserDataDO(rs));
    }

    public UserDataDO getUserData(String uuid){
        String sql=String.format("select uuid,name,type,project_id,user_name,prop,st_astext(geom) as wkt from %s where uuid=?",t_custom_data);
        List<UserDataDO> list=jdbcTemplate.query(
                sql,
                new Object[]{uuid},
                new int[]{Types.VARCHAR},
                (rs,rowNum)->toUserDataDO(rs));
        if(list.isEmpty()){
            return null;
        }else {
            return list.get(0);
        }
    }

    public void addUserData(UserDataDO customVO){
        String sql=String.format("insert into %s(uuid,name,type,project_id,user_name,prop,geom) values(?,?,?,?,?,?,ST_GeomFromText(?,4326))",t_custom_data);
        jdbcTemplate.update(sql,
                new Object[]{
                        customVO.getUuid(),
                        customVO.getName(),
                        customVO.getType(),
                        customVO.getProjectId(),
                        customVO.getUserName(),
                        customVO.getProp(),
                        customVO.getWkt()
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.INTEGER,
                        Types.BIGINT,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR
                });
    }

    public void updateUserData(UserDataDO customVO){
        String sql=String.format("update %s set name=?,type=?,prop=?,geom=ST_GeomFromText(?,4326),modify_time=now() where uuid=? and project_id=?",t_custom_data);
        jdbcTemplate.update(sql,
                new Object[]{
                        customVO.getName(),
                        customVO.getType(),
                        customVO.getProp(),
                        customVO.getWkt(),
                        customVO.getUuid(),
                        customVO.getProjectId()
                },
                new int[]{
                        Types.VARCHAR,
                        Types.INTEGER,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.BIGINT
                });
    }

    public void delUserData(String uuid,long projectId){
        String sql=String.format("delete from %s where uuid=? and project_id=?",t_custom_data);
        jdbcTemplate.update(sql,
                new Object[]{
                        uuid,
                        projectId
                },
                new int[]{
                        Types.VARCHAR,
                        Types.BIGINT
                });
    }

    public List<KmlDO> listKml(String userName,String type){
        String sql=String.format("select id,name,user_name,type,st_astext(geom) as wkt from %s where user_name=? and type=?",t_kml_data);
        return jdbcTemplate.query(
                sql,
                new Object[]{userName,type},
                new int[]{Types.VARCHAR,Types.VARCHAR},
                (rs,rowNum)->toKmlDO(rs));
    }

    public void addKml(KmlDO kmlDO){
        String sql=String.format("insert into %s(user_name,name,type,geom) values(?,?,?,ST_GeomFromText(?,4326))",t_kml_data);
        jdbcTemplate.update(sql,
                new Object[]{
                        kmlDO.getUserName(),
                        kmlDO.getName(),
                        kmlDO.getType(),
                        kmlDO.getGeometry().toText()
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR
                });
    }

    public void saveRow(String sql,Object[] objects,int[] types){
        jdbcTemplate.update(sql,objects,types);
    }

    private UserDataDO toUserDataDO(ResultSet rs)throws SQLException {
        UserDataDO u=new UserDataDO();

        u.setUuid(rs.getString("uuid"));
        u.setName(rs.getString("name"));
        u.setType(rs.getInt("type"));
        u.setProjectId(rs.getLong("project_id"));
        u.setUserName(rs.getString("user_name"));
        u.setProp(rs.getString("prop"));
        u.setWkt(rs.getString("wkt"));

        return u;
    }

    private KmlDO toKmlDO(ResultSet rs)throws SQLException {
        KmlDO u=new KmlDO();
        u.setId(rs.getString("id"));
        u.setName(rs.getString("name"));
        u.setUserName(rs.getString("user_name"));
        u.setType(rs.getString("type"));
        String wkt=rs.getString("wkt");
        try{
            WKTReader wktReader=new WKTReader();
            Geometry geometry=wktReader.read(wkt);
            u.setGeometry(geometry);
        }catch (Exception e){
            e.printStackTrace();
        }
        return u;
    }
}
