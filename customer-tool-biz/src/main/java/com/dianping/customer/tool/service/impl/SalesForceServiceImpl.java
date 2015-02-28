package com.dianping.customer.tool.service.impl;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.dianping.customer.tool.exception.SalesForceException;
import com.dianping.customer.tool.model.ServiceResult;
import com.dianping.customer.tool.service.SalesForceService;
import com.dianping.customer.tool.utils.ConfigUtils;
import com.dianping.customer.tool.utils.SalesForceOauthTokenUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: zhenwei.wang
 * Date: 14-12-18
 */
@Component
public class SalesForceServiceImpl implements SalesForceService {

	@Autowired
	private SalesForceOauthTokenUtil salesForceOauthTokenUtil;

	@Autowired
	private RestTemplate restTemplate;

	private String token;

	private String smtShopInfoURL;

	private String smtUserInfoURL;

	private String smtShopInfoListURL;

	Logger logger = LoggerFactory.getLogger(SalesForceServiceImpl.class);

	@Override
	public ServiceResult getSfShopInfo(String shopId) {
		ResponseEntity<ServiceResult> response = new ResponseEntity<ServiceResult>(HttpStatus.REQUEST_TIMEOUT);
		try {
			HttpHeaders headers = new HttpHeaders();
			if (token == null)
				token = salesForceOauthTokenUtil.getLoginToken();
			headers.set("Authorization", "Bearer " + token);
			Map<String, String> uriVariables = Maps.newHashMap();
			uriVariables.put("shopId", shopId);
			String url = smtShopInfoURL + "?shopId={shopId}";
			try{
				response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
			} catch (Exception e){
				token = salesForceOauthTokenUtil.getLoginToken();
				headers.set("Authorization", "Bearer " + token);
				response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
			}
			if (response.getStatusCode().value() == 401) {
				token = salesForceOauthTokenUtil.getLoginToken();
				headers.set("Authorization", "Bearer " + token);
				response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
			}
		} catch (Exception e) {
			logger.warn("get SalesForce data failed!", e);
		}
		return response.getBody();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getSalesForceInfoList(int begin, int end, String type) {

		HttpHeaders headers = new HttpHeaders();
		if (token == null)
			token = salesForceOauthTokenUtil.getLoginToken();
		headers.set("Authorization", "Bearer " + token);
		Map<String, String> uriVariables = com.beust.jcommander.internal.Maps.newHashMap();
		uriVariables.put("begin", String.valueOf(begin));
		uriVariables.put("end", String.valueOf(end));
		String url = null;
		if (type.equals("all")) {
			url = smtShopInfoListURL + "?type=all&begin={begin}&end={end}";
		}
		if (type.equals("territory")) {
			String territoryId = ConfigUtils.getSyncApolloDataTaskTerritoryId();
			uriVariables.put("territoryId", territoryId);
			url = smtShopInfoListURL + "?type=territory&territoryId={territoryId}&index={begin}&pageSize={end}";
		}
		if (type.equals("increment")) {
			url = smtShopInfoListURL + "?type=increment&index={begin}&pageSize={end}";
		}
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.REQUEST_TIMEOUT);
		try {
			response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), String.class, uriVariables);
		}catch (Exception e){
			token = salesForceOauthTokenUtil.getLoginToken();
			headers.set("Authorization", "Bearer " + token);
			response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), String.class, uriVariables);
		}
		if (response.getStatusCode().value() == 401) {
			token = salesForceOauthTokenUtil.getLoginToken();
			headers.set("Authorization", "Bearer " + token);
			response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), String.class, uriVariables);
		}

		List<Map<String, Object>> shopList;
		try {
			JSONObject json = new JSONObject(response.getBody());
			Map<String, Object> rs = jsonToMap(json);
			shopList = ((Map<String, List<Map<String, Object>>>) rs.get("msg")).get("shopList");
		} catch (Exception e) {
//			logger.warn("This thread: " + Thread.currentThread().getName() + response.getBody());
			logger.warn("This thread: " + Thread.currentThread().getName(), e);
			throw new SalesForceException("get SalesForce data failed!");
		}

		return shopList;
	}

	@Override
	public ServiceResult getSfUserTerritoryInfo(String loginId) {
		ResponseEntity<ServiceResult> response = new ResponseEntity<ServiceResult>(HttpStatus.REQUEST_TIMEOUT);
		try {
			HttpHeaders headers = new HttpHeaders();
			if (token == null)
				token = salesForceOauthTokenUtil.getLoginToken();
			headers.set("Authorization", "Bearer " + token);
			Map<String, String> uriVariables = Maps.newHashMap();
			uriVariables.put("loginId", loginId);
			String url = smtUserInfoURL + "?loginId={loginId}";
			try {
				response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
			}catch (Exception e){
				token = salesForceOauthTokenUtil.getLoginToken();
				headers.set("Authorization", "Bearer " + token);
				response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
			}
			if (response.getStatusCode().value() == 401) {
				token = salesForceOauthTokenUtil.getLoginToken();
				headers.set("Authorization", "Bearer " + token);
				response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
			}
		} catch (Exception e) {
			logger.warn("get SalesForce data failed!", e);
		}
		return response.getBody();
	}

	@Override
	@SuppressWarnings("unchecked")
	public int getSfMaxShopId() {
		ResponseEntity<ServiceResult> response = new ResponseEntity<ServiceResult>(HttpStatus.REQUEST_TIMEOUT);
		try {
			HttpHeaders headers = new HttpHeaders();
			if (token == null)
				token = salesForceOauthTokenUtil.getLoginToken();
			headers.set("Authorization", "Bearer " + token);
			Map<String, String> uriVariables = Maps.newHashMap();
			String url = smtShopInfoListURL + "?type=maxShopId";
			try {
				response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
			}catch (Exception e){
				token = salesForceOauthTokenUtil.getLoginToken();
				headers.set("Authorization", "Bearer " + token);
				response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
			}
			if (response.getStatusCode().value() == 401) {
				token = salesForceOauthTokenUtil.getLoginToken();
				headers.set("Authorization", "Bearer " + token);
				response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
			}
		} catch (Exception e) {
			logger.warn("get SalesForce data failed!", e);
		}
		return Integer.valueOf(((Map<String, String>) response.getBody().getMsg()).get("shopId"));
	}

	public void setSmtShopInfoURL(String smtShopInfoURL) {
		this.smtShopInfoURL = smtShopInfoURL;
	}

	public void setSmtUserInfoURL(String smtUserInfoURL) {
		this.smtUserInfoURL = smtUserInfoURL;
	}

	public void setSmtShopInfoListURL(String smtShopInfoListURL) {
		this.smtShopInfoListURL = smtShopInfoListURL;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> jsonToMap(JSONObject jsonObj) {
		Map<String, Object> jsonMap = Maps.newHashMap();
		Iterator<String> jsonKeys = jsonObj.keys();
		try {
			while (jsonKeys.hasNext()) {
				String jsonKey = jsonKeys.next();
				Object jsonValObj = jsonObj.get(jsonKey);
				if (jsonValObj instanceof JSONArray) {
					jsonMap.put(jsonKey, jsonToList((JSONArray) jsonValObj));
				} else if (jsonValObj instanceof JSONObject) {
					jsonMap.put(jsonKey, jsonToMap((JSONObject) jsonValObj));
				} else {
					jsonMap.put(jsonKey, jsonValObj);
				}
			}
		} catch (JSONException e) {
			//do nothing
		}
		return jsonMap;
	}

	private List<?> jsonToList(JSONArray jsonArr) {
		List<Object> jsonList = Lists.newArrayList();
		try {
			for (int i = 0; i < jsonArr.length(); i++) {
				Object object = jsonArr.get(i);
				if (object instanceof JSONArray) {
					jsonList.add(jsonToList((JSONArray) object));
				} else if (object instanceof JSONObject) {
					jsonList.add(jsonToMap((JSONObject) object));
				} else {
					jsonList.add(object);
				}
			}
		} catch (JSONException e) {
			//do nothing
		}
		return jsonList;
	}
}
