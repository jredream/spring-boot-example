package com.dreamer.study.springboot.web.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus配置信息
 *
 * @author mac
 */
@Configuration
@MapperScan("com.dreamer.study.springboot.web.*.**mapper")
public class MyBatisPlusConfig {
}
