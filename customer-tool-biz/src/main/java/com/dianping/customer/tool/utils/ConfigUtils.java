package com.dianping.customer.tool.utils;

import com.dianping.combiz.spring.util.PropertiesLoaderSupportUtils;

/**
 * Created by yangjie on 11/28/14.
 */
public class ConfigUtils {

    public static String getJsPath() {
        return PropertiesLoaderSupportUtils.getProperty("customer-tool-web.js.path");
    }

    public static String getJsVersion() {
        return PropertiesLoaderSupportUtils.getProperty("customer-tool-web.js.version");
    }

	public static Boolean getSyncApolloDataTaskTrigger() {
		return PropertiesLoaderSupportUtils.getBoolProperty("customer-tool-job.syncApolloDataTaskTrigger", true);
	}

	public static String getSyncApolloDataTaskType() {
		return PropertiesLoaderSupportUtils.getProperty("customer-tool-job.syncApolloDataTaskType");
	}

	public static String getSyncApolloDataTaskTerritoryId(){
		return PropertiesLoaderSupportUtils.getProperty("customer-tool-job.syncApolloDataTaskTerritoryId");
	}

	public static int getSyncApolloDataTaskDefaultThreadPage(){
		return PropertiesLoaderSupportUtils.getIntProperty("customer-tool-job.syncApolloDataTask.defualtThreadPage", 5100000);
	}

	public static int getSyncApolloDataTaskDefaultThreadNum() {
		return PropertiesLoaderSupportUtils.getIntProperty("customer-tool-job.syncApolloDataTask.defualtThreadNum", 5);
	}
}
