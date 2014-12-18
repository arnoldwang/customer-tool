package com.dianping.customer.tool.service;

import com.dianping.customer.tool.model.ServiceResult;

import java.util.HashMap;
import java.util.List;

/**
 * User: zhenwei.wang
 * Date: 14-12-18
 */
public interface SalesForceService {

	public ServiceResult getSfShopInfo(String shopId);

	public List<HashMap<String, Object>> getSalesForceInfoList(int begin, int end, String type);

	public ServiceResult getSfUserTerritoryInfo(String loginId);
}
