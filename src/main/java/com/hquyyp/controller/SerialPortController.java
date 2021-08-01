package com.hquyyp.controller;

import com.hquyyp.domain.dto.response.BaseResponse;
import com.hquyyp.domain.dto.response.OpenSerialPortResponse;
import com.hquyyp.service.SerialportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"串口管理接口"})
@RestController
@RequestMapping({"/serialPort"})
public class SerialPortController {

    @Autowired
    private SerialportService serialPortService;

    @ApiOperation(value = "开启串口",httpMethod = "GET",notes = "开启串口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="portName",value = "串口名称",dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "baudRate",value = "波特率",dataType = "int",paramType = "query")
    })
    @GetMapping({"/open"})
    public OpenSerialPortResponse openPort(@RequestParam("portName") String portName, @RequestParam("baudRate") int baudRate) {
        return this.serialPortService.openSerialPort(portName, baudRate);
    }

    @ApiOperation(value = "关闭串口",httpMethod = "GET",notes = "关闭串口")
    @GetMapping({"/close"})
    public BaseResponse closePort() {
        this.serialPortService.closePort("");
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "获取串口",httpMethod = "GET",notes = "获取所有可用串口")
    @GetMapping({"/list"})
    public BaseResponse listPort() {
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .data(this.serialPortService.findPorts())
                .msg("")
                .build();
    }

    @ApiOperation(value = "发送数据",httpMethod = "POST",notes = "向当前开启的串口发送数据")
   // @ApiImplicitParam(name = "data",value = "发送的数据",dataType = "String",paramType = "body")
    @PostMapping({"/data"})
    public BaseResponse sendData(@RequestBody String data){
        this.serialPortService.sendData(data.getBytes());
        return  BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "查询串口",httpMethod = "GET",notes = "查询当前串口信息")
    @GetMapping({"/getnew"})
    public BaseResponse getCom() {
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .data(this.serialPortService.getNowComStatus())
                .msg("")
                .build();
    }

    @ApiOperation(value = "暂停串口进程",httpMethod = "GET",notes = "暂停串口通信")
    @GetMapping({"/padding"})
    public BaseResponse padding() {
        this.serialPortService.threadPadding();
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }
}
