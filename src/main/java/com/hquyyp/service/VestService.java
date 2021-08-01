package com.hquyyp.service;

import com.hquyyp.MqttInterface.MqMessager;
import com.hquyyp.domain.entity.*;
import com.hquyyp.domain.model.PositionWebSocketModel;
import com.hquyyp.domain.model.ShootWebSocketModel;
import com.hquyyp.thread.VestThread;
import com.hquyyp.websocket.ShootWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Service
public class VestService {
    private static final Logger logger=Logger.getLogger(com.hquyyp.service.VestService.class.getName());

    @Autowired
    private SerialportService serialportService;

    private ExecutorService vestThreadPool = Executors.newSingleThreadExecutor();

    private VestThread vestThread;

    @Autowired
    MqMessager mqMessager;

    @Value("${mq.publishtopic}")
    public String publishtopic;

    @Autowired
    private ShootWebSocket shootWebSocket;

    protected Map<Integer, VestEntity> vestEntityMap = new ConcurrentHashMap<>();

    public void loadVest(BattleSettingEntity battleSettingEntity){
        //参加battle的红队马甲号码
        List<String> redVestNumList = Arrays.asList(battleSettingEntity.getRedVestNumStr().split(","));
        //武器类型
        List<String> redVestWeaponList = Arrays.asList(battleSettingEntity.getRedVestWeapon().split(","));

        if (redVestWeaponList.size() == 0) {
            redVestWeaponList = new ArrayList<>();
            redVestWeaponList.add("");
        }
        int index = 0;
        //记录每个参加battle的号码
        for (String str : redVestNumList) {
            //logger.info("【编号】"+str);
                addVest(Integer.parseInt(str), redVestWeaponList.get(index), "red");
            index++;
        }
        //蓝队相关操作
        List<String> blueVestNumList = Arrays.asList(battleSettingEntity.getBlueVestNumStr().split(","));
        List<String> blueVestWeaponList = Arrays.asList(battleSettingEntity.getBlueVestWeapon().split(","));
        if (blueVestWeaponList.size() == 0) {
            blueVestWeaponList = new ArrayList<>();
            blueVestWeaponList.add("");
        }
        index = 0;
        for (String str : blueVestNumList) {
                addVest(Integer.parseInt(str), blueVestWeaponList.get(index), "blue");
            index++;
        }
    }

    public void addVest(int vestNum,String weapon, String team) {
        VestEntity vestEntity = VestEntity.builder().num(vestNum).hp(100).ammo(100).weapon(weapon).team(team).build();
        this.vestEntityMap.put(Integer.valueOf(vestEntity.getNum()), vestEntity);
    }

    public void refreshVest(VestEntity vestEntity){
        if (this.vestEntityMap.remove(Integer.valueOf(vestEntity.getNum()))!=null){
            this.vestEntityMap.put(Integer.valueOf(vestEntity.getNum()),vestEntity);
        }
    }

    public List<VestEntity> listAllVest() {
        List<VestEntity> vestEntityList = new ArrayList<>();
        this.vestEntityMap.values().forEach(vestEntity -> vestEntityList.add(vestEntity));
        vestEntityList.sort(Comparator.comparingInt(VestEntity::getNum));
        return vestEntityList;
    }

    public VestEntity getVest(Integer id) {
        return this.vestEntityMap.get(id);
    }

    public void clearAllVest() {
        if (this.vestThread != null) {
            this.vestThread.stop();
            this.vestThread = null;
        }
        this.vestEntityMap.clear();
        this.vestThreadPool = Executors.newSingleThreadExecutor();
    }

    /*发送竞赛开始信号*/
    public void sendBattleStartSingle() {
        byte[] startBattleData = {0, 6, -1, 0, 6, -81, -6};
       // startBattleData[3] = (byte) battleMode;
       // startBattleData[4] = (byte) (startBattleData[0] + startBattleData[1] + startBattleData[2] + startBattleData[3]);
        this.serialportService.submitHighLevelMission(SendDataMissionEntity.builder().data(startBattleData).isWait(false).vestNum(0).build());
    }

