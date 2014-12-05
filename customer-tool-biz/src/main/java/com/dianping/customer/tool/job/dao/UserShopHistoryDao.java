package com.dianping.customer.tool.job.dao;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;
import com.dianping.avatar.dao.annotation.DAOParam;
import com.dianping.customer.tool.entity.UserShopHistory;

import java.util.Date;
import java.util.List;

/**
 * User: zhenwei.wang
 * Date: 14-12-5
 */
public interface UserShopHistoryDao extends GenericDao {

	@DAOAction(action = DAOActionType.INSERT)
	void addToUserShopHistory(@DAOParam("userShopHistoryList")List<UserShopHistory> userShopHistoryList);

	@DAOAction(action = DAOActionType.QUERY)
	List<UserShopHistory> queryUserShopHistoryByCreateTime(@DAOParam("createTime") Date createTime);
}
