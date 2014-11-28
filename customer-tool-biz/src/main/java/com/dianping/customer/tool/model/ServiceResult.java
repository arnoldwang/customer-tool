package com.dianping.customer.tool.model;

/**
 * Created by zaza on 14/11/26.
 */
public class ServiceResult {
    public static final Integer INTERNAL_ERROR = 500;
    public static final Integer SERVICE_EXCEPTION = 501;
    public static final int AUTHORITY_ERROR = 401;
    public static final Integer SUCCESS = 200;
    public static final Integer INPUT_ERROR = 402;

    private Integer code = SUCCESS;
    private Object msg;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