    public void sendDieData(int vestNum) {
        this.vestEntityMap.get(vestNum).setHp(0);
        String team = this.vestEntityMap.get(vestNum).getTeam();
        int num = this.vestEntityMap.get(vestNum).getNum();
        String DieData="2 "+(("red".equals(team)) ? 1 : 0)+" "+num;
        try {
            mqMessager.send(publishtopic,2,DieData);
        }catch (Exception e){
            System.out.println("下发mqtt数据出现错误！！1");
        }
    }

    public void sendDieInjure(int vestNum, int injure) {
        VestEntity vest = this.vestEntityMap.get(vestNum);
        String team = this.vestEntityMap.get(vestNum).getTeam();
        int num = this.vestEntityMap.get(vestNum).getNum();
        String injureData="1 "+(("red".equals(team)) ? 1 : 0)+" "+num+" "+1;
        try {
            if (injure<=0){
                return;
            }
            else if (injure==1){
                vest.setHp(66);
                mqMessager.send(publishtopic,2,injureData);
            }
            else if (injure==2){
                vest.setHp(33);
                mqMessager.send(publishtopic,2,injureData);
                mqMessager.send(publishtopic,2,injureData);
            }
            else{
                this.sendDieData(vestNum);
            }
        }catch (Exception e){
            System.out.println("下发mqtt数据出现错误！！!");
        }

    }

    public void AllLoadAmmo(int ammoNum){
        Set<Integer> keySet = this.vestEntityMap.keySet();
        for (Integer num:keySet){
            loadAmmo(num,ammoNum);
        }
    }

    public void loadAmmo(int vestNum, int ammoNum) {
        VestEntity vestEntity = this.vestEntityMap.get(vestNum);
        int oldammo = vestEntity.getAmmo();
        vestEntity.setAmmo(oldammo+ammoNum);
        String AmmoData=new String();
        int num = this.vestEntityMap.get(vestNum).getNum();
        String team = this.vestEntityMap.get(vestNum).getTeam();
        int ammo = this.vestEntityMap.get(vestNum).getAmmo();
        AmmoData=0+" "+(("red".equals(team)) ? 1 : 0)+" "+num+" "+ammo;
        try {
            mqMessager.send(publishtopic,2,AmmoData);
        }catch (Exception e){
            System.out.println("下发mqtt数据出现错误！！!");
        }


    }

    /*发送竞赛结束信号*/
    public void sendBattleEndSingle() {
        byte[] endBattleData = {0, 11, -1, -1, 10, -81, -6};
        this.serialportService.submitHighLevelMission(SendDataMissionEntity.builder().data(endBattleData).isWait(false).vestNum(0).build());
    }

    public void startVestThread() {
        VestThread vestThread = new VestThread(this, this.serialportService);
        this.vestThreadPool.submit((Runnable) vestThread);
        this.vestThread = vestThread;
    }


