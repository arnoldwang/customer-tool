package com.dianping.customer.tool.dao;

import com.dianping.avatar.dao.GenericDao;
import com.dianping.avatar.dao.annotation.DAOAction;
import com.dianping.avatar.dao.annotation.DAOActionType;

/**
 * Created by yangjie on 11/24/14.
 */
public interface TestDao extends GenericDao {
    @DAOAction(action = DAOActionType.LOAD)
    Integer test();
}
