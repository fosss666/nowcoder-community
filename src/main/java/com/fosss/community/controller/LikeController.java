package com.fosss.community.controller;

import com.fosss.community.annotation.LoginRequired;
import com.fosss.community.constant.EventConstant;
import com.fosss.community.constant.LikeConstant;
import com.fosss.community.constant.ResultEnum;
import com.fosss.community.entity.Event;
import com.fosss.community.entity.User;
import com.fosss.community.event.EventProducer;
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
    @Resource
    private EventProducer eventProducer;

    /**
     * 点赞
     *
     * @param entityType   实体类型：1帖子  2评论
     * @param entityId     实体id
     * @param entityUserId 实体的用户id
     * @return
     */
    @PostMapping(path = "/like")
    @ResponseBody
    @LoginRequired
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = threadLocalUtil.get();

        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);

        // 数量
        long likeCount = likeService.getEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.getLikeStatusByUserId(user.getId(), entityType, entityId);
        // 返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        //如果是点赞，触发点赞事件
        if (likeStatus == LikeConstant.LIKED) {
            Event event = new Event()
                    .setTopic(EventConstant.EVENT_TOPIC_LIKE)
                    .setUserId(threadLocalUtil.get().getId())
                    .setEntityId(entityId)
                    .setEntityType(entityType)
                    .setEntityUserId(entityUserId)
                    .setData(EventConstant.EVENT_CONTENT_POST_ID, postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJSONString(ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg, map);
    }
}
