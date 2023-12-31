package com.fosss.community.constant;

/**
 * @author: fosss
 * Date: 2023/10/3
 * Time: 23:08
 * Description:
 */
public class EventConstant {
    //评论
    public static final String EVENT_TOPIC_COMMENT = "comment";
    //点赞
    public static final String EVENT_TOPIC_LIKE = "like";
    //关注
    public static final String EVENT_TOPIC_FOLLOW = "follow";
    //帖子发布
    public static final String EVENT_TOPIC_PUBLISH = "publish";
    //删除帖子
    public static final String EVENT_TOPIC_DELETE = "delete";
    //生成长图
    public static final String EVENT_TOPIC_SHARE = "share";

    public static final int SYSTEM_USER_ID = 1;

    public static final String EVENT_CONTENT_USERID = "userId";
    public static final String EVENT_CONTENT_ENTITY_TYPE = "entityType";
    public static final String EVENT_CONTENT_ENTITY_ID = "entityId";
    public static final String EVENT_CONTENT_POST_ID = "postId";
}
