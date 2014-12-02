package com.dianping.customer.tool.task;

import com.dianping.customer.data.utils.Beans;
import com.dianping.customer.tool.dao.ShopTerritoryDao;
import com.dianping.customer.tool.dao.UserShopTerritoryDao;
import com.dianping.customer.tool.entity.UserShopTerritory;
import com.dianping.customer.tool.model.SalesForceInfo;
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

	private UserShopTerritoryDao userShopTerritoryDao = Beans.getBean(UserShopTerritoryDao.class);

	private ShopTerritoryDao shopTerritoryDao = Beans.getBean(ShopTerritoryDao.class);

	public void go() {
		logger.info("SyncSalesForceToApolloTask.running...");
		System.out.println("SyncSalesForceToApolloTask.running...");

		syncSalesForceToApollo();

		logger.info("SyncSalesForceToApolloTask.end");
		System.out.println("SyncSalesForceToApolloTask.end");
	}


	private void syncSalesForceToApollo() {
		int index = DEFAULT_PAGE_INDEX;
		while (true) {
			List<SalesForceInfo> salesForceInfoList = getSalesForceInfoList(DEFAULT_SIZE, index);
			if (salesForceInfoList.size() == 0)
				break;
			Map<String, String> shopUserMap = new HashMap<String, String>();
			Map<String, Set<String>> shopTerritoryMap = new HashMap<String, Set<String>>();
			Map<String, String> shopExternalMap = new HashMap<String, String>();
			SalesForceInfo saleForceInfoTmp;

			for (int i = 0; i < salesForceInfoList.size(); i++) {
				saleForceInfoTmp = salesForceInfoList.get(i);
				shopUserMap.put(saleForceInfoTmp.getShopId(), saleForceInfoTmp.getOwnerLoginId());
				shopTerritoryMap.put(saleForceInfoTmp.getShopId(), saleForceInfoTmp.getTerritoryId2Name().keySet());
				shopExternalMap.put(saleForceInfoTmp.getShopId(), saleForceInfoTmp.getSfId());
			}

			List<UserShopTerritory> userShopList = userShopTerritoryDao.queryUserShopTerritoryByNewShopIDList(new ArrayList<String>(shopUserMap.keySet()));
			UserShopTerritory userShopTerritoryTmp;
			for (int i = 0; i < userShopList.size(); i++) {
				userShopTerritoryTmp = userShopList.get(i);
				if (shopUserMap.get(String.valueOf(userShopTerritoryTmp.getNewShopID())).equals(String.valueOf(userShopTerritoryTmp.getUserID()))) {
					userShopList.remove(i);
					shopUserMap.remove(String.valueOf(userShopTerritoryTmp.getNewShopID()));
				}
			}
			try {
				userShopTerritoryDao.deleteUserShopTerritoryByUserShopList(userShopList);
				logger.info("删除Apollo里的脏数据");
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
				userShopTerritoryDao.addToUserShopTerritoryByUserShopTerritoryList(userShopTerritoryList);
				logger.info("将SalesForce中数据批量插入到Apollo中");
			} catch (Exception e) {
				logger.warn("insertApolloUserShopTerritory.error", e);
			}

			index++;
		}
	}

	public List<SalesForceInfo> getSalesForceInfoList(int pageSize, int index) {
		return null;
	}

}
