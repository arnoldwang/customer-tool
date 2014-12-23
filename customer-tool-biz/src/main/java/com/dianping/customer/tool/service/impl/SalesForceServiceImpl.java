package com.dianping.customer.tool.service.impl;

import com.dianping.customer.tool.model.ServiceResult;
import com.dianping.customer.tool.service.SalesForceService;
import com.dianping.customer.tool.utils.ConfigUtils;
import com.dianping.customer.tool.utils.SalesForceOauthTokenUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
			response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
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
	public List<HashMap<String, Object>> getSalesForceInfoList(int begin, int end, String type){

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

			ResponseEntity<ServiceResult> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);

			if (response.getStatusCode().value() == 401) {
				token = salesForceOauthTokenUtil.getLoginToken();
				headers.set("Authorization", "Bearer " + token);
				response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
			}

		return ((LinkedHashMap<String, ArrayList<HashMap<String, Object>>>) response.getBody().getMsg()).get("shopList");
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
			response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
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
	public int getSfMaxShopId() {
		ResponseEntity<ServiceResult> response = new ResponseEntity<ServiceResult>(HttpStatus.REQUEST_TIMEOUT);
		try {
			HttpHeaders headers = new HttpHeaders();
			if (token == null)
				token = salesForceOauthTokenUtil.getLoginToken();
			headers.set("Authorization", "Bearer " + token);
			Map<String, String> uriVariables = Maps.newHashMap();
			String url = smtShopInfoListURL + "?type=maxShopId";
			response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
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
}
