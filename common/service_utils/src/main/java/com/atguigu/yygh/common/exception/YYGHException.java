package com.atguigu.yygh.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RestControllerAdvice;




@Getter
@NoArgsConstructor
public class YYGHException extends RuntimeException {
    private Integer code;


    public YYGHException( Integer code ,String message) {
        super(message);
        this.code = code;
    }
}
