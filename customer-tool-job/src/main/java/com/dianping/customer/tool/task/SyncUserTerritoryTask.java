package com.dianping.customer.tool.task;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.beust.jcommander.internal.Sets;
import com.dianping.customer.tool.utils.ConfigUtils;
import com.salesforce.dataloader.client.PartnerClient;
import com.salesforce.dataloader.config.Config;
import com.salesforce.dataloader.controller.Controller;
import com.salesforce.dataloader.dyna.SforceDynaBean;
import com.salesforce.dataloader.exception.ControllerInitializationException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: zhenwei.wang
 * Date: 14-12-25
 */
public class SyncUserTerritoryTask {

	Logger logger = LoggerFactory.getLogger(SyncUserTerritoryTask.class);
	private Controller controller;

	public void go() {
		if (!ConfigUtils.getSyncUserTeriitoryTaskTrigger()) {
			logger.info("SyncUserTerritoryTask will not run!");
			System.out.println("SyncApolloDataTask will not run!");
			return;
		}

		try {
			controller = Controller.getInstance("ui", false);
		} catch (ControllerInitializationException e) {
			logger.warn("controller init failed! Task will not run!", e);
			return;
		}

		logger.info("SyncUserTerritoryTask.running...");
		System.out.println("SyncUserTerritoryTask.running...");
		long beginTime = System.currentTimeMillis();

		syncUserTerritoryData();

		long endTime = System.currentTimeMillis();
		logger.info("SyncUserTerritoryTask.end");
		System.out.println("SyncUserTerritoryTask.end");
		long useTime = (endTime - beginTime) / 1000;
		logger.info("This task use " + useTime / 3600 + " H " + useTime % 3600 / 60 + " m " + useTime % (3600 * 60) + " s!");

	}

