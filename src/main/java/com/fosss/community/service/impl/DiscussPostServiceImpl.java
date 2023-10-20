package com.fosss.community.service.impl;

import com.fosss.community.dao.DiscussPostMapper;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.properties.ApplicationProperty;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.utils.SensitiveFilter;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.fosss.community.constant.ExceptionConstant.PARAMETER_NULL;

@Slf4j
@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Resource
    private SensitiveFilter sensitiveFilter;
    @Resource
    private ApplicationProperty property;

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(property.getMaxSize())
                .expireAfterWrite(property.getExpireSeconds(), TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }

                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        // 这里可以做个二级缓存: 先从Redis查 ，没有的话再从mysql查

                        log.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                    }
                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(property.getMaxSize())
                .expireAfterWrite(property.getExpireSeconds(), TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        log.debug("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }

        log.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public int findDiscussPostRows(int userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);
        }

        log.debug("load post rows from DB.");
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

    /**
     * 更新帖子类型
     */
    @Override
    public void updateType(int id, int type) {
        discussPostMapper.updateType(id, type);
    }

    /**
     * 更新帖子状态
     */
    @Override
    public void updateStatus(int id, int status) {
        discussPostMapper.updateStatus(id, status);
    }

    /**
     * 更新帖子分数
     *
     * @param postId
     * @param score
     */
    @Override
    public void updateScore(int postId, double score) {
        discussPostMapper.updateScore(postId, score);
    }
}
