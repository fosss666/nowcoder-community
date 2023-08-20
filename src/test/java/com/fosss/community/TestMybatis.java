package com.fosss.community;

import com.fosss.community.dao.UserMapper;
import com.fosss.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author: fosss
 * Date: 2023/8/20
 * Time: 20:41
 * Description:
 */
@SpringBootTest
public class TestMybatis {

    @Resource
    private UserMapper userMapper;

    @Test
    void testSearchById() {
        User user = userMapper.selectById(101);
        System.out.println("user = " + user);
    }
}
