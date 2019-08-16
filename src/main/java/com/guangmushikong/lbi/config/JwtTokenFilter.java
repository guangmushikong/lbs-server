package com.guangmushikong.lbi.config;

import com.guangmushikong.lbi.service.UserService;
import com.guangmushikong.lbi.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@WebFilter
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    UserService userService;

    /**
     * 存放Token的Header Key
     */
    public static final String HEADER_STRING = "Authorization";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        String token=getToken(request);
        if (null != token) {
            String username = JwtTokenUtil.getUsernameFromToken(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);
                if (JwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        long star = System.currentTimeMillis();
        String url = request.getRequestURI();
        String remoteAddr = getRemortIP(request);
        String method = request.getMethod();
        String params=getParams(request);

        if(StringUtils.isNotEmpty(params)){
            url+="?"+params;
        }

        chain.doFilter(request, response);

        long end = System.currentTimeMillis();
        long duration=(end - star);
        MDC.put("ip", remoteAddr);
        MDC.put("duration", String.valueOf (duration));
        MDC.put("method", method);
        log.info(url);
    }

    /**
     * 获取请求URI
     * @param req 请求对象
     */
    private String getParams(HttpServletRequest req){
        String params="";
        HttpMethod method=HttpMethod.valueOf(req.getMethod());
        if(method==HttpMethod.POST || method==HttpMethod.PUT){
            // 遍历post参数
            Enumeration<String> e = req.getParameterNames();
            String parameterName, parameterValue;
            while (e.hasMoreElements()) {
                parameterName = e.nextElement();
                parameterValue = req.getParameter(parameterName);
                params += "&" + parameterName + "=" + parameterValue;
            }
            params=params.replaceAll("'", "\"");
        }else if(method==HttpMethod.GET || method==HttpMethod.DELETE){
            params= req.getQueryString();
        }

        return params;
    }

    private String getRemortIP(HttpServletRequest request) {
        if (request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        } else{
            return request.getHeader("x-forwarded-for");
        }
    }

    private String getToken(HttpServletRequest request){
        if(request.getHeader(HEADER_STRING)!=null){
            return request.getHeader(HEADER_STRING);
        }else if(request.getQueryString()!=null){
            String queryString=request.getQueryString();
            String[] params=queryString.split("&");
            for(String param:params){
                String[] arr=param.split("=");
                if("token".equalsIgnoreCase(arr[0])){
                    return arr[1];
                }
            }
        }
        return null;
    }
}
