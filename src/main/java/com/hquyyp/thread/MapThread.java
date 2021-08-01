package com.hquyyp.thread;


import com.hquyyp.domain.entity.MapEntity;
import com.hquyyp.service.VestService;
import com.hquyyp.utils.Uninterruptibles;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Logger;

/*该进程用于绘制各点坐标，马甲位置等信息*/
public class MapThread implements Runnable {
    private static final Logger log = Logger.getLogger(com.hquyyp.thread.MapThread.class.getName());

    private boolean running = true;
    private MapEntity mapEntity;
    private VestService vestService;
    private String originMapImgBase64;
    private int imgWidth;
    private int imgHeight;
    private String mapImgBase64;

    public MapThread(MapEntity mapEntity) throws IOException {

        //this.vestService = vestService;

        this.mapEntity = mapEntity;

        byte[] data = Files.readAllBytes(Paths.get(mapEntity.getPath(), new String[0]));

        this.originMapImgBase64 = new String(Base64.getEncoder().encode(data));

        InputStream inputStream = new ByteArrayInputStream(data);

        Image src = ImageIO.read(inputStream);

        this.imgWidth = src.getWidth(null);

        this.imgHeight = src.getHeight(null);
    }

    public void stop() {
        log.info("【地图管理进程】停止");
        this.running = false;
    }

    public String getMapImgBase64() {
        return this.mapImgBase64;
    }

    public void run() {
        log.info("【地图管理进程】启动");
        double width = this.mapEntity.getRightDownLng().doubleValue() - this.mapEntity.getLeftTopLng().doubleValue();
        double highth = this.mapEntity.getLeftTopLat().doubleValue() - this.mapEntity.getRightDownLat().doubleValue();
        while (this.running) {

            //地图数据处理逻辑

        }
    }
}