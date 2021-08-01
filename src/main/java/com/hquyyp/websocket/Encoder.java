package com.hquyyp.websocket;

import com.alibaba.fastjson.JSON;

import javax.websocket.EndpointConfig;




public class Encoder implements javax.websocket.Encoder.Text<Object> {

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(EndpointConfig arg0) {
        // TODO Auto-generated method stub

    }



    @Override
    public String encode(Object o)  {
        return JSON.toJSONString(o);
    }


}
