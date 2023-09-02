package com.fosss.community.utils;

import com.fosss.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @author: fosss
 * Date: 2023/9/2
 * Time: 15:50
 * Description:
 */
@Component
public class ThreadLocalUtil {
    public static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public void set(User user) {
        threadLocal.set(user);
    }

    public User get() {
        return threadLocal.get();
    }

    /**
     * 消除
     */
    public void clear() {
        threadLocal.remove();
    }
}
