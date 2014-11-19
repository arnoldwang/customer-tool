package com.dianping.customer.tool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @RequestMapping("/simple")
    public @ResponseBody String simple() {
        return "Hello world!";
    }
}
