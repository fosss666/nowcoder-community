package com.fosss.community.service.impl;

import com.fosss.community.constant.MessageConstant;
import com.fosss.community.dao.MessageMapper;
import com.fosss.community.entity.Message;
import com.fosss.community.service.MessageService;
import com.fosss.community.utils.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author: fosss
 * Date: 2023/9/14
 * Time: 22:49
 * Description:
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    /**
     * 设置已读
     *
     * @param ids
     */
    @Override
    public void readMessage(List<Integer> ids) {
        messageMapper.updateMessageStatus(ids, MessageConstant.ALREADY_READ);
    }

    /**
     * 添加消息
     *
     * @param message
     */
    @Override
    public void addMessage(Message message) {
        String s = HtmlUtils.htmlEscape(message.getContent());
        String filter = sensitiveFilter.filter(s);
        message.setContent(filter);
        messageMapper.insertMessage(message);
    }

    /**
     * 删除私信
     */
    public void deleteMessage(int id) {
        messageMapper.updateMessageStatus(Collections.singletonList(id), MessageConstant.DELETED);
    }
}