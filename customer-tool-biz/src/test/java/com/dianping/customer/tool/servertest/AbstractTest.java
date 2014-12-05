package com.dianping.customer.tool.servertest;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import spock.lang.Specification;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath*:/config/spring/common/appcontext-*.xml",
		"classpath*:/config/spring/local/appcontext-*.xml",
		"classpath*:/config/spring/test/appcontext-*.xml"})
@Ignore
public abstract class AbstractTest extends Specification {

	public void notNull(Object obj) {
		assert null != obj;
	}

	public void isNull(Object obj) {
		assert null == obj;
	}

	public void equal(Object expected, Object actual) {
		Assert.assertEquals(expected, actual);
	}

}
