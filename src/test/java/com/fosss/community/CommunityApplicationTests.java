package com.fosss.community;

import com.fosss.community.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class CommunityApplicationTests {

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Test
    void testSensitiveFilter() {
        String res = sensitiveFilter.filter("fabc");
        System.out.println(res);
    }

}
