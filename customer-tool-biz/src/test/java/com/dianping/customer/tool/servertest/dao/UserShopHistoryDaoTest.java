package com.dianping.customer.tool.servertest.dao;

import com.dianping.customer.tool.entity.UserShopHistory;
import com.dianping.customer.tool.job.dao.UserShopHistoryDao;
import com.dianping.customer.tool.servertest.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.beust.jcommander.internal.Lists.newArrayList;

/**
 * User: zhenwei.wang
 * Date: 14-11-26
 * Time: 下午4:26
 */

public class UserShopHistoryDaoTest extends AbstractTest{
	@Autowired
	private UserShopHistoryDao userShopHistoryDao;


	private UserShopHistory userShopHistory  = new UserShopHistory();
	private List<UserShopHistory> usList = newArrayList();

	@Before
	public void setUp() throws Exception{
		userShopHistory.setShopId(1);
		userShopHistory.setUserId(1);
		userShopHistory.setTypeId(0);
		usList.add(userShopHistory);
	}

	@After
	public void clean() throws Exception{

	}

	@Test
	public void goTest(){
		userShopHistoryDao.addToUserShopHistory(usList);

		String ds = "2014-12-05";
		DateFormat df = DateFormat.getDateInstance();
		Date d = new Date();
		try {
			 d = df.parse(ds);
		}
		catch(ParseException e) {
			System.out.println("Unable to parse " + ds);
		}
		int size = userShopHistoryDao.queryUserShopHistoryByCreateTime(d).size();

		System.out.println(size);
	}
}
