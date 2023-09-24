package com.fosss.community.utils;

/**
 * @author: fosss
 * Date: 2023/9/24
 * Time: 18:08
 * Description: 生成redis key
 */
public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX = "like:entity";

    /**
     * 生成某个实体点赞的key
     */
    public static String generateLikeKey(int entityType, int entityId) {
        return PREFIX + SPLIT + entityType + SPLIT + entityId;
    }
}
