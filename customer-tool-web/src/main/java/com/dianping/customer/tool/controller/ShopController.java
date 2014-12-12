package com.dianping.customer.tool.controller;

import com.dianping.customer.tool.model.ShopInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zaza on 14/11/26.
 */

@Controller
public class ShopController {
	@Autowired
	com.dianping.customer.tool.service.ShopService shopService;

	@RequestMapping(value = "/shopAndUserInfo", method = RequestMethod.GET)
	@ResponseBody
	public ShopInfoModel getShopInfo(HttpServletRequest request, @RequestParam("shopId") String shopId, @RequestParam("userId") String userId) {
		ShopInfoModel shopInfoModel = shopService.getShopAndUserInfo(shopId, userId);
		return shopInfoModel;
	}


	@RequestMapping(value = "/syncShopTerritory", method = RequestMethod.POST)
	@ResponseBody
	public ShopInfoModel updateShopTerritoryInfo(HttpServletRequest request, @RequestParam("shopId") String shopId) {
		ShopInfoModel shopInfoModel = shopService.updateShopTerritoryInfo(shopId);
		return shopInfoModel;
	}

	@RequestMapping(value = "/syncUserShop", method = RequestMethod.POST)
	@ResponseBody
	public ShopInfoModel updateUserShopInfo(HttpServletRequest request, @RequestParam("shopId") String shopId) {
		ShopInfoModel shopInfoModel = shopService.updateUserShopInfo(shopId);
		return shopInfoModel;
	}

	@RequestMapping(value = "/syncOrgTerritory", method = RequestMethod.POST)
	@ResponseBody
	public ShopInfoModel updateOrgTerritoryInfo(HttpServletRequest request, @RequestParam("userId")String userId) {
		ShopInfoModel shopInfoModel = shopService.updateOrgTerritoryInfo(userId);
		return shopInfoModel;
	}
}
