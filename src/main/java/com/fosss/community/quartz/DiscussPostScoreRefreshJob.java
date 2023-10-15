package com.fosss.community.quartz;

import com.fosss.community.constant.CommentConstant;
import com.fosss.community.constant.DiscussPostConstant;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.exception.BusinessException;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.service.ElasticsearchService;
import com.fosss.community.service.LikeService;
import com.fosss.community.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: fosss
 * Date: 2023/10/15
 * Time: 22:02
 * Description:
 */
@Slf4j
public class DiscussPostScoreRefreshJob implements Job {

    //牛客元年
    private static Date nowcoderYearOne;

    static {
        try {
            nowcoderYearOne = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new BusinessException("牛客元年初始化失败！");
        }
    }

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private LikeService likeService;
    @Resource
    private ElasticsearchService elasticsearchService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        BoundSetOperations boundSetOps = redisTemplate.boundSetOps(RedisKeyUtil.generatePostScoreRefreshKey());
        if (boundSetOps.size() == 0) {
            log.info("[任务取消] 当前没有需要进行分数刷新的帖子！");
            return;
        }
        log.info("[任务开始] 正在刷新帖子分数: " + boundSetOps.size());
        //计算分数
        while (boundSetOps.size() > 0) {
            int postId = (int) boundSetOps.pop();
            DiscussPost discussPost = discussPostService.selectById(postId);
            if (discussPost == null || discussPost.getStatus() == DiscussPostConstant.DELETED) {
                log.error("该帖子不存在：" + postId);
                return;
            }
            // 是否精华
            boolean wonderful = discussPost.getStatus() == DiscussPostConstant.WONDERFUL;
            //评论数
            int commentCount = discussPost.getCommentCount();
            //点赞数
            int likeCount = likeService.getEntityLikeCount(CommentConstant.ENTITY_TYPE_POST, postId);
            // 计算权重
            double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
            // 分数 = 帖子权重 + 距离天数   Math.max(w, 1)：防止log的结果为负数
            double score = Math.log10(Math.max(w, 1))
                    + (discussPost.getCreateTime().getTime() - nowcoderYearOne.getTime()) / (1000 * 3600 * 24);
            // 更新帖子分数
            discussPostService.updateScore(postId, score);
            // 同步搜索数据
            discussPost.setScore(score);
            elasticsearchService.saveDiscussPost(discussPost);
        }

        log.info("[任务结束] 帖子分数刷新完毕!");
    }
}






