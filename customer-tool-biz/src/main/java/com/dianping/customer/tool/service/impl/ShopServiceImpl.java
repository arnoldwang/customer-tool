package com.dianping.customer.tool.service.impl;

import com.dianping.ba.base.organizationalstructure.api.user.UserService;
import com.dianping.ba.base.organizationalstructure.api.user.dto.UserDto;
import com.dianping.customer.tool.dao.ShopTerritoryDao;
import com.dianping.customer.tool.dao.UserShopTerritoryDao;
import com.dianping.customer.tool.entity.ShopTerritory;
import com.dianping.customer.tool.entity.UserShopTerritory;
import com.dianping.customer.tool.exception.BizException;
import com.dianping.customer.tool.model.ServiceResult;
import com.dianping.customer.tool.model.ShopInfoModel;
import com.dianping.customer.tool.service.ShopService;
import com.dianping.customer.tool.utils.SalesforceOauthTokenUtil;
import com.dianping.salesbu.api.UserGroupService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zaza on 14/11/26.
 */
public class ShopServiceImpl implements ShopService {
	@Autowired
	private SalesforceOauthTokenUtil salesforceOauthTokenUtil;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ShopTerritoryDao shopTerritoryDao;

	@Autowired
	private UserShopTerritoryDao userShopTerritoryDao;

	@Autowired
	private UserService userService;

	@Autowired
	private UserGroupService userGroupService;

	private String smtShopInfoURL;

	public ServiceResult getSalesForceInfo(String shopId) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + salesforceOauthTokenUtil.getLoginToken());
		Map<String, String> uriVariables = Maps.newHashMap();
		uriVariables.put("shopId", shopId);
		String url = getRESTUrl(smtShopInfoURL);
		ResponseEntity<ServiceResult> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
		return response.getBody();
	}


	@Override
	public ShopInfoModel getShopInfo(String shopId) {
		if (!isNumeric(shopId))
			throw new BizException("您输入的ShopId不合法，请输入正确Id!");

		ShopInfoModel shopInfoModel = new ShopInfoModel();
		Map<String, Object> msg = (HashMap<String, Object>) getSalesForceInfo(shopId).getMsg();
		if (msg.get("shopId") == null)
			throw new BizException("未找到商户信息，请输入正确Id!");
		List<ShopTerritory> shopTerritoryList = shopTerritoryDao.queryShopTerritoryByNewShopID(Integer.valueOf(shopId));
		List<UserShopTerritory> userShopTerritoryList = userShopTerritoryDao.queryUserShopTerritoryByNewShopID(Integer.valueOf(shopId));
		UserShopTerritory userShopTerritory = new UserShopTerritory();
		for (UserShopTerritory ust : userShopTerritoryList) {
			if (userGroupService.getBUNamebyLogin(ust.getUserID()).contains("交易平台"))
				userShopTerritory = ust;
			if(userService.queryUserByLoginID(Integer.valueOf(ust.getUserID())).getRealName().contains("销售公海"))
				userShopTerritory = ust;
		}
		UserDto user = userService.queryUserByLoginID(userShopTerritory.getUserID());

		shopInfoModel.setShopId(shopId);
		shopInfoModel.setShopName((String) msg.get("name"));
		shopInfoModel.setCity((String) msg.get("city"));
		shopInfoModel.setDistrict((String) msg.get("district"));
		shopInfoModel.setMainCategory((String) msg.get("mainCategory"));

		shopInfoModel.setJhBU(msg.get("isJHBU").equals("true"));
		shopInfoModel.setHotelBU(msg.get("isJDLY").equals("true"));
		shopInfoModel.setVip(msg.get("type").equals("大客户"));//普通客户，大客户
		shopInfoModel.setSfOwner((String) msg.get("ownerName"));
		shopInfoModel.setSfLoginId((String) msg.get("ownerLoginId"));
		shopInfoModel.setApolloOwner(user.getRealName());
		shopInfoModel.setApolloLoginId(String.valueOf(user.getLoginId()));
		shopInfoModel.setSfTerritoryId2Name((LinkedHashMap<String, String>) msg.get("territoryId2Name"));
		List<String> apolloTerritoryIds = new ArrayList<String>();
		for (ShopTerritory st : shopTerritoryList)
			apolloTerritoryIds.add(String.valueOf(st.getTerritoryID()));
		shopInfoModel.setApolloTerritoryIds(apolloTerritoryIds);
		return shopInfoModel;
	}

	@Override
	public ShopInfoModel updateShopInfo(String shopId) {
		ShopInfoModel shopInfoModel = new ShopInfoModel();
		Map<String, Object> msg = (HashMap<String, Object>) getSalesForceInfo(shopId).getMsg();
		shopTerritoryDao.deleteShopTerritoryByNewShopID(Integer.valueOf(shopId));
		ShopTerritory shopTerritory = new ShopTerritory();
		Map<String, String> territoryId2Name = (LinkedHashMap<String, String>) msg.get("territoryId2Name");
		Iterator<String> iter = territoryId2Name.keySet().iterator();
		while (iter.hasNext()) {
			String territoryID = iter.next();
			shopTerritory.setExternalID(msg.get("sfId") + "-" + territoryID);
			shopTerritory.setNewShopID(Integer.valueOf(shopId));
			shopTerritory.setTerritoryID(Integer.valueOf(territoryID));
			shopTerritory.setStatus(1);
			shopTerritory.setApproveStatus(1);
			shopTerritoryDao.addToShopTerritory(shopTerritory);
		}

		List<ShopTerritory> shopTerritoryList = shopTerritoryDao.queryShopTerritoryByNewShopID(Integer.valueOf(shopId));
		List<String> apolloTerritoryIds = new ArrayList<String>();
		for (ShopTerritory st : shopTerritoryList)
			apolloTerritoryIds.add(String.valueOf(st.getTerritoryID()));
		shopInfoModel.setApolloTerritoryIds(apolloTerritoryIds);

		return shopInfoModel;
	}

	@Override
	public ShopInfoModel updateUserShopInfo(String shopId) {
		ShopInfoModel shopInfoModel = new ShopInfoModel();
		Map<String, Object> msg = (HashMap<String, Object>) getSalesForceInfo(shopId).getMsg();
		userShopTerritoryDao.deleteUserShopTerritoryByNewShopID(Integer.valueOf(shopId));
		UserShopTerritory userShopTerritory = new UserShopTerritory();
		userShopTerritory.setUserID(Integer.valueOf((String) msg.get("ownerLoginId")));
		userShopTerritory.setNewShopID(Integer.valueOf(shopId));
		userShopTerritory.setStatus(1);
		userShopTerritory.setApproveStatus(1);
		userShopTerritoryDao.addToUserShopTerritory(userShopTerritory);

		List<UserShopTerritory> userShopTerritoryList = userShopTerritoryDao.queryUserShopTerritoryByNewShopID(Integer.valueOf(shopId));
		for (UserShopTerritory ust : userShopTerritoryList) {
			if (userGroupService.getBUNamebyLogin(ust.getUserID()).contains("交易平台"))
				userShopTerritory = ust;
		}
		UserDto user = userService.queryUserByLoginID(userShopTerritory.getUserID());
		shopInfoModel.setApolloOwner(user.getRealName());
		return shopInfoModel;
	}

	private String getRESTUrl(String hostUrl) {
		hostUrl += "?shopId={shopId}";
		return hostUrl;
	}

	public void setSmtShopInfoURL(String smtShopInfoURL) {
		this.smtShopInfoURL = smtShopInfoURL;
	}

	private boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
}
