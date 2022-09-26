package com.atguigu.yygh.common.advice;

import com.alibaba.fastjson.JSON;
import com.atguigu.yygh.common.exception.YYGHException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Objects;

@Slf4j
@Aspect
@Component
public class ControllerExceptionAdvice {


    @Around("execution(* com..*Controller.*(..))")
    public Object enhanceAdvice(ProceedingJoinPoint joinPoint){

        try {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String method = request.getMethod();
            String remoteHost = request.getRemoteHost();
            Object[] args = joinPoint.getArgs();
            String s ;
            try {
                 s = JSON.toJSONString(args);
            }catch (Throwable e){
                StringBuilder sb = new StringBuilder();
                for (Object arg: args ){
                    sb.append(arg.getClass().getName());
                }
               s = sb.toString();
            }

            Long startTime = System.currentTimeMillis();
            log.debug("请求ip为{} , 请求方式:{}  , 请求的方法为{}, 携带的参数:{}  , " ,  remoteHost , method  , joinPoint.getSignature().getName(), s) ;

            Object returnValue  =  joinPoint.proceed();

            Long endTime = System.currentTimeMillis();
            log.debug("执行成功 ,耗时{}ms " , endTime -  startTime  );

            log.debug("返回的参数数据为[{}]"+JSON.toJSONString(returnValue));

            return  returnValue;
        } catch (Throwable e) {
            log.warn(e.getMessage());
            e.printStackTrace();
            throw new YYGHException(30000 , "系统出错了请稍后再试");
        }

    }
}
