package com.fosss.community.service.impl;

import com.fosss.community.service.FollowService;
import com.fosss.community.utils.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: fosss
 * Date: 2023/9/26
 * Time: 22:05
 * Description:
 */
@Service
public class FollowServiceImpl implements FollowService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 关注
     *
     * @param userId     当前登录用户的id
     * @param entityType 关注的实体类型
     * @param entityId   关注的实体id
     */
    @Override
    public void follow(int userId, int entityType, int entityId) {
        //获取key
        String followeeKey = RedisKeyUtil.generateFolloweeKey(userId, entityType);
        String followerKey = RedisKeyUtil.generateFollowerKey(entityType, entityId);

        //用事务保证同步性
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //开启事务
                operations.multi();
                //向关注列表中添加实体
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                //向实体的粉丝列表中添加当前用户
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    /**
     * 取关
     *
     * @param userId     当前登录用户的id
     * @param entityType 关注的实体类型
     * @param entityId   关注的实体id
     */
    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        //获取key
        String followeeKey = RedisKeyUtil.generateFolloweeKey(userId, entityType);
        String followerKey = RedisKeyUtil.generateFollowerKey(entityType, entityId);

        //用事务保证同步性
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //开启事务
                operations.multi();
                //向关注列表中添加实体
                operations.opsForZSet().remove(followeeKey, entityId);
                //向实体的粉丝列表中添加当前用户
                operations.opsForZSet().remove(followerKey, userId);
                return operations.exec();
            }
        });
    }

    /**
     * 获取关注数量
     *
     * @param userId         用户id
     * @param entityTypeUser 实体类型
     * @return
     */
    @Override
    public long getFolloweeCount(int userId, int entityTypeUser) {
        String followeeKey = RedisKeyUtil.generateFolloweeKey(userId, entityTypeUser);
        Long count = redisTemplate.opsForZSet().zCard(followeeKey);
        return count == null ? 0 : count;
    }

    /**
     * 获取粉丝数量
     *
     * @param userId         用户id
     * @param entityTypeUser 实体类型
     * @return
     */
    @Override
    public long getFollowerCount(int userId, int entityTypeUser) {
        String followeeKey = RedisKeyUtil.generateFollowerKey(entityTypeUser, userId);
        Long count = redisTemplate.opsForZSet().zCard(followeeKey);
        return count == null ? 0 : count;
    }

    /**
     * 当前登录的用户loginUser是否关注了当前访问的用户curUser
     *
     * @param loginUserId
     * @param curUserId
     * @param entityTypeUser
     * @return
     */
    @Override
    public boolean hasFollowed(int loginUserId, int curUserId, int entityTypeUser) {
        String followeeKey = RedisKeyUtil.generateFolloweeKey(loginUserId, entityTypeUser);
        //有分数则说明关注了
        Double score = redisTemplate.opsForZSet().score(followeeKey, curUserId);
        return score != null;
    }
}











