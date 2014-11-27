package com.dianping.customer.tool.dao;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;
import com.dianping.avatar.dao.annotation.DAOParam;
import com.dianping.customer.tool.entity.ShopTerritory;

import java.util.List;

/**
 * User: zhenwei.wang
 * Date: 14-11-26
 * Time: 下午1:25
 */
public interface ShopTerritoryDao extends GenericDao {
	@DAOAction(action = DAOActionType.QUERY)
	List<ShopTerritory> queryShopTerritoryByNewShopID(@DAOParam("newShopID") int newShopID);

	@DAOAction(action = DAOActionType.INSERT)
	void addToShopTerritory(@DAOParam("shopTerritory") ShopTerritory shopTerritory);

	@DAOAction(action = DAOActionType.DELETE)
	void deleteShopTerritoryByNewShopID(@DAOParam("newShopID") int newShopID);
}
