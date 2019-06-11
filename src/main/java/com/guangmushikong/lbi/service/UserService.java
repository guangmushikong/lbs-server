
package com.guangmushikong.lbi.service;

import com.guangmushikong.lbi.dao.UserDao;
import com.guangmushikong.lbi.model.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/*************************************
 * Class Name: UserService
 * Description:〈自定用用户服务〉
 * @author deyi
 * @create 2018/6/4
 * @since 1.0.0
 ************************************/
@Service("userService")
@Slf4j
public class UserService implements UserDetailsService {
    @Resource(name="userDao")
    UserDao userDao;

    @Cacheable(cacheNames= "userCache",key="#userName")
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        SysUser user = userDao.findByUsername(userName);
        log.info("【user】name={},pwd={}",userName,user.getPassword());
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        return user;
    }
}
