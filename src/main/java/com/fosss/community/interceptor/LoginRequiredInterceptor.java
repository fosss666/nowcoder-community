package com.fosss.community.interceptor;

import com.fosss.community.annotation.LoginRequired;
import com.fosss.community.utils.ThreadLocalUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author: fosss
 * Date: 2023/9/5
 * Time: 19:08
 * Description:
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Resource
    private ThreadLocalUtil threadLocalUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //得到注解
        // instanceof：测试它左边的对象是否是它右边的类的实例，返回 boolean 的数据类型。
        //判断拦截到的是不是方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired annotation = method.getAnnotation(LoginRequired.class);
            if (annotation != null && threadLocalUtil.get() == null) {
                //方法标注有`LoginRequired`这个注解，并且此时客户端未登录，则重定向到登录页面，并且不放行
                response.sendRedirect(request.getContextPath() + "/" + "login ");
                return false;
            }
        }
        return true;
    }
}
