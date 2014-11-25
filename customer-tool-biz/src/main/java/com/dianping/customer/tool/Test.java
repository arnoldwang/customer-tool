package com.dianping.customer.tool;

import com.dianping.customer.tool.dao.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Test {
    @Autowired
    private TestDao testDao;

    public String test() {
//        return "hello";
        return testDao.test().toString();
    }
}
