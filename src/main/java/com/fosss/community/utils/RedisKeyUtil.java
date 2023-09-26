package com.fosss.community.utils;

/**
 * @author: fosss
 * Date: 2023/9/24
 * Time: 18:08
 * Description: 生成redis key
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_LIKE_ENTITY = "like:entity";
    private static final String PREFIX_LIKE_USER = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

    /**
     * 生成某个实体点赞的key
     * like:entity:entityType:entityId -> set(userId)
     */
    public static String generateEntityLikeKey(int entityType, int entityId) {
        return PREFIX_LIKE_ENTITY + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 生成统计用户被点赞数的key
     * like:user:userId -> int
     */
    public static String generateUserLikeKey(int userId) {
        return PREFIX_LIKE_USER + SPLIT + userId;
    }

    /**
     * 生成关注的key
     * followee:userId:entityType -> zset(entityId,now)
     */
    public static String generateFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 生成粉丝的key
     * follower:entityType:entityId -> zset(userId,now)
     */
    public static String generateFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }
}
