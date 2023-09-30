package com.fosss.community.controller;

import com.fosss.community.constant.ActivationStatusConstant;
import com.fosss.community.constant.ExceptionConstant;
import com.fosss.community.constant.ResultEnum;
import com.fosss.community.constant.UserErrorEnum;
import com.fosss.community.entity.User;
import com.fosss.community.properties.ApplicationProperty;
import com.fosss.community.service.UserService;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.MailUtil;
import com.fosss.community.utils.RedisKeyUtil;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.fosss.community.constant.ExpiredConstant.DEFAULT_EXPIRED_SECONDS;
import static com.fosss.community.constant.ExpiredConstant.REMEMBER_EXPIRED_SECONDS;

/**
 * @author: fosss
 * Date: 2023/8/29
 * Time: 20:41
 * Description:
 */
@Slf4j
@Controller
public class LoginController {

    @Resource
    private ApplicationProperty applicationProperty;
    @Resource
    private UserService userService;
    @Resource
    private Producer kaptchaProducer;
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private MailUtil mailUtil;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


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
            model.addAttribute(UserErrorEnum.USERNAME_NULL.getKey(), map.get(UserErrorEnum.USERNAME_NULL.getKey()));
            model.addAttribute(UserErrorEnum.PASSWORD_NULL.getKey(), map.get(UserErrorEnum.PASSWORD_NULL.getKey()));
            model.addAttribute(UserErrorEnum.EMAIL_NULL.getKey(), map.get(UserErrorEnum.EMAIL_NULL.getKey()));
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

    /**
     * 获取验证码图片
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/) {
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        // 将验证码存入session
        //session.setAttribute("kaptcha", text);

        //为用户生成一个标识
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(applicationProperty.getContextPath());
        response.addCookie(cookie);
        //将验证码存入redis
        String redisKey = RedisKeyUtil.generateKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 将突图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            log.error(ExceptionConstant.CODE_IMAGE_ERROR + ":" + e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping(path = "/login")
    public String login(String username, String password, String code, boolean rememberme,
                        Model model, /*HttpSession session,*/ HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        // 检查验证码
        //String kaptcha = (String) session.getAttribute("kaptcha");
        String kaptcha = "";
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.generateKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute(UserErrorEnum.CODE_ERROR.getKey(), UserErrorEnum.CODE_ERROR.getMsg());
            return "/site/login";
        }

        // 检查账号,密码
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(applicationProperty.getContextPath());
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            //登录完成，重定向到首页
            return "redirect:/index";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    /**
     * 登出
     */
    @GetMapping(path = "/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }

    /**
     * 忘记密码页面
     */
    @GetMapping(path = "/forget")
    public String getForgetPage() {
        return "/site/forget";
    }

    /**
     * 获取验证码
     */
    @GetMapping(path = "/forget/code")
    @ResponseBody
    public String getForgetCode(String email, HttpSession session) {
        if (StringUtils.isBlank(email)) {
            return CommunityUtil.getJSONString(ResultEnum.EMAIL_NULL.code, ResultEnum.EMAIL_NULL.msg);
        }

        //查询邮箱是否注册
        User user = userService.findUserByEmail(email);

        // 发送邮件
        Context context = new Context();
        context.setVariable("email", email);
        String code = CommunityUtil.generateUUID().substring(0, 4);
        context.setVariable("verifyCode", code);
        String content = templateEngine.process("/mail/forget", context);
        mailUtil.sendMail(email, "找回密码", content);

        // 保存验证码
        session.setAttribute("verifyCode", code);

        return CommunityUtil.getJSONString(ResultEnum.SUCCESS.code);
    }

    /**
     * 重置密码
     */
    @PostMapping(path = "/forget/password")
    public String resetPassword(String email, String verifyCode, String password, Model model, HttpSession session) {
        String code = (String) session.getAttribute("verifyCode");
        if (StringUtils.isBlank(verifyCode) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(verifyCode)) {
            model.addAttribute("codeMsg", "验证码错误!");
            return "/site/forget";
        }

        Map<String, Object> map = userService.resetPassword(email, password);
        if (map.containsKey("user")) {
            return "redirect:/login";
        } else {
            model.addAttribute("emailMsg", map.get("emailMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/forget";
        }
    }
}
