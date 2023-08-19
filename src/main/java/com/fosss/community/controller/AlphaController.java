package com.fosss.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fosss
 * Date: 2023/8/18
 * Time: 19:06
 * Description:
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @GetMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "hello world";
    }

    /**
     * 相应html
     */
    @GetMapping("/teacher")
    public String teacher(Model model) {
        model.addAttribute("name", "王树国1");
        model.addAttribute("age", 50);
        return "/demo/teacher";
    }
}
