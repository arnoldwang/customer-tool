package com.dianping.customer.tool.entity;

import com.dianping.crm.ObjectUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * User: zhenwei.wang
 * Date: 14-12-11
 */
public class OrgTerritory implements Serializable {

	private long id;

	private long orgTerritoryID;

	private int orgID;

	/**
	 * Territory id
	 */
	private int territoryID;

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

	public long getOrgTerritoryID() {
		return orgTerritoryID;
	}

	public void setOrgTerritoryID(long orgTerritoryID) {
		this.orgTerritoryID = orgTerritoryID;
	}

	public int getOrgID() {
		return orgID;
	}

	public void setOrgID(int orgID) {
		this.orgID = orgID;
	}

	public int getTerritoryID() {
		return territoryID;
	}

	public void setTerritoryID(int territoryID) {
		this.territoryID = territoryID;
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
