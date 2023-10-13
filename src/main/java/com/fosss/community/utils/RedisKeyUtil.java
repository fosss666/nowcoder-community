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
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";
    //独立访客（按ip去重统计）
    public static final String PREFIX_UV = "uv";
    //每日活跃用户（按用户去重统计）
    public static final String PREFIX_DAU = "dau";


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

    /**
     * 生成验证码的key
     */
    public static String generateKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 生成登录凭证的key
     */
    public static String generateTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 生成用户信息的key
     */
    public static String generateUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    /**
     * 单日UV
     */
    public static String generateUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    /**
     * 某一时间范围内UV
     */
    public static String generateUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    /**
     * 单日DAU
     */
    public static String generateDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    /**
     * 某一时间范围内DAU
     */
    public static String generateDAUKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }
}
