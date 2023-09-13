package com.fosss.community.constant;

/**
 * @author: fosss
 * Date: 2023/9/12
 * Time: 22:11
 * Description:
 */
public class CommentConstant {
    /**
     * 实体类型: 帖子
     */
    public static int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型: 评论
     */
    public static int ENTITY_TYPE_COMMENT = 2;

    /**
     * 评论状态
     * 0：正常   1：被删除
     */
    public static final int COMMENT_NORMAL=0;
    public static final int COMMENT_DELETED=1;
}
