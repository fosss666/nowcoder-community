package com.fosss.community.service.impl;

import com.fosss.community.constant.LikeConstant;
import com.fosss.community.service.LikeService;
import com.fosss.community.utils.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        /*String key = RedisKeyUtil.generateEntityLikeKey(entityType, entityId);
        Boolean member = redisTemplate.opsForSet().isMember(key, userId);
        if (member) {
            redisTemplate.opsForSet().remove(key, userId);
        } else {
            //取消点赞
            redisTemplate.opsForSet().add(key, userId);
        }*/

        String entityLikeKey = RedisKeyUtil.generateEntityLikeKey(entityType, entityId);
        String userLikeKey = RedisKeyUtil.generateUserLikeKey(entityUserId);
        //使用事务统一管理
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //查询是否点过赞
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                //开启事务
                operations.multi();
                if (isMember) {
                    //点过赞，则取消点赞，目标用户点赞数-1
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    //没点过赞，则点赞，目标用户点赞数+1
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });

    }

    /**
     * 查询某实体点赞数量
     */
    @Override
    public int getEntityLikeCount(int entityType, int entityId) {
        Long size = redisTemplate.opsForSet().size(RedisKeyUtil.generateEntityLikeKey(entityType, entityId));
        return size == null ? 0 : size.intValue();
    }

    /**
     * 查询某人对某实体的点赞状态
     */
    @Override
    public int getLikeStatusByUserId(int userId, int entityType, int entityId) {
        String key = RedisKeyUtil.generateEntityLikeKey(entityType, entityId);
        Boolean member = redisTemplate.opsForSet().isMember(key, userId);
        return member ? LikeConstant.LIKED : LikeConstant.NOT_LIKED;
    }

    /**
     * 获取用户获赞数量
     *
     * @param userId
     * @return
     */
    @Override
    public int getUserLikeCount(int userId) {
        Integer userLikeCount = (Integer) redisTemplate.opsForValue().get(RedisKeyUtil.generateUserLikeKey(userId));
        return userLikeCount == null ? 0 : userLikeCount;
    }
}
