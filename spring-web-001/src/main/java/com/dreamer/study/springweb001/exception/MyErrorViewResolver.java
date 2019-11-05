package com.dreamer.study.springweb001.exception;

import com.dreamer.study.springweb001.utils.JacksonUtil;
import com.dreamer.study.springweb001.utils.RequestUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 错误页面处理
 *
 * @author mac
 */
@Slf4j
@RestController
public class MyErrorViewResolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request,
                                         HttpStatus status,
                                         Map<String, Object> model) {
        String errorContent = JacksonUtil.to(model);
        log.error("请求报错: status={} IP={}", status, RequestUtils.getIpAddr(request));
        log.error("请求报错: model={} ", errorContent);

        Map<String, Object> resultMap = new HashMap<>(model);
        resultMap.put("errorContent", model);
        JacksonUtil.from(errorContent, JsonNode.class);
        return new ModelAndView("error", resultMap);
    }

}