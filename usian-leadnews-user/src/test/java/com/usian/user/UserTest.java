package com.usian.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usian.model.user.pojos.ApUserRealname;
import com.usian.user.service.impl.ApUserRealnameServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UserTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApUserRealnameServiceImpl apUserRealnameService;

   //  基于restTemplate调用接口
    //   用户名和省份证号是否一致
    @Test
    public void idcardTest(){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("apicode"," f2648b58d851464da37b1625d35ea99c");
        httpHeaders.setContentType(MediaType.APPLICATION_JSON); // json
        Map<String,String> map = new HashMap<>();
        map.put("idNumber","210103195103222113");
        map.put("userName","王东镇");
        HttpEntity<String> formEntry = new HttpEntity<>(JSON.toJSONString(map), httpHeaders); // 封装请求参数
        ResponseEntity<String> response = restTemplate.postForEntity("https://api.yonyoucloud.com/apis/dst/matchIdentity/matchIdentity", formEntry, String.class);// 发送一个post请求
        System.out.println(response);
    }

    //   OCR 识别 图片信息
    @Test
    public void orcTest(){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("apicode","ff6cee08405445e1889c5767e13ce46f ");
        httpHeaders.setContentType(MediaType.APPLICATION_JSON); // json
        Map<String,String> map = new HashMap<>();
        map.put("image","https://hmtt122.oss-cn-shanghai.aliyuncs.com/demo_idcard.png");
        map.put("imageType","URL");
        map.put("ocrType","0");
        HttpEntity<String> formEntry = new HttpEntity<>(JSON.toJSONString(map), httpHeaders); // 封装请求参数
        ResponseEntity<String> response = restTemplate.postForEntity("https://api.yonyoucloud.com/apis/dst/IdcardOCR/IdcardOCR", formEntry, String.class);// 发送一个post请求
        System.out.println(response);
    }
