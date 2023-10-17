package com.fosss.community.config;

import com.fosss.community.properties.ApplicationProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;

/**
 * @author: fosss
 * Date: 2023/10/16
 * Time: 20:54
 * Description: wk生成  长图
 */
@Slf4j
@Configuration
public class WKConfig {

    @Resource
    private ApplicationProperty property;

    /**
     * 判断存储图片的文件夹是否创建，如果没有，则进行创建
     */
    @PostConstruct
    public void createFolder() {
        File file = new File(property.getWkImagePath());
        if (!file.exists()) {
            file.mkdir();
            log.info("长图存储目录初始化完毕！");
        }
    }
}
