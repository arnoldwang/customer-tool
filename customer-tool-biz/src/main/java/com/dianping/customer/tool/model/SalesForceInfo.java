package com.dianping.customer.tool.model;

import java.util.Map;

/**
 * User: zhenwei.wang
 * Date: 14-12-2
 */
public class SalesForceInfo {

	private String shopId;

	private String ownerLoginId;

	private String sfId;

	private Map<String, String> territoryId2Name;

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getOwnerLoginId() {
		return ownerLoginId;
	}

	public void setOwnerLoginId(String ownerLoginId) {
		this.ownerLoginId = ownerLoginId;
	}

	public String getSfId() {
		return sfId;
	}

	public void setSfId(String sfId) {
		this.sfId = sfId;
	}

	public Map<String, String> getTerritoryId2Name() {
		return territoryId2Name;
	}

	public void setTerritoryId2Name(Map<String, String> territoryId2Name) {
		this.territoryId2Name = territoryId2Name;
	}
}
