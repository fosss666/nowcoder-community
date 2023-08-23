package com.fosss.community.service;

import com.fosss.community.entity.User;

/**
 * @author: fosss
 * Date: 2023/8/23
 * Time: 15:29
 * Description:
 */
public interface UserService {
    User findUserById(int id);
}
