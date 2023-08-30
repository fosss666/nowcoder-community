package com.fosss.community.service.impl;

import com.fosss.community.constant.ActivationStatusConstant;
import com.fosss.community.constant.RegisterErrorEnum;
import com.fosss.community.constant.UserStatusConstant;
import com.fosss.community.dao.UserMapper;
import com.fosss.community.entity.User;
import com.fosss.community.service.UserService;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.MailUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${server.port}")
    private int port;

    @Autowired
    private UserMapper userMapper;
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private MailUtil mailUtil;


    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    /**
     * 用户注册
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put(RegisterErrorEnum.USERNAME_NULL.getKey(), RegisterErrorEnum.USERNAME_NULL.getMsg());
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put(RegisterErrorEnum.PASSWORD_NULL.getKey(), RegisterErrorEnum.PASSWORD_NULL.getMsg());
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put(RegisterErrorEnum.EMAIL_NULL.getKey(), RegisterErrorEnum.EMAIL_NULL.getMsg());
            return map;
        }

        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put(RegisterErrorEnum.USERNAME_EXIST.getKey(), RegisterErrorEnum.USERNAME_EXIST.getMsg());
            return map;
        }

        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put(RegisterErrorEnum.EMAIL_EXIST.getKey(), RegisterErrorEnum.EMAIL_EXIST.getMsg());
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8081/community/activation/101/code
        String url = domain + ":" + port + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailUtil.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    /**
     * 激活验证码
     */
    @Override
    public int activation(int userId, String code) {
        //查询激活状态
        User user = userMapper.selectById(userId);
        if (user.getStatus() == UserStatusConstant.ALREADY_ACTIVATED) {
            //已激活
            return ActivationStatusConstant.ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            //激活码正确,更新用户状态
            userMapper.updateStatus(userId, UserStatusConstant.ALREADY_ACTIVATED);
            return ActivationStatusConstant.ACTIVATION_SUCCESS;
        } else {
            return ActivationStatusConstant.ACTIVATION_FAILURE;
        }
    }
}
