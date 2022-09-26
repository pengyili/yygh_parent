package com.atguigu.yygh.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;



@ApiModel(description = "统一的返回结果")
public record Result(
        @ApiModelProperty( required = true,value ="统一返回的状态码")
         Integer code,
         @ApiModelProperty(value = "访问是否成功"  , required = true)
         boolean flag,
         @ApiModelProperty( value = "数据返回的提示信息" , required = true)
         String msg ,
         @ApiModelProperty( value = "封装返回到前端页面的数据" , dataType = "Map" )
         Map<String , Object> data
) {
   public static Result ok(){
       return new Result(ResultEnum.SUCCESS.getCode(),  ResultEnum.SUCCESS.isFlag(), ResultEnum.SUCCESS.getMsg(), new HashMap<>());
   }
   public static Result fail(){
       return new Result(ResultEnum.FAIL.getCode(),  ResultEnum.FAIL.isFlag(), ResultEnum.FAIL.getMsg(), new HashMap<>());
   }
   public static Result fail( String msg ){
       return new Result(ResultEnum.FAIL.getCode() , true , msg , new HashMap<>());
   }
   public static Result ok( String msg ){
       return new Result(ResultEnum.SUCCESS.getCode(), true , msg , new HashMap<>());
   }
   public static Result fail( Integer code, String msg ){
       return new Result(code , true , msg , new HashMap<>());
   }
   public static Result ok( Integer code, String msg  ){
       return new Result(code, true , msg , new HashMap<>());
   }
   public static Result fail( Integer code, String msg ,Map<String,Object> map ){
       return new Result(code , true , msg , map);
   }
   public static Result ok( Integer code, String msg  , Map<String,Object> map ){
       return new Result(code, true , msg , map);
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
