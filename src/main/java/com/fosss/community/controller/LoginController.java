package com.fosss.community.controller;

import com.fosss.community.constant.ActivationStatusConstant;
import com.fosss.community.constant.RegisterErrorEnum;
import com.fosss.community.entity.User;
import com.fosss.community.service.UserService;
import com.sun.org.apache.regexp.internal.RE;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
     * 跳转登录页面
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
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

    /**
     * 激活
     * http://localhost:8080/community/activation/101/code
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int res = userService.activation(userId, code);
        if (res == ActivationStatusConstant.ACTIVATION_SUCCESS) {
            //激活成功
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (res == ActivationStatusConstant.ACTIVATION_REPEAT) {
            //重复激活
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            //激活失败
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }
}