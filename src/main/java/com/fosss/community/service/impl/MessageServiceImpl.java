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
    @Override
    public void deleteMessage(int id) {
        messageMapper.updateMessageStatus(Collections.singletonList(id), MessageConstant.DELETED);
    }

    /**
     * 查询最新通知
     *
     * @param userId 用户id
     * @param topic  主题
     * @return 消息
     */
    @Override
    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    /**
     * 查询通知数量
     *
     * @param userId 用户id
     * @param topic  主题
     * @return 消息
     */
    @Override
    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    /**
     * 查询未读通知数量
     *
     * @param userId 用户id
     * @param topic  主题
     * @return 消息
     */
    @Override
    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    /**
     * 分页查询通知列表
     *
     * @param userId 用户id
     * @param topic  主题
     * @return 消息
     */
    @Override
    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
