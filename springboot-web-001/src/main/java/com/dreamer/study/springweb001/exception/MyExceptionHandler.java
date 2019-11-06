package com.dreamer.study.springweb001.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.stream.Collectors;

/**
 * 通用异常处理
 *
 * @author mac
 */
@Slf4j
@Controller
@ControllerAdvice(value = "com.dreamer.study.springweb001.controller")
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

//    @ExceptionHandler(DuplicateKeyException.class)
//    public ResponseEntity handleDuplicateKeyException(DuplicateKeyException ex) {
//        log.error("报错啦：" + ex.getMessage(), ex);
//        return ResponseEntity.badRequest().body("数据库中已存在该记录");
//    }

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

//    /**
//     * 授权异常
//     * <p>
//     * org.apache.shiro.authz.UnauthenticatedException  授权异常
//     * org.apache.shiro.authz.HostUnauthorizedException 没有访问权限
//     * org.apache.shiro.authz.UnauthorizedException     没有访问权限
//     * org.apache.shiro.authz.AuthorizationException    上面异常的父类
//     *
//     * @param ex 没有权限的异常
//     * @return ModelAndView
//     */
//    @ResponseBody
//    @ExceptionHandler(value = {AuthorizationException.class})
//    public ResponseEntity authorizationExceptionHandler(AuthorizationException ex, HttpServletRequest request) {
//        log.warn("没有权限的异常=>{}", ex.getMessage());
//        Subject subject = SecurityUtils.getSubject();
//        if (subject != null) {
//            Object principal = subject.getPrincipal();
//            Serializable id = subject.getSession().getId();
//            long timeout = subject.getSession().getTimeout();
//            log.warn("没有权限的异常=> {}, {}, {}", principal, id, timeout);
//        }
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("没有权限");
//    }

    /**
     * 认证异常
     * <p>
     * org.apache.shiro.authc.pam.UnsupportedTokenException 身份令牌异常，不支持的身份令牌
     * org.apache.shiro.authc.UnknownAccountException       未知账户/没找到帐号,登录失败
     * org.apache.shiro.authc.LockedAccountException        帐号锁定
     * org.apache.shiro.authz.DisabledAccountException      用户禁用
     * org.apache.shiro.authc.ExcessiveAttemptsException    登录重试次数，超限。只允许在一段时间内允许有一定数量的认证尝试
     * org.apache.shiro.authc.ConcurrentAccessException     一个用户多次登录异常：不允许多次登录，只能登录一次 。即不允许多处登录
     * org.apache.shiro.authz.AccountException              账户异常
     * org.apache.shiro.authz.ExpiredCredentialsException   过期的凭据异常
     * org.apache.shiro.authc.IncorrectCredentialsException 错误的凭据异常
     * org.apache.shiro.authc.CredentialsException          凭据异常
     * org.apache.shiro.authc.AuthenticationException       上面异常的父类
     *
     * @param ex 认证异常
     * @return ModelAndView
     */
//    @ResponseBody
//    @ExceptionHandler(value = {AuthenticationException.class})
//    public ResponseEntity authenticationExceptionHandler(AuthenticationException ex, HttpServletRequest request) {
//        String message;
//        if (ex instanceof UnknownAccountException) {
//            message = "没找到帐号";
//        } else if (ex instanceof DisabledAccountException) {
//            message = "账号已锁定或禁用";
//        } else if (ex instanceof CredentialsException) {
//            message = "用户名或者密码错误";
//        } else if (ex instanceof ConcurrentAccessException) {
//            message = "用户已经在别处登录";
//        } else if (ex instanceof ExcessiveAttemptsException) {
//            message = "登录重试次数过多，请稍后重试";
//        } else if (ex instanceof AccountException) {
//            message = "账户异常";
//        } else {
//            message = "用户名或者密码错误";
//        }
//        log.error("认证异常:" + message, ex);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
//    }

}
