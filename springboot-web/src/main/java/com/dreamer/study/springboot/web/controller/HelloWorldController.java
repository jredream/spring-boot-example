package com.dreamer.study.springboot.web.controller;

import com.dreamer.study.springboot.web.component.Author;
import com.dreamer.study.springboot.web.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mac
 */
@Slf4j
@Controller
@RequestMapping("/hello")
public class HelloWorldController {

    @Resource
    private Author author;

    @ResponseBody
    @GetMapping("world")
    public ResponseEntity word() {
        return ResponseEntity.ok(author);
    }

    @GetMapping("world/page")
    public String word2(ModelMap modelMap) {
        List<String> adeptList = new ArrayList<>();
        adeptList.add("啥都不会");
        adeptList.add("就会搬砖");
        adeptList.add("粘贴复制");
        adeptList.add("设计模式是一套可以被反复使用的、多数人知晓的、经过分类编目的、代码设计经验的总结。使用设计模式，是为了可重用代码，让代码更容易被他人理解并且提高代码的可靠性。");
        modelMap.addAttribute("author", author);
        modelMap.addAttribute("adeptList", adeptList);
        return "hello";
    }

    @GetMapping("world/error")
    public ResponseEntity testError() {
        throw new ServiceException(501, "测试消息内容在这里！");
    }

}
