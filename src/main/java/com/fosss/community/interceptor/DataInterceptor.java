package com.fosss.community.interceptor;

import com.fosss.community.entity.User;
import com.fosss.community.service.DataService;
import com.fosss.community.utils.ThreadLocalUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: fosss
 * Date: 2023/10/14
 * Time: 0:14
 * Description:
 */
@Component
public class DataInterceptor implements HandlerInterceptor {
    @Resource
    private DataService dataService;
    @Resource
    private ThreadLocalUtil threadLocalUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计UV
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);
        //统计DAU
        User user = threadLocalUtil.get();
        if (user != null) {
            //登录才需要统计DAU
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
