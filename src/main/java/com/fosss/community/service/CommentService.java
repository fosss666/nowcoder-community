package com.fosss.community.service;

import com.fosss.community.entity.Comment;

import java.util.List;

/**
 * @author: fosss
 * Date: 2023/9/12
 * Time: 22:15
 * Description:
 */
public interface CommentService {
    /**
     * 分页查询评论
     *
     * @param entityType 评论的类型
     * @param entityId   评论的帖子id
     * @param offset     第几页
     * @param limit      每页显示多少条
     * @return 返回查询到的评论
     */
    List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit);

    /**
     * 条件查询评论的数量
     */
    int findCommentCount(int entityType, int entityId);

    /**
     * 添加评论
     */
    void addComment(Comment comment);

    /**
     * 查询用户回复的数量
     */
    int findUserCount(int userId);

    /**
     * 查询用户的回复
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<Comment> findUserComments(int userId, int offset, int limit);

    Comment selectById(int id);
}
