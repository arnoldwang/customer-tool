package com.dianping.customer.tool.servertest.dao;

import com.dianping.customer.tool.entity.ShopTerritoryHistory;
import com.dianping.customer.tool.job.dao.ShopTerritoryHistoryDao;
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
 * Date: 14-12-5
 */
public class ShopTerritoryHistoryDaoTest extends AbstractTest{
	@Autowired
	private ShopTerritoryHistoryDao shopTerritoryDao;

	private ShopTerritoryHistory st = new ShopTerritoryHistory();
	private List<ShopTerritoryHistory> stList = newArrayList();

	@Before
	public void setUp() throws Exception{
		st.setShopId(1);
		st.setTerritoryId(1);
		st.setTypeId(0);
		stList.add(st);
	}

	@After
	public void clean() throws Exception{

	}

	@Test
	public void goTest(){
		shopTerritoryDao.addToShopTerritoryHistory(stList);
		System.out.println("ok");

		String ds = "2014-12-05";
		DateFormat df = DateFormat.getDateInstance();
		Date d = new Date();
		try {
			d = df.parse(ds);
		}
		catch(ParseException e) {
			System.out.println("Unable to parse " + ds);
		}
		int size = shopTerritoryDao.queryShopTerritoryHistoryByCreateTime(d).size();

		System.out.println(size);
	}
}
