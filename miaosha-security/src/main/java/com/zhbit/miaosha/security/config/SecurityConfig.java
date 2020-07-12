package com.zhbit.miaosha.security.config;


import com.zhbit.miaosha.security.handler.JwtAuthenticationTokenFilter;
import com.zhbit.miaosha.security.handler.RestAuthenticationEntryPoint;
import com.zhbit.miaosha.security.handler.RestfulAccessDeniedHandler;
import com.zhbit.miaosha.security.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security配置
 */
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity.authorizeRequests();
        //放行不受保护的资源访问路径-白名单urls
        for (String url : ignoreUrlsConfig().getUrls()) {
            registry.antMatchers(url).permitAll();
        }
        registry.antMatchers(HttpMethod.OPTIONS)    //跨域请求会先进行一次options请求
                .permitAll();
        registry.and().csrf().disable()   //由于使用的是JWT，这里不需要csrf
                .sessionManagement()    // 基于token，所以不需要session
                /**
                 * session策略：SessionCreationPolicy.STATELESS
                 * Spring Security永远不会创建{@link HttpSession}，并且永远不会使用它来获取{@link SecurityContext}
                 */
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //关闭Security默认的登录以及注销页面，由自己实现
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                // 认证请求
                .authorizeRequests()
                .anyRequest()   // 除以上所有请求全部需要鉴权认证
                .authenticated()
                .and()
                 // 自定义权限拦截器JWT过滤器
                .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                //添加自定义未授权和未登录结果返回
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler())
                .authenticationEntryPoint(restAuthenticationEntryPoint());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());    //TODO 权限验证
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    public RestfulAccessDeniedHandler restfulAccessDeniedHandler() {
        log.info("========RestfulAccessDeniedHandler========");
        return new RestfulAccessDeniedHandler();
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        log.info("========RestAuthenticationEntryPoint========");
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public JwtTokenUtil jwtUtil() {
        return new JwtTokenUtil();
    }
    @Bean
    public IgnoreUrlsConfig ignoreUrlsConfig() {
        return new IgnoreUrlsConfig();
    }
}
