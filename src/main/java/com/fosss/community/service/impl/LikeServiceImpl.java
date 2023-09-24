package com.fosss.community.service.impl;

import com.fosss.community.constant.LikeConstant;
import com.fosss.community.service.LikeService;
import com.fosss.community.utils.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: fosss
 * Date: 2023/9/24
 * Time: 18:13
 * Description:
 */
@Service
public class LikeServiceImpl implements LikeService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 点赞
     */
    @Override
    public void like(int userId, int entityType, int entityId) {
        String key = RedisKeyUtil.generateLikeKey(entityType, entityId);
        Boolean member = redisTemplate.opsForSet().isMember(key, userId);
        if (member) {
            redisTemplate.opsForSet().remove(key, userId);
        } else {
            //取消点赞
            redisTemplate.opsForSet().add(key, userId);
        }
    }

    /**
     * 查询某实体点赞数量
     */
    @Override
    public int getLikeCount(int entityType, int entityId) {
        Long size = redisTemplate.opsForSet().size(RedisKeyUtil.generateLikeKey(entityType, entityId));
        return size == null ? 0 : size.intValue();
    }

    /**
     * 查询某人对某实体的点赞状态
     */
    @Override
    public int getLikeStatusByUserId(int userId, int entityType, int entityId) {
        String key = RedisKeyUtil.generateLikeKey(entityType, entityId);
        Boolean member = redisTemplate.opsForSet().isMember(key, userId);
        return member ? LikeConstant.LIKED : LikeConstant.NOT_LIKED;
    }
}
