package com.fosss.community;

import com.fosss.community.dao.DiscussPostMapper;
import com.fosss.community.dao.UserMapper;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: fosss
 * Date: 2023/8/20
 * Time: 20:41
 * Description:
 */
@SpringBootTest
public class MybatisTest {

    @Resource
    private UserMapper userMapper;
    @Resource
    private DiscussPostMapper discussPostMapper;

    @Test
    void testSearchById() {
        User user = userMapper.selectById(101);
        System.out.println("user = " + user);
    }

    @Test
    void test1() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        System.out.println(discussPosts);
    }
}
