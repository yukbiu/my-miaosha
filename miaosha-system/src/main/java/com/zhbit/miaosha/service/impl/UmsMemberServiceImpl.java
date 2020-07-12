package com.zhbit.miaosha.service.impl;

import com.zhbit.miaosha.common.Constant;
import com.zhbit.miaosha.dao.UmsMemberDao;
import com.zhbit.miaosha.model.entity.UmsMember;
import com.zhbit.miaosha.model.entity.UmsRole;
import com.zhbit.miaosha.security.bo.UserPrincipal;
import com.zhbit.miaosha.security.util.JwtTokenUtil;
import com.zhbit.miaosha.service.UmsMemberService;
import com.zhbit.miaosha.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UmsMemberService实现类
 */
@CacheConfig(cacheNames = "member")
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UmsMemberDao memberDao;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String login(String username, String password) {
        //1、获取用户信息
        UserDetails userDetails = loadUserByUsername(username);
        //2、验证密码正确
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("密码不正确");
        }
        //3、获取用户认证凭证
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null);
        //4、将用户认证凭证注册到SpringSecurity的上下文中
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        //5、生成JwtToken
        String token = jwtTokenUtil.sign(authentication);
        //6、将token存入redis缓存中
        redisUtil.set(Constant.PREFIX_USER_TOKEN + token, token, JwtTokenUtil.EXPIRE_TIME * 2 / 1000);

        return token;
    }

    @Cacheable(key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) {
        //根据用户名查找会员
        UmsMember member = memberDao.findUserByUsername(username);
        if (member != null) {
            //根据会员id查找用户角色
            List<UmsRole> roles = memberDao.selectByUserId(member.getId());
            //TODO selectPermissions
            return UserPrincipal.create(member, roles); //返回用户本体
        } else {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
    }
}
