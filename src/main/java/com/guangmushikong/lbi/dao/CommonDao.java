package com.guangmushikong.lbi.dao;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CommonDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 获取表记录总数
     * @param tableName 表名
     * @return 记录总数
     */
    public int getTotal(String tableName){
        String sql="select count(*) as total from "+tableName;
        return jdbcTemplate.queryForObject(sql,Integer.class);
    }
    /**
     * 根据条件获取表记录总数
     * @param tableName 表名
     * @param whereSql 关键字
     * @return 记录总数
     */
    public int getTotal(String tableName, String whereSql){
        String sql="select count(*) as total from "+tableName;
        if(StringUtils.isNotEmpty(whereSql)){
            sql+=" where "+whereSql;
        }
        return jdbcTemplate.queryForObject(sql,Integer.class);
    }
    public long nextVal(String sequenceName){
        String sql="select nextval('"+sequenceName+"')";
        return jdbcTemplate.queryForObject(sql,Long.class);
    }

    /**
     * 判断查询结果集中是否存在某列
     * @param rs 查询结果集
     * @param columnName 列名
     * @return true 存在; false 不存咋
     */
    public boolean isExistColumn(ResultSet rs, String columnName) {
        try {
            if (rs.findColumn(columnName) > 0 ) {
                return true;
            }
        }
        catch (SQLException e) {
            return false;
        }
        return false;
    }
}
