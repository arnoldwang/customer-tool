package com.dianping.customer.tool.task;

import com.beust.jcommander.internal.Maps;
import com.dianping.customer.tool.dao.ShopTerritoryDao;
import com.dianping.customer.tool.dao.UserShopTerritoryDao;
import com.dianping.customer.tool.entity.ShopTerritory;
import com.dianping.customer.tool.entity.UserShopTerritory;
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

import static java.lang.Thread.sleep;

/**
 * User: zhenwei.wang
 * Date: 14-12-2
 */
public class SyncApolloDataTask {
	private static final int DEFAULT_SIZE = 1000;

	private static final int DEFAULT_PAGE_INDEX = 10;

	private SalesForceOauthTokenUtil salesForceOauthTokenUtil = Beans.getBean(SalesForceOauthTokenUtil.class);

	private RestTemplate restTemplate = Beans.getBean(RestTemplate.class);

	private UserShopTerritoryDao userShopTerritoryDao = Beans.getBean(UserShopTerritoryDao.class);

	private ShopTerritoryDao shopTerritoryDao = Beans.getBean(ShopTerritoryDao.class);

	private  String token = this.token != null ? this.token : salesForceOauthTokenUtil.getLoginToken();

	Logger logger = LoggerFactory.getLogger(SyncApolloDataTask.class);


	public void go() {
		if (!ConfigUtils.getSyncApolloDataTaskTrigger()){
			logger.info("SyncApolloDataTask will not run!");
			System.out.println("SyncApolloDataTask will not run!");
			return;
		}

		logger.info("SyncApolloDataTask.running...");
		System.out.println("SyncApolloDataTask.running...");

		syncSalesForceToApollo();

		logger.info("SyncApolloDataTask.end");
		System.out.println("SyncApolloDataTask.end");
	}


	private void syncSalesForceToApollo() {

		int index = DEFAULT_PAGE_INDEX;

		//******************************test data******************************************
//		List<SalesForceInfo> salesForceInfoList = new ArrayList<SalesForceInfo>();
//		SalesForceInfo sfInfo1 = new SalesForceInfo();
//		sfInfo1.setShopId("500018");
//		sfInfo1.setOwnerLoginId("-24351");
//		Map<String, String> t2n1 = Maps.newHashMap("11", "huadong", "220", "huabei", "235", "huanan");
//		sfInfo1.setTerritoryId2Name(t2n1);
//		salesForceInfoList.add(sfInfo1);
//		SalesForceInfo sfInfo2 = new SalesForceInfo();
//		sfInfo2.setShopId("500017");
//		sfInfo2.setOwnerLoginId("-24350");
//		Map<String, String> t2n2 = Maps.newHashMap("22", "huaxi");
//		sfInfo2.setTerritoryId2Name(t2n2);
//		salesForceInfoList.add(sfInfo2);

		//******************************end of test data************************************
		while (true) {
			List<HashMap<String, Object>> salesForceInfoList = getSalesForceInfoList(DEFAULT_SIZE, index);

			if (salesForceInfoList.size() == 0)
				break;
			Map<String, String> shopUserMap = new HashMap<String, String>();
			Map<String, Set<String>> shopTerritoryMap = new HashMap<String, Set<String>>();
			Map<String, String> shopExternalMap = new HashMap<String, String>();

			try{
				for (HashMap<String, Object> sfInfo : salesForceInfoList) {
					shopUserMap.put((String)sfInfo.get("shopId"), (String)sfInfo.get("ownerLoginId"));
					shopTerritoryMap.put((String)sfInfo.get("shopId"), ((LinkedHashMap<String, String>)sfInfo.get("territoryId2Name")).keySet());
					shopExternalMap.put((String)sfInfo.get("shopId"), (String)sfInfo.get("sfId"));
				}
			}catch (Exception e){
				//todo logger
				index++;
				continue;
			}

			List<UserShopTerritory> userShopList = userShopTerritoryDao.queryUserShopTerritoryByNewShopIDList(new ArrayList<String>(shopUserMap.keySet()));
			UserShopTerritory ust;
			for (int i = 0; i < userShopList.size(); i++) {
				ust = userShopList.get(i);
				if (shopUserMap.get(String.valueOf(ust.getNewShopID())) == null){
					userShopList.remove(i);
					i--;
					continue;
				}

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
				if (shopTerritoryMap.get(String.valueOf(st.getNewShopID())) == null){
					shopTerritoryList.remove(i);
					i--;
					continue;
				}

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
					logger.info("将SalesForce中数据批量插入到Apollo.ShopTerritory中");
				}
			} catch (Exception e) {
				logger.warn("insertApolloShopTerritory.error", e);
			}
//			try {
//				sleep(5000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//			}
			index++;
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
		if(response.getStatusCode().value() == 401){
			token = salesForceOauthTokenUtil.getLoginToken();
			headers.set("Authorization", "Bearer " + token);
			response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
		}

		List<HashMap<String, Object>> salesForceInfoList = ((LinkedHashMap<String, ArrayList<HashMap<String, Object>>>)response.getBody().getMsg()).get("shopList");

		return salesForceInfoList;
	}

}
