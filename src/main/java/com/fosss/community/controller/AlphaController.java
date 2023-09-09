package com.fosss.community.controller;

import com.fosss.community.utils.CommunityUtil;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author: fosss
 * Date: 2023/8/18
 * Time: 19:06
 * Description:
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @GetMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "hello world";
    }

    /**
     * 相应html
     */
    @GetMapping("/teacher")
    public String teacher(Model model) {
        model.addAttribute("name", "王树国1");
        model.addAttribute("age", 50);
        return "/demo/teacher";
    }

    /**
     * 设置Cookie
     */
    @GetMapping("/cookie/set")
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置生效域名
        //cookie.setDomain();
        //设置生效路径
        cookie.setPath("/community/alpha");
        //设置存储时间，单位为秒
        cookie.setMaxAge(60 * 30);
        response.addCookie(cookie);
        return "set cookie";
    }

    /**
     * 获取Cookie
     * 1.从request中获取所有cookie
     * 2.用注解获取指定cookie
     */
    @GetMapping("/cookie/get")
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        return code;
    }

    /**
     * 设置session
     * 浏览器cookie中会自动保存session的id(JSESSIONID)
     */
    @GetMapping("/session/set")
    @ResponseBody
    public String setSession(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "张三");
        return "set session";
    }

    /**
     * 获取session
     */
    @GetMapping("/session/get")
    @ResponseBody
    public String getSession(HttpSession session) {
        int id = (int) session.getAttribute("id");
        String name = (String) session.getAttribute("name");
        return id + ":" + name;
    }

    /**
     * 测试ajax
     */
    @PostMapping("/ajax")
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println("name = " + name);
        System.out.println("age = " + age);
        return CommunityUtil.getJSONString(0, "操作成功");
    }

}
