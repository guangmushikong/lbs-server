package com.guangmushikong.lbi.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
public class LogDao extends CommonDao{
    @Value("${spring.table.t_log}")
    String t_log;

    public int addLog(String ip,String message,String method,long usetime){
        String sql="insert into "+t_log+"(ip,message,method,usetime) values(?,?,?,?)";
        return jdbcTemplate.update(sql,
                new Object[]{
                        ip,
                        message,
                        method,
                        usetime
                },
                new int[]{
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.VARCHAR,
                        Types.BIGINT
                });
    }
}
