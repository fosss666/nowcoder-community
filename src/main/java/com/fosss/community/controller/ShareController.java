package com.fosss.community.controller;

import com.fosss.community.constant.EventConstant;
import com.fosss.community.constant.ResultEnum;
import com.fosss.community.entity.Event;
import com.fosss.community.event.EventConsumer;
import com.fosss.community.event.EventProducer;
import com.fosss.community.properties.ApplicationProperty;
import com.fosss.community.utils.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: fosss
 * Date: 2023/10/16
 * Time: 21:01
 * Description: 分享功能
 */
@Slf4j
@RestController
public class ShareController {

    @Resource
    private EventProducer eventProducer;
    @Resource
    private ApplicationProperty property;

    @GetMapping("/share")
    public String share(String htmlUrl) {
        //随机图片名，本地的
        String fileName = CommunityUtil.generateUUID();
        //异步生成长图
        Event event = new Event()
                .setTopic(EventConstant.EVENT_TOPIC_SHARE)
                .setData("htmlUrl", htmlUrl)
                .setData("fileName", fileName)
                .setData("suffix", ".png");
        eventProducer.fireEvent(event);


        // 返回访问路径
        Map<String, Object> map = new HashMap<>();
        //map.put("shareUrl", property.getDomain() + ":" + property.getPort() + property.getContextPath() + "/share/image/" + fileName);
        //由于上传操作在kafka中执行，所以暂时无法获得访问路径，所以暂时打印在控制台吧
        map.put("shareUrl", "请看控制台！！！！！");
        return CommunityUtil.getJSONString(ResultEnum.SUCCESS.code, ResultEnum.SUCCESS.msg, map);
    }

    // 获取长图——将图片上传至阿里云，废弃该方法
    //@GetMapping(path = "/share/image/{fileName}")
    public void getShareImage(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空!");
        }

        response.setContentType("image/png");
        File file = new File(property.getWkImagePath() + "/" + fileName + ".png");
        try {
            OutputStream os = response.getOutputStream();

            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            log.error("获取长图失败: " + e.getMessage());
        }
    }
}