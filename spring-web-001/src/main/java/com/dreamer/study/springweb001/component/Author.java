package com.dreamer.study.springweb001.component;

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
@ConfigurationProperties(prefix = "author")
@PropertySource("classpath:/author.properties")
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Integer age;

    private String email;

}
