package com.dianping.customer.tool.service;


import com.dianping.customer.tool.model.ShopInfoModel;

/**
 * Created by zaza on 14/11/26.
 */
public interface ShopService {
    public ShopInfoModel getShopInfo(String shopId);

	public ShopInfoModel updateShopInfo(String shopId);

	public ShopInfoModel updateUserShopInfo(String shopId);
}
