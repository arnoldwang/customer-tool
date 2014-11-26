package com.dianping.customer.tool.service.impl;

import com.dianping.customer.tool.model.ServiceResult;
import com.dianping.customer.tool.model.ShopInfoModel;
import com.dianping.customer.tool.service.ShopService;
import com.dianping.customer.tool.utils.SalesforceOauthTokenUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by zaza on 14/11/26.
 */
public class ShopServiceImpl implements ShopService {
    @Autowired
    private SalesforceOauthTokenUtil salesforceOauthTokenUtil;
    @Autowired
    private RestTemplate restTemplate;

    private String smtShopInfoURL;

    @Override
    public ShopInfoModel getShopInfo(String shopId){
        ShopInfoModel shopInfoModel = new ShopInfoModel();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + salesforceOauthTokenUtil.getLoginToken());
        Map<String,String> uriVariables = Maps.newHashMap();
        uriVariables.put("shopId",shopId);
        String url = getRESTUrl(smtShopInfoURL);
        ResponseEntity<ServiceResult> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<byte[]>(headers), ServiceResult.class, uriVariables);
        ServiceResult result = response.getBody();
        //todo
        return shopInfoModel;
    }

    private String getRESTUrl(String hostUrl){
        hostUrl +="?shopId={shopId}";
        return hostUrl;
    }

    public void setSmtShopInfoURL(String smtShopInfoURL){
        this.smtShopInfoURL = smtShopInfoURL;
    }
}
