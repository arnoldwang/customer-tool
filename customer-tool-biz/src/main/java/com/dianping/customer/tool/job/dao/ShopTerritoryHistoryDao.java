package com.dianping.customer.tool.job.dao;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;
import com.dianping.avatar.dao.annotation.DAOParam;
import com.dianping.customer.tool.entity.ShopTerritoryHistory;

import java.util.Date;
import java.util.List;

/**
 * User: zhenwei.wang
 * Date: 14-12-5
 */
public interface ShopTerritoryHistoryDao extends GenericDao {

	@DAOAction(action = DAOActionType.INSERT)
	void addToShopTerritoryHistory(@DAOParam("shopTerritoryHistoryList")List<ShopTerritoryHistory> shopTerritoryHistoryList);

	@DAOAction(action = DAOActionType.QUERY)
	List<ShopTerritoryHistory> queryShopTerritoryHistoryByCreateTime(@DAOParam("createTime")Date createTime);
}
