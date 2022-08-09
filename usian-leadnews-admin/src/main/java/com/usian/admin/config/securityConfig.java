package com.usian.admin.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ServletComponentScan("com.usian.common.admin.security")
public class securityConfig {
}
