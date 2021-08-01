package com.hquyyp.controller;

import com.hquyyp.domain.dto.response.BaseResponse;
import com.hquyyp.service.VestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@Api(tags = {"马甲数据接口"})
@RestController
@RequestMapping({"/vest"})
public class VestController {
    private static final Logger log = Logger.getLogger(com.hquyyp.controller.VestController.class.getName());


    @Autowired
    VestService vestService;

    @ApiOperation(value = "当前所有马甲数据 ",httpMethod = "GET")
    @GetMapping("/list")
    public BaseResponse listAllVest() {
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .data(this.vestService.listAllVest())
                .msg("")
                .build();
    }

    @GetMapping({"/get"})
    @ApiOperation(value = "根据马甲编号获取数据",httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name="vestNum",value = "马甲编号",dataType = "int",paramType = "query")
    })
    public BaseResponse getVest(@RequestParam Integer vestNum){
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .data(this.vestService.getVest(vestNum))
                .msg("")
                .build();
    }

    @ApiOperation(value = "阵亡指令",httpMethod = "GET",notes = "指定编号的马甲阵亡")
    @ApiImplicitParams({
            @ApiImplicitParam(name="vestNum",value = "马甲编号",dataType = "int",paramType = "query"),
    })
    @GetMapping({"/die"})
    public BaseResponse sendVestDie(@RequestParam("vestNum") int vestNum) {
        //  log.info("+ vestNum + ");
        this.vestService.sendDieData(vestNum);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "受伤指令",httpMethod = "GET",notes = "指定编号的马甲受伤等级")
    @ApiImplicitParams({
            @ApiImplicitParam(name="vestNum",value = "马甲编号",dataType = "int",paramType = "query"),
            @ApiImplicitParam(name="injure",value = "受伤等级：1-轻伤,2-重伤",dataType = "int",paramType = "query")
    })
    @GetMapping({"/injure"})
    public BaseResponse sendVestInjure(@RequestParam("vestNum") int vestNum,@RequestParam("injure")int injure) {
       // log.info("+ vestNum + ");
        this.vestService.sendDieInjure(vestNum,injure);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "全部装弹",httpMethod = "GET",notes="给全部马甲装上指定数量弹药")
    @ApiImplicitParams({
            @ApiImplicitParam(name="ammoNum",value = "弹药数量",dataType = "int",paramType = "query")
    })
    @GetMapping({"/loadall"})
    public BaseResponse AllLoadAmmo(@RequestParam("ammoNum") int ammoNum){
        this.vestService.AllLoadAmmo(ammoNum);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "给指定马甲装弹",httpMethod = "GET",notes="给指定编号的马甲装上指定数量弹药")
    @ApiImplicitParams({
            @ApiImplicitParam(name="vestNum",value = "马甲编号",dataType = "int",paramType = "query"),
            @ApiImplicitParam(name="ammoNum",value = "弹药数量",dataType = "int",paramType = "query")
    })
    @GetMapping({"/loadone"})
    public BaseResponse LoadAmmo(@RequestParam("ammoNum") int ammoNum,@RequestParam("vestNum") int vestNum){
        this.vestService.loadAmmo(vestNum,ammoNum);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }
}
