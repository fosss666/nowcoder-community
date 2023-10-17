package com.fosss.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @author: fosss
 * Date: 2023/10/16
 * Time: 21:56
 * Description:
 */
@SpringBootTest
public class WKTests {

    @Test
    public void testWkToImage() throws IOException, InterruptedException {
        String command="cmd /c wkhtmltoimage --quality 75 http://localhost:8081/community/index E:/PROJECT/community/wk-data/images/2.png";
        Runtime.getRuntime().exec(command);
    }
}
