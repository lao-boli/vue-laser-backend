package com.hquyyp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hquyyp.domain.query.BaseQueryEntity;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * <p>
 * ChirpStack服务类
 * <p>
 *
 * @author liulingyu
 * @date 2022/5/24 17:14
 * @version 1.0
 */
@Service
@ConfigurationProperties(prefix = "chirp-stack")
@Data
public class ChirpStackService {
    private static final Logger log = Logger.getLogger(com.hquyyp.service.ChirpStackService.class.getName());

    private String baseURL;
    private String email;
    private String password;
    private String applicationID;

    private String jwt = "";


    @Autowired
    private RestTemplate restTemplate;

    /**
     * <p>
     * 登录ChirpStack服务器
     * </p>
     *
     * @date 2022/5/24 20:53 <br>
     * @author liulingyu <br>
     */
    public void login() {
        StringBuffer urlBuf = new StringBuffer(baseURL);

        urlBuf.append("/api/internal/login");

        Map requestMap = new HashMap<>(16);

        requestMap.put("email", email);
        requestMap.put("password", password);

        JSONObject body = restTemplate.postForEntity(urlBuf.toString() , requestMap, JSONObject.class).getBody();

        jwt = (String) body.get("jwt");


        log.info(JSON.toJSONString(body));

    }

    /**
     * <p>
     * 从ChirpStack服务器获取的设备列表
     * </p>
     *
     * @param query 查询参数,包含页数和页码
     * @return {@link Map<String,Object>}包含设备列表和总设备数
     * @date 2022/5/24 20:53 <br>
     * @author liulingyu <br>
     */
    public Map<String,Object> getDeviceList(BaseQueryEntity query) {
        login();

        StringBuffer urlBuf = new StringBuffer(baseURL);

        urlBuf.append("/api/devices");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlBuf.toString());


        HttpHeaders headers = new HttpHeaders();
        headers.set("Grpc-Metadata-Authorization", jwt);


        Map<String,Object> requestMap = new HashMap<>(16);

        requestMap.put("limit", query.getPageSize());
        requestMap.put("applicationID", applicationID);

        if (query.getPage() > 1) {
            requestMap.put("offset", query.getPageSize() * (query.getPage() - 1));

        }

        if (!requestMap.isEmpty()) {
            requestMap.forEach((key, value) -> {
                builder.queryParam((String) key, value);
            });
        }


        HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(builder.build().toString(), HttpMethod.GET, httpEntity, JSONObject.class);


        Map<String,Object> resultMap = new HashMap<>(16);

        resultMap.put("total", responseEntity.getBody().get("totalCount"));
        resultMap.put("deviceList", responseEntity.getBody().get("result"));

        return resultMap;
    }

    /**
     * <p>
     * 获取所有设备的EUI
     * </p>
     *
     * @return {@link Map<String,Object>} 设备EUI列表
     * @date 2022/5/26 17:34 <br>
     * @author liulingyu <br>
     */

    public Map<String,Object> getDevEUIList() {
        Map deviceList = getDeviceList(new BaseQueryEntity(1, 999));

        List<String> devEUIList = ((List<LinkedHashMap<String, String>>) deviceList.get("deviceList"))

                .stream()
                .map(device -> device.get("devEUI"))
                .collect(Collectors.toList());


        Map<String,Object> resultMap = new HashMap<>(16);

        resultMap.put("devEUIList", devEUIList);

        return resultMap;

    }

    /**
     * <p>
     * 通过[devEUI]删除设备
     * </p>
     *
     * @param devEUI 设备uid
     * @date 2022/5/25 20:17 <br>
     * @author liulingyu <br>
     */
    public void deleteDeviceByEUI(String devEUI) {
        login();
        StringBuffer urlBuf = new StringBuffer(baseURL);

        urlBuf.append("/api/devices/");

        urlBuf.append(devEUI);


        HttpHeaders headers = new HttpHeaders();
        headers.set("Grpc-Metadata-Authorization", jwt);

        HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);

        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(urlBuf.toString(), HttpMethod.DELETE, httpEntity, JSONObject.class);


    }
}
