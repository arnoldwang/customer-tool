package com.dianping.customer.tool.service;


import com.dianping.customer.tool.model.ShopInfoModel;

/**
 * Created by zaza on 14/11/26.
 */
public interface ShopService {
    public ShopInfoModel getShopAndUserInfo(String shopId, String userId);

	public ShopInfoModel updateShopTerritoryInfo(String shopId);

	public ShopInfoModel updateUserShopInfo(String shopId);

	public ShopInfoModel updateOrgTerritoryInfo(String userId);
}
