package com.fosss.community.controller;

import com.fosss.community.constant.RegisterErrorEnum;
import com.fosss.community.entity.User;
import com.fosss.community.service.UserService;
import com.sun.org.apache.regexp.internal.RE;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: fosss
 * Date: 2023/8/29
 * Time: 20:41
 * Description:
 */
@Controller
public class LoginController {

    @Resource
    private UserService userService;

    /**
     * 跳转注册页面
     */
    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    /**
     * 用户注册
     */
    @PostMapping(path = "/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute(RegisterErrorEnum.USERNAME_NULL.getKey(), map.get(RegisterErrorEnum.USERNAME_NULL.getKey()));
            model.addAttribute(RegisterErrorEnum.PASSWORD_NULL.getKey(), map.get(RegisterErrorEnum.PASSWORD_NULL.getKey()));
            model.addAttribute(RegisterErrorEnum.EMAIL_NULL.getKey(), map.get(RegisterErrorEnum.EMAIL_NULL.getKey()));
            return "/site/register";
        }
    }
}
