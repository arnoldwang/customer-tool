package com.dianping.customer.tool.task;

import com.beust.jcommander.internal.Lists;
import com.dianping.customer.tool.utils.Beans;
import com.dianping.customer.tool.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * User: zhenwei.wang
 * Date: 14-12-2
 */
public class SyncApolloDataTask {
	private static final int DEFAULT_INDEX = 1;

	private static final int DEFAULT_THREAD_NUM = 5;

	Logger logger = LoggerFactory.getLogger(SyncApolloDataTask.class);

	public void go() {
		if (!ConfigUtils.getSyncApolloDataTaskTrigger()) {
			logger.info("SyncApolloDataTask will not run!");
			System.out.println("SyncApolloDataTask will not run!");
			return;
		}

		String type = ConfigUtils.getSyncApolloDataTaskType();
		int threadShopNum = ConfigUtils.getSyncApolloDataTaskDefaultThreadPage();
		System.out.println("+++++++++++++++++++++++++++++++threadShopNum = " + threadShopNum);

		ExecutorService exe = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);
		List<Future> futureList = Lists.newArrayList();

		logger.info("SyncApolloDataTask.running...");
		System.out.println("SyncApolloDataTask.running...");
		long beginTime = System.currentTimeMillis();

		for(int i = 0; i < DEFAULT_THREAD_NUM; i++){
			Runnable r = new SyncApolloDataWorkThread(type, DEFAULT_INDEX + threadShopNum * i, DEFAULT_INDEX + threadShopNum * (i + 1));
			Beans.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(r);
			futureList.add(exe.submit(r));
		}
		System.out.println("+++++++++++++++++++++++++++++++futureListNum = " + futureList.size());

		for (int i = 0; i < DEFAULT_THREAD_NUM; i++){
			try {
				futureList.get(i).get();
			} catch (InterruptedException e) {
				logger.warn("This thread: " + Thread.currentThread().getName() + " is interrupted!", e);
			} catch (ExecutionException e) {
				logger.warn("This thread: " + Thread.currentThread().getName() + " is failed!", e);
			}
		}

		exe.shutdown();

		long endTime = System.currentTimeMillis();
		logger.info("SyncApolloDataTask.end");
		System.out.println("SyncApolloDataTask.end");
		long useTime = (endTime - beginTime) / 1000;
		logger.info("This task use " + useTime / 3600 + " H " + useTime % 3600 / 60 + " m " + useTime % (3600 * 60) + " s!");
	}


}
