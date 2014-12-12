package com.dianping.customer.tool.servertest.dao;

import com.dianping.customer.tool.dao.OrgTerritoryDao;
import com.dianping.customer.tool.entity.OrgTerritory;
import com.dianping.customer.tool.servertest.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: zhenwei.wang
 * Date: 14-12-12
 */
public class OrgTerritoryDaoTest extends AbstractTest {

	@Autowired
	private OrgTerritoryDao orgTerritoryDao;

	@Before
	public void setUp() throws Exception{

	}

	@After
	public void clean() throws Exception{

	}

	@Test
	public void goTest(){
		List<OrgTerritory> orgTerritoryList = orgTerritoryDao.queryOrgTerritoryByOrgID(-33532);
		System.out.println(orgTerritoryList.size());
	}
}
