package com.dianping.customer.tool.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * User: zhenwei.wang
 * Date: 14-11-26
 * Time: 上午11:41
 */
public class ShopTerritory implements Serializable {

	private long id;

	private long shopTerritoryID;

	/**
	 * 本系统使用的ShopID
	 */
	private int newShopID;

	/**
	 * Territory id
	 */
	private int territoryID;

	/**
	 * 商户等级
	 */
	private String kabc;

	/**
	 * 团购上线时间
	 */
	private Date tgReleaseDate;

	/**
	 * 团购下线时间
	 */
	private Date tgOfflineDate;

	/**
	 * 商户合作状态
	 */
	private int cooperationStatus;

	/**
	 * 1正常，0删除
	 */
	private int status;

	/**
	 * 创建时间
	 */
	private Date createdTime;

	/**
	 * 创建人
	 */
	private int createdBy;

	/**
	 * 最后更新时间
	 */
	private Date lastModifiedTime;

	/**
	 * 最后更新人
	 */
	private int lastModifiedBy;

	private int approveID;

	private int approveBy;

	private int approveStatus;

	/**
	 * 审核意见
	 */
	private String approveComment;

	private String externalID;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getShopTerritoryID() {
		return shopTerritoryID;
	}

	public void setShopTerritoryID(long shopTerritoryID) {
		this.shopTerritoryID = shopTerritoryID;
	}

	public int getNewShopID() {
		return newShopID;
	}

	public void setNewShopID(int newShopID) {
		this.newShopID = newShopID;
	}

	public int getTerritoryID() {
		return territoryID;
	}

	public void setTerritoryID(int territoryID) {
		this.territoryID = territoryID;
	}

	public String getKabc() {
		return kabc;
	}

	public void setKabc(String kabc) {
		this.kabc = kabc;
	}

	public Date getTgReleaseDate() {
		return tgReleaseDate;
	}

	public void setTgReleaseDate(Date tgReleaseDate) {
		this.tgReleaseDate = tgReleaseDate;
	}

	public Date getTgOfflineDate() {
		return tgOfflineDate;
	}

	public void setTgOfflineDate(Date tgOfflineDate) {
		this.tgOfflineDate = tgOfflineDate;
	}

	public int getCooperationStatus() {
		return cooperationStatus;
	}

	public void setCooperationStatus(int cooperationStatus) {
		this.cooperationStatus = cooperationStatus;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public int getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(int lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public int getApproveID() {
		return approveID;
	}

	public void setApproveID(int approveID) {
		this.approveID = approveID;
	}

	public int getApproveBy() {
		return approveBy;
	}

	public void setApproveBy(int approveBy) {
		this.approveBy = approveBy;
	}

	public int getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(int approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getApproveComment() {
		return approveComment;
	}

	public void setApproveComment(String approveComment) {
		this.approveComment = approveComment;
	}

	public String getExternalID() {
		return externalID;
	}

	public void setExternalID(String externalID) {
		this.externalID = externalID;
	}
}
