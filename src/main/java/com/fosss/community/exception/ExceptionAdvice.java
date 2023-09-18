package com.fosss.community.exception;

import com.fosss.community.constant.ExceptionConstant;
import com.fosss.community.constant.ResultEnum;
import com.fosss.community.utils.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: fosss
 * Date: 2023/9/17
 * Time: 22:24
 * Description:
 */
@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    @ExceptionHandler({Exception.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error(ExceptionConstant.SERVER_EXCEPTION + ":" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            log.error(element.toString());
        }
        //根据是否是异步请求确定返回数据，异步请求需要返回json
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            //是异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(ResultEnum.SERVER_EXCEPTION.code, ResultEnum.SERVER_EXCEPTION.msg));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
