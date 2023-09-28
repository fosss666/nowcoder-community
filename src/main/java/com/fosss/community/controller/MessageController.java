package com.fosss.community.controller;

import com.fosss.community.constant.ResultEnum;
import com.fosss.community.entity.Message;
import com.fosss.community.entity.Page;
import com.fosss.community.entity.User;
import com.fosss.community.service.MessageService;
import com.fosss.community.service.UserService;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.ThreadLocalUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private ThreadLocalUtil threadLocalUtil;

    @Resource
    private UserService userService;

    // 私信列表
    @GetMapping(path = "/letter/list")
    public String getLetterList(Model model, Page page) {
        User user = threadLocalUtil.get();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        // 会话列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            conversations = conversationList.stream().map(message -> {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                return map;
            }).collect(Collectors.toList());
        }
        model.addAttribute("conversations", conversations);

        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        return "/site/letter";
    }

    /**
     * 查询会话详情
     *
     * @param conversationId
     * @param page
     * @param model
     * @return
     */
    @GetMapping(path = "/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            letters = letterList.stream().map(message -> {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                return map;
            }).collect(Collectors.toList());
        }
        model.addAttribute("letters", letters);

        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        // 设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    /**
     * 获取当前用户对话的人
     *
     * @param conversationId 会话id
     * @return
     */
    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);

        if (threadLocalUtil.get().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    /**
     * 获取正在读的消息id
     *
     * @param letterList
     * @return
     */
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();

        if (letterList != null) {
            for (Message message : letterList) {
                if (threadLocalUtil.get().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    /**
     * 发送私信
     */
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendMessage(String toName, String content) {
        //查询目标用户
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(ResultEnum.USER_NOT_FOUND.code, ResultEnum.USER_NOT_FOUND.msg);
        }
        int toId = target.getId();
        int fromId = threadLocalUtil.get().getId();
        Message message = new Message();
        message.setToId(toId);
        message.setFromId(fromId);
        message.setContent(content);
        message.setCreateTime(new Date());
        String conversationId = toId < fromId ? toId + "_" + fromId : fromId + "_" + toId;
        message.setConversationId(conversationId);
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(ResultEnum.SUCCESS.code);
    }

    /**
     * 删除私信
     */
    @PostMapping(path = "/letter/delete")
    @ResponseBody
    public String deleteLetter(int id) {
        messageService.deleteMessage(id);
        return CommunityUtil.getJSONString(ResultEnum.SUCCESS.code);
    }
}
