package com.fosss.community;

import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author: fosss
 * Date: 2023/10/14
 * Time: 22:20
 * Description:
 */
@SpringBootTest
public class QuartzTests {

    @Resource
    private Scheduler scheduler;

    /**
     * 删除任务调度
     */
    @Test
    public void testDeleteJob() throws SchedulerException {
        boolean res = scheduler.deleteJob(new JobKey("alphaJob", "alphaJobGroup"));
        System.out.println(res);
    }
}
