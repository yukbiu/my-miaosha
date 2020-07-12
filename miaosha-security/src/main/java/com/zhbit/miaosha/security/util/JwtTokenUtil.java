package com.zhbit.miaosha.security.util;

import cn.hutool.core.util.StrUtil;
import com.zhbit.miaosha.common.Constant;
import com.zhbit.miaosha.security.bo.UserPrincipal;
import com.zhbit.miaosha.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtToken生成工具类
 * JWT token的格式：header.payload.signature
 *  * header的格式（算法、token的类型）：
 *  * {"alg": "HS512","typ": "JWT"}
 *  * payload的格式（用户名、有效时间）：
 *  * {"exp":1489684781 , "username":"wang"}
 *  * signature的生成算法：
 *  * HMACSHA512(base64UrlEncode(header) + "." +base64UrlEncode(payload),secret)
 */
@Slf4j
public class JwtTokenUtil {
    //Token过期时间30分钟
    public static final long EXPIRE_TIME = 30 * 60 * 1000;
    //用户名信息
    private static final String CLAIM_KEY_USERNAME = "sub";
    //密钥
    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 生成JwtToken
     * @param claims 载荷参数：username、email...
     * @return
     */
    private String generateToken(Map<String, Object> claims) {
        Date now = new Date();
        // 返回生成的Token,不设置过期时间，由redis掌管Token的有效性
        return Jwts.builder()
                .setClaims(claims)  // sub--默认为用户名
                .setIssuedAt(now)   //创建时间
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 生成JWT
     * @param authentication 用户认证信息
     * @return
     */
    public String sign(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        HashMap<String, Object> claims = new HashMap<>();
        //存放负载参数
        claims.put(CLAIM_KEY_USERNAME, userPrincipal.getUsername());
        return generateToken(claims);
    }

    /**
     * 从token中获取JWT中的载荷
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;

        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Token校验失败{}",token);
            throw new BadCredentialsException("无效的Token");
        }

        return claims;
    }

    /**
     * 从token中获取登录用户名
     */
    public String getUserNameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }
    /**
     * 验证token是否还有效
     *
     * @param token       客户端传入的token
     * @param userDetails 从数据库中查询出来的用户信息
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUserNameFromToken(token);
        //Token用户名是否匹配Security上下文中记录的用户名
        //Token是否已过期失效
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 判断token是否已经失效
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        //从token中获取过期时间
        Claims claims = getClaimsFromToken(token);
        Date expiredDate = claims.getExpiration();
        //判断过期时间是否在当前时间之前
        /**
         * <code>true</code>token已过期
         * <code>false</code>token未过期
         */
        return expiredDate.before(new Date());
    }

    /**
     * 校验Token
     * @param token 客户端传入的token
     * @return
     */
    public boolean validateCacheToken(String token) {
        //根据token获取redis缓存中的token
        String cacheToken = (String) redisUtil.get(Constant.PREFIX_USER_TOKEN + token);
        if (StrUtil.isNotEmpty(cacheToken)) {
            //校验redis中的jwt是否于当前token一致
            if (StrUtil.equals(token, cacheToken)) {
                //一致则更新redis中的token
                this.tokenRefresh(cacheToken);
                return true;
            }
        }
        return false;
    }
    /**
     * 更新redis缓存中的Token
     * @param cacheToken 缓存中存在的jwt
     */
    private boolean tokenRefresh(String cacheToken) {
        return redisUtil.expire(Constant.PREFIX_USER_TOKEN + cacheToken, EXPIRE_TIME * 2 / 1000);
    }

}
