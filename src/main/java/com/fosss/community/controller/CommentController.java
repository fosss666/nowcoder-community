package com.fosss.community.controller;

import com.fosss.community.annotation.LoginRequired;
import com.fosss.community.constant.CommentConstant;
import com.fosss.community.constant.EventConstant;
import com.fosss.community.entity.Comment;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.entity.Event;
import com.fosss.community.event.EventProducer;
import com.fosss.community.service.CommentService;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.Date;

import static com.fosss.community.constant.CommentConstant.*;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ThreadLocalUtil threadLocalUtil;
    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private EventProducer eventProducer;

    /**
     * 添加评论
     *
     * @param discussPostId
     * @param comment
     * @return
     */
    @LoginRequired
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(threadLocalUtil.get().getId());
        comment.setStatus(COMMENT_NORMAL);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //添加评论事件的系统通知
        Event event = new Event()
                .setTopic(EventConstant.EVENT_TOPIC_COMMENT)
                .setUserId(threadLocalUtil.get().getId())
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setData(EventConstant.EVENT_CONTENT_POST_ID, discussPostId);
        //触发评论事件
        //判断评论的是帖子还是回复
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            //查询帖子的作者
            DiscussPost discussPost = discussPostService.selectById(comment.getEntityId());
            event.setEntityUserId(discussPost.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.selectById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        //触发es发布帖子事件
        event = new Event()
                .setTopic(EventConstant.EVENT_TOPIC_PUBLISH)
                .setUserId(comment.getUserId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(discussPostId);
        eventProducer.fireEvent(event);

        return "redirect:/discuss/detail/" + discussPostId;
    }

}
