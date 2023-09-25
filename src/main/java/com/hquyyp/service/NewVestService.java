package com.hquyyp.service;

import com.alibaba.fastjson.JSONObject;
import com.hquyyp.MqttInterface.MqMessager;
import com.hquyyp.domain.entity.NewBattleSettingEntity;
import com.hquyyp.domain.entity.NewRecordEntity;
import com.hquyyp.domain.entity.NewVestEntity;
import com.hquyyp.domain.model.PositionWebSocketModel;
import com.hquyyp.domain.model.SendDataModel;
import com.hquyyp.domain.model.ShootWebSocketModel;
import com.hquyyp.domain.po.BattleNewSetting;
import com.hquyyp.domain.vo.NewVestView;
import com.hquyyp.protocol.HitEntity;
import com.hquyyp.protocol.PingEntity;
import com.hquyyp.protocol.ProtoEntity;
import com.hquyyp.utils.OutsiderUtil;
import com.hquyyp.websocket.ShootWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class NewVestService {

    private static final Logger logger = Logger.getLogger(NewVestService.class.getName());

    @Autowired
    MqMessager mqMessager;

    @Autowired
    private ShootWebSocket shootWebSocket;

    @Autowired
    private NewBattleService newBattleService;

    protected List<NewVestView> vestEntityList = new ArrayList<>();

    @Value("${mq.PublishTopicPrefix}")
    private String PublishTopicPrefix;

    @Value("${mq.PublishTopicSuffix}")
    private String PublishTopicSuffix;


    public void NewLoadVest(BattleNewSetting battleSettingEntity) {
        this.vestEntityList.clear();
        initVestListAndRecord(battleSettingEntity.getBlueTeamList(), "blue");
        initVestListAndRecord(battleSettingEntity.getRedTeamList(), "red");
    }

    /**
     * 初始化马甲列表和作战记录
     * @param teamList 队伍列表, 应为 {@link NewBattleSettingEntity#getBlueTeamList()} 或 {@link NewBattleSettingEntity#getRedTeamList()}
     * @param team 队伍名称, "blue" 或 "red"
     */
    private void initVestListAndRecord(String teamList, String team) {
        List<NewVestEntity> blueTeamList = JSONObject.parseArray(teamList, NewVestEntity.class);
        for (NewVestEntity nve : blueTeamList) {
            NewVestView newVestView = new NewVestView(nve);
            newVestView.setTeam(team);
            newVestView.setAmmo(0);
            newVestView.setHp(100);
            this.vestEntityList.add(newVestView);
            NewRecordEntity newRecordEntity = NewRecordEntity.builder()
                    .id(nve.getId()).name(nve.getName()).kill(0)
                    .team(team).beShooted(0).shoot(0)
                    .build();
            this.newBattleService.recordData.add(newRecordEntity);
        }
    }

    public List<NewVestView> listAllNewVest() {
        //vestEntityList.sort(Comparator.comparingInt(VestEntity::getNum));
        return this.vestEntityList;
    }

    public void clearAllVest() {
        this.vestEntityList.clear();
    }

    public void newSendDieData(int vestNum) {
        for (NewVestView nvv : this.vestEntityList) {
            if (Integer.parseInt(nvv.getId()) == vestNum) {
                nvv.setHp(0);
                String team = nvv.getTeam();
                String equipment = nvv.getEquipment();
                String DieData = "2 " + (("red".equals(team)) ? 1 : 0) + " " + equipment;
                try {
                    mqMessager.send(PublishTopicPrefix + nvv.getEquipment() + PublishTopicSuffix, 2, DieData);
                } catch (Exception e) {
                    System.out.println("下发mqtt数据出现错误！！!");
                }
            }
        }
    }

    public void newSendDieInjure(int vestNum, int injure) {
        for (NewVestView nvv : this.vestEntityList) {
            if (Integer.parseInt(nvv.getId()) == vestNum) {
                String team = nvv.getTeam();
                String equipment = nvv.getEquipment();
                String injureData = "1 " + (("red".equals(team)) ? 1 : 0) + " " + equipment + " " + 1;
                try {
                    if (injure <= 0) {
                        return;
                    } else if (injure == 1) {
                        nvv.setHp(66);
                        mqMessager.send(PublishTopicPrefix + nvv.getEquipment() + PublishTopicSuffix, 2, injureData);
                    } else if (injure == 2) {
                        nvv.setHp(33);
                        mqMessager.send(PublishTopicPrefix + nvv.getEquipment() + PublishTopicSuffix, 2, injureData);
                        mqMessager.send(PublishTopicPrefix + nvv.getEquipment() + PublishTopicSuffix, 2, injureData);
                    } else {
                        this.newSendDieData(vestNum);
                    }
                } catch (Exception e) {
                    System.out.println("下发mqtt数据出现错误！！!");
                }
            }
        }
    }

    public void NewAllLoadAmmo(int ammoNum) {
        for (NewVestView nvv : this.vestEntityList) {
            NewLoadAmmo(Integer.parseInt(nvv.getId()), ammoNum);
        }
    }

    public void NewLoadAmmo(int vestNum, int ammoNum) {
        for (NewVestView nvv : this.vestEntityList) {
            if (Integer.parseInt(nvv.getId()) == vestNum) {
                int oldammo = nvv.getAmmo();
                nvv.setAmmo(oldammo + ammoNum);

                //下发mqtt装弹数据
                SendDataModel sendDataModel = SendDataModel.builder()
                        .fPort(5).data(OutsiderUtil.intToBase64(ammoNum)).build();
                try {
                    mqMessager.send(PublishTopicPrefix + nvv.getEquipment() + PublishTopicSuffix, 2, JSONObject.toJSONString(sendDataModel));
                } catch (Exception e) {
                    System.out.println("下发mqtt数据出现错误！！!");
                }
            }
        }
    }

    public void refreshVest(NewVestView newVestEntity) {
        this.vestEntityList.add(newVestEntity);
        Collections.sort(this.vestEntityList);
    }

    public void handleVestMqData(ProtoEntity data) {
        //fixed: when train is not started, receiving data will cause NPE.
        if (vestEntityList == null || vestEntityList.isEmpty()) {
            return;
        }
        if (data instanceof HitEntity) {
            handleHitData((HitEntity) data);
        }
        if (data instanceof PingEntity) {
            handlePingData((PingEntity) data);
        }

    }

    public void handlePingData(PingEntity data) {
        NewVestView newVestView = findVestView(data.number);
        if (newVestView == null) {
            logger.info("【mqtt接收】" + data.number + "号马甲不存在");
            return;
        }

        this.vestEntityList.remove(newVestView);
        double lat = data.lat;
        double lng = data.lng;

        if (newVestView.getLat() != lat || newVestView.getLng() != lng) {
            PositionWebSocketModel positionWebSocketModel = PositionWebSocketModel.builder()
                    .mark("1").num(newVestView.getId())
                    .lat(lat).lng(lng).time(new Date())
                    .build();
            shootWebSocket.sendMessage(positionWebSocketModel);
        }

        //做存亡处理
        int hp;
        //若头部中弹，直接死亡
        if (data.headHit > 0) {
            hp = 0;
        } else {
            hp = Math.max(100 - (33 * data.backHit), 0);
        }

        //刷新vestEntityMap中的数据
        //newVestView.setId(Integer.parseInt(dataSplit[2]));
        newVestView.setLng(lng);
        newVestView.setLat(lat);
        newVestView.setHp(hp);
        newVestView.setAmmo(data.leftAmmo);
        newVestView.setLastReportTime(new Date());
        this.refreshVest(newVestView);
    }

    public void handleHitData(HitEntity data) {
        if (!data.isUpdate) {
            return;
        }

        NewVestView shootee = findVestView(data.shootee);
        NewVestView shooter = findVestView(data.shooter);
        if (shooter == null || shootee == null) {
            return;
        }

        // 更新数据
        vestEntityList.remove(shootee);
        // 若hp小于34且被击中部位是头部,则为击杀数据
        boolean isKill = shootee.getHp() <= 34 || "头部".equals(data.hitPart);
        if (isKill) {
            shootee.setHp(0);
        } else {
            shootee.setHp(shootee.getHp() - 33);
        }
        updateRecord(isKill, shootee.getId(), shooter.getId());

        refreshVest(shootee);

        //通过websocket推送击中数据
        logger.info("【射击信息】" + shooter.getTeam() + "队的" + shooter.getId() + "号击中" + shootee.getTeam() + "队的" + shootee.getId() + "号的" + data.hitPart + "部位");
        ShootWebSocketModel shootWebSocketModel = ShootWebSocketModel.builder()
                .mark("0").shooteeNum(shootee.getId()).shooteeTeam(shootee.getTeam())
                .shooterTeam(shooter.getTeam()).shooterNum(shooter.getId())
                .position(data.hitPart).time(new Date())
                .build();
        shootWebSocket.sendMessage(shootWebSocketModel);
    }

    /**
     * 更新作战记录,并重新排序.
     *
     * @param isKill    是否是击杀数据
     * @param shooteeId 受击士兵编号
     * @param shooterId 射击士兵编号
     */
    private void updateRecord(boolean isKill, String shooteeId, String shooterId) {
        NewRecordEntity shooteeRecord = findRecord(shooteeId);
        if (shooteeRecord != null) {
            Integer beShooted = shooteeRecord.getBeShooted();
            shooteeRecord.setBeShooted(++beShooted);
        }

        NewRecordEntity shooterRecord = findRecord(shooterId);
        if (shooterRecord != null) {
            Integer shoot = shooterRecord.getShoot();
            shooterRecord.setShoot(++shoot);
            if (isKill) {
                Integer kill = shooterRecord.getKill();
                shooterRecord.setKill(++kill);
            }
        }
        Collections.sort(newBattleService.getRecordData());
    }

    /**
     * 根据编号查找 {@link NewBattleService#recordData} 中存在的 {@link NewRecordEntity}对象
     *
     * @param id 士兵编号
     * @return 若找到则返回 {@link NewRecordEntity} 对象，否则返回null.
     */
    private NewRecordEntity findRecord(String id) {
        return newBattleService.getRecordData().stream()
                .filter(nre -> Objects.equals(nre.getId(), id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据编号查找 {@link #vestEntityList} 中存在的 {@link NewVestView}对象
     *
     * @param id 士兵编号
     * @return 若找到则返回 {@link NewVestView} 对象，否则返回null.
     */
    public NewVestView findVestView(int id) {
        return vestEntityList.stream()
                .filter(nvv -> Integer.parseInt(nvv.getId()) == id)
                .findFirst()
                .orElse(null);
    }

}
