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
}
