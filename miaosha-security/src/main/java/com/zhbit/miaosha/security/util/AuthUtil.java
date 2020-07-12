package com.zhbit.miaosha.security.util;

import com.alibaba.fastjson.JSONObject;
import com.zhbit.miaosha.common.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 认证失败返回工具
 */
@Slf4j
public class AuthUtil {

    public static void responseJSON(HttpServletResponse response, Exception exception) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            if (exception != null) {
                if (exception instanceof AuthenticationException) {
                    writer.print(JSONObject.toJSONString(RestResponse.unauthorized(exception.getMessage())));
                    writer.flush();
                    return;
                } else if (exception instanceof AccessDeniedException) {
                    writer.print(JSONObject.toJSONString(RestResponse.forbidden()));
                    writer.flush();
                    return;
                }
            }
            writer.print(JSONObject.toJSONString(RestResponse.unauthorized()));
            writer.flush();
        } catch (IOException e) {
            log.error("ResponseJSON写出异常{}",e.getMessage());
        }
    }
}
