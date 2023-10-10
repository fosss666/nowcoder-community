package com.fosss.community.service;

import com.fosss.community.entity.LoginTicket;
import com.fosss.community.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
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

    /**
     * 更新用户头像
     *
     * @param userId
     * @param avatarPath
     */
    void upload(int userId, String avatarPath);

    /**
     * 更新密码
     */
    Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword);

    /**
     * 根据用户名查询用户
     *
     * @param targetName
     * @return
     */
    User findUserByName(String targetName);

    /**
     * 重置密码
     *
     * @param email
     * @param password
     * @return
     */
    Map<String, Object> resetPassword(String email, String password);

    /**
     * 根据邮箱查询用户
     *
     * @param email
     * @return
     */
    User findUserByEmail(String email);

    /**
     * 获取用户权限
     */
    Collection<? extends GrantedAuthority> getAuthority(int userId);
}
