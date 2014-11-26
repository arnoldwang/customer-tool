package com.dianping.customer.tool.model;

/**
 * User: Administrator
 * Date: 14-11-26
 * Time: 上午10:44
 */
public class SalesforceOauthToken {
	private String id;
	private String issued_at;
	private String instance_url;
	private String token_type;
	private String signature;
	private String access_token;

	public SalesforceOauthToken() {
		super();

	}

	public SalesforceOauthToken(String id, String issued_at,
								String instance_url, String token_type, String signature, String access_token) {
		super();
		this.id = id;
		this.issued_at = issued_at;
		this.instance_url = instance_url;
		this.token_type = token_type;
		this.signature = signature;
		this.access_token = access_token;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIssued_at() {
		return issued_at;
	}

	public void setIssued_at(String issued_at) {
		this.issued_at = issued_at;
	}

	public String getInstance_url() {
		return instance_url;
	}

	public void setInstance_url(String instance_url) {
		this.instance_url = instance_url;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getToken_type() {
		return this.token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

}
