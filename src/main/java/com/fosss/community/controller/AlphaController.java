package com.fosss.community.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fosss
 * Date: 2023/8/18
 * Time: 19:06
 * Description:
 */
@RestController
@RequestMapping("/alpha")
public class AlphaController {

    @GetMapping("/hello")
    public String sayHello() {
        return "hello world";
    }
}
