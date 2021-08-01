package com.hquyyp.websocket;


import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/shootwebsocket", encoders = {Encoder.class},decoders = {Decoder.class})
@Component
public class ShootWebSocket {


    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    //private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<ShootWebSocket> webSocketSet = new CopyOnWriteArraySet<ShootWebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid
    private String sid = "";


    /**
     * 连接建立成功调用的方法
     *
     * @param session
     * @param
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        System.out.println("连接成功,当前连接总数" + webSocketSet.size());
    }

    /**
     * 连接断开调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        System.out.println("有连接断开,当前连接总数" + webSocketSet.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("客户端发送：" + message);
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) {
        for (ShootWebSocket webSocket : webSocketSet) {
            try {
                webSocket.session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(Object o) {
        for (ShootWebSocket webSocket : webSocketSet) {
            try {
                webSocket.session.getBasicRemote().sendObject(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}