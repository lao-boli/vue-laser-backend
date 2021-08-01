package com.hquyyp.domain.dto;


import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;



public class BaseRestfulEntity implements Serializable {
    public String toJsonString() {
        return JSONObject.toJSONString(this);
    }
}


