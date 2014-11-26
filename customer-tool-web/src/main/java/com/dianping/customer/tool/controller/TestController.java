package com.dianping.customer.tool.controller;

import com.dianping.customer.tool.dao.ShopTerritoryDao;
import com.dianping.customer.tool.dao.UserShopTerritoryDao;
import com.dianping.customer.tool.entity.ShopTerritory;
import com.dianping.customer.tool.entity.UserShopTerritory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/shops")
public class TestController {

	@Autowired
	private ShopTerritoryDao shopTerritoryDao;

	@Autowired
	private UserShopTerritoryDao userShopTerritoryDao;

	@RequestMapping("/test")
	@ResponseBody
	public String simple() {
		String result = "";
		List<UserShopTerritory> userShopTerritoryList = userShopTerritoryDao.queryUserShopTerritoryByNewShopID(4274039);
		result += userShopTerritoryList.get(0).getUserID() + " ";
		List<ShopTerritory> shopTerritoryList = shopTerritoryDao.queryShopTerritoryByNewShopID(4274039);
		result += shopTerritoryList.get(0).getTerritoryID() + " ";
		return result;
	}
}
