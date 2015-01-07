package com.dianping.customer.tool.task;

import com.beust.jcommander.internal.Lists;
import com.dianping.customer.tool.dao.ShopTerritoryDao;
import com.dianping.customer.tool.dao.UserShopTerritoryDao;
import com.dianping.customer.tool.entity.ShopTerritory;
import com.dianping.customer.tool.entity.ShopTerritoryHistory;
import com.dianping.customer.tool.entity.UserShopHistory;
import com.dianping.customer.tool.entity.UserShopTerritory;
import com.dianping.customer.tool.exception.SalesForceException;
import com.dianping.customer.tool.job.dao.ShopTerritoryHistoryDao;
import com.dianping.customer.tool.job.dao.UserShopHistoryDao;
import com.dianping.customer.tool.service.SalesForceService;
import com.dianping.customer.tool.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: zhenwei.wang
 * Date: 14-12-22
 */
@Component
public class SyncApolloDataWorkThread implements Runnable {
	private static final int DEFAULT_SIZE = 5000;
	private static final int DEFAULT_INDEX = 1;

	String type;
	int threadBegin;
	int threadEnd;

	@Autowired
	private UserShopTerritoryDao userShopTerritoryDao;
	@Autowired
	private ShopTerritoryDao shopTerritoryDao;
	@Autowired
	private UserShopHistoryDao userShopHistoryDao;
	@Autowired
	private ShopTerritoryHistoryDao shopTerritoryHistoryDao;
	@Autowired
	SalesForceService salesForceService;

	Logger logger = LoggerFactory.getLogger(SyncApolloDataWorkThread.class);

	public SyncApolloDataWorkThread() {

	}

	public SyncApolloDataWorkThread(String type, int threadBegin, int threadEnd) {
		this.type = type;
		this.threadBegin = threadBegin;
		this.threadEnd = threadEnd;
	}

	@Override
	public void run() {
		syncSalesForceToApollo(type, threadBegin, threadEnd);
		logger.info("This thread: " + Thread.currentThread().getName() + " end!");
	}

	@SuppressWarnings("unchecked")
	private void syncSalesForceToApollo(String type, int threadBegin, int threadEnd) {
		int begin = threadBegin;
		int end = begin + DEFAULT_SIZE;
		int index = DEFAULT_INDEX;
		int pageSize = DEFAULT_SIZE;

		int flag = 0;

		while (flag < 1000 && end <= threadEnd) {
			try {
				if (!ConfigUtils.getSyncApolloDataTaskTrigger()) {
					logger.info("SyncApolloDataTask stop!");
					return;
				}

				List<Map<String, Object>> salesForceInfoList;
				try {
					if (type.equals("all")) {
						salesForceInfoList = salesForceService.getSalesForceInfoList(begin, end, type);
						begin = end;
						end = begin + DEFAULT_SIZE;
					} else {
						salesForceInfoList = salesForceService.getSalesForceInfoList(index, pageSize, type);
					}
				} catch (SalesForceException e) {
					flag++;
					logger.warn("This thread: " + Thread.currentThread().getName() + " get SalesForce data failed!", e);
					logger.info("This thread: " + Thread.currentThread().getName() + " this task run about " + end + " data failed!");
					continue;
				}


				if (salesForceInfoList == null || salesForceInfoList.size() == 0) {
					flag++;
					logger.info("This thread: " + Thread.currentThread().getName() +
							" From " + (begin - DEFAULT_SIZE) + " to " + begin + " has no data!");
					continue;
				}
				Map<String, String> shopUserMap = new HashMap<String, String>();
				Map<String, Set<String>> shopTerritoryMap = new HashMap<String, Set<String>>();
				Map<String, String> shopExternalMap = new HashMap<String, String>();

				try {
					for (Map<String, Object> sfInfo : salesForceInfoList) {
						shopUserMap.put((String) sfInfo.get("shopId"), (String) sfInfo.get("ownerLoginId"));
						shopTerritoryMap.put((String) sfInfo.get("shopId"), ((Map<String, String>) sfInfo.get("territoryId2Name")).keySet());
						shopExternalMap.put((String) sfInfo.get("shopId"), (String) sfInfo.get("sfId"));
					}
				} catch (Exception e) {
					logger.info("This thread: " + Thread.currentThread().getName() + " SalesForce data incomplete");
					flag++;
					continue;
				}

				List<UserShopTerritory> userShopList = userShopTerritoryDao.queryUserShopTerritoryByNewShopIDList(
						new ArrayList<String>(shopUserMap.keySet()));
				UserShopTerritory ust;
				for (int i = 0; i < userShopList.size(); i++) {
					ust = userShopList.get(i);
					if (shopUserMap.get(String.valueOf(ust.getNewShopID())) == null) {
						//Apollo中的数据，SalesForce中没有，将Apollo中的数据删除
						continue;
					}

					if (shopUserMap.get(String.valueOf(ust.getNewShopID())).equals(String.valueOf(ust.getUserID()))) {
						userShopList.remove(i);
						shopUserMap.remove(String.valueOf(ust.getNewShopID()));
						i--;
					}
				}

				deleteUserShopWrongData(userShopList);

				insertUserShopRightData(shopUserMap);

				List<ShopTerritory> shopTerritoryList = shopTerritoryDao.queryShopTerritoryByNewShopIDList(new ArrayList<String>(shopTerritoryMap.keySet()));
				ShopTerritory st;
				for (int i = 0; i < shopTerritoryList.size(); i++) {
					st = shopTerritoryList.get(i);
					if (shopTerritoryMap.get(String.valueOf(st.getNewShopID())) == null) {
						continue;
					}

					if (shopTerritoryMap.get(String.valueOf(st.getNewShopID())).contains(String.valueOf(st.getTerritoryID()))) {
						shopTerritoryList.remove(i);
						shopTerritoryMap.get(String.valueOf(st.getNewShopID())).remove(String.valueOf(st.getTerritoryID()));
						i--;
					}
				}

				deleteShopTerritoryWrongData(shopTerritoryList);

				insertShopTerritoryRightData(shopTerritoryMap, shopExternalMap);

				index++;
				flag = 0;
			} catch (Exception e) {
				flag++;
				logger.warn("This thread: " + Thread.currentThread().getName() + " Sql runs failed!", e);
			}
			if (type.equals("all"))
				logger.info("This thread: " + Thread.currentThread().getName() + " this task run about " + end + " data!");
			else
				logger.info("this task run about " + index * pageSize + " data!");
		}
	}

