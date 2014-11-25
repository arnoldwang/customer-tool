package com.dianping.customer.tool.controller;

import com.dianping.customer.tool.service.Test;
import com.dianping.customer.tool.service.impl.TestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @Autowired
    private Test test;

    @RequestMapping("/test")
    public @ResponseBody String simple() {
        String s = test.test();
        return s;
    }
}
