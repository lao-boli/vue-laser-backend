package com.hquyyp.controller;

import com.hquyyp.config.MqttConfig;
import com.hquyyp.domain.dto.response.BaseResponse;
import com.hquyyp.service.NewVestService;
import com.sun.jersey.core.util.Base64;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@Api(tags = {"新马甲数据接口"})
@RestController
@RequestMapping({"/newvest"})
public class NewVestController {
    private static final Logger log = Logger.getLogger(NewVestController.class.getName());



    @Autowired
    private NewVestService vestService;


    @ApiOperation(value = "新当前所有马甲数据 ",httpMethod = "GET")
    @GetMapping("/newlist")
    public BaseResponse listAllNewVest() {
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .data(this.vestService.listAllNewVest())
                .msg("")
                .build();
    }


    @GetMapping({"/newget"})
    @ApiOperation(value = "根据人员编号获取数据",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="vestNum",value = "人员编号",dataType = "int",paramType = "query")
    })
    public BaseResponse getNewVest(@RequestParam Integer vestNum){
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .data(this.vestService.getNewVest(vestNum))
                .msg("")
                .build();
    }


    @ApiOperation(value = "新阵亡指令",httpMethod = "GET",notes = "指定编号的人员阵亡")
    @ApiImplicitParams({
            @ApiImplicitParam(name="vestNum",value = "人员编号",dataType = "int",paramType = "query"),
    })
    @GetMapping({"/newdie"})
    public BaseResponse sendNewVestDie(@RequestParam("vestNum") int vestNum) {
        //  log.info("+ vestNum + ");
       // this.vestService.newSendDieData(vestNum);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }


    @ApiOperation(value = "新受伤指令",httpMethod = "GET",notes = "指定编号的人员受伤等级")
    @ApiImplicitParams({
            @ApiImplicitParam(name="vestNum",value = "人员编号",dataType = "int",paramType = "query"),
            @ApiImplicitParam(name="injure",value = "受伤等级：1-轻伤,2-重伤",dataType = "int",paramType = "query")
    })
    @GetMapping({"/newinjure"})
    public BaseResponse NewSendVestInjure(@RequestParam("vestNum") int vestNum,@RequestParam("injure")int injure) {
        // log.info("+ vestNum + ");
        //this.vestService.newSendDieInjure(vestNum,injure);

        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @GetMapping({"/test1"})
    public BaseResponse test1() {
//        MqttConfig mqtt = new MqttConfig();
//        byte[] bytes = {0x40,0x00,0x00,0x01,0x00,0x02,0x5B};
//        String s = mqtt.HexToString(bytes);
//        vestService.handleVestMqData(s);
        String data = "91 1 5 2 1";
        vestService.handleVestMqData(data);

        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }
    @GetMapping({"/test2"})
    public BaseResponse test2() {
        String data = "91 10 2 4 1";
        vestService.handleVestMqData(data);

        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @GetMapping({"/test3"})
    public BaseResponse test3() {
        vestService.handleVestMqData("138 2 0.1 0.0 1 0 10");

        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }
    @ApiOperation(value = "新全部装弹",httpMethod = "GET",notes="给全部人员装上指定数量弹药")
    @ApiImplicitParams({
            @ApiImplicitParam(name="ammoNum",value = "弹药数量",dataType = "int",paramType = "query")
    })
    @GetMapping({"/newloadall"})
    public BaseResponse NewAllLoadAmmo(@RequestParam("ammoNum") int ammoNum){
        this.vestService.NewAllLoadAmmo(ammoNum);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "新给指定马甲装弹",httpMethod = "GET",notes="给指定人员的马甲装上指定数量弹药")
    @ApiImplicitParams({
            @ApiImplicitParam(name="vestNum",value = "人员编号",dataType = "int",paramType = "query"),
            @ApiImplicitParam(name="ammoNum",value = "弹药数量",dataType = "int",paramType = "query")
    })
    @GetMapping({"/newloadone"})
    public BaseResponse NewLoadAmmo(@RequestParam("ammoNum") int ammoNum,@RequestParam("vestNum") int vestNum){
        this.vestService.NewLoadAmmo(vestNum,ammoNum);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }
}
