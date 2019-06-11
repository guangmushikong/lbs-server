package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.service.AuthService;
import com.lbi.model.ResultBody;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource(name="authService")
    private AuthService authService;


    /**
     * 登录
     */
    @PostMapping(value = "/login")
    public ResultBody login(
            @RequestParam("username") String username,
            @RequestParam("password") String password) throws AuthenticationException {
        // 登录成功会返回Token给用户
        String info= authService.login( username, password );
        return new ResultBody<>(0,"OK",info);
    }
}
