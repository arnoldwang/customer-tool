package com.dianping.customer.tool.service.impl;

import com.dianping.customer.tool.dao.TestDao;
import com.dianping.customer.tool.service.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestImpl implements Test {
    @Autowired
    private TestDao testDao;

    public String test() {
//        return "hello";
        return testDao.test().toString();
    }
}
