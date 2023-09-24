package com.fosss.community.controller;

import com.fosss.community.annotation.LoginRequired;
import com.fosss.community.constant.ResultEnum;
import com.fosss.community.entity.User;
import com.fosss.community.service.LikeService;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.ThreadLocalUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: fosss
 * Date: 2023/9/24
 * Time: 18:30
 * Description:
 */
@Controller
public class LikeController {

    @Resource
    private ThreadLocalUtil threadLocalUtil;
    @Resource
    private LikeService likeService;

    @PostMapping(path = "/like")
    @ResponseBody
    @LoginRequired
    public String like(int entityType, int entityId, int entityUserId) {
        User user = threadLocalUtil.get();

        // 点赞
        likeService.like(user.getId(), entityType, entityId);

        // 数量
        long likeCount = likeService.getLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.getLikeStatusByUserId(user.getId(), entityType, entityId);
        // 返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg, map);
    }
}
