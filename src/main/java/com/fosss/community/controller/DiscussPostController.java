package com.fosss.community.controller;

import com.fosss.community.constant.ResultEnum;
import com.fosss.community.entity.Comment;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.entity.Page;
import com.fosss.community.entity.User;
import com.fosss.community.service.CommentService;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.service.UserService;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.ThreadLocalUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.fosss.community.constant.CommentConstant.ENTITY_TYPE_COMMENT;
import static com.fosss.community.constant.CommentConstant.ENTITY_TYPE_POST;

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
    @Resource
    private CommentService commentService;

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
    public String selectById(@PathVariable("id") int id, Model model, Page page) {
        DiscussPost discussPost = discussPostService.selectById(id);
        model.addAttribute("post", discussPost);
        //查询用户详情
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("user", user);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + id);
        page.setRows(discussPost.getCommentCount());

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, discussPost.getId(), page.getOffset(), page.getLimit());
        // 评论VO列表
        List<Object> commentVoList = new ArrayList<>();
        if (commentList != null) {
            commentVoList = commentList.stream().map(comment -> {
                Map<String, Object> commentVo = new HashMap<>();
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));
                //查询此评论的回复
                // 回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                //封装每条回复对应的作者及目标用户对象
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    replyVoList = replyList.stream().map(reply -> {
                        Map<String, Object> replyVo = new HashMap<>();
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        return replyVo;
                    }).collect(Collectors.toList());
                }
                commentVo.put("replys", replyVoList);
                // 查询回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                return commentVo;
            }).collect(Collectors.toList());
        }
        model.addAttribute("comments", commentVoList);

        //跳转到详情页面
        return "/site/discuss-detail";
    }
}
