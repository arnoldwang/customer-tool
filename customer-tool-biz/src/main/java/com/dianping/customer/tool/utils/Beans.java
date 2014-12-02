package com.dianping.customer.tool.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * User: zhenwei.wang
 * Date: 14-12-2
 */
public class Beans implements ApplicationContextAware {

	public static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		applicationContext = ctx;
	}

	public static <T> T getBean(Class<T> type){
		T bean = null;

		Map<String, T> map = applicationContext.getBeansOfType(type);
		if(map.size() == 1)
			bean = map.values().iterator().next();

		return bean;
	}
}
