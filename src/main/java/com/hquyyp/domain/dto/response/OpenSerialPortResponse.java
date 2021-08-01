package com.hquyyp.domain.dto.response;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "串口响应格式")
public class OpenSerialPortResponse extends BaseResponse {
     public static final int NO_SUCH_PORT_CODE = 400001;
     public static final String NO_SUCH_PORT_MSG = "指定端口无设备";
     public static final int PORT_IN_USE_CODE = 400002;
     public static final String PORT_IN_USE_MSG = "指定端口设备被其他设备占用";
     public static final int UNSUPPORTED_COMM_OPERATION_CODE = 400003;
     public static final String UNSUPPORTED_COMM_OPERATION_MSG = "指定端口设备被其他设备占用";
     public static final int NO_DLL_CODE = 400004;
     public static final String NO_DLL_MSG = "RXTX驱动未配置";


    public OpenSerialPortResponse(NoSuchPortException ex) {
        super(400001, "FAIL", "指定端口无设备", null);
    }

    public OpenSerialPortResponse(PortInUseException ex) {
        super(400002, "FAIL", "指定端口设备被其他设备占用", null);
    }

    public OpenSerialPortResponse(UnsupportedCommOperationException ex) {
        super(400003, "FAIL", "指定端口设备被其他设备占用", null);
    }

    public OpenSerialPortResponse(UnsatisfiedLinkError e) {
        super(400004, "FAIL", "RXTX驱动未配置", null);
    }

    public OpenSerialPortResponse() {
        super(200, "SUCCESS", "成功连接指定端口设备", null);
    }
}


