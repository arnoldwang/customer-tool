package com.dianping.customer.tool.task;

import com.beust.jcommander.internal.Lists;
import com.dianping.customer.tool.dao.ShopTerritoryDao;
import com.dianping.customer.tool.dao.UserShopTerritoryDao;
import com.dianping.customer.tool.entity.ShopTerritory;
import com.dianping.customer.tool.entity.ShopTerritoryHistory;
import com.dianping.customer.tool.entity.UserShopHistory;
import com.dianping.customer.tool.entity.UserShopTerritory;
import com.dianping.customer.tool.job.dao.ShopTerritoryHistoryDao;
import com.dianping.customer.tool.job.dao.UserShopHistoryDao;
import com.dianping.customer.tool.service.SalesForceService;
import com.dianping.customer.tool.utils.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * User: zhenwei.wang
 * Date: 14-12-2
 */
public class SyncApolloDataTask {
	private static final int DEFAULT_SIZE = 10000;

	private static final int DEFAULT_INDEX = 1;

	private static final int DEFAULT_THREAD_NUM = 5;

	@Autowired
	private UserShopTerritoryDao userShopTerritoryDao;
	@Autowired
	private ShopTerritoryDao shopTerritoryDao;
	@Autowired
	private UserShopHistoryDao userShopHistoryDao;
	@Autowired
	private ShopTerritoryHistoryDao shopTerritoryHistoryDao;
	@Autowired
	private SalesForceService salesForceService;


	Logger logger = LoggerFactory.getLogger(SyncApolloDataTask.class);


	public void go() {
		if (!ConfigUtils.getSyncApolloDataTaskTrigger()) {
			logger.info("SyncApolloDataTask will not run!");
			System.out.println("SyncApolloDataTask will not run!");
			return;
		}

		String type = ConfigUtils.getSyncApolloDataTaskType();
		int maxShopId = salesForceService.getSfMaxShopId();
		int threadShopNum = maxShopId / 5;
		System.out.println("+++++++++++++++++++++++++++++++maxShopId = " + maxShopId);
		System.out.println("+++++++++++++++++++++++++++++++threadShopNum = " + threadShopNum);

		ExecutorService exe = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);
		List<Future> futureList = Lists.newArrayList();

		logger.info("SyncApolloDataTask.running...");
		System.out.println("SyncApolloDataTask.running...");
		long beginTime = System.currentTimeMillis();

		for(int i = 0; i < DEFAULT_THREAD_NUM; i++){
			futureList.add(exe.submit(new WorkThread(type, DEFAULT_INDEX + threadShopNum * i, DEFAULT_INDEX + threadShopNum * (i + 1))));
		}
		System.out.println("+++++++++++++++++++++++++++++++futureListNum = " + futureList.size());

		for (int i = 0; i < DEFAULT_THREAD_NUM; i++){
			try {
				futureList.get(i).get();
			} catch (InterruptedException e) {
				logger.warn("This thread: " + Thread.currentThread().getName() + " is interrupted!", e);
			} catch (ExecutionException e) {
				logger.warn("This thread: " + Thread.currentThread().getName() + " is failed!", e);
			}
		}

		long endTime = System.currentTimeMillis();
		logger.info("SyncApolloDataTask.end");
		System.out.println("SyncApolloDataTask.end");
		long useTime = (endTime - beginTime) / 1000;
		logger.info("This task use " + useTime / 3600 + " H " + useTime % 3600 / 60 + " m " + useTime % (3600 * 60) + " s!");
	}


	private void syncSalesForceToApollo(String type, int threadBegin, int threadEnd) {
		int begin = threadBegin;
		int end = begin + DEFAULT_SIZE;
		int index = DEFAULT_INDEX;
		int pageSize = DEFAULT_SIZE;

		int flag = 0;

		while (flag < 100 && end <= threadEnd) {
			try {
				if (!ConfigUtils.getSyncApolloDataTaskTrigger()) {
					logger.info("SyncApolloDataTask stop!");
					return;
				}

				List<HashMap<String, Object>> salesForceInfoList;
				if (type.equals("all")) {
					salesForceInfoList = salesForceService.getSalesForceInfoList(begin, end, type);
					begin = end;
					end = begin + DEFAULT_SIZE;
				} else {
					salesForceInfoList = salesForceService.getSalesForceInfoList(index, pageSize, type);
				}


				if (salesForceInfoList == null || salesForceInfoList.size() == 0) {
					flag++;
					continue;
				}
				Map<String, String> shopUserMap = new HashMap<String, String>();
				Map<String, Set<String>> shopTerritoryMap = new HashMap<String, Set<String>>();
				Map<String, String> shopExternalMap = new HashMap<String, String>();

				try {
					for (HashMap<String, Object> sfInfo : salesForceInfoList) {
						shopUserMap.put((String) sfInfo.get("shopId"), (String) sfInfo.get("ownerLoginId"));
						shopTerritoryMap.put((String) sfInfo.get("shopId"), ((LinkedHashMap<String, String>) sfInfo.get("territoryId2Name")).keySet());
						shopExternalMap.put((String) sfInfo.get("shopId"), (String) sfInfo.get("sfId"));
					}
				} catch (Exception e) {
					logger.info("SalesForce data incomplete");
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
				logger.warn("something error", e);
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
		List<UserShopTerritory> userShopTerritoryList = Lists.newArrayList();

		for (Map.Entry<String, String> entry : shopUserMap.entrySet()) {
			if (entry.getValue().equals("-38178")) {
				continue;
			}
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
		List<ShopTerritory> newShopTerritoryList = Lists.newArrayList();

		for (Map.Entry<String, Set<String>> entry : shopTerritoryMap.entrySet()) {
			for (String territoryID : entry.getValue()) {
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

	private class WorkThread implements Runnable{
		String type;
		int threadBegin;
		int threadEnd;

		public WorkThread(String type, int threadBegin, int threadEnd ){
			this.type = type;
			this.threadBegin = threadBegin;
			this.threadEnd = threadEnd;
		}

		@Override
		public void run() {
			syncSalesForceToApollo(type, threadBegin, threadEnd);
			logger.info("This thread: " + Thread.currentThread().getName() + " end!");
		}
	}
}
