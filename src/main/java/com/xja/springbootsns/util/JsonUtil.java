package com.xja.springbootsns.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * json工具类
 **/

public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public static String getJsonString(String msg, int code){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", msg);
        jsonObject.put("code", code);
        return jsonObject.toJSONString();
    }

    public static String getJsonString(int code){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        return jsonObject.toJSONString();
    }

}
