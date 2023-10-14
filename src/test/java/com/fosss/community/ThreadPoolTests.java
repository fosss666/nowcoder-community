package com.fosss.community;

import com.fosss.community.service.impl.AlphaServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author: fosss
 * Date: 2023/10/14
 * Time: 18:57
 * Description:
 */
@Slf4j
@SpringBootTest
public class ThreadPoolTests {
    //测试类执行后会直接结束，导致线程池显示出作用，所以需要sleep一下
    private void sleep(long millisSeconds) {
        try {
            Thread.sleep(millisSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //JDK普通线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    //JDK能使用定时任务的线程池
    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    //编码方式实现spring的普通线程池以及定时任务线程池
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    @Resource
    private AlphaServiceImpl alphaService;

    /**
     * 测试JDK普通线程池
     */
    @Test
    public void testExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                log.debug("hello testExecutorService");
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(task);
        }
        sleep(10000);
    }

    /**
     * 测试JDK可以执行定时任务的线程池
     */
    @Test
    public void testScheduledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                log.debug("hello testScheduledExecutorService");
            }
        };
        scheduledExecutorService.scheduleAtFixedRate(task, 5000, 2000, TimeUnit.MILLISECONDS);
        sleep(30000);
    }

    /**
     * 测试Spring普通线程池——编码实现
     */
    @Test
    public void testSpringExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                log.debug("hello testSpringExecutorService");
            }
        };
        for (int i = 0; i < 10; i++) {
            threadPoolTaskExecutor.submit(task);
        }
        sleep(10000);
    }

    /**
     * 测试Spring定时任务线程池——编码实现
     */
    @Test
    public void testSpringScheduledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                log.debug("hello testSpringScheduledExecutorService");
            }
        };
        Date startTime = new Date(System.currentTimeMillis() + 10000);
        threadPoolTaskScheduler.scheduleAtFixedRate(task, startTime, 2000);
        sleep(30000);
    }

    /**
     * 测试Spring定时线程池——编码实现
     */
    @Test
    public void testSpringExecutorService2() {
        alphaService.test1();
        sleep(10000);
    }

    /**
     * 测试Spring定时任务线程池——编码实现
     */
    @Test
    public void testSpringScheduledExecutorService2() {
        alphaService.test2();
        sleep(30000);
    }
}
