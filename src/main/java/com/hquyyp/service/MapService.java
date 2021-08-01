package com.hquyyp.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hquyyp.dao.MapEntityMapper;
import com.hquyyp.domain.dto.request.CreateMapRequest;
import com.hquyyp.domain.entity.MapEntity;
import com.hquyyp.domain.query.MapQueryEntity;
import com.hquyyp.thread.MapThread;
import com.hquyyp.utils.OutsiderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


@Service
public class MapService {
    private static final Logger log = Logger.getLogger(com.hquyyp.service.MapService.class.getName());

    @Autowired
    private MapEntityMapper mapEntityMapper;

    private MapThread mapThread;

    @Value("${MAP_DIR}")
    private String MAP_DIR;

    @Value("${LINUX_MAP_DIR}")
    private String LINUX_MAP_DIR;

    @Value("${IN_LINUX}")
    private String IN_LINUX;


    private ExecutorService mapThreadPool = Executors.newSingleThreadExecutor();

    public void addMap(MultipartFile file, CreateMapRequest createMapRequest){
        String mapPath="";
        if ("0".equals(IN_LINUX)){
            //组合文件路径
            //windows环境
             mapPath = MAP_DIR + createMapRequest.getId() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        }else if ("1".equals(IN_LINUX)){
            //linux环境
             mapPath = LINUX_MAP_DIR + createMapRequest.getId() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        }else {
            log.info("IN_LINUX参数未配置！！！");
        }

        File imgFile = new File(mapPath);
        if (!imgFile.getParentFile().exists()) {
            imgFile.getParentFile().mkdirs();
        }
        try {
            //直接将传入file对象的输入流转换成文件
            OutsiderUtil.streamToFile(file.getInputStream(), new FileOutputStream(imgFile));
        }catch (Exception e){
            log.info("【地图管理】图片数据流转存出现错误！");
        }

        createMapRequest.setPath(mapPath);

        this.mapEntityMapper.insertSelective((MapEntity) createMapRequest);
    }

    public void deleteMap(String id) {
        this.mapEntityMapper.deleteByPrimaryKey(id);
    }


    public MapEntity getMap(String id) {
        return this.mapEntityMapper.selectByPrimaryKey(id);
    }

    public ArrayList<MapEntity> listMapByQuery() {
       return  this.mapEntityMapper.listMapByQuery();
    }

    public PageInfo<MapEntity> listMapByQueryPage(MapQueryEntity mapQueryEntity) {
        PageHelper.startPage(mapQueryEntity.getPage().intValue(), mapQueryEntity.getPageSize().intValue());
        return new PageInfo(this.mapEntityMapper.listMapByQueryPage(mapQueryEntity));
    }

    public void initMapThread(String mapId) throws IOException {
        endMapThread();
        MapEntity mapEntity = this.mapEntityMapper.selectByPrimaryKey(mapId);
        this.mapThread = new MapThread(mapEntity);
        this.mapThreadPool.submit((Runnable) this.mapThread);
    }

    public void endMapThread() {
        if (this.mapThread != null) {
            this.mapThread.stop();
        }
    }
}


