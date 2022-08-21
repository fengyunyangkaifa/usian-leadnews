package com.usian.tools;


import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ToolsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ToolsApplication.class,args);
    }

    /**
     * mybatis-plus分页插件
     */

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
