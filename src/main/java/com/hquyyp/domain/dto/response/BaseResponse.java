/*    */
package com.hquyyp.domain.dto.response;


import com.hquyyp.domain.dto.BaseRestfulEntity;
import io.swagger.annotations.ApiModel;
import lombok.Builder;


@Builder
@ApiModel(value = "基本响应格式")
public class BaseResponse extends BaseRestfulEntity {
     public static final int SUCCESS_CODE = 200;
     public static final int SERIALPORT_ERROR_CODE=400001;
     public static final int CREATE_MAP_ERROR_CODE = 400002;
     public static final int MAP_FILE_ERROR_CODE = 400003;
     public static final int NET_ERROR_CODE = 400004;
     public static final String EMPTY_FILE_MSG = "空文件";
     public static final String CREATE_MAP_ERROR_MSG = "地图创建出错";

    public static final String MAP_FILE_ERROR_MSG = "本地地图文件出错";
    public static final String SUCCESS_STATUS = "SUCCESS";
    public static final String FAIL_STATUS = "FAIL";
    public static final String NET_ERROR_MSG = "无法连接至服务器，请检查本机网络是否联网。";
    private int code;
    private String status;
    private String msg;
    private Object data;

    public void setCode(int code) {
        this.code = code;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public void setData(Object data) {
        this.data = data;
    }

    public BaseResponse() {
    }

    public BaseResponse(int code, String status, String msg, Object data) {
        this.code = code;
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static BaseResponseBuilder builder() {
        return new BaseResponseBuilder();
    }

    public int getCode() { return this.code; }
    public String getStatus() {
        return this.status;
    }
    public String getMsg() {
        return this.msg;
    }
    public Object getData() { return this.data; }
}


