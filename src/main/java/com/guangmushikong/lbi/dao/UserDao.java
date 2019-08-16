package com.guangmushikong.lbi.dao;

import com.guangmushikong.lbi.model.SysRole;
import com.guangmushikong.lbi.model.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/*************************************
 * Class Name: UserDao
 * Description:〈系统用户Dao〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@Repository
@Slf4j
public class UserDao extends CommonDao{
    @Value("${spring.table.t_sys_role}")
    String t_sys_role;
    @Value("${spring.table.t_sys_user}")
    String t_sys_user;

    public SysUser findByUsername(String username){
        String sql=String.format("select * from %s where username=?",t_sys_user);
        List<SysUser> list=jdbcTemplate.query(
                sql,
                new Object[]{username},
                new int[]{Types.VARCHAR},
                (rs,rowNum)->toSysUser(rs));
        if(list.isEmpty()){
            return null;
        }else {
            return list.get(0);
        }
    }

    private SysRole findRoleById(long id){
        String sql=String.format("select * from %s where id=?",t_sys_role);
        List<SysRole> list=jdbcTemplate.query(
                sql,
                new Object[]{id},
                new int[]{Types.BIGINT},
                (rs,rowNum)->toSysRole(rs));
        if(list.isEmpty()){
            return null;
        }else {
            return list.get(0);
        }
    }

    private SysUser toSysUser(ResultSet rs)throws SQLException{
        SysUser u=new SysUser();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setEmail(rs.getString("email"));
        u.setProjectIds(rs.getString("project_ids"));
        long roleId=rs.getLong("role_id");
        //get Role
        List<SysRole> list=new ArrayList<>();
        SysRole role=findRoleById(roleId);
        if(role!=null){
            list.add(role);
        }
        u.setRoles(list);

        return u;
    }

    private SysRole toSysRole(ResultSet rs)throws SQLException{
        SysRole u=new SysRole();
        u.setId(rs.getLong("id"));
        u.setName(rs.getString("name"));
        return u;
    }
}
