package com.hquyyp.service;

import com.hquyyp.dao.ComEntityMapper;
import com.hquyyp.domain.dto.response.OpenSerialPortResponse;
import com.hquyyp.domain.entity.ComEntity;
import com.hquyyp.domain.entity.SendDataMissionEntity;
import com.hquyyp.domain.entity.VestEntity;
import com.hquyyp.domain.model.PositionWebSocketModel;
import com.hquyyp.domain.model.ShootWebSocketModel;
import com.hquyyp.thread.SerialPortThread;
import com.hquyyp.utils.OutsiderUtil;
import com.hquyyp.websocket.ShootWebSocket;
import gnu.io.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

@Service
public class SerialportService implements SerialPortEventListener{
    private static final Logger log = Logger.getLogger(SerialportService.class.getName());

    public SerialPort serialPort;

    @Value("${pollingTimeOut}")
    private int pollingTimeOut;//100

    @Autowired
    private VestService vestService;

    @Autowired
    private ShootWebSocket shootWebSocket;

    @Autowired
    private ComEntityMapper comEntityMapper;

    //马甲轮询队列
    private BlockingQueue<SendDataMissionEntity> rollingMissionBlockingQueue;

    //上级（controller）指令队列？
    private BlockingQueue<SendDataMissionEntity> highLevelMissionBlockingQueue;

    private SerialPortThread serialPortThread;

