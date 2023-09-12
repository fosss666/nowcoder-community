package com.fosss.community.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class Comment {

    private int id;
    private int userId;
    //评论类型   1：帖子的评论   2：评论的回复
    private int entityType;
    //评论的帖子的id
    private int entityId;
    //普通评论为0   如果是回复别人的评论则为别人的id
    private int targetId;
    private String content;
    private int status;
    private Date createTime;

}
