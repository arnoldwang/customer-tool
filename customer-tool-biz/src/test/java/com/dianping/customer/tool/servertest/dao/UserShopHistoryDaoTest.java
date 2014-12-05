package com.dianping.customer.tool.servertest.dao;

import com.dianping.customer.tool.entity.UserShopHistory;
import com.dianping.customer.tool.job.dao.UserShopHistoryDao;
import com.dianping.customer.tool.servertest.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: zhenwei.wang
 * Date: 14-11-26
 * Time: 下午4:26
 */

public class UserShopHistoryDaoTest extends AbstractTest{
	@Autowired
	private UserShopHistoryDao userShopHistoryDao;


	private UserShopHistory userShopHistory  = new UserShopHistory();

	@Before
	public void setUp() throws Exception{
		userShopHistory.setShopId(1);
		userShopHistory.setUserId(1);
		userShopHistory.setTypeId(0);
	}

	@After
	public void clean() throws Exception{

	}

	@Test
	public void goTest(){
		userShopHistoryDao.addToUserShopHistory(userShopHistory);
	}
}
