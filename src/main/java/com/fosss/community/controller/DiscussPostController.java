package com.fosss.community.controller;

import com.fosss.community.constant.ResultEnum;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.entity.User;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.service.UserService;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.ThreadLocalUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: fosss
 * Date: 2023/9/9
 * Time: 20:13
 * Description:
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Resource
    private ThreadLocalUtil threadLocalUtil;
    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private UserService userService;

    /**
     * 发布帖子
     */
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(DiscussPost discussPost) {
        //获取用户判断是否登录
        User user = threadLocalUtil.get();
        if (user == null)
            return CommunityUtil.getJSONString(ResultEnum.PERMISSION_ERROR.code, ResultEnum.PERMISSION_ERROR.msg);
        discussPost.setUserId(user.getId());
        discussPost.setCreateTime(new Date());
        discussPostService.insertDiscussPost(discussPost);

        return CommunityUtil.getJSONString(ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg);
    }

    /**
     * 查询帖子详情
     */
    @GetMapping("/detail/{id}")
    public String selectById(@PathVariable("id") int id, Model model) {
        DiscussPost discussPost = discussPostService.selectById(id);
        model.addAttribute("post", discussPost);
        //查询用户详情
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        //跳转到详情页面
        return "/site/discuss-detail";
    }
}
