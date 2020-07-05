package com.lemon.core.error;

import com.lemon.base.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseBody
    public RestResult exceptionHandler(Exception e) {
        log.info("###全局捕获异常###,error:{}", e);
        return RestResult.server_error().msg("系统错误");
    }
}
