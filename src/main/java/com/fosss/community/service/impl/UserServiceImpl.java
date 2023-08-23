package com.fosss.community.service.impl;

import com.fosss.community.dao.UserMapper;
import com.fosss.community.entity.User;
import com.fosss.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

}
