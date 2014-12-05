package com.dianping.customer.tool.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: zhenwei.wang
 * Date: 14-12-5
 */
public class ShopTerritoryHistory implements Serializable {

	private long id;

	private int typeId;

	private Date createTime;

	private int shopId;

	private int territoryId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public int getTerritoryId() {
		return territoryId;
	}

	public void setTerritoryId(int territoryId) {
		this.territoryId = territoryId;
	}
}
