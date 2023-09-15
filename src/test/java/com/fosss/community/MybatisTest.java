package com.fosss.community;

import com.fosss.community.dao.DiscussPostMapper;
import com.fosss.community.dao.LoginTicketMapper;
import com.fosss.community.dao.MessageMapper;
import com.fosss.community.dao.UserMapper;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.entity.LoginTicket;
import com.fosss.community.entity.Message;
import com.fosss.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
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
    @Resource
    private LoginTicketMapper loginTicketMapper;
    @Resource
    private MessageMapper messageMapper;

    @Test
    void testInsertMessage() {
        Message message = new Message();
        message.setConversationId("test");
        messageMapper.insertMessage(message);
    }

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

    @Test
    void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(999);
        loginTicket.setTicket("testtesttest");
        loginTicket.setExpired(new Date());
        loginTicket.setStatus(0);
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    void testSelectByTicket() {
        LoginTicket testtesttest = loginTicketMapper.selectByTicket("testtesttest");
        System.out.println("testtesttest = " + testtesttest);
    }

    @Test
    void testUpdateStatus() {
        loginTicketMapper.updateStatus(null, 1);
    }
}
