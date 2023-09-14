package com.fosss.community.dao;

import com.fosss.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     * 查询当前用户的会话列表,针对每个会话只返回一条最新的私信.
     *
     * @param userId 用户id
     * @param offset 分页起始索引
     * @param limit  每页显示条数
     */
    List<Message> selectConversations(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询当前用户的会话数量.
     *
     * @param userId 用户id
     * @return 返回当前用户会话数量
     */
    int selectConversationCount(@Param("userId") int userId);

    /**
     * 查询某个会话所包含的私信列表.
     *
     * @param conversationId 会话的两个用户id拼接成的字符串  如：“111_113”
     * @param offset         分页起始索引
     * @param limit          每页显示条数
     */
    List<Message> selectLetters(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询某个会话所包含的私信数量
     *
     * @param conversationId 会话的两个用户id拼接成的字符串
     */
    int selectLetterCount(@Param("conversationId") String conversationId);

    /**
     * 查询未读私信的数量
     *
     * @param userId         用户id
     * @param conversationId 会话的两个用户id拼接成的字符串  如果为null,代表查询的是总的未读数量
     */
    int selectLetterUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    /**
     * 设置已读
     *
     * @param ids
     */
    void updateMessageStatus(List<Integer> ids, int status);
}
