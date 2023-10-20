package com.fosss.community.quartz;

import com.fosss.community.properties.ApplicationProperty;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author: fosss
 * Date: 2023/10/20
 * Time: 13:55
 * Description: 定时删除分享时生成的本地长图
 */
@Slf4j
public class LocalShareImageDeleteJob implements Job {
    @Resource
    private ApplicationProperty property;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //获取图片所在地址
        String wkImagePath = property.getWkImagePath();
        //获取所有文件
        File[] files = new File(wkImagePath).listFiles();
        if (files == null || files.length == 0) {
            log.info("没有wk图片，停止任务");
            return;
        }
        //删除存储时间超过一分钟的
        for (File file : files) {
            if (System.currentTimeMillis() - file.lastModified() > 1000 * 60) {
                log.info("删除wk长图：" + file.getName());
                file.delete();
            }
        }
    }
}
