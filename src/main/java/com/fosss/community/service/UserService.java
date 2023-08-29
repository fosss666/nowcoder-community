package com.fosss.community.service;

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
}
