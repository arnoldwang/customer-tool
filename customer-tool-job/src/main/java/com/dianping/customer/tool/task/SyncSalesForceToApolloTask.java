package com.dianping.customer.tool.task;

import com.beust.jcommander.internal.Maps;
import com.dianping.customer.tool.dao.ShopTerritoryDao;
import com.dianping.customer.tool.dao.UserShopTerritoryDao;
import com.dianping.customer.tool.entity.ShopTerritory;
import com.dianping.customer.tool.entity.UserShopTerritory;
import com.dianping.customer.tool.model.SalesForceInfo;
import com.dianping.customer.tool.utils.Beans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * User: zhenwei.wang
 * Date: 14-12-2
 */
public class SyncSalesForceToApolloTask {
	private static final int DEFAULT_SIZE = 1000;

	private static final int DEFAULT_PAGE_INDEX = 1;

	Logger logger = LoggerFactory.getLogger(SyncSalesForceToApolloTask.class);


	public void go() {
		logger.info("SyncSalesForceToApolloTask.running...");
		System.out.println("SyncSalesForceToApolloTask.running...");

		syncSalesForceToApollo();

		logger.info("SyncSalesForceToApolloTask.end");
		System.out.println("SyncSalesForceToApolloTask.end");
	}


	private void syncSalesForceToApollo() {

		UserShopTerritoryDao userShopTerritoryDao = Beans.getBean(UserShopTerritoryDao.class);

		ShopTerritoryDao shopTerritoryDao = Beans.getBean(ShopTerritoryDao.class);

		int index = DEFAULT_PAGE_INDEX;

		//******************************test data******************************************
		List<SalesForceInfo> salesForceInfoList = new ArrayList<SalesForceInfo>();
		SalesForceInfo sfInfo1 = new SalesForceInfo();
		sfInfo1.setShopId("500018");
		sfInfo1.setOwnerLoginId("-24351");
		Map<String, String> t2n1 = Maps.newHashMap("11", "huadong", "220", "huabei", "235", "huanan");
		sfInfo1.setTerritoryId2Name(t2n1);
		salesForceInfoList.add(sfInfo1);
		SalesForceInfo sfInfo2 = new SalesForceInfo();
		sfInfo2.setShopId("500017");
		sfInfo2.setOwnerLoginId("-24350");
		Map<String, String> t2n2 = Maps.newHashMap("22", "huaxi");
		sfInfo2.setTerritoryId2Name(t2n2);
		salesForceInfoList.add(sfInfo2);

		//******************************end of test data************************************
		while (index < 2) {//true
//			List<SalesForceInfo> salesForceInfoList = getSalesForceInfoList(DEFAULT_SIZE, index);

			if (salesForceInfoList.size() == 0)
				break;
			Map<String, String> shopUserMap = new HashMap<String, String>();
			Map<String, Set<String>> shopTerritoryMap = new HashMap<String, Set<String>>();
			Map<String, String> shopExternalMap = new HashMap<String, String>();

			for (SalesForceInfo sfInfo : salesForceInfoList) {
				shopUserMap.put(sfInfo.getShopId(), sfInfo.getOwnerLoginId());
				shopTerritoryMap.put(sfInfo.getShopId(), sfInfo.getTerritoryId2Name().keySet());
//				shopExternalMap.put(sfInfo.getShopId(), sfInfo.getSfId());
			}

			if (shopUserMap.size() == 0 || shopTerritoryMap.size() == 0)
				continue;

			List<UserShopTerritory> userShopList = userShopTerritoryDao.queryUserShopTerritoryByNewShopIDList(new ArrayList<String>(shopUserMap.keySet()));
			UserShopTerritory ust;
			for (int i = 0; i < userShopList.size(); i++) {
				ust = userShopList.get(i);
				if (shopUserMap.get(String.valueOf(ust.getNewShopID())).equals(String.valueOf(ust.getUserID()))) {
					userShopList.remove(i);
					shopUserMap.remove(String.valueOf(ust.getNewShopID()));
					i--;
				}
			}

			try {
				if (userShopList.size() != 0) {
					userShopTerritoryDao.deleteUserShopTerritoryByUserShopList(userShopList);
					logger.info("删除Apollo.UserShopTerritory里的脏数据");
				}
			} catch (Exception e) {
				logger.warn("deleteApolloUserShopTerritory.error", e);
			}

			List<UserShopTerritory> userShopTerritoryList = new ArrayList<UserShopTerritory>();
			UserShopTerritory userShopTerritory = new UserShopTerritory();
			for (Map.Entry<String, String> entry : shopUserMap.entrySet()) {
				userShopTerritory.setUserID(Integer.valueOf(entry.getValue()));
				userShopTerritory.setNewShopID(Integer.valueOf(entry.getKey()));
				userShopTerritory.setStatus(1);
				userShopTerritory.setApproveStatus(1);
				userShopTerritoryList.add(userShopTerritory);
			}

			try {
				if (userShopTerritoryList.size() != 0) {
					userShopTerritoryDao.addToUserShopTerritoryByUserShopTerritoryList(userShopTerritoryList);
					logger.info("将SalesForce中数据批量插入到Apollo.UserShopTerritory中");
				}
			} catch (Exception e) {
				logger.warn("insertApolloUserShopTerritory.error", e);
			}

			List<ShopTerritory> shopTerritoryList = shopTerritoryDao.queryShopTerritoryByNewShopIDList(new ArrayList<String>(shopTerritoryMap.keySet()));
			ShopTerritory st;
			for (int i = 0; i < shopTerritoryList.size(); i++) {
				st = shopTerritoryList.get(i);
				if (shopTerritoryMap.get(String.valueOf(st.getNewShopID())).contains(String.valueOf(st.getTerritoryID()))) {
					shopTerritoryList.remove(i);
					shopTerritoryMap.get(String.valueOf(st.getNewShopID())).remove(String.valueOf(st.getTerritoryID()));
					i--;
				}
			}

			try {
				if (shopTerritoryList.size() != 0) {
					shopTerritoryDao.deleteShopTerritoryByShopTerritoryList(shopTerritoryList);
					logger.info("删除Apollo.ShopTerritory里的脏数据");
				}
			} catch (Exception e) {
				logger.info("deleteShopTerritoryByShopTerritoryList.error", e);
			}

			List<ShopTerritory> newShopTerritoryList = new ArrayList<ShopTerritory>();
			ShopTerritory shopTerritory = new ShopTerritory();
			for (Map.Entry<String, Set<String>> entry : shopTerritoryMap.entrySet()) {
				for (String territoryID : entry.getValue()) {
//					shopTerritory.setExternalID(shopExternalMap.get(entry.getKey()) + "-" + territoryID);
					shopTerritory.setNewShopID(Integer.valueOf(entry.getKey()));
					shopTerritory.setTerritoryID(Integer.valueOf(territoryID));
					shopTerritory.setStatus(1);
					shopTerritory.setApproveStatus(1);
					newShopTerritoryList.add(shopTerritory);
				}
			}

			try {
				if (newShopTerritoryList.size() != 0) {
					shopTerritoryDao.addToShopTerritoryByShopTerritoryList(newShopTerritoryList);
					logger.info("将SalesForce中数据批量插入到Apollo.ShopTerritory中");
				}
			} catch (Exception e) {
				logger.warn("insertApolloShopTerritory.error", e);
			}

			index++;
		}
	}

	public List<SalesForceInfo> getSalesForceInfoList(int pageSize, int index) {
		return null;
	}

}
