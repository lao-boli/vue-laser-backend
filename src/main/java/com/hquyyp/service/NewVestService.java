package com.hquyyp.service;

import com.alibaba.fastjson.JSONObject;
import com.hquyyp.MqttInterface.MqMessager;
import com.hquyyp.domain.entity.NewRecordEntity;
import com.hquyyp.domain.entity.NewVestEntity;
import com.hquyyp.domain.model.PositionWebSocketModel;
import com.hquyyp.domain.model.SendDataModel;
import com.hquyyp.domain.model.ShootWebSocketModel;
import com.hquyyp.domain.po.BattleNewSetting;
import com.hquyyp.domain.vo.NewVestView;
import com.hquyyp.utils.OutsiderUtil;
import com.hquyyp.websocket.ShootWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class NewVestService {
    private static final Logger logger=Logger.getLogger(NewVestService.class.getName());

    @Autowired
    MqMessager mqMessager;

    @Autowired
    private ShootWebSocket shootWebSocket;

    @Autowired
    private NewBattleService newBattleService;

    protected List<NewVestView> vestEntityList=new ArrayList<>();

    @Value("${mq.PublishTopicPrefix}")
    private String PublishTopicPrefix;

    @Value("${mq.PublishTopicSuffix}")
    private String PublishTopicSuffix;


    public void NewLoadVest(BattleNewSetting battleSettingEntity) {
        this.vestEntityList.clear();
        List<NewVestEntity> blueTeamList = JSONObject.parseArray(battleSettingEntity.getBlueTeamList(), NewVestEntity.class);
        List<NewVestEntity> redTeamList = JSONObject.parseArray(battleSettingEntity.getRedTeamList(), NewVestEntity.class);

        for (NewVestEntity nve:blueTeamList){
            NewVestView newVestView=new NewVestView(nve);
            newVestView.setTeam("blue");
            newVestView.setAmmo(0);
            newVestView.setHp(100);
            this.vestEntityList.add(newVestView);
            NewRecordEntity newRecordEntity=NewRecordEntity.builder()
                    .id(nve.getId()).name(nve.getName()).kill(0)
                    .team("blue").beShooted(0).shoot(0)
                    .build();
            this.newBattleService.recordData.add(newRecordEntity);
        }
        for (NewVestEntity nve:redTeamList){
            NewVestView newVestView=new NewVestView(nve);
            newVestView.setTeam("red");
            newVestView.setAmmo(0);
            newVestView.setHp(100);
            this.vestEntityList.add(newVestView);
            NewRecordEntity newRecordEntity=NewRecordEntity.builder()
                    .id(nve.getId()).name(nve.getName()).kill(0)
                    .team("red").beShooted(0).shoot(0)
                    .build();
            this.newBattleService.recordData.add(newRecordEntity);
        }
    }

    public List<NewVestView> listAllNewVest() {
        //vestEntityList.sort(Comparator.comparingInt(VestEntity::getNum));
        return this.vestEntityList;
    }

    public NewVestView getNewVest(Integer id) {
        NewVestView newVestView=new NewVestView();
        for (NewVestView nvv:this.vestEntityList) {
            if (Integer.parseInt(nvv.getId()) == id){
                newVestView=nvv;
            }
        }
        return newVestView;
    }

    public void clearAllVest() {
        this.vestEntityList.clear();
    }

    public void newSendDieData(int vestNum) {
        for (NewVestView nvv:this.vestEntityList) {
            if (Integer.parseInt(nvv.getId()) == vestNum){
                nvv.setHp(0);
                String team=nvv.getTeam();
                String  equipment = nvv.getEquipment();
                String DieData="2 "+(("red".equals(team)) ? 1 : 0)+" "+equipment;
                try {
                    mqMessager.send(PublishTopicPrefix+nvv.getEquipment()+PublishTopicSuffix,2,DieData);
                }catch (Exception e){
                    System.out.println("下发mqtt数据出现错误！！!");
                }
            }
        }
    }

    public void newSendDieInjure(int vestNum, int injure) {
        for (NewVestView nvv:this.vestEntityList) {
            if (Integer.parseInt(nvv.getId()) == vestNum){
                String team=nvv.getTeam();
                String equipment=nvv.getEquipment();
                String injureData="1 "+(("red".equals(team)) ? 1 : 0)+" "+equipment+" "+1;
                try {
                    if (injure<=0){
                        return;
                    }
                    else if (injure==1){
                        nvv.setHp(66);
                        mqMessager.send(PublishTopicPrefix+nvv.getEquipment()+PublishTopicSuffix,2,injureData);
                    }
                    else if (injure==2){
                        nvv.setHp(33);
                        mqMessager.send(PublishTopicPrefix+nvv.getEquipment()+PublishTopicSuffix,2,injureData);
                        mqMessager.send(PublishTopicPrefix+nvv.getEquipment()+PublishTopicSuffix,2,injureData);
                    }
                    else{
                        this.newSendDieData(vestNum);
                    }
                }catch (Exception e){
                    System.out.println("下发mqtt数据出现错误！！!");
                }
            }
        }
    }

    public void NewAllLoadAmmo(int ammoNum){
       for (NewVestView nvv:this.vestEntityList){
           NewLoadAmmo(Integer.parseInt(nvv.getId()),ammoNum);
       }
    }

    public void NewLoadAmmo(int vestNum, int ammoNum) {
        for (NewVestView nvv:this.vestEntityList){
            if (Integer.parseInt(nvv.getId())==vestNum){
                int oldammo = nvv.getAmmo();
                nvv.setAmmo(oldammo+ammoNum);

                //下发mqtt装弹数据
                SendDataModel sendDataModel=SendDataModel.builder()
                        .fPort(5).data(OutsiderUtil.intToBase64(ammoNum)).build();
                try {
                    mqMessager.send(PublishTopicPrefix+nvv.getEquipment()+PublishTopicSuffix,2,JSONObject.toJSONString(sendDataModel));
                }catch (Exception e){
                    System.out.println("下发mqtt数据出现错误！！!");
                }
            }
        }
    }

    public void refreshVest(NewVestView newVestEntity){
       this.vestEntityList.add(newVestEntity);
       Collections.sort(this.vestEntityList);
    }

    public void handleVestMqData(String data){
        //fixed: when train is not started, receiving data will cause NPE.
        if (this.vestEntityList == null){
            return;
        }
        logger.info("【mqtt接收】接收"+data);
        String[] dataSplit=null;
        //拆分数据成字符串数组
        try{
            dataSplit = data.split(" ");
        }catch (Exception e){
            logger.info("【mqtt接收】数据格式不符合规范");
        }
        //数据类型 射击者 被射击者 击中部位 是否是更新的数据
        //    0     1       2       3      4
        if("91".equals(dataSplit[0])) {
            //射击逻辑
            if (dataSplit.length == 5) {
                // 如果标志位为0，说明不是更新的数据，即为重复发送的数据，丢弃
                if (dataSplit[4].equals("0")){
                    return;
                }
                String position="";
                switch (Integer.parseInt(dataSplit[3])) {
                    case 1:
                        position = "头部";
                        break;
                    case 2:
                        position = "腹部";
                        break;
                    case 4:
                        position = "左手";
                        break;
                    case 8:
                        position = "右手";
                        break;
                    case 16:
                        position = "左脚";
                        break;
                    case 32:
                        position = "右脚";
                        break;
                    case 64:
                        position = "后甲";
                        break;
                    case 128:
                        position = "前甲";
                        break;
                }
                //射击者
                NewVestView shooter=null;
                NewVestView shootee=null;
                for (NewVestView nvv:this.vestEntityList) {
                    if (Integer.parseInt(nvv.getId()) == Integer.parseInt(dataSplit[1])){
                        shooter=nvv;
                    }
                    if (Integer.parseInt(nvv.getId()) == Integer.parseInt(dataSplit[2])){
                        shootee=nvv;
                    }
                }
                if (shooter!=null && shootee!=null){
                    this.vestEntityList.remove(shootee);
                    //首先刷新被击中者血量
                    // 若hp大于34且被击中部位不是头部
                    if (shootee.getHp() > 34 && !"头部".equals(position)) {
                        shootee.setHp(shootee.getHp()-33);
                        //非击杀数据
                        for (NewRecordEntity nre:this.newBattleService.getRecordData()) {
                            if (nre.getId() == shootee.getId()) {
                                Integer beShooted = nre.getBeShooted();
                                nre.setBeShooted(++beShooted);
                                //System.out.println("shootee"+nre.toString());
                            }
                            if (nre.getId()==shooter.getId()){
                                Integer shoot = nre.getShoot();
                                nre.setShoot(++shoot);
                                //System.out.println("shooter"+nre.toString());
                            }
                        }
                    } else if (shootee.getHp()<=34 || "头部".equals(position)){
                        shootee.setHp(0);
                        //击杀数据
                        for (NewRecordEntity nre:this.newBattleService.getRecordData()) {
                            if (nre.getId() == shootee.getId()) {
                                Integer beShooted = nre.getBeShooted();
                                nre.setBeShooted(++beShooted);
                                //System.out.println("shootee"+nre.toString());
                            }
                            if (nre.getId()==shooter.getId()){
                                Integer shoot = nre.getShoot();
                                nre.setShoot(++shoot);
                                Integer kill = nre.getKill();
                                nre.setKill(++kill);
                            }
                        }
                    }
                    //System.out.println("记录list："+JSONObject.toJSONString(this.newBattleService.getRecordData()));
                    //System.out.println(JSONObject.toJSONString();
                    Collections.sort(this.newBattleService.getRecordData());
                    this.refreshVest(shootee);
                    //处理射击数据并做推送处理
                    logger.info("【射击信息】"+shooter.getTeam()+"队的"+shooter.getId()+"号击中"+shootee.getTeam()+"队的"+shootee.getId()+"号的"+position+"部位");
                    //websocket推送回调
                    ShootWebSocketModel shootWebSocketModel= ShootWebSocketModel.builder()
                            .mark("0").shooteeNum(shootee.getId()).shooteeTeam(shootee.getTeam())
                            .shooterTeam(shooter.getTeam()).shooterNum(shooter.getId())
                            .position(position).time(new Date())
                            .build();
                    //通过websocket推送击中数据
                    shootWebSocket.sendMessage(shootWebSocketModel);
                    //shootWebSocket.sendMessage(JSON.toJSONString(shootWebSocketModel););
                    // } else {
                    //    log.info("【射击信息】不可射击队友!");
                    // }
                }else {
                    logger.info("【mqtt接收】射击者或被射击者不存在");
                }
            } else {
                logger.info("【mqtt接收】射击数据格式错误或者不完整！");
            }
        }
        //数据类型0  编号1 经度lng2 维度lat3 背甲中弹数4 头盔中弹数5  弹药6
        //    1     2       3        4         5        6           7
        if ("138".equals(dataSplit[0])) {
            if (dataSplit.length == 7) {
                NewVestView newVestView=null;
                for (NewVestView nvv:this.vestEntityList) {
                    if (Integer.parseInt(nvv.getId()) == Integer.parseInt(dataSplit[1])){
                        newVestView=nvv;
                    }
                }
                this.vestEntityList.remove(newVestView);
                if (newVestView != null) {
                    // log.info("111");
                    // vestEntity.builder()
                    //        .num(Integer.parseInt(dataSplit[2])).lng(Integer.parseInt(dataSplit[3]))
                    //        .lat(Integer.parseInt(dataSplit[4])).hp(Integer.parseInt(dataSplit[5]))
                    //       .ammo(Integer.parseInt(dataSplit[6])).lastReportTime(new Date())
                    //        .build();
                    //判断经纬度是否刷新，如果刷新推送到websocket
                    Double lng=Double.parseDouble(dataSplit[2]);
                    Double lat=Double.parseDouble(dataSplit[3]);
                    // double[] doubles = GpsCoordinateUtils.calWGS84toGCJ02(lat, lng);
                    //lat=doubles[0];
                    //lng=doubles[1];
                    //System.out.println("解析后的GCJ02为:经度"+lng+"纬度"+lat);
                    if (newVestView.getLat()!=lat || newVestView.getLng()!= lng){
                        PositionWebSocketModel positionWebSocketModel= PositionWebSocketModel.builder()
                                .mark("1").num(newVestView.getId())
                                .lat(lat).lng(lng).time(new Date())
                                .build();
                        shootWebSocket.sendMessage(positionWebSocketModel);
                    }

                    //做存亡处理
                    int shootNum=Integer.parseInt(dataSplit[4])+Integer.parseInt(dataSplit[5]);
                    int hp;
                    //若头部中弹，直接死亡
                    if(shootNum<0 || shootNum>2 || Integer.parseInt(dataSplit[5]) >0){
                        hp=0;
                    }else {
                        hp = 100 - (33 * shootNum);
                    }

                    //刷新vestEntityMap中的数据
                    //newVestView.setId(Integer.parseInt(dataSplit[2]));
                    newVestView.setLng(lng);
                    newVestView.setLat(lat);
                    if (hp>=0){
                        newVestView.setHp(hp);
                    }else {
                        newVestView.setHp(0);
                    }
                    newVestView.setAmmo(Integer.parseInt(dataSplit[6]));
                    newVestView.setLastReportTime(new Date());
                    System.out.println(newVestView.toString());
                } else {
                    logger.info("【mqtt接收】" + dataSplit[1] + "号马甲不存在");
                }
                this.refreshVest(newVestView);
            } else {
                logger.info("【mqtt接收】刷新数据格式错误或者不完整！");
            }
        }
        //
        if ("2".equals(dataSplit[0])){}
    }
}
