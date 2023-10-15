package com.fosss.community.dao;

import com.fosss.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     * 分页查询
     *
     * @param orderMode 0:按时间查询  1：按分数（热度）查询
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 发布帖子
     */
    void insertDiscussPost(DiscussPost discussPost);

    /**
     * 查询帖子
     */
    @Select("select * from discuss_post where id=#{id}")
    DiscussPost selectById(int id);

    /**
     * 更新帖子评论数量
     *
     * @param entityId
     * @param count
     */
    @Update("update discuss_post set comment_count =#{count} where id=#{entityId}")
    void updateCommentCount(@Param("entityId") int entityId, @Param("count") int count);

    /**
     * 更新帖子状态
     */
    @Update("update discuss_post set type=#{type} where id=#{id}")
    void updateType(int id, int type);

    /**
     * 更新帖子状态
     */
    @Update("update discuss_post set status=#{status} where id=#{id}")
    void updateStatus(int id, int status);

    /**
     * 更新帖子分数
     *
     * @param postId
     * @param score
     */
    @Update("update discuss_post set score=#{score} where id=#{postId}")
    void updateScore(int postId, double score);
}
