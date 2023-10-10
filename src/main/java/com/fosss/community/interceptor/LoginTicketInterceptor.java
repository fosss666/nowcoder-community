package com.fosss.community.interceptor;

import com.fosss.community.constant.LoginTicketStatusConstant;
import com.fosss.community.entity.LoginTicket;
import com.fosss.community.entity.User;
import com.fosss.community.service.UserService;
import com.fosss.community.utils.CookieUtil;
import com.fosss.community.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author: fosss
 * Date: 2023/9/2
 * Time: 15:38
 * Description:拦截器，处理用户登录后信息的存储，向模板引擎的数据注入
 */
@Component
@Slf4j
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;
    @Resource
    private ThreadLocalUtil threadLocalUtil;

    /**
     * controller执行之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandler执行");
        //获取ticket的值
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {
            //获取登录凭证
            LoginTicket loginTicket = userService.getByTicket(ticket);
            //凭证不能空，不能失效，不能过期
            if (loginTicket != null && loginTicket.getStatus() == LoginTicketStatusConstant.EFFECTIVE && loginTicket.getExpired().after(new Date())) {
                //查询用户信息
                User user = userService.findUserById(loginTicket.getUserId());
                //放入ThreadLocal中，保存用户信息
                threadLocalUtil.set(user);

                //向security中存储用户授权token
                Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                        user, ticket, userService.getAuthority(user.getId())
                );
                SecurityContextHolder.setContext(new SecurityContextImpl(authenticationToken));
            }
        }
        return true;
    }

    /**
     * controller执行之后
     * 将用户信息放到视图中
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle执行");
        User user = threadLocalUtil.get();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 模板引擎加载完后
     * 清除用户信息
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion执行");
        threadLocalUtil.clear();
    }
}