	public void addUserShopLog(List<UserShopTerritory> userShopList, int typeId) {
		List<UserShopHistory> userShopHistoryList = Lists.newArrayList();

		for (UserShopTerritory ust : userShopList) {
			UserShopHistory userShopHistory = new UserShopHistory();
			userShopHistory.setUserId(ust.getUserID());
			userShopHistory.setShopId(ust.getNewShopID());
			userShopHistory.setTypeId(typeId);
			userShopHistoryList.add(userShopHistory);
		}

		userShopHistoryDao.addToUserShopHistory(userShopHistoryList);
	}

	public void deleteUserShopWrongData(List<UserShopTerritory> userShopList) {
		try {
			if (userShopList.size() != 0) {
				userShopTerritoryDao.deleteUserShopTerritoryByUserShopList(userShopList);

				addUserShopLog(userShopList, 0);//删除数据typeId=0
			}
		} catch (Exception e) {
			logger.warn("deleteApolloUserShopTerritory.error", e);
		}
	}

	public void insertUserShopRightData(Map<String, String> shopUserMap) {
		if (shopUserMap.size() == 0)
			return;

		List<UserShopTerritory> userShopTerritoryList = Lists.newArrayList();

		for (Map.Entry<String, String> entry : shopUserMap.entrySet()) {
			if(entry.getValue() == null)
				continue;
			if (entry.getValue().equals("-38178"))
				continue;

			UserShopTerritory userShopTerritory = new UserShopTerritory();
			userShopTerritory.setUserID(Integer.valueOf(entry.getValue()));
			userShopTerritory.setNewShopID(Integer.valueOf(entry.getKey()));
			userShopTerritory.setStatus(1);
			userShopTerritory.setApproveStatus(1);
			userShopTerritoryList.add(userShopTerritory);
		}

		try {
			if (userShopTerritoryList.size() != 0) {
				userShopTerritoryDao.addToUserShopTerritoryByUserShopTerritoryList(userShopTerritoryList);

				addUserShopLog(userShopTerritoryList, 1);//插入数据typeId=1
			}
		} catch (Exception e) {
			logger.warn("insertApolloUserShopTerritory.error", e);
		}
	}

	public void addShopTerritoryLog(List<ShopTerritory> shopTerritoryList, int typeId) {
		List<ShopTerritoryHistory> shopTerritoryHistoryList = Lists.newArrayList();

		for (ShopTerritory st : shopTerritoryList) {
			ShopTerritoryHistory shopTerritoryHistory = new ShopTerritoryHistory();
			shopTerritoryHistory.setShopId(st.getNewShopID());
			shopTerritoryHistory.setTerritoryId(st.getTerritoryID());
			shopTerritoryHistory.setTypeId(typeId);
			shopTerritoryHistory.setExternalId(st.getExternalID());
			shopTerritoryHistoryList.add(shopTerritoryHistory);
		}

		shopTerritoryHistoryDao.addToShopTerritoryHistory(shopTerritoryHistoryList);
	}

	public void deleteShopTerritoryWrongData(List<ShopTerritory> shopTerritoryList) {
		try {
			if (shopTerritoryList.size() != 0) {
				shopTerritoryDao.deleteShopTerritoryByShopTerritoryList(shopTerritoryList);

				addShopTerritoryLog(shopTerritoryList, 0);//删除数据typeId=0
			}
		} catch (Exception e) {
			logger.info("deleteShopTerritoryByShopTerritoryList.error", e);
		}
	}

	public void insertShopTerritoryRightData(Map<String, Set<String>> shopTerritoryMap, Map<String, String> shopExternalMap) {
		if (shopTerritoryMap.size() == 0 || shopExternalMap.size() == 0)
			return;

		List<ShopTerritory> newShopTerritoryList = Lists.newArrayList();

		for (Map.Entry<String, Set<String>> entry : shopTerritoryMap.entrySet()) {
			for (String territoryID : entry.getValue()) {
				if(territoryID == null)
					continue;
				ShopTerritory shopTerritory = new ShopTerritory();
				shopTerritory.setExternalID(shopExternalMap.get(entry.getKey()) + "-" + territoryID);
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

				addShopTerritoryLog(newShopTerritoryList, 1);//插入typeId=1
			}
		} catch (Exception e) {
			logger.warn("insertApolloShopTerritory.error", e);
		}
	}
}