    public void handleVestMqData(String data){
        logger.info("【mqtt接收】接收"+data);
        String[] dataSplit=null;
        //拆分数据成字符串数组
        try{
            dataSplit = data.split(" ");
        }catch (Exception e){
            logger.info("【mqtt接收】数据格式不符合规范");
        }
        //数据类型 射击者 被射击者 击中部位
        //    0     1       2       3
        if("0".equals(dataSplit[0])) {
            //射击逻辑
            if (dataSplit.length == 4) {
                //射击者
                VestEntity shooter = this.vestEntityMap.get(Integer.parseInt(dataSplit[1]));
                //被射击者
                VestEntity shootee=this.vestEntityMap.get(Integer.parseInt(dataSplit[2]));
                if (shooter!=null && shootee!=null){
                    //  if (!shootee.getTeam().equals(shooter.getTeam())){
                    //首先刷新被击中者血量
                    if (shootee.getHp() > 34) {
                        shootee.setHp(shootee.getHp()-33);
                    } else if (shootee.getHp()<=34){
                        shootee.setHp(0);
                    }
                    this.refreshVest(shootee);
                    //处理射击数据并做推送处理
                    logger.info("【射击信息】"+shooter.getTeam()+"队的"+shooter.getNum()+"号击中"+shootee.getTeam()+"队的"+shootee.getNum()+"号的"+dataSplit[3]+"号部位");
                    //websocket推送回调
                    ShootWebSocketModel shootWebSocketModel= ShootWebSocketModel.builder()
                            .mark("0").shooteeNum(""+shootee.getNum()).shooteeTeam(shootee.getTeam())
                            .shooterTeam(shooter.getTeam()).shooterNum(""+shooter.getNum())
                            .position(dataSplit[3]).time(new Date())
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
        //数据类型0 队伍1 编号2 经度lng3 维度lat4 背甲中弹数5 头盔中弹数6 是否阵亡7 弹药8
        //    1     2   3     4       5        6        7         8     9
        if ("1".equals(dataSplit[0])) {
            if (dataSplit.length == 9) {
                VestEntity vestEntity = this.vestEntityMap.get(Integer.parseInt(dataSplit[2]));
                String team = dataSplit[1];
                if (vestEntity != null) {
                    if ("1".equals(team)) {
                        vestEntity.setTeam("red");
                    } else if ("0".equals(team)) {
                        vestEntity.setTeam("blue");
                    } else {
                        logger.info("【mqtt接收】队伍数据解析出错");
                    }
                    // log.info("111");
                    // vestEntity.builder()
                    //        .num(Integer.parseInt(dataSplit[2])).lng(Integer.parseInt(dataSplit[3]))
                    //        .lat(Integer.parseInt(dataSplit[4])).hp(Integer.parseInt(dataSplit[5]))
                    //       .ammo(Integer.parseInt(dataSplit[6])).lastReportTime(new Date())
                    //        .build();
                    //判断经纬度是否刷新，如果刷新推送到websocket
                    Double lng=Double.parseDouble(dataSplit[3]);
                    Double lat=Double.parseDouble(dataSplit[4]);
                    if (vestEntity.getLat()!=lat || vestEntity.getLng()!= lng){
                        PositionWebSocketModel positionWebSocketModel= PositionWebSocketModel.builder()
                                .mark("1").num(""+vestEntity.getNum())
                                .lat(lat).lng(lng).time(new Date())
                                .build();
                        shootWebSocket.sendMessage(positionWebSocketModel);
                    }

                    //做存亡处理
                    int shootNum=Integer.parseInt(dataSplit[5])+Integer.parseInt(dataSplit[6]);
                    int live = Integer.parseInt(dataSplit[7]);
                    int hp;
                    if(live == 0 || shootNum>=3){
                        hp=0;
                    }else {
                        hp = 100 - (33 * shootNum);
                    }

                    //刷新vestEntityMap中的数据
                    vestEntity.setNum(Integer.parseInt(dataSplit[2]));
                    vestEntity.setLng(Double.parseDouble(dataSplit[3]));
                    vestEntity.setLat(Double.parseDouble(dataSplit[4]));
                    if (hp>=0){
                        vestEntity.setHp(hp);
                    }else {
                        vestEntity.setHp(0);
                    }
                    vestEntity.setAmmo(Integer.parseInt(dataSplit[8]));
                    vestEntity.setLastReportTime(new Date());
                    System.out.println(vestEntity.toString());
                } else {
                    logger.info("【mqtt接收】" + dataSplit[2] + "号马甲不存在");
                }
                this.refreshVest(vestEntity);
            } else {
                logger.info("【mqtt接收】刷新数据格式错误或者不完整！");
            }
        }
        //
        if ("2".equals(dataSplit[0])){}
    }

}
