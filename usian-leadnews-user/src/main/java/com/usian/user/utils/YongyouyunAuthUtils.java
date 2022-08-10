package com.usian.user.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 封装用友云 身份验证 各种接口
 */
public class YongyouyunAuthUtils {
    /**
     * 公用请求方法
     * @return
     */
    public static ResponseEntity<String> conns(String url,String apiCode, Map<String, String> params){
        // 使用RestTemplate 进行远程调用  Java端Ajax
        RestTemplate restTemplate = new RestTemplate();
        //设置请求头信息
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("apicode",apiCode);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        //设置请求参数
        HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(params),httpHeaders);
        //进行请求并返回response
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url,entity,String.class);
        return responseEntity;
    }
}
