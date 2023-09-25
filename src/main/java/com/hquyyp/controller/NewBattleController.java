package com.hquyyp.controller;

import com.hquyyp.domain.dto.request.CreateMapRequest;
import com.hquyyp.domain.dto.request.CreateNewBattleSettingRequest;
import com.hquyyp.domain.dto.response.BaseResponse;
import com.hquyyp.domain.query.BattleSettingQueryEntity;
import com.hquyyp.service.NewBattleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@Api(tags = {"新对局设置接口"})
@RestController
@RequestMapping({"/newbattle"})
public class NewBattleController {

    private static final Logger log = Logger.getLogger(NewBattleController.class.getName());

    @Autowired
    private NewBattleService battleService;

    //@ApiImplicitParam(name = "createBattleSettingRequest",value = "对局设置参数",paramType = "body")
    @PostMapping({"/newset"})
    @ApiOperation(value = "新创建对局", httpMethod = "POST", notes = "对新建对局进行参数设置")
    public BaseResponse NewcreateBattleSetting(@RequestBody CreateNewBattleSettingRequest createBattleSettingRequest) {
        this.battleService.createNewBattleSetting(createBattleSettingRequest);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "新查询对局设置", httpMethod = "POST", notes = "分页效果查询所有已创建的新对局设置")
    @PostMapping({"/set/newquerylist"})
    public BaseResponse NewqueryBattleSetting(@RequestBody BattleSettingQueryEntity battleSettingQueryEntity) {
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .data(this.battleService.newListBattleSettingByQueryPage(battleSettingQueryEntity))
                .msg("")
                .build();
    }

    @ApiOperation(value = "新删除对局设置", httpMethod = "GET", notes = "删除已创建的对局设置")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "id标识", required = true, paramType = "query", dataType = "String")
    )
    @GetMapping({"/set/newdelete"})
    public BaseResponse NewDeleteBattleSetting(@RequestParam("id") String id) {
        this.battleService.NewDeleteBattleSetting(id);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }


    @ApiOperation(value = "新修改对局设置", httpMethod = "POST", notes = "修改已创建的对局设置")
    @PostMapping({"/set/newupdate"})
    public BaseResponse NewUpdate(@RequestBody CreateNewBattleSettingRequest createBattleSettingRequest) {
        this.battleService.updateNewBattleSetting(createBattleSettingRequest);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "新初始化对局按钮", httpMethod = "GET", notes = "选择已创建对局设置id后开启对局")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "id标识", required = true, paramType = "query", dataType = "String")
    )
    @GetMapping({"/newinit"})
    public BaseResponse newInitBattle(@RequestParam("id") String id) {
        try {
            this.battleService.newInitBattle(id);
        } catch (Exception e) {
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

    @ApiOperation(value = "新开始对局按钮", httpMethod = "GET", notes = "选择已创建对局设置id后开启对局")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "id标识", required = true, paramType = "query", dataType = "String")
    )
    @GetMapping({"/newstart"})
    public BaseResponse newStartBattle(@RequestParam("id") String id) {
        //        if (this.serialportService.serialPort==null)
        //            return BaseResponse.builder()
        //            .code(400001)
        //            .status("SERIALPORT_ERROR")
        //            .msg("串口还未开启")
        //            .build();
        // log.info("+ id");
        try {
            this.battleService.newStartBattle(id);
        } catch (IOException e) {
            return BaseResponse.builder()
                    .code(400003)
                    .status("")
                    .msg("")
                    .build();

        } catch (NoSuchElementException e) {
            // handle map not exist when start a battle
            return BaseResponse.builder()
                    .code(400002)
                    .status("")
                    .msg(e.getMessage())
                    .build();
        }
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }


    @ApiOperation(value = "新结束对局按钮", httpMethod = "GET", notes = "结束正在进行的对局")
    @GetMapping({"/newend"})
    public BaseResponse NewEndBattle() {
        log.info("");
        this.battleService.newEndBattle();
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "test", notes = "空测接口")
    @PostMapping({"/test"})
    public BaseResponse test(@RequestBody CreateMapRequest createMapRequest) {
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

}
