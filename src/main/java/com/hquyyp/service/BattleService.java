package com.hquyyp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hquyyp.MqttInterface.MqMessager;
import com.hquyyp.dao.BattleEntityMapper;
import com.hquyyp.dao.BattleSettingEntityMapper;
import com.hquyyp.dao.NewBattleEntityMapper;
import com.hquyyp.dao.NewBattleSettingMapper;
import com.hquyyp.domain.dto.request.CreateBattleSettingRequest;
import com.hquyyp.domain.dto.request.CreateNewBattleSettingRequest;
import com.hquyyp.domain.entity.*;
import com.hquyyp.domain.model.BattleSettingViewModel;
import com.hquyyp.domain.model.NewBattleSettingDaoModel;
import com.hquyyp.domain.po.BattleNewSetting;
import com.hquyyp.domain.query.BattleSettingQueryEntity;
import com.hquyyp.domain.vo.NewBattleSettingView;
import com.hquyyp.domain.vo.NewVestView;
import com.hquyyp.utils.OutsiderUtil;
import com.hquyyp.utils.Uninterruptibles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class BattleService {
    Logger logger=Logger.getLogger(com.hquyyp.service.BattleService.class.getName());

    @Autowired
    private BattleSettingEntityMapper battleSettingEntityMapper;

    @Autowired
    private BattleEntityMapper battleEntityMapper;

    @Autowired
    private MapService mapService;

    @Autowired
    private VestService vestService;

    @Autowired
    private SerialportService serialportService;

    @Autowired
    private RecordService recordService;

    @Autowired
    MqMessager mqMessager;

    protected BattleEntity battleEntity;

    public void createBattleSetting(CreateBattleSettingRequest createBattleSettingRequest) {
        this.battleSettingEntityMapper.insertSelective((BattleSettingEntity) createBattleSettingRequest);
    }


    public void deleteBattleSetting(String id) {
        this.battleSettingEntityMapper.deleteByPrimaryKey(id);
    }

    public ArrayList<BattleSettingViewModel> listBattleSettingByQuery() {
       return this.battleSettingEntityMapper.listBattleSettingByQuery();
    }

    public PageInfo<BattleSettingViewModel> listBattleSettingByQueryPage(BattleSettingQueryEntity battleSettingQueryEntity) {
        PageHelper.startPage(battleSettingQueryEntity.getPage().intValue(), battleSettingQueryEntity.getPageSize().intValue());
        return new PageInfo(this.battleSettingEntityMapper.listBattleSettingByQueryPage(battleSettingQueryEntity));
    }

    public void startBattle(String id) throws IOException {
        BattleSettingEntity battleSettingEntity = this.battleSettingEntityMapper.selectByPrimaryKey(id);

        BattleEntity battleEntity = BattleEntity.builder()
                .id(OutsiderUtil.getUUID()).beginTime(new Date()).isLoadAmmo(0)
                .mapId(battleSettingEntity.getMapId()).name(battleSettingEntity.getName())
                .status(Integer.valueOf(1)).remark(battleSettingEntity.getId()).build();

        //开启地图管理线程
        this.mapService.initMapThread(battleSettingEntity.getMapId());

        //结束所有battle
        this.battleEntityMapper.endAll();

        this.battleEntityMapper.insert(battleEntity);

        this.battleEntity = battleEntity;

        //通过串口发送battle开始信号
        //this.vestService.sendBattleStartSingle();
        //初始化vestMap容器
        this.vestService.loadVest(battleSettingEntity);

        Uninterruptibles.sleepUninterruptibly(1L, TimeUnit.SECONDS);

        //下发mqtt数据初始化数据
        for (VestEntity vestEntity : this.vestService.listAllVest()) {
            SendDataMissionEntity build = SendDataMissionEntity.builder().data(buildData(vestEntity)).isWait(false).vestNum(vestEntity.getNum()).build();
            byte[] data = build.getData();
            String Mqttdata = new String(data);
            mqMessager.send(vestService.publishtopic,2,Mqttdata);
    }

        /*开启马甲线程-用于轮询各个马甲的数据
        * */
        this.vestService.startVestThread();
    }

    public void endBattle() {
        this.battleEntityMapper.endAll();
        this.recordService.battleEntityRecord=this.battleEntity;
        this.battleEntity = null;

        //清空vestMap容器并关闭马甲线程
        this.vestService.clearAllVest();
        Uninterruptibles.sleepUninterruptibly(2L, TimeUnit.SECONDS);
        for (int i = 0; i <= 4; i++) {
            this.vestService.sendBattleEndSingle();
        }
        this.mapService.endMapThread();
        this.vestService.sendBattleStartSingle();
        this.serialportService.closePort("对局结束!");
    }

    public byte[] buildData(VestEntity vestEntity) {
        //byte[] data= {0, 0, 0, 0, 0, 0};
        String data = (("red".equals(vestEntity.getTeam())) ? 1 : 0) + " "
                + vestEntity.getNum() + " "
                + vestEntity.getLng() + " "
                + vestEntity.getLat() + " "
                + 0 + " "
                + 0 + " "
                + 0 + " "
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
            logger.info("【串口发送数据】数据转换出错！");
            return null;
        }
    }

}
