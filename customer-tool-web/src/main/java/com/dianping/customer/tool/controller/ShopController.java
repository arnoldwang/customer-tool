package com.dianping.customer.tool.controller;

import com.dianping.customer.tool.model.ServiceResult;
import com.dianping.customer.tool.model.ShopInfoModel;
import com.dianping.customer.tool.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by zaza on 14/11/26.
 */

@Controller
@RequestMapping("/shops")
public class ShopController {
    @Autowired
    ShopService shopService;

    @RequestMapping(value="/{shopId}",method= RequestMethod.GET)
    @ResponseBody
    public ServiceResult getShopInfo(HttpServletRequest request,@PathVariable("shopId") String shopId) {
        ServiceResult serviceResult = new ServiceResult();
        ShopInfoModel shopInfoModel = shopService.getShopInfo(shopId);
        serviceResult.setMsg(shopInfoModel);
        return serviceResult;
    }
}
