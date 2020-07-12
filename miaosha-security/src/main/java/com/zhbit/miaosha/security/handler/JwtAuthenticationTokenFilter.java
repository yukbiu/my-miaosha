package com.zhbit.miaosha.security.handler;

import cn.hutool.core.util.StrUtil;
import com.zhbit.miaosha.common.Constant;
import com.zhbit.miaosha.security.config.IgnoreUrlsConfig;
import com.zhbit.miaosha.security.util.AuthUtil;
import com.zhbit.miaosha.security.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT登录认证过滤器
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException{
        //OPTIONS请求直接放行
        if(request.getMethod().equals(HttpMethod.OPTIONS.toString())){
            filterChain.doFilter(request, response);
            return;
        }
        //白名单请求直接放行
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String path : ignoreUrlsConfig.getUrls()) {
            if(pathMatcher.match(path,request.getRequestURI())){
                filterChain.doFilter(request, response);
                return;
            }
        }
        log.info("========================[authentication]===========================");
        //1、从请求中存有Token的Header里获取jwt
        String jwt = request.getHeader(Constant.X_ACCESS_TOKEN);
        if (StrUtil.isNotEmpty(jwt)) {
            //2、从jwt中获取负载的用户名
            String username = jwtTokenUtil.getUserNameFromToken(jwt);
            log.info("checking username:{}", username);
            // 从缓存中获取用户信息校验
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            //3、校验Token有效性
            if (jwtTokenUtil.validateCacheToken(jwt)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }
        }
        //无效token
        AuthUtil.responseJSON(response,null);
    }
}