	private void syncUserTerritoryData() {

		Config config = controller.getConfig();
		//online
//		config.putValue("sfdc.username", "siqin.liu@dianping.com");
//		config.putValue("sfdc.password", "DpCRM1234");
//		config.putValue("sfdc.endpoint", "https://dper.my.salesforce.com");
		//beta
		config.putValue("sfdc.username", "huagui.zhang123@dianping.com.dpstg");
		config.putValue("sfdc.password", "zhang@123456");
		config.putValue("sfdc.endpoint", "https://dper--dpstg.cs6.my.salesforce.com/");

		int flag = 0;
		while (!controller.isLoggedIn() && flag < 100) {
			try {
				controller.login();
			} catch (ConnectionException e) {
				logger.warn("can not login SalesForce!", e);
				flag++;
			}
		}
		if (!controller.isLoggedIn())
			return;

		PartnerClient client = controller.getPartnerClient();
		client.getClient().setQueryOptions(2000);//设置query返回records值为2000
		QueryResult user_qr = null;
		boolean done = false;
		while (user_qr == null && flag < 100) {
			try {
				user_qr = client.query("select UserID__c from EmployeeTerritory__c limit 50000");
			} catch (ConnectionException e) {
				logger.warn("can not get EmployeeTerritory__c data!", e);
				flag++;
			}
		}
		if (user_qr == null || user_qr.getSize() == 0) {
			logger.warn("EmployeeTerritory__c has no data!");
			return;
		}

		Set<String> userIDs = Sets.newHashSet();

		while (!done) {
			SObject[] user_results = user_qr.getRecords();
			for (SObject user_Object : user_results) {
				Iterator<XmlObject> user_iter = user_Object.getChildren();
				while (user_iter.hasNext()) {
					XmlObject xmlObject = user_iter.next();
					if (xmlObject.getName().getLocalPart().equals("UserID__c"))
						userIDs.add((String) xmlObject.getValue());
				}
			}
			if (user_qr.isDone()) {
				done = true;
			} else {
				try {
					user_qr = client.queryMore(user_qr.getQueryLocator());
				} catch (ConnectionException e) {
					logger.warn("can not get EmployeeTerritory__c data!", e);
				}
			}
		}
		BasicDynaClass dynaClass = null;
		while (dynaClass == null && flag < 100)
			try {
				dynaClass = setupDynaClass("UserTerritory");
			} catch (ConnectionException e) {
				logger.warn("can not set up UserTerritory class!", e);
				flag++;
			}
		if (dynaClass == null)
			return;

		for (String userID : userIDs) {
			List<DynaBean> deleteList = Lists.newArrayList();
			List<DynaBean> insertList = Lists.newArrayList();
			Map<String, Object> userTerritoryMap = Maps.newHashMap();
			userTerritoryMap.put("UserId", userID);

			QueryResult et_qr = null;
			while (et_qr == null && flag < 100) {
				try {
					et_qr = client.query("select TerritoryID__c from EmployeeTerritory__c where UserID__c='" + userTerritoryMap.get("UserId") + "' order by CreatedDate desc limit 1");
				} catch (ConnectionException e) {
					logger.warn("can not get EmployeeTerritory__c data!", e);
					flag++;
				}
			}

			if (et_qr == null)
				return;

			SObject[] et_results = et_qr.getRecords();
			if (et_results.length > 0) {
				Iterator<XmlObject> et_iter = et_results[0].getChildren();
				while (et_iter.hasNext()) {
					XmlObject xmlObject = et_iter.next();
					if (xmlObject.getName().getLocalPart().equals("TerritoryID__c"))
						userTerritoryMap.put("TerritoryId", xmlObject.getValue());
				}
			}
			try {
				DynaBean insertObject = dynaClass.newInstance();
				BeanUtils.copyProperties(insertObject, userTerritoryMap);
				insertList.add(insertObject);
			} catch (Exception e) {
				logger.warn("convert object failed!", e);
			}

			QueryResult ut_qr = null;
			while (ut_qr == null && flag < 100) {
				try {
					ut_qr = client.query("select Id from UserTerritory where UserId='" + userTerritoryMap.get("UserId") + "'");
				} catch (ConnectionException e) {
					logger.warn("can not get UserTerritory data!", e);
					flag++;
				}
			}
			if (ut_qr == null)
				return;

			SObject[] ut_results = ut_qr.getRecords();
			if (ut_results.length > 0) {//UserId已存在与UserTerritory表中，先删除再插入
				for (SObject ut_object : ut_results) {
					Iterator<XmlObject> ut_iter = ut_object.getChildren();
					while (ut_iter.hasNext()) {
						XmlObject xmlObject = ut_iter.next();
						if (xmlObject.getName().getLocalPart().equals("Id")) {
							userTerritoryMap.put("Id", xmlObject.getValue());
							try {
								DynaBean deleteObject = dynaClass.newInstance();
								BeanUtils.copyProperties(deleteObject, userTerritoryMap);
								deleteList.add(deleteObject);
							} catch (Exception e) {
								logger.warn("convert object failed!", e);
							}
							break;
						}
					}
				}
				DeleteResult[] deleteResults = new DeleteResult[0];
				try {
					deleteResults = client.loadDeletes(deleteList);
				} catch (ConnectionException e) {
					logger.warn("delete data failed!", e);
				}
				for (int i = 0; i < deleteResults.length; i++) {
					DeleteResult result = deleteResults[i];
					if (result.getSuccess())
						logger.info("ID: " + result.getId() + " UserID: " + deleteList.get(i).get("UserId")
								+ " TerritoryID: " + deleteList.get(i).get("TerritoryId") + " has been deleted!");

					if (!result.getSuccess()) {
						logger.warn("ID: " + result.getId() + " UserID: " + deleteList.get(i).get("UserId")
								+ " TerritoryID: " + deleteList.get(i).get("TerritoryId") + " has not been deleted!");
					}
				}
			}
			//UserId不在UserTerritory表中，直接插入
			SaveResult[] insertResults = new SaveResult[0];
			try {
				insertResults = client.loadInserts(insertList);
			} catch (ConnectionException e) {
				logger.warn("insert data failed!", e);
			}
			for (int i = 0; i < insertResults.length; i++) {
				SaveResult result = insertResults[i];
				if (result.getSuccess())
					logger.info("ID: " + result.getId() + " UserID: " + insertList.get(i).get("UserId")
							+ " TerritoryID: " + insertList.get(i).get("TerritoryId") + " has been inserted!");
				if (!result.getSuccess()) {
					logger.warn("ID: " + result.getId() + " UserID: " + insertList.get(i).get("UserId")
							+ " TerritoryID: " + insertList.get(i).get("TerritoryId") + " has not been inserted!");
				}
			}

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
}
