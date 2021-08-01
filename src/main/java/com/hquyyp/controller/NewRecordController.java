package com.hquyyp.controller;

import com.hquyyp.domain.dto.response.BaseResponse;
import com.hquyyp.domain.entity.NewRecordEntity;
import com.hquyyp.domain.query.RecordQueryEntity;
import com.hquyyp.service.NewBattleService;
import com.hquyyp.service.NewRecordService;
import com.hquyyp.service.RecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(tags = {"新演习回放接口"})
@RestController
@RequestMapping({"/newrecord"})
public class NewRecordController {

    @Autowired
    NewRecordService newRecordService;

    @Autowired
    NewBattleService newBattleService;

    @ApiOperation(value = "上传演习视频",httpMethod = "POST",notes = "上传演习视频")
    @ApiImplicitParams({
          //  @ApiImplicitParam(name = "BattleName",value = "指明是哪场对局的演习记录",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "VideoName",value = "该场对局的回放视频名称（包含扩展名——MP4、avi等）",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "md5",value = "视频的MD5值，作为分片上传，是否为同一文件的判断辨识",required = true,dataType = "string",paramType = "query"),
            @ApiImplicitParam(name = "size",value = "视频大小",required = true,dataType = "long",paramType = "query"),
            @ApiImplicitParam(name = "chunks",value = "分片上传的总分片数",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "chunk",value = "分片上传的当前片编号",required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "file",value = "视频数据主体，类型为multipartFile",required = true),
    })
    @PostMapping({"/upload"})
    public BaseResponse chunkUpload(String VideoName, String md5, Long size, Integer chunks,
                                    Integer chunk, @RequestParam("file") MultipartFile multipartFile){
        this.newRecordService.newChunkUpload(VideoName, md5, size, chunks, chunk, multipartFile);
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .build();
    }

    @ApiOperation(value = "请求视频",httpMethod = "GET",notes = "根据id请求视频数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "所需要请求的视频id", required = true, dataType = "string", paramType = "query"),
    })
    @GetMapping({"/play"})
    public void play(@RequestParam("id") String id,HttpServletRequest request, HttpServletResponse response) {
        this.newRecordService.play(id,request,response);
    }

    @ApiOperation(value = "根据分页进行查询",httpMethod = "POST",notes = "根据分页进行查询")
    @PostMapping({"/querylist"})
    public BaseResponse queryList(@RequestBody RecordQueryEntity recordQueryEntity){
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .data(this.newRecordService.listRecordQueryByPage(recordQueryEntity))
                .build();
    }

    @ApiOperation(value = "统计数据",httpMethod = "GET",notes = "获取当前统计数据")
    @GetMapping({"/recordlist"})
    public BaseResponse RecordList(){
        List<NewRecordEntity> recordData;
        if(this.newRecordService.getRecordData()!=null){
            recordData=this.newRecordService.getRecordData();
        }else{
            recordData=this.newBattleService.getRecordData();
        }
        return BaseResponse.builder()
                .code(200)
                .status("SUCCESS")
                .msg("")
                .data(recordData)
                .build();
    }

}
