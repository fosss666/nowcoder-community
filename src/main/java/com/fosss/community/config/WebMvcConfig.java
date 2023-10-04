package com.fosss.community.config;

import com.fosss.community.interceptor.LoginRequiredInterceptor;
import com.fosss.community.interceptor.LoginTicketInterceptor;
import com.fosss.community.interceptor.MessageInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author: fosss
 * Date: 2023/9/2
 * Time: 15:40
 * Description:
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginTicketInterceptor loginTicketInterceptor;
    @Resource
    private LoginRequiredInterceptor loginRequiredInterceptor;
    @Resource
    private MessageInterceptor messageInterceptor;

    /**
     * 添加拦截器，配置拦截路径，放行静态资源
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/static/**");
        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/static/**");
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/static/**");
    }
}
