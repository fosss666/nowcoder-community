package com.fosss.community.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.fosss.community.constant.ExceptionConstant.PARAMETER_NULL;

/**
 * @author: fosss
 * Date: 2023/9/2
 * Time: 15:43
 * Description:获取cookie的值
 */
public class CookieUtil {
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null || name.equals("")) throw new IllegalArgumentException(PARAMETER_NULL);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
