package com.atguigu.yygh.common.result;

import java.util.HashMap;
import java.util.Map;

public record Result(
         Integer code,
         boolean flag,
         String msg ,
         Map<String , Object> data
) {
   public static Result ok(){
       return new Result(ResultEnum.SUCCESS.getCode(),  ResultEnum.SUCCESS.isFlag(), ResultEnum.SUCCESS.getMsg(), new HashMap<>());
   }
   public static Result fail(){
       return new Result(ResultEnum.FAIL.getCode(),  ResultEnum.FAIL.isFlag(), ResultEnum.FAIL.getMsg(), new HashMap<>());
   }

   public static Result ok(ResultEnum resultEnum){
       return new Result(resultEnum.getCode(),  true, resultEnum.getMsg(), new HashMap<>());
   }
   public static Result fail(ResultEnum resultEnum){
       return new Result(resultEnum.getCode(), false, resultEnum.getMsg(), new HashMap<>());
   }


   public Result data(String key , Object value){
        this.data.put(key,value);
        return this;
   }
   public Result data(Map<String , Object> map){
        this.data.clear();
        this.data.putAll(map);
        return this;
   }

}
