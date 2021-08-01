package com.hquyyp.websocket;

import javax.websocket.DecodeException;
import javax.websocket.EndpointConfig;


public class Decoder implements javax.websocket.Decoder.Text<Object> {

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(EndpointConfig arg0) {
        // TODO Auto-generated method stub
    }

//    @Override
//    public Object decode(String equip)  {
//        return JSON.parseObject(equip, Object.class);
//    }

    @Override
    public Object decode(String s) throws DecodeException {
        System.out.println("传进来的数据：" + s);
        return (Object) s;
    }

    @Override
    public boolean willDecode(String arg0) {
        return true;
    }

}

