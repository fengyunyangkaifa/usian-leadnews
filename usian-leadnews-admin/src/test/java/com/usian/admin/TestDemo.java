package com.usian.admin;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.Map;

public class TestDemo {


    @Test
    public void test01(){
        String s="{\n" +
                "\t\"message\": \"成功\",\n" +
                "\t\"data\": {\n" +
                "              \"result\":{\n" +
                "                  \"antiporn\":\"啊哈哈哈哈\",\n" +
                "                  \"terror\":\"嘻嘻嘻嘻嘻嘻\", \n" +
                "                  \"watermark\":[{\"text\":\"神马鬼\"}]\n" +
                "               },\n" +
                "             \"content\":{\n" +
                "                  \"antiporn\":\"啊哈哈哈哈\",\n" +
                "                  \"terror\":\"嘻嘻嘻嘻嘻嘻\", \n" +
                "                  \"watermark\":[{\"text\":\"神马鬼\"}]\n" +
                "               }\n" +
                "          },\n" +
                "\t\"code\": \"601200000\"\n" +
                "}";
        System.out.println(s);
        Map map = JSONObject.parseObject(s, Map.class);
        Object message = map.get("message");
        System.out.println(message.toString());
    }

}
