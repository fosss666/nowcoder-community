package com.fosss.community.service.impl;

import com.fosss.community.constant.*;
import com.fosss.community.dao.UserMapper;
import com.fosss.community.entity.LoginTicket;
import com.fosss.community.entity.User;
import com.fosss.community.properties.ApplicationProperty;
import com.fosss.community.service.UserService;
import com.fosss.community.utils.CommunityUtil;
import com.fosss.community.utils.MailUtil;
import com.fosss.community.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private MailUtil mailUtil;
    //@Resource
    //private LoginTicketMapper loginTicketMapper;
    @Resource
    private ApplicationProperty applicationProperty;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    public User findUserById(int id) {
        //优先从缓存中取值,缓存中没有的话再从数据库中查，并将结果存入缓存
        String key = RedisKeyUtil.generateUserKey(id);
        User user = (User) redisTemplate.opsForValue().get(key);
        if (user == null) {
            user = userMapper.selectById(id);
            redisTemplate.opsForValue().set(key, user, ExpiredConstant.REDIS_LOGIN_TICKET, TimeUnit.SECONDS);
        }
        return user;
    }

    /**
     * 用户数据变更时删除缓存
     */
    private void clearCache(int userId) {
        String key = RedisKeyUtil.generateUserKey(userId);
        redisTemplate.delete(key);
    }

    /**
     * 用户注册
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put(UserErrorEnum.USERNAME_NULL.getKey(), UserErrorEnum.USERNAME_NULL.getMsg());
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put(UserErrorEnum.PASSWORD_NULL.getKey(), UserErrorEnum.PASSWORD_NULL.getMsg());
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put(UserErrorEnum.EMAIL_NULL.getKey(), UserErrorEnum.EMAIL_NULL.getMsg());
            return map;
        }

        // 验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put(UserErrorEnum.USERNAME_EXIST.getKey(), UserErrorEnum.USERNAME_EXIST.getMsg());
            return map;
        }

        // 验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put(UserErrorEnum.EMAIL_EXIST.getKey(), UserErrorEnum.EMAIL_EXIST.getMsg());
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8081/community/activation/101/code
        String url = applicationProperty.getDomain() + ":" + applicationProperty.getPort() + applicationProperty.getContextPath() + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailUtil.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    /**
     * 激活用户
     */
    @Override
    public int activation(int userId, String code) {
        //查询激活状态
        User user = userMapper.selectById(userId);
        if (user.getStatus() == UserStatusConstant.ALREADY_ACTIVATED) {
            //已激活
            return ActivationStatusConstant.ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            //激活码正确,更新用户状态
            userMapper.updateStatus(userId, UserStatusConstant.ALREADY_ACTIVATED);
            clearCache(userId);
            return ActivationStatusConstant.ACTIVATION_SUCCESS;
        } else {
            return ActivationStatusConstant.ACTIVATION_FAILURE;
        }
    }

    /**
     * 用户登录
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put(UserErrorEnum.USERNAME_NULL.getKey(), UserErrorEnum.USERNAME_NULL.getMsg());
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put(UserErrorEnum.PASSWORD_NULL.getKey(), UserErrorEnum.PASSWORD_NULL.getMsg());
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put(UserErrorEnum.USERNAME_NOT_EXIST.getKey(), UserErrorEnum.USERNAME_NOT_EXIST.getMsg());
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put(UserErrorEnum.USERNAME_NOT_ACTIVATION.getKey(), UserErrorEnum.USERNAME_NOT_ACTIVATION.getMsg());
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put(UserErrorEnum.PASSWORD_ERROR.getKey(), UserErrorEnum.PASSWORD_ERROR.getMsg());
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(LoginTicketStatusConstant.EFFECTIVE);
        //过期时间  expiredSeconds * 1000L：转成毫秒
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        //loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.generateTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 登出
     */
    public void logout(String ticket) {
        //loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.generateTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(LoginTicketStatusConstant.NOT_EFFECTIVE);
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        //清理security的用户信息
        SecurityContextHolder.clearContext();
    }

    /**
     * 根据ticket获取登录凭证
     */
    @Override
    public LoginTicket getByTicket(String ticket) {
        String redisKey = RedisKeyUtil.generateTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 更新用户头像
     *
     * @param userId
     * @param avatarPath
     */
    @Override
    public void upload(int userId, String avatarPath) {
        userMapper.updateHeader(userId, avatarPath);
        clearCache(userId);
    }

    /**
     * 重置密码
     */
    public Map<String, Object> resetPassword(String email, String password) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证邮箱
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "该邮箱尚未注册!");
            return map;
        }

        // 重置密码
        password = CommunityUtil.md5(password + user.getSalt());
        userMapper.updatePassword(user.getId(), password);
        clearCache(user.getId());

        map.put("user", user);
        return map;
    }

    // 修改密码
    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(oldPassword)) {
            map.put("oldPasswordMsg", "原密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "新密码不能为空!");
            return map;
        }

        // 验证原始密码
        User user = userMapper.selectById(userId);
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "原密码输入有误!");
            return map;
        }

        // 更新密码
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(userId, newPassword);

        return map;
    }

    /**
     * 根据用户名查询用户
     *
     * @param targetName
     * @return
     */
    @Override
    public User findUserByName(String targetName) {
        return userMapper.selectByName(targetName);
    }

    /**
     * 根据邮箱查询用户
     */

    @Override
    public User findUserByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    /**
     * 获取用户权限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthority(int userId) {
        User user = findUserById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case UserTypeConstant.ADMIN_USER:
                        return UserTypeConstant.ADMIN_USER_TYPE;
                    case UserTypeConstant.MASTER_USER:
                        return UserTypeConstant.MASTER_USER_Type;
                    default:
                        return UserTypeConstant.COMMON_USER_TYPE;
                }
            }
        });
        return list;
    }
}
