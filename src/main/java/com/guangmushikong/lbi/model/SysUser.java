package com.guangmushikong.lbi.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*************************************
 * Class Name: SysUser
 * Description:〈系统用户〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@Data
public class SysUser implements UserDetails {
    /**
     * 主键
     */
    Long id;
    /**
     * 用户名
     */
    String username;
    /**
     * 密码
     */
    String password;
    /**
     * 邮件
     */
    String email;
    /**
     * 角色列表
     */
    List<SysRole> roles;
    /**
     * 项目ID
     */
    String projectIds;
    /**
     * 所属项目列表
     */
    List<ProjectDO> projects;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auths = new ArrayList<>();
        List<SysRole> roles = this.getRoles();
        for (SysRole role : roles) {
            auths.add(new SimpleGrantedAuthority(role.getName()));
        }
        return auths;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

