package com.atguigu.yygh.dict;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient( name = "cmn-service" , path ="/admin/cmn/dict")
public interface DictClient {
    @GetMapping("/getNameByValueAndParentCode/{value}/{parentDictCode}")
    public String getNameByValueAndParentCode(@PathVariable("value")  String value , @PathVariable("parentDictCode") String parentDictCode);

    @GetMapping("/getNameByValue/{value}")
    public String getNameByValue(@PathVariable("value")  String value);
}
