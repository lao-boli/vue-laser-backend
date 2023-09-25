package com.hquyyp.service;

import com.alibaba.fastjson.JSON;
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
import com.hquyyp.domain.model.SendDataModel;
import com.hquyyp.domain.po.BattleNewSetting;
import com.hquyyp.domain.po.NewRecord;
import com.hquyyp.domain.query.BattleSettingQueryEntity;
import com.hquyyp.domain.vo.NewBattleSettingView;
import com.hquyyp.domain.vo.NewVestView;
import com.hquyyp.utils.OutsiderUtil;
import com.hquyyp.utils.Uninterruptibles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class NewBattleService {
    Logger logger=Logger.getLogger(NewBattleService.class.getName());

    @Autowired
    private NewBattleSettingMapper newBattleSettingMapper;

    @Autowired
    private NewBattleEntityMapper newBattleEntityMapper;

    @Autowired
    private MapService mapService;

    @Autowired
    private NewVestService vestService;

    @Autowired
    private NewRecordService newRecordService;

    @Autowired
    MqMessager mqMessager;

    protected NewBattleEntity newBattleEntity;

    protected List<NewRecordEntity> recordData;

    // idk why is it not applying when compline
    @Value("${mq.PublishTopicPrefix}")
    private String PublishTopicPrefix;

    @Value("${mq.PublishTopicSuffix}")
    private String PublishTopicSuffix;



    public void createNewBattleSetting(CreateNewBattleSettingRequest cbr) {
        this.newBattleSettingMapper.insertBattleSetting(
                BattleNewSetting.builder()
                .id(cbr.getId())
                .name(cbr.getName())
                .mapId(cbr.getMapId())
                .blueTeamList(JSON.toJSONString(cbr.getBlueTeamList()))
                .redTeamList(JSON.toJSONString(cbr.getRedTeamList()))
                .build()
        );
    }

    public void updateNewBattleSetting(CreateNewBattleSettingRequest cbr){
        this.newBattleSettingMapper.updateByPrimaryKey(
                BattleNewSetting.builder()
                        .id(cbr.getId())
                        .name(cbr.getName())
                        .mapId(cbr.getMapId())
                        .blueTeamList(JSON.toJSONString(cbr.getBlueTeamList()))
                        .redTeamList(JSON.toJSONString(cbr.getRedTeamList()))
                        .build()
        );
    }


    public void NewDeleteBattleSetting(String id) {
        this.newBattleSettingMapper.deleteByPrimaryKey(id);
    }


    public PageInfo<NewBattleSettingDaoModel> newListBattleSettingByQueryPage(BattleSettingQueryEntity battleSettingQueryEntity) {
       // List<NewBattleSettingDaoModel> BattleNewSettingList = this.newBattleSettingMapper.listBattleSettingByQueryPage(battleSettingQueryEntity);
        PageHelper.startPage(battleSettingQueryEntity.getPage().intValue(), battleSettingQueryEntity.getPageSize().intValue());
        PageInfo pageInfo = new PageInfo(this.newBattleSettingMapper.listBattleSettingByQueryPage(battleSettingQueryEntity));
        List<NewBattleSettingDaoModel> list = pageInfo.getList();
        List<NewBattleSettingEntity> newBattleSettingList=new ArrayList<>();
        for (NewBattleSettingDaoModel bns:list){
            NewBattleSettingView newBattleSettingView=new NewBattleSettingView();
            newBattleSettingView.setId(bns.getId());
            newBattleSettingView.setName(bns.getName());
            newBattleSettingView.setMapId(bns.getMapId());
            newBattleSettingView.setMapName(bns.getMapName());
            // newBattleSettingEntity.setBlueTeamList();
            newBattleSettingView.setBlueTeamList(JSONObject.parseArray(bns.getBlueTeamList(),NewVestEntity.class));
            newBattleSettingView.setRedTeamList(JSONObject.parseArray(bns.getRedTeamList(),NewVestEntity.class));
            newBattleSettingList.add(newBattleSettingView);
        }
        pageInfo.setList(newBattleSettingList);
        return pageInfo;
    }

    public void newStartBattle(String id) throws IOException {
        BattleNewSetting battleSettingEntity = this.newBattleSettingMapper.selectByPrimaryKey(id);

        NewBattleEntity newBattleEntity = NewBattleEntity.builder()
                .id(OutsiderUtil.getUUID()).beginTime(new Date())
                .mapId(battleSettingEntity.getMapId()).name(battleSettingEntity.getName())
                .status(Integer.valueOf(1)).build();

        //开启地图管理线程
        this.mapService.initMapThread(battleSettingEntity.getMapId());

        //结束所有battle

        this.newBattleEntityMapper.endAll();

        this.newBattleEntityMapper.insert(newBattleEntity);

        //this.newEndBattle();
        //this.newBattleEntityMapper.endAll();

        this.newBattleEntity = newBattleEntity;

        this.recordData=new ArrayList<>();
        // fixed: 第二次开始训练后，recorddata是上一次的训练记录,故需要重置为null.
        newRecordService.recordData = null;

        //初始化vestMap容器以及数据统计容器
        this.vestService.NewLoadVest(battleSettingEntity);

        Uninterruptibles.sleepUninterruptibly(1L, TimeUnit.SECONDS);

        this.sendBeginSignal();

    }

    public void newInitBattle(String id){
        BattleNewSetting battleSettingEntity = this.newBattleSettingMapper.selectByPrimaryKey(id);

        this.recordData=new ArrayList<>();
        this.vestService.NewLoadVest(battleSettingEntity);
    }

    public void newEndBattle() {
        this.sendEndSignal();
        this.newBattleEntityMapper.endAll();
        this.newRecordService.newBattleEntityRecord=this.newBattleEntity;
        this.newRecordService.recordData=this.recordData;
        this.newBattleEntity = null;
        this.recordData=null;
        //清空vestMap容器并关闭马甲线程
        this.vestService.clearAllVest();
        this.mapService.endMapThread();
    }


    public NewBattleEntity getNowBattle() {
        return this.newBattleEntity;
    }


    public List<NewRecordEntity> getRecordData(){
        return this.recordData;
    }

    //下发mqtt数据开始对局
    public void sendBeginSignal(){
        SendDataModel sendDataModel=SendDataModel.builder()
                .fPort(3).data(OutsiderUtil.intToBase64(0))
                .build();
        try {
            for (NewVestView vestEntity : this.vestService.listAllNewVest()) {

                mqMessager.send(PublishTopicPrefix+vestEntity.getEquipment()+PublishTopicSuffix,2,JSONObject.toJSONString(sendDataModel));
            }
        }catch (Exception e){
            System.out.println("下发mqtt数据初始化数据错误！！！");
        }
    }

    //下发mqtt数据结束对局
    public void sendEndSignal(){
        SendDataModel sendDataModel=SendDataModel.builder()
                .fPort(3).data(OutsiderUtil.intToBase64(1))
                .build();
        try {
            for (NewVestView vestEntity : this.vestService.listAllNewVest()) {
                mqMessager.send(PublishTopicPrefix+vestEntity.getEquipment()+PublishTopicSuffix,2,JSONObject.toJSONString(sendDataModel));
            }
        }catch (Exception e){
            System.out.println("下发mqtt数据初始化数据错误！！！");
        }
    }

}
