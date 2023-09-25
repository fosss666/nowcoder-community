package com.fosss.community.service;

/**
 * @author: fosss
 * Date: 2023/9/24
 * Time: 18:12
 * Description:
 */
public interface LikeService {
    /**
     * 点赞
     */
    void like(int userId, int entityType, int entityId, int entityUserId);

    /**
     * 查询某实体点赞数量
     */
    int getEntityLikeCount(int entityType, int entityId);

    /**
     * 查询某人对某实体的点赞状态
     */
    int getLikeStatusByUserId(int userId, int entityType, int entityId);

    /**
     * 获取用户获赞数量
     *
     * @param userId
     * @return
     */
    int getUserLikeCount(int userId);
}
