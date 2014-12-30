package com.dianping.customer.tool.tasktest;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.dianping.customer.tool.task.SyncUserTerritoryTask;
import com.salesforce.dataloader.client.PartnerClient;
import com.salesforce.dataloader.config.Config;
import com.salesforce.dataloader.controller.Controller;
import com.salesforce.dataloader.dyna.SforceDynaBean;
import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.bind.XmlObject;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zaza on 14/12/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath*:/config/spring/common/appcontext-*.xml",
		"classpath*:/config/spring/local/appcontext-*.xml"})
public class DataLoaderTest {

	private Controller controller;
	@Autowired
	private SyncUserTerritoryTask syncUserTerritoryTask;

	@Before
	public void setUp() throws Exception {
		controller = Controller.getInstance("ui", false);

	}

	@After
	public void clean() throws Exception {

	}

	@Test
	public void dateLoaderTest() {
		try {
			//controller.createAndShowGUI();
			Config config = controller.getConfig();
			config.putValue("sfdc.username", "huagui.zhang123@dianping.com.dpstg");
			config.putValue("sfdc.password", "zhang@123456");
			config.putValue("sfdc.endpoint", "https://dper--dpstg.cs6.my.salesforce.com/");

			controller.login();
			PartnerClient client = controller.getPartnerClient();
			QueryResult et_qr = client.query("select TerritoryID__c,UserID__c from EmployeeTerritory__c");
			SObject[] et_results = et_qr.getRecords();
			//todo

			BasicDynaClass dynaClass = setupDynaClass("UserTerritory");

			for (SObject et_Object : et_results) {
				List<DynaBean> deleteList = Lists.newArrayList();
				List<DynaBean> insertList = Lists.newArrayList();
				Map<String, Object> userTerritoryMap = Maps.newHashMap();
				Iterator<XmlObject> et_iter = et_Object.getChildren();
				while (et_iter.hasNext()) {
					XmlObject xmlObject = et_iter.next();
					if (xmlObject.getName().getLocalPart().equals("TerritoryID__c"))
						userTerritoryMap.put("TerritoryId", xmlObject.getValue());
					if (xmlObject.getName().getLocalPart().equals("UserID__c"))
						userTerritoryMap.put("UserId", xmlObject.getValue());
				}

				if (userTerritoryMap.containsKey("UserId")) {
					DynaBean insertObject = dynaClass.newInstance();
					BeanUtils.copyProperties(insertObject, userTerritoryMap);
					insertList.add(insertObject);

					QueryResult ut_qr = client.query("select Id,TerritoryId,UserId from UserTerritory where UserId='" + userTerritoryMap.get("UserId") + "'");
					SObject[] ut_results = ut_qr.getRecords();
					if (ut_results.length > 0) {//UserId已存在与UserTerritory表中，先删除再插入
						for (SObject ut_object : ut_results) {
							Iterator<XmlObject> ut_iter = ut_object.getChildren();
							while (ut_iter.hasNext()) {
								XmlObject xmlObject = ut_iter.next();
								if (xmlObject.getName().getLocalPart().equals("Id")) {
									userTerritoryMap.put("Id", xmlObject.getValue());
									DynaBean deleteObject = dynaClass.newInstance();
									BeanUtils.copyProperties(deleteObject, userTerritoryMap);
									deleteList.add(deleteObject);
									break;
								}
							}
						}
						DeleteResult[] deleteResults = client.loadDeletes(deleteList);
						for (DeleteResult result : deleteResults) {
							if (!result.getSuccess()) {

							}
						}
					}
					//UserId不在UserTerritory表中，直接插入
					SaveResult[] insertResults = client.loadInserts(insertList);
					for (SaveResult result : insertResults) {
						if (!result.getSuccess()) {
							//Assert.fail("Update returned an error" + result.getErrors()[0].getMessage());
						}
					}
				}
			}

			if (controller.isLoggedIn()) {
				System.out.printf("login in");
			} else {
				System.out.printf("login out");
			}


		} catch (Exception ex) {
			System.out.printf(ex.getMessage());
		}

	}

	private BasicDynaClass setupDynaClass(String entity) throws ConnectionException {
		controller.getConfig().setValue(Config.ENTITY, entity);
		PartnerClient client = controller.getPartnerClient();
		if (!client.isLoggedIn()) {
			client.connect();
		}

		controller.setFieldTypes();
		controller.setReferenceDescribes();
		DynaProperty[] dynaProps = SforceDynaBean.createDynaProps(controller.getPartnerClient().getFieldTypes(), controller);
		BasicDynaClass dynaClass = SforceDynaBean.getDynaBeanInstance(dynaProps);
		SforceDynaBean.registerConverters(controller.getConfig());
		return dynaClass;
	}

//	@Test
//	public void goTest(){
//		syncUserTerritoryTask.go();
//	}
}
