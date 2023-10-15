package com.fosss.community.controller;

import com.fosss.community.annotation.LoginRequired;
import com.fosss.community.constant.DiscussPostConstant;
import com.fosss.community.constant.EventConstant;
import com.fosss.community.constant.LikeConstant;
import com.fosss.community.constant.ResultEnum;
import com.fosss.community.entity.*;
import com.fosss.community.event.EventProducer;
import com.fosss.community.service.CommentService;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.service.LikeService;
import com.fosss.community.service.UserService;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.RedisKeyUtil;
import com.fosss.community.utils.ThreadLocalUtil;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Resource
    private LikeService likeService;
    @Resource
    private EventProducer eventProducer;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 发布帖子
     */
    @LoginRequired
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

        //触发es事件
        Event event = new Event()
                .setTopic(EventConstant.EVENT_TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPost.getId());
        eventProducer.fireEvent(event);

        //记录一下需要进行分数刷新
        String key = RedisKeyUtil.generatePostScoreRefreshKey();
        redisTemplate.opsForSet().add(key, discussPost.getId());

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

        //点赞数量
        int likeCount = likeService.getEntityLikeCount(ENTITY_TYPE_POST, id);
        model.addAttribute("likeCount", likeCount);
        //点赞状态
        int likeStatus = threadLocalUtil.get() == null ? LikeConstant.NOT_LIKED : likeService.getLikeStatusByUserId(threadLocalUtil.get().getId(), ENTITY_TYPE_POST, id);
        model.addAttribute("likeStatus", likeStatus);

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
                //点赞数量
                int likeCount2 = likeService.getEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount2);
                //点赞状态
                int likeStatus2 = threadLocalUtil.get() == null ? 0 : likeService.getLikeStatusByUserId(threadLocalUtil.get().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeStatus", likeStatus2);

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
                        //点赞数量
                        int likeCount3 = likeService.getEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount3);
                        //点赞状态
                        int likeStatus3 = threadLocalUtil.get() == null ? 0 : likeService.getLikeStatusByUserId(threadLocalUtil.get().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus3);

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

    /**
     * 置顶、取消置顶
     */

    @PostMapping(path = "/top")
    @ResponseBody
    public String setTop(int id) {
        DiscussPost discussPostById = discussPostService.selectById(id);
        // 获取置顶状态，1为置顶，0为正常状态,1^1=0 0^1=1
        int type = discussPostById.getType() == DiscussPostConstant.TOP ? DiscussPostConstant.UN_TOP : DiscussPostConstant.TOP;
        //更新帖子类型
        discussPostService.updateType(id, type);
        // 返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);

        // 触发发帖事件(更改帖子类型)
        Event event = new Event()
                .setTopic(EventConstant.EVENT_TOPIC_PUBLISH)
                .setUserId(threadLocalUtil.get().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg, map);
    }

    /**
     * 加精、取消加精
     */
    @PostMapping(path = "/wonderful")
    @ResponseBody
    public String setWonderful(int id) {
        DiscussPost discussPostById = discussPostService.selectById(id);
        int status = discussPostById.getStatus() == DiscussPostConstant.WONDERFUL ? DiscussPostConstant.UN_WONDERFUL :
                DiscussPostConstant.WONDERFUL;

        // 1为加精，0为正常
        discussPostService.updateStatus(id, status);
        //此时的status为更新后帖子的状态
        if (status == DiscussPostConstant.WONDERFUL) {
            //记录一下需要进行分数刷新
            String key = RedisKeyUtil.generatePostScoreRefreshKey();
            redisTemplate.opsForSet().add(key, id);
        }

        // 返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);

        // 触发发帖事件(更改帖子状态)
        Event event = new Event()
                .setTopic(EventConstant.EVENT_TOPIC_PUBLISH)
                .setUserId(threadLocalUtil.get().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg, map);
    }

    /**
     * 删除帖子
     */
    @PostMapping(path = "/delete")
    @ResponseBody
    public String setDelete(int id) {
        discussPostService.updateStatus(id, DiscussPostConstant.DELETED);

        // 触发删帖事件
        Event event = new Event()
                .setTopic(EventConstant.EVENT_TOPIC_DELETE)
                .setUserId(threadLocalUtil.get().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }
}
