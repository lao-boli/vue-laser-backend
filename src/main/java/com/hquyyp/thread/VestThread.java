
package com.hquyyp.thread;



import com.hquyyp.domain.entity.SendDataMissionEntity;
import com.hquyyp.domain.entity.VestEntity;
import com.hquyyp.service.SerialportService;
import com.hquyyp.service.VestService;
import com.hquyyp.utils.Uninterruptibles;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class VestThread implements Runnable {
    private static final Logger log = Logger.getLogger(com.hquyyp.thread.VestThread.class.getName());

    private VestService vestService;

    private SerialportService serialPortService;

    private boolean running = true;

    private Boolean padding = Boolean.valueOf(false);

    private VestEntity nowRollVest = null;

    public VestThread(VestService vestService, SerialportService serialPortService) {
        this.vestService = vestService;
        this.serialPortService = serialPortService;
    }

    public synchronized VestEntity getNowRollVest() {
        return this.nowRollVest;
    }


    public synchronized void setNowRollVest(VestEntity nowRollVest) {
        this.nowRollVest = nowRollVest;
    }


    public void run() {
        log.info("【马甲轮询进程】启动");
        while (this.running) {
            if (this.serialPortService.getRollingMissionBlockingQueue().isEmpty()) {
                for (VestEntity vestEntity : this.vestService.listAllVest()) {
                    if (vestEntity.getHp() == 0) {
                        log.info("【马甲轮询进程】" + vestEntity.getNum() + "号马甲阵亡 跳过轮询");
                        continue;
                    }
                    this.serialPortService.submitRollingMission(SendDataMissionEntity.builder().data(buildData(vestEntity)).isWait(false).vestNum(vestEntity.getNum()).build());
                }
            }
            if (!this.running) {
                break;
            }
            Uninterruptibles.sleepUninterruptibly(1L, TimeUnit.SECONDS);
        }
        this.serialPortService.getRollingMissionBlockingQueue().clear();
        log.info("【马甲轮询进程】轮询队列清空");
    }


//    private byte[] refreashRollingData(VestEntity vestEntity) {
//        byte[] rollingData = {2, 0, 0, 0, 0, 100, 0, 30, 104, -81, -6};
//        rollingData[2] = (byte) (vestEntity.getNum() >> 8);
//        rollingData[3] = (byte) (vestEntity.getNum() & 0xFF);
//        rollingData[5] = (byte) vestEntity.getHp();
//        rollingData[6] = (byte) (vestEntity.getAmmo() >> 8);
//        rollingData[7] = (byte) (vestEntity.getAmmo() & 0xFF);
//        rollingData[8] = (byte) (rollingData[0] + rollingData[2] + rollingData[3] + rollingData[4] + rollingData[5] + rollingData[6] + rollingData[7]);
//        return rollingData;
//    }

    public byte[] buildData(VestEntity vestEntity) {
        //byte[] data= {0, 0, 0, 0, 0, 0};
        String data = (("red".equals(vestEntity.getTeam())) ? 1 : 0) + " "
                + vestEntity.getNum() + " "
                + vestEntity.getLng() + " "
                + vestEntity.getLat() + " "
                + vestEntity.getHp() + " "
                + vestEntity.getAmmo();

       /* data[0]=(byte) ( ("red".equals(vestEntity.getTeam())) ? 1 : 0 );
        data[1]=(byte) ( vestEntity.getNum() );
        data[2]=(byte) ( vestEntity.getLng() );
        data[3]=(byte) ( vestEntity.getLat() );
        data[4]=(byte) ( vestEntity.getHp());
        data[5]=(byte) (vestEntity.getAmmo());
        */
        try {
            return data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.info("【串口发送数据】数据转换出错！");
            return null;
        }
    }




    public void stop() {
        log.info("【马甲轮询进程】停止");
        this.running = false;
    }
}