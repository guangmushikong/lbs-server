
package com.guangmushikong.lbi.dao;


import com.guangmushikong.lbi.model.CustomVO;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
public class CustomDataSetDao extends CommonDao{

    @Value("${spring.table.t_custom_data}")
    String t_custom_data;

    public List<CustomVO> listCustomDataSet(long projectId){
        String sql=String.format("select uuid,name,project_id,user_name,prop,st_astext(geom) as wkt from %s where project_id=%d",t_custom_data,projectId);
        return jdbcTemplate.query(sql, (rs,rowNum)->toCustomVO(rs));
    }

    public void addCustomData(CustomVO customVO){
        String sql=String.format("insert into %s(uuid,name,project_id,user_name,prop,geom) values(?,?,?,?,?,ST_GeomFromText(?,4326))",t_custom_data);
        jdbcTemplate.update(sql,
                new Object[]{
                        customVO.getUuid(),
                        customVO.getName(),
                        customVO.getProjectId(),
                        customVO.getUserName(),
                        customVO.getProp(),
                        customVO.getWkt()
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.BIGINT,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR
                });
    }

    public void updateCustomData(CustomVO customVO){
        String sql=String.format("update %s set name=?,prop=?,geom=ST_GeomFromText(?,4326) where uuid=? and project_id=? and user_name=?",t_custom_data);
        jdbcTemplate.update(sql,
                new Object[]{
                        customVO.getName(),
                        customVO.getProp(),
                        customVO.getWkt(),
                        customVO.getUuid(),
                        customVO.getProjectId(),
                        customVO.getUserName()
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.BIGINT,
                        Types.VARCHAR
                });
    }

    private CustomVO toCustomVO(ResultSet rs)throws SQLException {
        CustomVO u=new CustomVO();

        u.setUuid(rs.getString("uuid"));
        u.setName(rs.getString("name"));
        u.setProjectId(rs.getLong("project_id"));
        u.setUserName(rs.getString("user_name"));
        u.setProp(rs.getString("prop"));
        u.setWkt(rs.getString("wkt"));

        return u;
    }
}
