package com.dreamer.study.springboot.web.component;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author mac
 */
@Data
@Component
@ApiModel("作者信息")
@ConfigurationProperties(prefix = "author")
@PropertySource("classpath:/author.properties")
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty("电子邮箱")
    private String email;

}
