package com.hquyyp.controller;

import com.hquyyp.domain.dto.response.BaseResponse;
import com.hquyyp.domain.query.BaseQueryEntity;
import com.hquyyp.service.ChirpStackService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;


/**
 * <p>
 *      ChirpStack服务控制器
 * <p>
 *
 * @author liulingyu
 * @date 2022/5/24 17:24
 * @Version 1.0
 */
@RestController
@RequestMapping("/chirpStack")
public class ChirpStackController {
    private static final Logger log = Logger.getLogger(com.hquyyp.controller.ChirpStackController.class.getName());


    @Autowired
    private ChirpStackService chirpStackService;

    @GetMapping("/login")
    public BaseResponse login(){

        chirpStackService.login();

        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @GetMapping("/get-devices")
    public BaseResponse getDevices(@RequestBody BaseQueryEntity query){

        Map deviceList = chirpStackService.getDeviceList(query);

        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .data(deviceList)
                .build();
    }
    @GetMapping("/get-devEUI-list")
    public BaseResponse getDevEUIList(){

        Map devEUIList = chirpStackService.getDevEUIList();

        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .data(devEUIList)
                .build();
    }

    @GetMapping("/delete-device")
    public BaseResponse deleteDevice(String devEUI){

        try {
            chirpStackService.deleteDeviceByEUI(devEUI);
        } catch (HttpClientErrorException e) {

            log.error(e.getMessage());
            return BaseResponse.builder()
                    .code(404)
                    .status("fail")
                    .msg("要删除的设备不存在")
                    .build();
        }

        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }
}
