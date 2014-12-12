package com.dianping.customer.tool.dao;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;
import com.dianping.avatar.dao.annotation.DAOParam;
import com.dianping.customer.tool.entity.OrgTerritory;

import java.util.List;

/**
 * User: zhenwei.wang
 * Date: 14-12-11
 */
public interface OrgTerritoryDao extends GenericDao {
	@DAOAction(action = DAOActionType.QUERY)
	List<OrgTerritory> queryOrgTerritoryByOrgID(@DAOParam("orgId") int orgId);

	@DAOAction(action = DAOActionType.DELETE)
	void deleteOrgTerritoryByOrgID(@DAOParam("orgId") int orgId);

	@DAOAction(action = DAOActionType.INSERT)
	void addToOrgTerritory(@DAOParam("orgTerritory") OrgTerritory orgTerritory);
}
