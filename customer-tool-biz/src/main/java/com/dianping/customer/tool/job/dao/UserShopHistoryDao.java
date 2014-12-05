package com.dianping.customer.tool.job.dao;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;
import com.dianping.avatar.dao.annotation.DAOParam;
import com.dianping.customer.tool.entity.UserShopHistory;

/**
 * User: zhenwei.wang
 * Date: 14-12-5
 */
public interface UserShopHistoryDao extends GenericDao {

	@DAOAction(action = DAOActionType.INSERT)
	void addToUserShopHistory(@DAOParam("userShopHistory")UserShopHistory userShopHistory);

}
