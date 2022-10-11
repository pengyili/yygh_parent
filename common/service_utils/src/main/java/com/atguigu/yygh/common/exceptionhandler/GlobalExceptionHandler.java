package com.atguigu.yygh.common.exceptionhandler;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(YYGHException.class)
    public Result handleException(YYGHException e){
        log.warn(e.getMessage());
        return Result.fail(e.getCode() , e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result handleException(RuntimeException e){
        log.error((e.getMessage()));
        return Result.ok(e.getMessage());
    }
}