package com.fosss.community.controller;

import com.fosss.community.constant.ResultEnum;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.entity.User;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.ThreadLocalUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: fosss
 * Date: 2023/9/9
 * Time: 20:13
 * Description:
 */
@RestController
@RequestMapping("/discuss")
public class DiscussPostController {

    @Resource
    private ThreadLocalUtil threadLocalUtil;
    @Resource
    private DiscussPostService discussPostService;

    /**
     * 发布帖子
     */
    @PostMapping("/add")
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
}
