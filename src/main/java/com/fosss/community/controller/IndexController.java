package com.fosss.community.controller;

import com.fosss.community.dao.DiscussPostMapper;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.entity.Page;
import com.fosss.community.entity.User;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: fosss
 * Date: 2023/8/23
 * Time: 16:36
 * Description:
 */
@Controller
public class IndexController {

    @Resource
    private UserService userService;
    @Resource
    private DiscussPostService discussPostService;

    /**
     * 查询首页
     */
    @GetMapping("/index")
    public String index(Model model, Page page) {
        //设置分页, 方法调用前,SpringMVC会自动实例化Model和Page,并将Page注入Model。所以,在thymeleaf中可以直接访问Page对象中的数据.
        page.setPath("/index");
        page.setRows(discussPostService.findDiscussPostRows(0));

        //查询数据
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), 10);
        //查询用户
        List<Map<String, Object>> discussPosts = list.stream().map(discussPost -> {
            Map<String, Object> map = new HashMap<>();
            map.put("post", discussPost);
            User user = userService.findUserById(discussPost.getUserId());
            map.put("user", user);
            return map;
        }).collect(Collectors.toList());

        model.addAttribute("discussPosts", discussPosts);

        return "/index";
    }
}












