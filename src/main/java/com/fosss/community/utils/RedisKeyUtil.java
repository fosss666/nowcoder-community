package com.fosss.community.utils;

/**
 * @author: fosss
 * Date: 2023/9/24
 * Time: 18:08
 * Description: 生成redis key
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String ENTITY_PREFIX = "like:entity";
    private static final String USER_PREFIX = "like:user";

    /**
     * 生成某个实体点赞的key
     * like:entity:entityType:entityId -> set(userId)
     */
    public static String generateEntityLikeKey(int entityType, int entityId) {
        return ENTITY_PREFIX + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 生成统计用户被点赞数的key
     * like:user:userId -> int
     */
    public static String generateUserLikeKey(int userId) {
        return USER_PREFIX + SPLIT + userId;
    }
}
