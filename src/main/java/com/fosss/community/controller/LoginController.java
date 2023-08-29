package com.fosss.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: fosss
 * Date: 2023/8/29
 * Time: 20:41
 * Description:
 */
@Controller
public class LoginController {

    /**
     * 用户注册
     */
    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }
}
