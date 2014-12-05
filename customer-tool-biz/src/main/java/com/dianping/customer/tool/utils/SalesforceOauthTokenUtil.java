package com.dianping.customer.tool.utils;

import com.dianping.customer.tool.entity.SalesforceOauthToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by zaza on 14/11/26.
 */
public class SalesForceOauthTokenUtil {
	private String tokenURL;
	private String clientId;
	private String clientSecret;
	private String grantType;
	private String username;
	private String password;
	private RestTemplate restTemplate;

	private static final Logger log = LoggerFactory.getLogger(SalesForceOauthTokenUtil.class);

	public String getTokenURL() {
		return tokenURL;
	}

	public void setTokenURL(String tokenURL) {
		this.tokenURL = tokenURL;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getGrantType() {
		return grantType;
	}

	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public String getLoginToken() {
		String token;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> content = new LinkedMultiValueMap<String, String>();
			content.set("grant_type", grantType);
			content.set("client_id", clientId);
			content.set("client_secret", clientSecret);
			content.set("username", username);
			content.set("password", password);
			HttpEntity httpEntity = new HttpEntity(content, headers);
			ResponseEntity<SalesforceOauthToken> response = restTemplate.exchange(tokenURL, HttpMethod.POST, httpEntity, SalesforceOauthToken.class);
			token = response.getBody().getAccess_token();
		} catch (Exception ex) {
			log.error("Get Salesforce Oauth Token Failed:" + ex.getMessage());
			return null;
		}
		return token;
	}
}
