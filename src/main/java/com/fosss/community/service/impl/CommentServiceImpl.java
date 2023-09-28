package com.fosss.community.service.impl;

import com.fosss.community.dao.CommentMapper;
import com.fosss.community.entity.Comment;
import com.fosss.community.service.CommentService;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.utils.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.fosss.community.constant.CommentConstant.ENTITY_TYPE_POST;
import static com.fosss.community.constant.ExceptionConstant.PARAMETER_NULL;

/**
 * @author: fosss
 * Date: 2023/9/12
 * Time: 22:15
 * Description:
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private SensitiveFilter sensitiveFilter;
    @Resource
    private DiscussPostService discussPostService;

    /**
     * 分页查询评论
     *
     * @param entityType 评论的类型
     * @param entityId   评论的帖子id
     * @param offset     第几页
     * @param limit      每页显示多少条
     * @return 返回查询到的评论
     */
    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    /**
     * 条件查询评论的数量
     */
    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * 添加评论
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public void addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException(PARAMETER_NULL);
        }

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
    }

    @Override
    public int findUserCount(int userId) {
        return commentMapper.selectCountByUser(userId);
    }

    /**
     * 查询用户的回复
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Comment> findUserComments(int userId, int offset, int limit) {
        return commentMapper.selectCommentsByUser(userId, offset, limit);
    }
}
