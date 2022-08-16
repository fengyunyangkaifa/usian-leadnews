package com.usian.user.config;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.ComponentScan;

@CacheConfig
@ComponentScan("com.usian.common.feignconfig")
public class FeignConfig {
}
