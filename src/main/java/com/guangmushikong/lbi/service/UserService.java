
package com.guangmushikong.lbi.service;

import com.guangmushikong.lbi.dao.UserDao;
import com.guangmushikong.lbi.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/*************************************
 * Class Name: UserService
 * Description:〈自定用用户服务〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserDao userDao;

    @Cacheable(cacheNames= "userCache",key="#userName")
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        SysUser user = userDao.findByUsername(userName);
        if (null==user) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        return user;
    }
}
