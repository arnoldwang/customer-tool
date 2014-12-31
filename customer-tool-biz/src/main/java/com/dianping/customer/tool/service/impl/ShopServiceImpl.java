package com.dianping.customer.tool.service.impl;

import com.dianping.ba.base.organizationalstructure.api.user.UserService;
import com.dianping.ba.base.organizationalstructure.api.user.dto.UserDto;
import com.dianping.customer.tool.dao.OrgTerritoryDao;
import com.dianping.customer.tool.dao.ShopTerritoryDao;
import com.dianping.customer.tool.dao.UserShopTerritoryDao;
import com.dianping.customer.tool.entity.OrgTerritory;
import com.dianping.customer.tool.entity.ShopTerritory;
import com.dianping.customer.tool.entity.UserShopTerritory;
import com.dianping.customer.tool.exception.BizException;
import com.dianping.customer.tool.model.ShopInfoModel;
import com.dianping.customer.tool.service.SalesForceService;
import com.dianping.customer.tool.service.ShopService;
import com.dianping.salesbu.api.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zaza on 14/11/26.
 */
@Component
public class ShopServiceImpl implements ShopService {

	@Autowired
	private ShopTerritoryDao shopTerritoryDao;

	@Autowired
	private UserShopTerritoryDao userShopTerritoryDao;

	@Autowired
	private OrgTerritoryDao orgTerritoryDao;

	@Autowired
	private UserService userService;

	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private SalesForceService salesForceService;

