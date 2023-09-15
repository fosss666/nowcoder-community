package com.fosss.community.service;

import com.fosss.community.entity.Message;

import java.util.List;

/**
 * @author: fosss
 * Date: 2023/9/14
 * Time: 22:48
 * Description:
 */
public interface MessageService {
    List<Message> findConversations(int userId, int offset, int limit);

    int findConversationCount(int userId);

    List<Message> findLetters(String conversationId, int offset, int limit);

    int findLetterCount(String conversationId);

    int findLetterUnreadCount(int userId, String conversationId);

    /**
     * 设置已读
     *
     * @param ids
     */
    void readMessage(List<Integer> ids);

    /**
     * 添加消息
     *
     * @param message
     */
    void addMessage(Message message);

    /**
     * 删除私信
     *
     * @param id
     */
    void deleteMessage(int id);
}
