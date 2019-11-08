# Spring-Boot集成Web服务

## 引入jar包

web依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

模板引擎：thymeleaf依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

读取properties文件
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

lombok简化代码
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

## 统一异常处理

### Controller层的异常

```java
@Slf4j
@Controller
@ControllerAdvice(value = "com.dreamer.study.springweb.controller")
public class MyExceptionHandler {

    /**
     * 业务异常处理
     *
     * @param ex 活动异常
     * @return 错误消息 + 错误码
     */
    @ResponseBody
    @ExceptionHandler(value = {ServiceException.class})
    public ResponseEntity handleServiceException(ServiceException ex) {
        log.error("报错啦：" + ex.getMessage(), ex);
        return ResponseEntity.status(ex.getCode()).body(ex.getMessage());
    }

    /**
     * 其他的异常
     *
     * @param ex 没有权限的异常
     * @return 原始错误消息
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity handleNoPermission(Exception ex, HttpServletRequest request) {
        log.error("报错啦：" + ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity handleMissingServletRequestParameterException(Exception ex) {
        log.error("报错啦：" + ex.getMessage(), ex);
        return ResponseEntity.status(500).body("必填参数不能为空！");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleMissingHttpMessageNotReadableException(Exception ex) {
        log.error("报错啦：" + ex.getMessage(), ex);
        return ResponseEntity.status(500).body("请求数据格式不对！");
    }

    /**
     * hibernate validator 异常处理
     *
     * @param ex hibernate validator 异常
     * @return 错误消息 + 错误码
     */
    @ResponseBody
    @ExceptionHandler(value = {ValidationException.class})
    public ResponseEntity handleValidationException(ValidationException ex) {
        String result = ex.getMessage();
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException cex = (ConstraintViolationException) ex;
            result = cex.getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(","));
        }
        log.error("验证异常：", ex);
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * MethodArgumentNotValidException 异常处理
     *
     * @param ex MethodArgumentNotValidException异常
     * @return 错误消息 + 错误码
     */
    @ResponseBody
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String result = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
        log.error("验证异常：", ex);
        return ResponseEntity.badRequest().body(result);
    }

}
```

其他异常处理，例如:404没有走到Controller中，集成ErrorViewResolver
```java
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
```

### 常用工具类

* [JacksonUtil.java](https://github.com/duanxinyuan/Json-Utils/blob/master/library-json-jackson/src/main/java/com/dxy/library/json/jackson/JacksonUtil.java)
* RequestUtils.java
```java
/**
 * HttpServletRequest工具类
 *
 * @author mac
 */
@Slf4j
public class RequestUtils {

    /**
     * 是否是Ajax请求
     *
     * @param request 请求
     * @return true/false
     */
    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }

    /**
     * 获取IP地址
     * <p>
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR ", e);
        }
        return ip;
    }


    /**
     * 是否支付宝客户端
     *
     * @param request 请求
     * @return true or false
     */
    public static boolean isAliPay(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        log.info("user-agent={}", userAgent);
        return userAgent.contains("Alipay");
    }

}

```

### 集成`Swagger`
* 参考文档：https://www.ibm.com/developerworks/cn/java/j-using-swagger-in-a-spring-boot-project/index.html
* 添加依赖：
```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
```

```xml
<!-- Swagger UI -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
```

* 添加Swagger配置
```java
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dreamer.study.springboot.web.controller"))
                // 加了ApiOperation注解的类，才生成接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger APIs")
                .description("swagger api 管理")
                .termsOfServiceUrl("http://swagger.io/")
                .contact(new Contact("mac", "http://www.xxx.com", "XXXXXXX@qq.com"))
                .version("1.0")
                .build();

    }

}
```
 