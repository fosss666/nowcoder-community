package com.fosss.community.service;

import com.fosss.community.entity.DiscussPost;

import java.util.List;

/**
 * @author: fosss
 * Date: 2023/8/23
 * Time: 15:31
 * Description:
 */
public interface DiscussPostService {
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode);

    int findDiscussPostRows(int userId);

    /**
     * 发布帖子
     */
    void insertDiscussPost(DiscussPost discussPost);

    /**
     * 查询帖子
     */
    DiscussPost selectById(int id);

    /**
     * 更新帖子评论数量
     *
     * @param entityId
     * @param count
     */
    void updateCommentCount(int entityId, int count);

    /**
     * 更新帖子类型
     */
    void updateType(int id, int type);

    /**
     * 更新帖子状态
     */
    void updateStatus(int id, int status);

    /**
     * 更新帖子分数
     *
     * @param postId
     * @param score
     */
    void updateScore(int postId, double score);
}
