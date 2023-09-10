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
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    int findDiscussPostRows(int userId);

    /**
     * 发布帖子
     */
    void insertDiscussPost(DiscussPost discussPost);

    /**
     * 查询帖子
     */
    DiscussPost selectById(int id);
}
