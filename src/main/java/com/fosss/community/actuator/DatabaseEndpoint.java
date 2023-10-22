package com.fosss.community.actuator;

import com.fosss.community.utils.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author: fosss
 * Date: 2023/10/22
 * Time: 18:59
 * Description: 可以使用spring boot actuator提供的端点，也可以自定义断点
 */
@Component
@Slf4j
@Endpoint(id = "database")
public class DatabaseEndpoint {
    @Resource
    private DataSource dataSource;

    @ReadOperation
    public String checkConnection() {
        try (
                Connection connection = dataSource.getConnection();
        ) {
            return CommunityUtil.getJSONString(200, "获取连接成功");
        } catch (Exception e) {
            log.error("获取连接失败");
            return CommunityUtil.getJSONString(400, "获取连接失败");
        }
    }
}
