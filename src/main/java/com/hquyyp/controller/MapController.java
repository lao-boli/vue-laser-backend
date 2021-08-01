package com.hquyyp.controller;
import com.alibaba.fastjson.JSONObject;
import com.hquyyp.domain.dto.request.CreateMapRequest;
import com.hquyyp.domain.dto.response.BaseResponse;
import com.hquyyp.domain.query.MapQueryEntity;
import com.hquyyp.service.MapService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.logging.Logger;

@Api(tags = {"地图管理接口"})
@RestController
@RequestMapping({"/map"})
public class MapController {
    private static final Logger log=Logger.getLogger(com.hquyyp.controller.MapController.class.getName());

    @Autowired
    MapService mapService;

    @ApiOperation(value = "创建地图",httpMethod = "POST",notes = "创建新地图")
    @ApiImplicitParams({
          // @ApiImplicitParam(name = "map",paramType = "file",dataType = "file"),
          @ApiImplicitParam(name = "CreateMapRequest",value = "地图设置参数",paramType = "query")
    })
    @PostMapping({""})
    public BaseResponse addMap(@RequestParam("map") MultipartFile map,@RequestParam("CreateMapRequest") String str) {
        CreateMapRequest createMapRequest = (CreateMapRequest) JSONObject.parseObject(str, CreateMapRequest.class);
        try {
            this.mapService.addMap(map, createMapRequest);
           // System.out.println("enter2");
            return BaseResponse.builder()
                    .code(200)
                    .status("SUCCESS")
                    .msg("")
                    .build();
        } catch (Exception e) {
            return BaseResponse.builder()
                    .code(400002)
                    .status("FAIL")
                    .msg("" + e.getMessage())
                    .build();
        }
    }

    @ApiOperation(value = "删除地图",httpMethod = "DELETE",notes = "删除已创建地图地图")
    //@ApiImplicitParam(name = "id",value = "地图id标识",required = true,dataType = "string",paramType = "query")
    @DeleteMapping({"{id}"})
    public BaseResponse deleteMap(@PathVariable("id") String id) {
       // System.out.println("enter1");
        this.mapService.deleteMap(id);
            return BaseResponse.builder()
                   .code(200)
                  .status("SUCCESS")
                  .msg("")
                  .build();
    }

    @ApiOperation(value = "查询指定id地图",httpMethod = "GET",notes = "根据id查询已创建的地图")
    @ApiImplicitParam(name = "id",value = "地图id标识",required = true,dataType = "string",paramType = "query")
    @GetMapping({"/get"})
    public BaseResponse getMap(@RequestParam("id") String id) {
        System.out.println("enter1");
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .data(this.mapService.getMap(id))
                .build();
    }

    @ApiOperation(value = "查询所有地图",httpMethod = "GET",notes = "查询所有已创建的地图")
    @GetMapping({"/query"})
    public BaseResponse query() {
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .data(this.mapService.listMapByQuery())
                .build();
    }

    @ApiOperation(value = "查询所有地图",httpMethod = "POST",notes = "分页效果查询所有已创建的地图")
    //@ApiImplicitParam(name = "mapQueryEntity",value = "地图id标识",required = true,dataType = "string",paramType = "query")
    @PostMapping({"/querylist"})
    public BaseResponse queryList(@RequestBody MapQueryEntity mapQueryEntity) {
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .data(this.mapService.listMapByQueryPage(mapQueryEntity))
                .build();
    }



}
