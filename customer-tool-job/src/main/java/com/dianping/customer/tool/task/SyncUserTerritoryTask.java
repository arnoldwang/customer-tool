package com.dianping.customer.tool.task;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
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
		QueryResult et_qr = null;
		while (et_qr == null && flag < 100) {
			try {
				et_qr = client.query("select TerritoryID__c,UserID__c from EmployeeTerritory__c");
			} catch (ConnectionException e) {
				logger.warn("can not get EmployeeTerritory__c data!", e);
				flag++;
			}
		}
		if (et_qr == null || et_qr.getSize() == 0) {
			logger.warn("EmployeeTerritory__c has no data!");
			return;
		}
		SObject[] et_results = et_qr.getRecords();

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
						ut_qr = client.query("select Id,TerritoryId,UserId from UserTerritory where UserId='" + userTerritoryMap.get("UserId") + "'");
					} catch (ConnectionException e) {
						logger.warn("can not get UserTerritory data!", e);
						flag++;
					}
				}
				if(ut_qr == null)
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
					for (DeleteResult result : deleteResults) {
						if (!result.getSuccess()) {
							logger.warn("ID: "+result.getId() + " has not been deleted!");
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
				for (SaveResult result : insertResults) {
					if (!result.getSuccess()) {
						logger.warn("ID: "+result.getId() + " has not been inserted!");
					}
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