    private ExecutorService serialPortPool = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        this.comEntityMapper.clearComTable();
    }

    //串口进程控制
    public void threadPadding() {
        System.out.println("threadpadding");
        if (this.serialPortThread != null) {
            this.serialPortThread.setPadding(true);
        }
    }

    public OpenSerialPortResponse openSerialPort(String serialPortName, int baudRate) {
        if(this.serialPort!=null)
            closePort("");
        try {
            /*识别串口并开启*/
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(serialPortName);
            CommPort commPort = portIdentifier.open(serialPortName, 2222);
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(baudRate, 8, 1, 0);
                log.info("【串口管理】开启串口成功，串口名称：" + serialPortName);
                this.serialPort=serialPort;

                /*给串口设置listener*/
                setListenerToSerialPort(serialPort);

                /*刷新库中的串口数据*/
                this.serialPort = serialPort;
                this.comEntityMapper.clearComTable();
                ComEntity comEntity = ComEntity.builder().id(OutsiderUtil.getUUID()).baudRate(Integer.valueOf(baudRate)).portName(serialPortName).status(Integer.valueOf(0)).build();
                this.comEntityMapper.insertSelective(comEntity);

                /*实时推送串口状态*/
                /*开启串口时开启新thread*/
                this.rollingMissionBlockingQueue = new LinkedBlockingDeque<>();
                this.highLevelMissionBlockingQueue = new LinkedBlockingDeque<>();
                SerialPortThread serialPortThread = new SerialPortThread(this.rollingMissionBlockingQueue, this.highLevelMissionBlockingQueue, this, this.pollingTimeOut);
                this.serialPortPool.submit((Runnable) serialPortThread);
                this.serialPortThread = serialPortThread;
                return new OpenSerialPortResponse();
            }
            throw new NoSuchPortException();
        } catch (NoSuchPortException e) {
            return new OpenSerialPortResponse(e);
        } catch (PortInUseException e) {
            return new OpenSerialPortResponse(e);
        } catch (UnsupportedCommOperationException e) {
            return new OpenSerialPortResponse(e);
        } catch (UnsatisfiedLinkError e) {
            return new OpenSerialPortResponse(e);
        }
    }

    private byte[] readData() {
        InputStream is = null;
        byte[] bytes = null;
        try {
            is = this.serialPort.getInputStream();
            int bufflenth = is.available();
            while (bufflenth != 0) {
                bytes = new byte[bufflenth];
                is.read(bytes);
                bufflenth = is.available();
            }
           // log.info("【串口管理】接收" + new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getMessage().contains("nativeavailable")) {
                log.info("【串口管理】串口通讯发生错误 关闭串口");
                closePort("意外拔出");
            }
        }
        return bytes;
    }

    public void closePort(String msg) {
        log.info("【串口管理】关闭串口");
        if (this.serialPort != null) {
            this.serialPort.removeEventListener();
            this.serialPort.close();
            this.serialPort = null;
        }
        ComEntity comEntity = this.comEntityMapper.getComEntity();
        if (comEntity != null) {
            comEntity.setMsg(msg);
            comEntity.setStatus(Integer.valueOf(1));
            this.comEntityMapper.updateByPrimaryKey(comEntity);
        }
        if (this.serialPortThread !=null){
            this.serialPortThread.stop();
            this.rollingMissionBlockingQueue = null;
            this.highLevelMissionBlockingQueue = null;
            this.serialPortPool = Executors.newSingleThreadExecutor();
        }
    }

    public synchronized void sendData(byte[] data) {
         log.info("【串口管理】发送" + new String(data, StandardCharsets.UTF_8));
        OutputStream os = null;
        try {
            if(this.serialPort==null) System.out.println("null");
            os = this.serialPort.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ComEntity getNowComStatus() {
        return this.comEntityMapper.getComEntity();
    }

    public ArrayList<String> findPorts() {
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> portNameList = new ArrayList<>();

        while (portList.hasMoreElements()) {
            String portName = ((CommPortIdentifier) portList.nextElement()).getName();
            portNameList.add(portName);
        }
        return portNameList;
    }

    private void setListenerToSerialPort(SerialPort serialPort) {
        try {
            serialPort.addEventListener(this);
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
        serialPort.notifyOnDataAvailable(true);
        serialPort.notifyOnBreakInterrupt(true);
    }

    public void submitHighLevelMission(SendDataMissionEntity sendDataMissionEntity) {
        if (this.highLevelMissionBlockingQueue != null) {
            this.highLevelMissionBlockingQueue.add(sendDataMissionEntity);
        }
    }

    public void submitRollingMission(SendDataMissionEntity sendDataMissionEntity) {
        if (this.rollingMissionBlockingQueue != null) {
            this.rollingMissionBlockingQueue.add(sendDataMissionEntity);
        }
    }

    public BlockingQueue<SendDataMissionEntity> getRollingMissionBlockingQueue() {
        return this.rollingMissionBlockingQueue;
    }

    //监听器（做数据接收处理）
    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        String data=new String(readData());
        log.info("【mqtt接收】接收"+data);
        String[] dataSplit=null;
        //拆分数据成字符串数组
        try{
            dataSplit = data.split(" ");
        }catch (Exception e){
            log.info("【串口接收】数据格式不符合规范");
        }
        //数据类型 射击者 被射击者 击中部位
        //    0     1       2       3
        if("0".equals(dataSplit[0])) {
            //射击逻辑
            if (dataSplit.length == 4) {
                //射击者
                VestEntity shooter = this.vestService.vestEntityMap.get(Integer.parseInt(dataSplit[1]));
                //被射击者
                VestEntity shootee=this.vestService.vestEntityMap.get(Integer.parseInt(dataSplit[2]));
                if (shooter!=null && shootee!=null){
                    //  if (!shootee.getTeam().equals(shooter.getTeam())){
                    //首先刷新被击中者血量
                    if (shootee.getHp() > 34) {
                        shootee.setHp(shootee.getHp()-33);
                    } else if (shootee.getHp()<=34){
                        shootee.setHp(0);
                    }
                    this.vestService.refreshVest(shootee);
                    //处理射击数据并做推送处理
                    log.info("【射击信息】"+shooter.getTeam()+"队的"+shooter.getNum()+"号击中"+shootee.getTeam()+"队的"+shootee.getNum()+"号的"+dataSplit[3]+"号部位");
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
                    log.info("【串口接收】射击者或被射击者不存在");
                }
            } else {
                log.info("【串口接收】射击数据格式错误或者不完整！");
            }
        }
        //数据类型0 队伍1 编号2 经度lng3 维度lat4 背甲中弹数5 头盔中弹数6 是否阵亡7 弹药8
        //    1     2   3     4       5        6        7         8     9
        if ("1".equals(dataSplit[0])) {
            if (dataSplit.length == 9) {
                VestEntity vestEntity = this.vestService.vestEntityMap.get(Integer.parseInt(dataSplit[2]));
                String team = dataSplit[1];
                if (vestEntity != null) {
                    if ("1".equals(team)) {
                        vestEntity.setTeam("red");
                    } else if ("0".equals(team)) {
                        vestEntity.setTeam("blue");
                    } else {
                        log.info("【串口接收】队伍数据解析出错");
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
                    log.info("【串口接收】" + dataSplit[2] + "号马甲不存在");
                }
                this.vestService.refreshVest(vestEntity);
            } else {
                log.info("【串口接收】刷新数据格式错误或者不完整！");
            }
        }
        //
        if ("2".equals(dataSplit[0])){}
    }


}