	@Override
	@SuppressWarnings("unchecked")
	public ShopInfoModel getShopAndUserInfo(String shopId, String userId) {
		if (!shopId.equals("") && !isNumber(shopId))
			throw new BizException("您输入的ShopId不合法，请输入正确Id!");
		if (!userId.equals("") && !isNumber(userId))
			throw new BizException("您输入的UserId不合法，请输入正确Id!");

		ShopInfoModel shopInfoModel = new ShopInfoModel();

		if (!shopId.equals("")) {
			Map<String, Object> shopMsg = (HashMap<String, Object>) salesForceService.getSfShopInfo(shopId).getMsg();
			if (shopMsg.get("shopId") == null)
				throw new BizException("未找到商户信息，请输入正确Id!");
			List<ShopTerritory> shopTerritoryList = shopTerritoryDao.queryShopTerritoryByNewShopID(Integer.valueOf(shopId));
			List<UserShopTerritory> userShopTerritoryList = userShopTerritoryDao.queryUserShopTerritoryByNewShopID(Integer.valueOf(shopId));
			UserShopTerritory userShopTerritory = new UserShopTerritory();
			for (UserShopTerritory ust : userShopTerritoryList) {
				if (userGroupService.getBUNamebyLogin(ust.getUserID()).contains("交易平台"))
					userShopTerritory = ust;
				if (userService.queryUserByLoginID(ust.getUserID()).getRealName().contains("销售公海"))
					userShopTerritory = ust;
			}
			UserDto user = userService.queryUserByLoginID(userShopTerritory.getUserID());

			shopInfoModel.setShopId(shopId);
			shopInfoModel.setShopName((String) shopMsg.get("name"));
			shopInfoModel.setCity((String) shopMsg.get("city"));
			shopInfoModel.setDistrict((String) shopMsg.get("district"));
			shopInfoModel.setMainCategory((String) shopMsg.get("mainCategory"));

			shopInfoModel.setJhBU(shopMsg.get("isJHBU").equals("true"));
			shopInfoModel.setHotelBU(shopMsg.get("isJDLY").equals("true"));
			shopInfoModel.setVip(shopMsg.get("type").equals("大客户"));//普通客户，大客户
			shopInfoModel.setSfOwner((String) shopMsg.get("ownerName"));
			shopInfoModel.setSfLoginId((String) shopMsg.get("ownerLoginId"));
			shopInfoModel.setApolloOwner(user.getRealName());
			shopInfoModel.setApolloLoginId(String.valueOf(user.getLoginId()));
			shopInfoModel.setSfTerritoryId2Name((LinkedHashMap<String, String>) shopMsg.get("territoryId2Name"));
			List<String> apolloTerritoryIds = new ArrayList<String>();
			for (ShopTerritory st : shopTerritoryList)
				apolloTerritoryIds.add(String.valueOf(st.getTerritoryID()));
			shopInfoModel.setApolloTerritoryIds(apolloTerritoryIds);
		}

		if(!userId.equals("")){
			Map<String, Object> userMsg = (HashMap<String, Object>) salesForceService.getSfUserTerritoryInfo(userId).getMsg();
			if(userMsg.get("userId") == null)
				throw new BizException("未找到用户信息，请输入正确Id!");
			shopInfoModel.setUserId((String)userMsg.get("userId"));
			List<OrgTerritory> orgTerritoryList = orgTerritoryDao.queryOrgTerritoryByOrgID(Integer.valueOf(userId));
			if(orgTerritoryList.size() != 0)
				shopInfoModel.setApolloTerritoryId(String.valueOf(orgTerritoryList.get(0).getTerritoryID()));
			shopInfoModel.setSfTerritoryId((String)userMsg.get("sfTerritoryId"));
			shopInfoModel.setSfTerritoryName((String)userMsg.get("sfTerritoryName"));
		}
		return shopInfoModel;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ShopInfoModel updateShopTerritoryInfo(String shopId) {
		ShopInfoModel shopInfoModel = new ShopInfoModel();
		Map<String, Object> msg = (HashMap<String, Object>) salesForceService.getSfShopInfo(shopId).getMsg();
		shopTerritoryDao.deleteShopTerritoryByNewShopID(Integer.valueOf(shopId));
		ShopTerritory shopTerritory = new ShopTerritory();
		Map<String, String> territoryId2Name = (LinkedHashMap<String, String>) msg.get("territoryId2Name");
		Iterator<String> iter = territoryId2Name.keySet().iterator();
		while (iter.hasNext()) {
			String territoryID = iter.next();
			shopTerritory.setExternalID(msg.get("sfId") + "-" + territoryID);
			shopTerritory.setNewShopID(Integer.valueOf(shopId));
			shopTerritory.setTerritoryID(Integer.valueOf(territoryID));
			shopTerritory.setStatus(1);
			shopTerritory.setApproveStatus(1);
			shopTerritoryDao.addToShopTerritory(shopTerritory);
		}

		List<ShopTerritory> shopTerritoryList = shopTerritoryDao.queryShopTerritoryByNewShopID(Integer.valueOf(shopId));
		List<String> apolloTerritoryIds = new ArrayList<String>();
		for (ShopTerritory st : shopTerritoryList)
			apolloTerritoryIds.add(String.valueOf(st.getTerritoryID()));
		shopInfoModel.setApolloTerritoryIds(apolloTerritoryIds);

		return shopInfoModel;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ShopInfoModel updateUserShopInfo(String shopId) {
		ShopInfoModel shopInfoModel = new ShopInfoModel();
		Map<String, Object> msg = (HashMap<String, Object>) salesForceService.getSfShopInfo(shopId).getMsg();
		userShopTerritoryDao.deleteUserShopTerritoryByNewShopID(Integer.valueOf(shopId));
		UserShopTerritory userShopTerritory = new UserShopTerritory();
		userShopTerritory.setUserID(Integer.valueOf((String) msg.get("ownerLoginId")));
		userShopTerritory.setNewShopID(Integer.valueOf(shopId));
		userShopTerritory.setStatus(1);
		userShopTerritory.setApproveStatus(1);
		userShopTerritoryDao.addToUserShopTerritory(userShopTerritory);

		List<UserShopTerritory> userShopTerritoryList = userShopTerritoryDao.queryUserShopTerritoryByNewShopID(Integer.valueOf(shopId));
		for (UserShopTerritory ust : userShopTerritoryList) {
			if (userGroupService.getBUNamebyLogin(ust.getUserID()).contains("交易平台"))
				userShopTerritory = ust;
		}
		UserDto user = userService.queryUserByLoginID(userShopTerritory.getUserID());
		shopInfoModel.setApolloOwner(user.getRealName());
		return shopInfoModel;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ShopInfoModel updateOrgTerritoryInfo(String userId) {
		ShopInfoModel shopInfoModel = new ShopInfoModel();
		Map<String, Object> userMsg = (HashMap<String, Object>) salesForceService.getSfUserTerritoryInfo(userId).getMsg();
		orgTerritoryDao.deleteOrgTerritoryByOrgID(Integer.valueOf(userId));
		OrgTerritory orgTerritory = new OrgTerritory();
		orgTerritory.setOrgID(Integer.valueOf(userId));
		orgTerritory.setTerritoryID(Integer.valueOf((String)userMsg.get("sfTerritoryId")));
		orgTerritory.setStatus(1);
		orgTerritory.setApproveStatus(1);
		orgTerritoryDao.addToOrgTerritory(orgTerritory);

		List<OrgTerritory> orgTerritoryList = orgTerritoryDao.queryOrgTerritoryByOrgID(Integer.valueOf(userId));
		if(orgTerritoryList != null && orgTerritoryList.size() != 0)
			shopInfoModel.setApolloTerritoryId(String.valueOf(orgTerritoryList.get(0).getTerritoryID()));
		return shopInfoModel;
	}


	private boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("-?[0-9]+");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
}
