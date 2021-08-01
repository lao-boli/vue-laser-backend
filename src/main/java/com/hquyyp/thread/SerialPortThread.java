package com.hquyyp.thread;


import com.hquyyp.domain.entity.SendDataMissionEntity;
import com.hquyyp.service.SerialportService;
import com.hquyyp.utils.Uninterruptibles;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class SerialPortThread implements Runnable {
    private static final Logger log = Logger.getLogger(com.hquyyp.thread.SerialPortThread.class.getName());

    private SerialportService serialPortService;

    private boolean running = true;

    private Lock lock;

    private Condition condition;

    private BlockingQueue<SendDataMissionEntity> rollingMissionBlockingQueue;

    private BlockingQueue<SendDataMissionEntity> highLevelMissionBlockingQueue;

    private SendDataMissionEntity nowSendDataMissionEntity;

    private boolean padding = false;

    private int pollingTimeOut;

    public synchronized boolean isPadding() {
        return this.padding;
    }


    public synchronized void setPadding(boolean padding) {
        System.out.println("setpadding");
        this.padding = padding;
    }

    public SerialPortThread(BlockingQueue<SendDataMissionEntity> rollingMissionBlockingQueue, BlockingQueue<SendDataMissionEntity> highLevelMissionBlockingQueue, SerialportService serialPortService, int pollingTimeOut) {
        this.rollingMissionBlockingQueue = rollingMissionBlockingQueue;
        this.highLevelMissionBlockingQueue = highLevelMissionBlockingQueue;
        this.serialPortService = serialPortService;
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
        this.pollingTimeOut = Math.max(1, pollingTimeOut);
    }
    public synchronized SendDataMissionEntity getNowSendDataMissionEntity() {
        return this.nowSendDataMissionEntity;
    }

    public synchronized void setNowSendDataMissionEntity(SendDataMissionEntity nowSendDataMissionEntity) {
        this.nowSendDataMissionEntity = nowSendDataMissionEntity;
    }

    public void stop() {
        log.info("【串口任务控制进程】终止");
        this.running = false;
    }

    /*线程入口*/
    /**
     * 暂时只做串口控制和数据下发工作
     * 下发内容：highLevelMissionBlockingQueue（高级数据控制：来自controller的各种死亡、受伤等指令）
     *          rollingMissionBlockingQueue（马甲轮询数据，正常的数据更新，来自马甲线程）
     */
    public void run() {
        log.info("【串口任务控制进程】启动");
        while (this.running) {
           // log.info("【串口任务控制进程】运行中");
            /*进程暂停*/
            if (isPadding()) {
                log.info("【串口任务控制进程】收到暂停信号 进程暂停“" + this.pollingTimeOut + "ms");
                Uninterruptibles.sleepUninterruptibly(10000, TimeUnit.MILLISECONDS);
                setPadding(false);
            }

            SendDataMissionEntity sendDataMissionEntity = null;
            try {
                if (!this.highLevelMissionBlockingQueue.isEmpty()) {
                    sendDataMissionEntity = this.highLevelMissionBlockingQueue.take();
                } else {
                    sendDataMissionEntity = this.rollingMissionBlockingQueue.poll(this.pollingTimeOut, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (sendDataMissionEntity == null) {
                continue;
            }
            setNowSendDataMissionEntity(sendDataMissionEntity);
            /*通过串口发送数据*/
            this.serialPortService.sendData(sendDataMissionEntity.getData());
            /*
            * 马甲轮询请求会执行用于提示数据是否返回，高级数据控制跳过
            * */
            if (sendDataMissionEntity.isWait()) {
                try {
                    this.lock.lock();
                    //等待一段时间或者被唤醒（暂时等待时间到即唤醒）
                    boolean isWake = this.condition.await(this.pollingTimeOut, TimeUnit.MILLISECONDS);
                    if (isWake == true) {
                        log.info("【串口控制进程】收到" + sendDataMissionEntity.getVestNum() + "号马甲数据");
                    } else {
                        log.info("【串口控制进程】未收到" + sendDataMissionEntity.getVestNum() + "号马甲数据 超时跳过");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    this.lock.unlock();
                    setNowSendDataMissionEntity(null);
                }
                continue;
            }

            //log.info("【串口控制进程】发送无回复命令，暂停暂停“" + this.pollingTimeOut + "ms");
            Uninterruptibles.sleepUninterruptibly(this.pollingTimeOut, TimeUnit.MILLISECONDS);
        }
    }
}