package com.fosss.community.controller;

import com.fosss.community.annotation.LoginRequired;
import com.fosss.community.constant.ExceptionConstant;
import com.fosss.community.constant.LikeConstant;
import com.fosss.community.constant.ResultEnum;
import com.fosss.community.entity.Page;
import com.fosss.community.entity.User;
import com.fosss.community.exception.BusinessException;
import com.fosss.community.service.FollowService;
import com.fosss.community.service.UserService;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.ThreadLocalUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    @Resource
    private UserService userService;

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

    /**
     * 获取关注列表
     */
    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Model model, Page page) {
        //查询用户
        User user = userService.findUserById(userId);
        //非空判断
        if (user == null) throw new BusinessException(ExceptionConstant.USER_NOT_FOUND);
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.getFolloweeCount(userId, LikeConstant.ENTITY_TYPE_USER));
        //查询关注列表
        List<Map<String, Object>> list = followService.getFollowees(userId, page.getOffset(), page.getLimit());
        //记录当前登录用户是否关注了userId关注的用户
        if (list != null) {
            for (Map<String, Object> map : list) {
                User targetUser = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(targetUser.getId()));
            }
        }
        //添加到模板中
        model.addAttribute("users", list);
        return "/site/followee";
    }

    /**
     * 获取粉丝列表
     */
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Model model, Page page) {
        //查询用户
        User user = userService.findUserById(userId);
        //非空判断
        if (user == null) throw new BusinessException(ExceptionConstant.USER_NOT_FOUND);
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.getFollowerCount(userId, LikeConstant.ENTITY_TYPE_USER));
        //查询粉丝列表
        List<Map<String, Object>> list = followService.getFollowers(userId, page.getOffset(), page.getLimit());
        //记录当前登录用户是否关注了userId关注的用户
        if (list != null) {
            for (Map<String, Object> map : list) {
                User targetUser = (User) map.get("user");
                //是否关注
                map.put("hasFollowed", hasFollowed(targetUser.getId()));
            }
        }
        //添加到模板中
        model.addAttribute("users", list);
        return "/site/followee";
    }

    /**
     * 判断当前登录用户是否关注了某个用户
     */
    private boolean hasFollowed(int targetUserId) {
        if (threadLocalUtil.get() == null) return false;
        return followService.hasFollowed(threadLocalUtil.get().getId(), targetUserId, LikeConstant.ENTITY_TYPE_USER);
    }

}
