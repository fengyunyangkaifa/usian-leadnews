package com.usian.article.config;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.usian.common.knife4jConfig")  //  指定扫描其他模块包做启动扫描（从上向下扫描包）
public class Knife4jConfiguration {

}