//    自动审核
//   "{\"message\":\"成功\",\"data\":{\"tradeNo\":\"22080820005558019\",\"code\":\"0\",\"riskType\":\"normal\",\"address\":\"沈阳市东陵区文化东路24-8号1-3-6\",\"birth\":\"19510322\",\"name\":\"王东镇\",\"cardNum\":\"210103195103222113\",\"sex\":\"男\",\"nation\":\"汉\",\"issuingDate\":\"\",\"issuingAuthority\":\"\",\"expiryDate\":\"\"},\"code\":\"601200000\"}"   ORC
//  "{\"success\":true,\"code\":400100,\"message\":\"一致\",\"data\":{\"orderNumber\":\"021659962888389491\"}}"   二要素
//  "{\"message\":\"成功\",\"data\":{\"checkStatus\":\"0\",\"score\":\"84\",\"tradeNo\":\"22080820581666222\",\"remark\":\"检测成功\",\"code\":\"0\"},\"code\":\"601200000\"}"     活体
@Test
public void orcTest1(){
        //   先检测ORC信息，json得到数据
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders httpHeaders = new HttpHeaders();     //   ORC 识别
    httpHeaders.add("apicode","ff6cee08405445e1889c5767e13ce46f");
    httpHeaders.setContentType(MediaType.APPLICATION_JSON); // json
    Map<String,String> orcMap = new HashMap<>();
    orcMap.put("image","https://hmtt122.oss-cn-shanghai.aliyuncs.com/demo_idcard.png");
    orcMap.put("imageType","URL");
    orcMap.put("ocrType","0");
    HttpEntity<String> formEntry = new HttpEntity<>(JSON.toJSONString(orcMap), httpHeaders); // 封装请求参数
    ResponseEntity<String> response = restTemplate.postForEntity("https://api.yonyoucloud.com/apis/dst/IdcardOCR/IdcardOCR", formEntry, String.class);// 发送一个post请求
    System.out.println(response);
    String s="{\"message\":\"成功\",\"data\":{\"tradeNo\":\"22080820005558019\",\"code\":\"0\",\"riskType\":\"normal\",\"address\":\"沈阳市东陵区文化东路24-8号1-3-6\",\"birth\":\"19510322\",\"name\":\"王东镇\",\"cardNum\":\"210103195103222113\",\"sex\":\"男\",\"nation\":\"汉\",\"issuingDate\":\"\",\"issuingAuthority\":\"\",\"expiryDate\":\"\"},\"code\":\"601200000\"}";
//       Map map1 = JSONObject.parseObject(response.getBody(),Map.class);
       Map map1 = JSONObject.parseObject(s,Map.class);
    if ("成功".equals(map1.get("message"))){
        httpHeaders.add("apicode","f2648b58d851464da37b1625d35ea99c");     //   二要素识别
        httpHeaders.setContentType(MediaType.APPLICATION_JSON); // json
        Map twomap = (Map)map1.get("data");
        Map map=new HashMap();
        map.put("idNumber",twomap.get("cardNum"));
        map.put("userName",twomap.get("name"));
        HttpEntity<String> formEntry2 = new HttpEntity<>(JSON.toJSONString(map), httpHeaders); // 封装请求参数
        ResponseEntity<String> response2 = restTemplate.postForEntity("https://api.yonyoucloud.com/apis/dst/matchIdentity/matchIdentity",formEntry2, String.class);// 发送一个post请求
        System.out.println(response2);
        String s2="{\"success\":true,\"code\":400100,\"message\":\"一致\",\"data\":{\"orderNumber\":\"021659962888389491\"}}";
//        Map map2 = JSONObject.parseObject(response2.getBody(),Map.class);
        Map map2 = JSONObject.parseObject(s2,Map.class);
        if ("一致".equals(map2.get("message"))){     //   开始活体检测
            httpHeaders.add("apicode","2e87b9e20228464a9b3e816ffa105ea6");
            httpHeaders.setContentType(MediaType.APPLICATION_JSON); // json
            Map map3=new HashMap();
            map3.put("image","https://hmtt122.oss-cn-shanghai.aliyuncs.com/222.png");   //  活体照片
            map3.put("imageType","URL");  //  类型
            HttpEntity<String> formEntry3 = new HttpEntity<>(JSON.toJSONString(map3), httpHeaders); // 封装请求参数
            ResponseEntity<String> response3 = restTemplate.postForEntity("https://api.yonyoucloud.com/apis/dst/Biologicalexamination/Biologicalexamination", formEntry3, String.class);// 发送一个post请求
            System.out.println(response3);
            String s3= "{\"message\":\"成功\",\"data\":{\"checkStatus\":\"0\",\"score\":\"84\",\"tradeNo\":\"22080820581666222\",\"remark\":\"检测成功\",\"code\":\"0\"},\"code\":\"601200000\"}";
//            Map map4 = JSONObject.parseObject(response3.getBody(),Map.class);
            Map map4 = JSONObject.parseObject(s3,Map.class);
            Map map5=(Map) map4.get("data");
            String score = map5.get("score").toString();
            Integer fen = Integer.parseInt(score);
            if ("成功".equals(map4.get("message")) || fen>=80){   //  是否成功和匹配度
                    ApUserRealname apUserRealname = new ApUserRealname();
                    apUserRealname.setUserId(9);
                    String name =(String) twomap.get("name");
                    apUserRealname.setName(name);
                    String image = orcMap.get("image");
                    apUserRealname.setFontImage(image);
                    apUserRealname.setBackImage(image);
                    apUserRealname.setCreatedTime(new Date());
                    apUserRealname.setStatus((short) 9);
                    apUserRealnameService.save(apUserRealname);
            }else {
                ApUserRealname apUserRealname = new ApUserRealname();
                apUserRealname.setUserId(9);
                String name =(String) twomap.get("name");
                apUserRealname.setName(name);
                    String image = orcMap.get("image");
                    apUserRealname.setFontImage(image);
                    apUserRealname.setBackImage(image);
                apUserRealname.setCreatedTime(new Date());
                apUserRealname.setStatus((short) 8);
                apUserRealnameService.save(apUserRealname);
                System.out.println("活体检测失败");
            }
        }else {
            ApUserRealname apUserRealname = new ApUserRealname();
            apUserRealname.setUserId(9);
            String name =(String) twomap.get("name");
            apUserRealname.setName(name);
                    String image = orcMap.get("image");
                    apUserRealname.setFontImage(image);
                    apUserRealname.setBackImage(image);
            apUserRealname.setCreatedTime(new Date());
            apUserRealname.setStatus((short) 8);
            apUserRealnameService.save(apUserRealname);
            System.out.println("二要素检测失败");
        }
    } else {
        ApUserRealname apUserRealname = new ApUserRealname();
        Map twomap = (Map)map1.get("data");
        apUserRealname.setUserId(9);
        String name =(String) twomap.get("name");
        apUserRealname.setName(name);
                    String image = orcMap.get("image");
                    apUserRealname.setFontImage(image);
                    apUserRealname.setBackImage(image);
        apUserRealname.setCreatedTime(new Date());
        apUserRealname.setStatus((short) 8);
        apUserRealnameService.save(apUserRealname);
        System.out.println("ORC检测失败");
    }
}
}
