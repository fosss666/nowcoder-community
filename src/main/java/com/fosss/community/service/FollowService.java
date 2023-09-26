package com.fosss.community.service;

/**
 * @author: fosss
 * Date: 2023/9/26
 * Time: 22:05
 * Description:
 */
public interface FollowService {
    /**
     * 关注
     *
     * @param userId     当前登录用户的id
     * @param entityType 关注的实体类型
     * @param entityId   关注的实体id
     */
    void follow(int userId, int entityType, int entityId);

    /**
     * 取关
     *
     * @param userId     当前登录用户的id
     * @param entityType 关注的实体类型
     * @param entityId   关注的实体id
     */
    void unfollow(int userId, int entityType, int entityId);

    /**
     * 获取关注数量
     *
     * @param userId         用户id
     * @param entityTypeUser 实体类型
     * @return
     */
    long getFolloweeCount(int userId, int entityTypeUser);

    /**
     * 获取粉丝数量
     *
     * @param userId
     * @param entityTypeUser
     * @return
     */
    long getFollowerCount(int userId, int entityTypeUser);

    /**
     * 当前登录的用户loginUser是否关注了当前访问的用户curUser
     *
     * @param loginUserId
     * @param curUserId
     * @param entityTypeUser
     * @return
     */
    boolean hasFollowed(int loginUserId, int curUserId, int entityTypeUser);
}
