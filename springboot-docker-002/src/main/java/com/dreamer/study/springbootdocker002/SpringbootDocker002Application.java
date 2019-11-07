package com.dreamer.study.springbootdocker002;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@SpringBootApplication
public class SpringbootDocker002Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootDocker002Application.class, args);
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "title", required = false) String title) {
        return "Hello " + Optional.ofNullable(title).orElse("Docker World");
    }

}
