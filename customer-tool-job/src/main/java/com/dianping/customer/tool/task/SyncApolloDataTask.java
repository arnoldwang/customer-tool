package com.dianping.customer.tool.task;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.dianping.customer.tool.dao.ShopTerritoryDao;
import com.dianping.customer.tool.dao.UserShopTerritoryDao;
import com.dianping.customer.tool.entity.ShopTerritory;
import com.dianping.customer.tool.entity.ShopTerritoryHistory;
import com.dianping.customer.tool.entity.UserShopHistory;
import com.dianping.customer.tool.entity.UserShopTerritory;
import com.dianping.customer.tool.job.dao.ShopTerritoryHistoryDao;
import com.dianping.customer.tool.job.dao.UserShopHistoryDao;
import com.dianping.customer.tool.model.ServiceResult;
import com.dianping.customer.tool.utils.Beans;
import com.dianping.customer.tool.utils.ConfigUtils;
import com.dianping.customer.tool.utils.SalesForceOauthTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * User: zhenwei.wang
 * Date: 14-12-2
 */
public class SyncApolloDataTask {
	private static final int DEFAULT_SIZE = 1000;

	private static final int DEFAULT_PAGE_INDEX = 1;

	private SalesForceOauthTokenUtil salesForceOauthTokenUtil = Beans.getBean(SalesForceOauthTokenUtil.class);

	private RestTemplate restTemplate = Beans.getBean(RestTemplate.class);

	private UserShopTerritoryDao userShopTerritoryDao = Beans.getBean(UserShopTerritoryDao.class);

	private ShopTerritoryDao shopTerritoryDao = Beans.getBean(ShopTerritoryDao.class);

	private UserShopHistoryDao userShopHistoryDao = Beans.getBean(UserShopHistoryDao.class);

	private ShopTerritoryHistoryDao shopTerritoryHistoryDao = Beans.getBean(ShopTerritoryHistoryDao.class);

	private String token = this.token != null ? this.token : salesForceOauthTokenUtil.getLoginToken();

	Logger logger = LoggerFactory.getLogger(SyncApolloDataTask.class);


	public void go() {
		if (!ConfigUtils.getSyncApolloDataTaskTrigger()) {
			logger.info("SyncApolloDataTask will not run!");
			System.out.println("SyncApolloDataTask will not run!");
			return;
		}

		logger.info("SyncApolloDataTask.running...");
		System.out.println("SyncApolloDataTask.running...");
		long startTime = System.currentTimeMillis();

		syncSalesForceToApollo();

		long endTime = System.currentTimeMillis();
		logger.info("SyncApolloDataTask.end");
		System.out.println("SyncApolloDataTask.end");
		long useTime = (endTime - startTime)/1000;
		logger.info("This task use " + useTime/3600 + " H " + useTime %3600/60 + " m " + useTime %(3600*60));
	}


	private void syncSalesForceToApollo() {

		int index = DEFAULT_PAGE_INDEX;

		while (true) {
			try {
				List<HashMap<String, Object>> salesForceInfoList = getSalesForceInfoList(DEFAULT_SIZE, index);

				if (salesForceInfoList.size() == 0)
					break;
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
					index++;
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

//			try {
//				sleep(5000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//			}
				index++;
			} catch (Exception e) {
				index++;
				logger.info("time out", e);
			}
		}
	}

	public List<HashMap<String, Object>> getSalesForceInfoList(int pageSize, int pageNum) {

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		Map<String, String> uriVariables = Maps.newHashMap();
		uriVariables.put("pageSize", String.valueOf(pageSize));
		uriVariables.put("pageNum", String.valueOf(pageNum));
		String url = "https://dper--dpstg.cs6.my.salesforce.com/services/apexrest/SMTTool/shops" + "?pageNum={pageNum}&pageSize={pageSize}";
		ResponseEntity<ServiceResult> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
		if (response.getStatusCode().value() == 401) {
			token = salesForceOauthTokenUtil.getLoginToken();
			headers.set("Authorization", "Bearer " + token);
			response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
		}

		List<HashMap<String, Object>> salesForceInfoList = ((LinkedHashMap<String, ArrayList<HashMap<String, Object>>>) response.getBody().getMsg()).get("shopList");

		return salesForceInfoList;
	}

	public void addUserShopLog(List<UserShopTerritory> userShopList, int typeId) {
		List<UserShopHistory> userShopHistoryList = Lists.newArrayList();
		UserShopHistory userShopHistory = new UserShopHistory();
		for (UserShopTerritory ust : userShopList) {
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
				for (UserShopTerritory userShopTerritory : userShopList) {
					logger.info("删除Apollo.UserShopTerritory里的脏数据: " + "shopId = " + userShopTerritory.getNewShopID()
							+ "userId = " + userShopTerritory.getUserID());
				}
				addUserShopLog(userShopList, 0);//删除数据typeId=0
			}
		} catch (Exception e) {
			logger.warn("deleteApolloUserShopTerritory.error", e);
		}
	}

	public void insertUserShopRightData(Map<String, String> shopUserMap) {
		List<UserShopTerritory> userShopTerritoryList = Lists.newArrayList();
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
				for (UserShopTerritory ust : userShopTerritoryList) {
					logger.info("插入到Apollo.UserShopTerritory中: " + "shopId = " + ust.getNewShopID()
							+ "userId = " + ust.getUserID());
				}
				addUserShopLog(userShopTerritoryList, 1);//插入数据typeId=1
			}
		} catch (Exception e) {
			logger.warn("insertApolloUserShopTerritory.error", e);
		}
	}

	public void addShopTerritoryLog(List<ShopTerritory> shopTerritoryList, int typeId) {
		List<ShopTerritoryHistory> shopTerritoryHistoryList = Lists.newArrayList();
		ShopTerritoryHistory shopTerritoryHistory = new ShopTerritoryHistory();
		for (ShopTerritory st : shopTerritoryList) {
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
				for (ShopTerritory st : shopTerritoryList) {
					logger.info("删除Apollo.ShopTerritory里的脏数据: " + "shopId = " + st.getNewShopID()
							+ "territoryId = " + st.getTerritoryID());
				}
				addShopTerritoryLog(shopTerritoryList, 0);//删除数据typeId=0
			}
		} catch (Exception e) {
			logger.info("deleteShopTerritoryByShopTerritoryList.error", e);
		}
	}

	public void insertShopTerritoryRightData(Map<String, Set<String>> shopTerritoryMap, Map<String, String> shopExternalMap) {
		List<ShopTerritory> newShopTerritoryList = Lists.newArrayList();
		ShopTerritory shopTerritory = new ShopTerritory();
		for (Map.Entry<String, Set<String>> entry : shopTerritoryMap.entrySet()) {
			for (String territoryID : entry.getValue()) {
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
				for (ShopTerritory st : newShopTerritoryList) {
					logger.info("插入到Apollo.ShopTerritory中: " + "shopId = " + st.getNewShopID()
							+ "territoryId = " + st.getTerritoryID());
				}
				addShopTerritoryLog(newShopTerritoryList, 1);//插入typeId=1
			}
		} catch (Exception e) {
			logger.warn("insertApolloShopTerritory.error", e);
		}
	}
}
