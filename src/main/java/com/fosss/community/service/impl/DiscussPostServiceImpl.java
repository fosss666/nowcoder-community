package com.fosss.community.service.impl;

import com.fosss.community.dao.DiscussPostMapper;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.fosss.community.constant.ExceptionConstant.PARAMETER_NULL;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Resource
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 发布帖子
     */
    @Override
    public void insertDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) throw new IllegalArgumentException(PARAMETER_NULL);

        //将html标签转义
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        //添加到数据库
        discussPostMapper.insertDiscussPost(discussPost);
    }

    /**
     * 查询帖子
     */
    @Override
    public DiscussPost selectById(int id) {
        return discussPostMapper.selectById(id);
    }

    /**
     * 更新帖子评论数量
     *
     * @param entityId
     * @param count
     */
    @Override
    public void updateCommentCount(int entityId, int count) {
        discussPostMapper.updateCommentCount(entityId, count);
    }
}
