package com.dianping.customer.tool.service.impl;

import com.dianping.ba.base.organizationalstructure.api.user.UserService;
import com.dianping.ba.base.organizationalstructure.api.user.dto.UserDto;
import com.dianping.customer.tool.dao.ShopTerritoryDao;
import com.dianping.customer.tool.dao.UserShopTerritoryDao;
import com.dianping.customer.tool.entity.ShopTerritory;
import com.dianping.customer.tool.entity.UserShopTerritory;
import com.dianping.customer.tool.model.ServiceResult;
import com.dianping.customer.tool.model.ShopInfoModel;
import com.dianping.customer.tool.service.ShopService;
import com.dianping.customer.tool.utils.SalesforceOauthTokenUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

	private String smtShopInfoURL;

	@Override
	public ShopInfoModel getShopInfo(String shopId) {
		ShopInfoModel shopInfoModel = new ShopInfoModel();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + salesforceOauthTokenUtil.getLoginToken());
		Map<String, String> uriVariables = Maps.newHashMap();
		uriVariables.put("shopId", shopId);
		String url = getRESTUrl(smtShopInfoURL);
		ResponseEntity<ServiceResult> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
		ServiceResult result = response.getBody();
		Map<String, Object> msg = (HashMap<String, Object>) result.getMsg();
		//todo
		List<ShopTerritory> shopTerritory = shopTerritoryDao.queryShopTerritoryByNewShopID(Integer.valueOf(shopId));
		UserShopTerritory userShopTerritory = userShopTerritoryDao.queryUserShopTerritoryByNewShopID(Integer.valueOf(shopId)).get(0);
		UserDto user = userService.queryUserByLoginID(userShopTerritory.getUserID());

		shopInfoModel.setShopId(shopId);
		shopInfoModel.setShopName((String) msg.get("name"));
		shopInfoModel.setCity((String) msg.get("city"));
		shopInfoModel.setDistrict((String) msg.get("district"));
		shopInfoModel.setMainCategory((String) msg.get("mainCategory"));

		shopInfoModel.setJhBU(msg.get("isJHBU").equals("true"));
		shopInfoModel.setHotelBU(msg.get("isJDLY").equals("true"));
		shopInfoModel.setVip(msg.get("type").equals("vip"));//??
		shopInfoModel.setSfOwner((String)msg.get("ownerName"));
		shopInfoModel.setSfLoginId((String)msg.get("ownerLoginId"));
		shopInfoModel.setApolloOwner(user.getRealName());
		shopInfoModel.setApolloLoginId(String.valueOf(user.getLoginId()));
		shopInfoModel.setSfTerritoryId2Name((LinkedHashMap<String, String>)msg.get("territoryId2Name"));
		List<String> apolloTerritoryIds = new ArrayList<String>();
		for(ShopTerritory st: shopTerritory)
			apolloTerritoryIds.add(String.valueOf(st.getTerritoryID()));
		shopInfoModel.setApolloTerritoryIds(apolloTerritoryIds);
		return shopInfoModel;
	}

	private String getRESTUrl(String hostUrl) {
		hostUrl += "?shopId={shopId}";
		return hostUrl;
	}

	public void setSmtShopInfoURL(String smtShopInfoURL) {
		this.smtShopInfoURL = smtShopInfoURL;
	}
}
