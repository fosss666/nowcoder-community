package com.fosss.community.service.impl;

import com.fosss.community.dao.CommentMapper;
import com.fosss.community.entity.Comment;
import com.fosss.community.service.CommentService;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
}
