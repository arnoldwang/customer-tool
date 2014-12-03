package com.dianping.customer.tool.tasktest;

import com.dianping.customer.tool.task.SyncSalesForceToApolloTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * User: zhenwei.wang
 * Date: 14-12-3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath*:/config/spring/common/appcontext-*.xml",
		"classpath*:/config/spring/local/appcontext-*.xml"})

public class SyncSalesForceToApolloTaskTest {
	@Mock
	SyncSalesForceToApolloTask syncSalesForceToApolloTask;

	@Before
	public void initMock(){
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void clean() throws Exception{

	}

	@Test
	public void goTest(){
		syncSalesForceToApolloTask.go();
	}

}
