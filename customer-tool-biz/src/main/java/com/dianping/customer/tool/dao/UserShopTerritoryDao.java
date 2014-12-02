package com.dianping.customer.tool.dao;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;
import com.dianping.avatar.dao.annotation.DAOParam;
import com.dianping.customer.tool.entity.UserShopTerritory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: zhenwei.wang
 * Date: 14-11-26
 * Time: 下午2:25
 */
public interface UserShopTerritoryDao extends GenericDao {

	@DAOAction(action = DAOActionType.QUERY)
	List<UserShopTerritory> queryUserShopTerritoryByNewShopID(@DAOParam("newShopID")int newShopID);

	@DAOAction(action = DAOActionType.INSERT)
	void addToUserShopTerritory(@DAOParam("userShopTerritory") UserShopTerritory userShopTerritory);

	@DAOAction(action = DAOActionType.DELETE)
	void deleteUserShopTerritoryByNewShopID(@DAOParam("newShopID") int newShopID);

	/**
	 * 按shopId批量查询
	 * @param newShopIDList
	 * @return
	 */
	@DAOAction(action = DAOActionType.QUERY)
	List<UserShopTerritory> queryUserShopTerritoryByNewShopIDList(@DAOParam("newShopIDList") List<String> newShopIDList);

	/**
	 * 按userShop对批量删除
	 * @param userShopList
	 */
	@DAOAction(action = DAOActionType.DELETE)
	void deleteUserShopTerritoryByUserShopList(@DAOParam("userShopList") List<UserShopTerritory> userShopList);

	/**
	 * 批量插入userShopTerritory
	 * @param userShopTerritoryList
	 */
	@DAOAction(action = DAOActionType.INSERT)
	void addToUserShopTerritoryByUserShopTerritoryList(@DAOParam("userShopTerritoryList") List<UserShopTerritory> userShopTerritoryList);
}
