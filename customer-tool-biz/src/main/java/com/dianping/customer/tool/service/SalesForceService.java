package com.dianping.customer.tool.service;

import com.dianping.customer.tool.model.ServiceResult;

import java.util.List;
import java.util.Map;

/**
 * User: zhenwei.wang
 * Date: 14-12-18
 */
public interface SalesForceService {

	public ServiceResult getSfShopInfo(String shopId);

	public List<Map<String, Object>> getSalesForceInfoList(int begin, int end, String type);

	public ServiceResult getSfUserTerritoryInfo(String loginId);

	public int getSfMaxShopId();
}
