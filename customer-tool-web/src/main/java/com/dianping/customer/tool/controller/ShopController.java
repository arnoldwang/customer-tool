package com.dianping.customer.tool.controller;

import com.dianping.customer.tool.model.ServiceResult;
import com.dianping.customer.tool.model.ShopInfoModel;
import com.dianping.customer.tool.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zaza on 14/11/26.
 */

@Controller
@RequestMapping("/shops")
public class ShopController {
    @Autowired
    ShopService shopService;

    @RequestMapping(method= RequestMethod.GET)
    @ResponseBody
    public ServiceResult getShopInfo(HttpServletRequest request,@RequestParam("shopId") String shopId) {
        ServiceResult serviceResult = new ServiceResult();
        ShopInfoModel shopInfoModel = shopService.getShopInfo(shopId);
		serviceResult.setMsg(shopInfoModel);
        return serviceResult;
    }

	@RequestMapping(value="/territory",method= RequestMethod.PUT)
	@ResponseBody
	public ServiceResult updateShopInfo(HttpServletRequest request,@RequestParam("shopId") String shopId) {
		ServiceResult serviceResult = new ServiceResult();
		shopService.updateShopInfo(shopId);
		ShopInfoModel shopInfoModel = shopService.getShopInfo(shopId);
		serviceResult.setMsg(shopInfoModel);
		return serviceResult;
	}

	@RequestMapping(value="/userShop",method= RequestMethod.PUT)
	@ResponseBody
	public ServiceResult updateUserShopInfo(HttpServletRequest request,@RequestParam("shopId") String shopId) {
		ServiceResult serviceResult = new ServiceResult();
		shopService.updateUserShopInfo(shopId);
		ShopInfoModel shopInfoModel = shopService.getShopInfo(shopId);
		serviceResult.setMsg(shopInfoModel);
		return serviceResult;
	}
}
