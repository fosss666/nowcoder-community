package com.fosss.community.controller;

import com.fosss.community.annotation.LoginRequired;
import com.fosss.community.constant.ResultEnum;
import com.fosss.community.service.FollowService;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.ThreadLocalUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author: fosss
 * Date: 2023/9/26
 * Time: 22:01
 * Description:
 */
@Controller
public class FollowController {

    @Resource
    private FollowService followService;
    @Resource
    private ThreadLocalUtil threadLocalUtil;

    /**
     * 关注
     * 关注的不一定是人，所以要传过来实体类型
     */
    @PostMapping("/follow")
    @ResponseBody
    @LoginRequired
    public String follow(int entityType, int entityId) {
        followService.follow(threadLocalUtil.get().getId(), entityType, entityId);

        return CommunityUtil.getJSONString(ResultEnum.FOLLOW_SUCCESS.code, ResultEnum.FOLLOW_SUCCESS.msg);
    }

    /**
     * 取关
     */
    @PostMapping("/unfollow")
    @ResponseBody
    @LoginRequired
    public String unfollow(int entityType, int entityId) {
        followService.unfollow(threadLocalUtil.get().getId(), entityType, entityId);

        return CommunityUtil.getJSONString(ResultEnum.UNFOLLOW_SUCCESS.code, ResultEnum.UNFOLLOW_SUCCESS.msg);
    }
}
