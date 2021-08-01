package com.hquyyp.controller;

import com.hquyyp.domain.dto.request.CreateBattleSettingRequest;
import com.hquyyp.domain.dto.request.CreateMapRequest;
import com.hquyyp.domain.dto.response.BaseResponse;
import com.hquyyp.domain.query.BattleSettingQueryEntity;
import com.hquyyp.service.BattleService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.logging.Logger;

@Api(tags = {"对局设置接口"})
@RestController
@RequestMapping({"/battle"})
public class BattleController {
    private static final Logger log=Logger.getLogger(com.hquyyp.controller.BattleController.class.getName());

    @Autowired
    private BattleService battleService;


    //@ApiImplicitParam(name = "createBattleSettingRequest",value = "对局设置参数",paramType = "body")
    @PostMapping({"/set"})
    @ApiOperation(value = "创建对局",httpMethod = "POST",notes = "对新建对局进行参数设置")
    public BaseResponse createBattleSetting(@RequestBody CreateBattleSettingRequest createBattleSettingRequest) {
        this.battleService.createBattleSetting(createBattleSettingRequest);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "查询对局设置",httpMethod = "GET",notes = "查询已创建的对局设置")
    @GetMapping({"/set/query"})
    public BaseResponse queryBattleSetting() {
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .data(this.battleService.listBattleSettingByQuery())
                .msg("")
                .build();
    }

    @ApiOperation(value = "查询对局设置",httpMethod = "POST",notes = "分页效果查询所有已创建的对局设置")
    @PostMapping({"/set/querylist"})
    public BaseResponse queryBattleSetting(@RequestBody BattleSettingQueryEntity battleSettingQueryEntity) {
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .data(this.battleService.listBattleSettingByQueryPage(battleSettingQueryEntity))
                .msg("")
                .build();
    }


    @ApiOperation(value = "删除对局设置",httpMethod = "GET",notes = "删除已创建的对局设置")
    @ApiImplicitParams(
        @ApiImplicitParam(name="id",value = "id标识",required = true,paramType = "query", dataType = "String")
    )
    @GetMapping({"/set/delete"})
    public BaseResponse deleteBattleSetting(@RequestParam("id") String id) {
        this.battleService.deleteBattleSetting(id);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }


    @ApiOperation(value = "开始对局按钮",httpMethod = "GET",notes = "选择已创建对局设置id后开启对局")
    @ApiImplicitParams(
            @ApiImplicitParam(name="id",value = "id标识",required = true,paramType = "query", dataType = "String")
    )
    @GetMapping({"/start"})
    public BaseResponse startBattle(@RequestParam("id") String id) {
//        if (this.serialportService.serialPort==null)
//            return BaseResponse.builder()
//            .code(400001)
//            .status("SERIALPORT_ERROR")
//            .msg("串口还未开启")
//            .build();
       // log.info("+ id");
        try {
            this.battleService.startBattle(id);
        } catch (IOException e) {
            return BaseResponse.builder()
                    .code(400003)
                    .status("")
                    .msg("")
                    .build();
        }
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "结束对局按钮",httpMethod = "GET",notes = "结束正在进行的对局")
    @GetMapping({"/end"})
    public BaseResponse endBattle() {
        log.info("");
        this.battleService.endBattle();
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "test",notes = "空测接口")
    @PostMapping({"/test"})
    public BaseResponse test(@RequestBody CreateMapRequest createMapRequest){
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }
}
