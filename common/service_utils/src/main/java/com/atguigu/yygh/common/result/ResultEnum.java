package com.atguigu.yygh.common.result;

import lombok.Getter;

@Getter
public enum ResultEnum {
    SUCCESS(20000 , "成功" , true),
    FAIL(30000 , "失败" , false);

    private final Integer code;
    private final String msg;
    private final boolean flag;

    ResultEnum(Integer code, String msg, boolean flag) {
        this.code = code;
        this.msg = msg;
        this.flag = flag;
    }


}
