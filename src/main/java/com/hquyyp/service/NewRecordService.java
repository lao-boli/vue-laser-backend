package com.hquyyp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hquyyp.dao.NewRecordEntityMapper;
import com.hquyyp.domain.dto.response.BaseResponse;
import com.hquyyp.domain.entity.NewBattleEntity;
import com.hquyyp.domain.entity.NewRecordEntity;
import com.hquyyp.domain.model.NewRecordDaoModel;
import com.hquyyp.domain.po.NewRecord;
import com.hquyyp.domain.query.RecordQueryEntity;
import com.hquyyp.domain.vo.NewRecordView;
import com.hquyyp.utils.OutsiderUtil;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

@Service
public class NewRecordService {
    private static final Logger log=Logger.getLogger(NewRecordService.class.getName());

    @Autowired
    private NewRecordEntityMapper recordEntityMapper;

    protected NewBattleEntity newBattleEntityRecord;

    protected List<NewRecordEntity> recordData;

    // 存放文件的临时目录
    @Value("${RECORD_DIR}")
    private String RECORD_DIR;

    @Value("${LINUX_RECORD_DIR}")
    private String LINUX_RECORD_DIR;

    @Value("${IN_LINUX}")
    private String IN_LINUX;


    // 文件MD5的缓存容器
    private static final ConcurrentMap<String, File> MD5_CACHE = new ConcurrentHashMap<>();

    public BaseResponse newChunkUpload(String VideoName, String md5, Long size, Integer chunks, Integer chunk, MultipartFile multipartFile){
        if (this.newBattleEntityRecord==null){
            log.info("对局还未结束!");
            return BaseResponse.builder()
                    .code(200)
                    .status("ERROR")
                    .msg("对局还未结束!")
                    .build();
        }
        // 是否生成了文件？？？
        File targetFile = MD5_CACHE.get(md5);
        if (targetFile == null) {
            // 没有生成的话就生成一个新的文件

            if ("0".equals(IN_LINUX)){
                //组合文件路径
                //windows环境下
                targetFile = new File(RECORD_DIR, UUID.randomUUID().toString()+ "." + FilenameUtils.getExtension(VideoName));

            }else if ("1".equals(IN_LINUX)){
                //LINUX环境下
                targetFile = new File(LINUX_RECORD_DIR, UUID.randomUUID().toString()+ "." + FilenameUtils.getExtension(VideoName));

            }else {
                log.info("IN_LINUX参数未配置！！！");
            }
            targetFile.getParentFile().mkdirs();
            MD5_CACHE.put(md5, targetFile);
        }
        boolean finished = chunk == chunks;//是否最后一片
        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(targetFile, "rw");
            long length = accessFile.length();
            accessFile.seek(length);
            // 写入分片的数据
            System.out.println("multipartfile大小"+multipartFile.getSize());
            accessFile.write(multipartFile.getBytes());
            accessFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (finished){
            MD5_CACHE.remove(md5);
            NewRecord recordEntity=NewRecord.builder()
                    .id(OutsiderUtil.getUUID())
                    .battleId(this.newBattleEntityRecord.getId())
                    .path(targetFile.getPath()).time(new Date())
                    .recordData(JSON.toJSONString(recordData))
                    .build();
            this.newBattleEntityRecord=null;
            this.recordData=null;
            this.recordEntityMapper.insertSelective(recordEntity);
            log.info("【视频上传】上传完成");
            return BaseResponse.builder()
                    .code(200).status("SUCCESS").msg("uploadend")
                    .build();
        }
        log.info("【视频上传】还未结束还有"+(chunks-chunk)+"次");
        return null;
    }


    public void play(String id,HttpServletRequest request, HttpServletResponse response) {
        response.reset();
        NewRecordDaoModel recordEntity = this.recordEntityMapper.selectByPrimaryKey(id);
        File file = new File(recordEntity.getPath());
        long fileLength = file.length();
        // 随机读文件
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //获取从那个字节开始读取文件
        String rangeString = request.getHeader("Range");
        long range=0;
        if (StringUtils.isNotBlank(rangeString)) {
            range = Long.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
        }
        //System.out.println(range);
        //获取响应的输出流
        int len=0;
        try {
        OutputStream outputStream = response.getOutputStream();
        //设置内容类型
        response.setHeader("Content-Type", "video/mp4");
        //返回码需要为206，代表只处理了部分请求，响应了部分数据
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        // 移动访问指针到指定位置
        randomAccessFile.seek(range);
        // 每次请求只返回1MB的视频流
        byte[] bytes = new byte[1024*1024];
        len = randomAccessFile.read(bytes);
        //设置此次相应返回的数据长度
        response.setContentLength(len);
        //设置此次相应返回的数据范围
        response.setHeader("Content-Range", "bytes "+range+"-"+(fileLength-1)+"/"+fileLength);
        // 将这1MB的视频流响应给客户端
        outputStream.write(bytes, 0, len);
        outputStream.close();
        randomAccessFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("返回数据区间:【"+range+"-"+(range+len)+"】");
    }

    public PageInfo<NewRecordView> listRecordQueryByPage(RecordQueryEntity queryEntity){
       //List<NewRecordDaoModel> newRecordDaoModels = this.recordEntityMapper.listRecordByQueryPage(queryEntity);

        PageHelper.startPage(queryEntity.getPage().intValue(),queryEntity.getPageSize().intValue());
        PageInfo pageInfo = new PageInfo(this.recordEntityMapper.listRecordByQueryPage(queryEntity));
        List<NewRecordDaoModel> list = pageInfo.getList();
        List<NewRecordView> newRecordViews=new ArrayList<>();
        for (NewRecordDaoModel nrdm:list){
            NewRecordView newRecordView=new NewRecordView();
            newRecordView.setId(nrdm.getId());
            newRecordView.setName(nrdm.getName());
            newRecordView.setTime(nrdm.getTime());
            newRecordView.setBeginTime(nrdm.getBeginTime());
            newRecordView.setEndTime(nrdm.getEndTime());
            newRecordView.setPath(nrdm.getPath());
            newRecordView.setBattleId(nrdm.getBattleId());
            newRecordView.setNewRecordEntityList(JSONObject.parseArray(nrdm.getRecordData(), NewRecordEntity.class));
            newRecordViews.add(newRecordView);
        }
        pageInfo.setList(newRecordViews);
        return pageInfo;
    }

    public List<NewRecordEntity> getRecordData(){
        return this.recordData;
    }
}
