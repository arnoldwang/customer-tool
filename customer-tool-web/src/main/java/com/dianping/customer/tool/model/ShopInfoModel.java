package com.dianping.customer.tool.model;

import java.util.List;
import java.util.Map;

/**
 * Created by zaza on 14/11/26.
 */
public class ShopInfoModel {
    String shopId;
    String shopName;
    String city;
    String district;//行政区
    String mainCategory;//主分类
    boolean jhBU;//是否归属结婚BU
    boolean hotelBU;//是否归属酒店旅游
    boolean vip;//是否大客户
    String sfOwner;
    String sfLoginId;
    String apolloOwner;
    String apolloLoginId;
    Map<String,String> sfTerritoryId2Name;
    List<String> apolloTerritoryIds;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public boolean isJhBU() {
        return jhBU;
    }

    public void setJhBU(boolean jhBU) {
        this.jhBU = jhBU;
    }

    public boolean isHotelBU() {
        return hotelBU;
    }

    public void setHotelBU(boolean hotelBU) {
        this.hotelBU = hotelBU;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public String getSfOwner() {
        return sfOwner;
    }

    public void setSfOwner(String sfOwner) {
        this.sfOwner = sfOwner;
    }

    public String getSfLoginId() {
        return sfLoginId;
    }

    public void setSfLoginId(String sfLoginId) {
        this.sfLoginId = sfLoginId;
    }

    public String getApolloOwner() {
        return apolloOwner;
    }

    public void setApolloOwner(String apolloOwner) {
        this.apolloOwner = apolloOwner;
    }

    public String getApolloLoginId() {
        return apolloLoginId;
    }

    public void setApolloLoginId(String apolloLoginId) {
        this.apolloLoginId = apolloLoginId;
    }

    public Map<String, String> getSfTerritoryId2Name() {
        return sfTerritoryId2Name;
    }

    public void setSfTerritoryId2Name(Map<String, String> sfTerritoryId2Name) {
        this.sfTerritoryId2Name = sfTerritoryId2Name;
    }

    public List<String> getApolloTerritoryIds() {
        return apolloTerritoryIds;
    }

    public void setApolloTerritoryIds(List<String> apolloTerritoryIds) {
        this.apolloTerritoryIds = apolloTerritoryIds;
    }
}
