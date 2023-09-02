package com.fosss.community.service;

import com.fosss.community.entity.LoginTicket;
import com.fosss.community.entity.User;

import java.util.Map;

/**
 * @author: fosss
 * Date: 2023/8/23
 * Time: 15:29
 * Description:
 */
public interface UserService {
    /**
     * 根据用户id查询用户
     */
    User findUserById(int id);

    /**
     * 用户注册
     */
    Map<String, Object> register(User user);

    /**
     * 激活验证码
     */
    int activation(int userId, String code);

    /**
     * 用户登录
     */
    Map<String, Object> login(String username, String password, int expiredSeconds);

    /**
     * 登出
     */
    void logout(String ticket);

    /**
     * 根据ticket获取登录凭证
     */
    LoginTicket getByTicket(String ticket);

}
