package com.atguigu.yygh.dict.controller;

import com.atguigu.yygh.dict.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/cmn/dict")
public class DictClient {
    @Autowired
    private DictService dictService;
    @GetMapping("/getNameByValueAndParentCode/{value}/{parentDictCode}")
    public String getNameByValueAndParentCode(@PathVariable("value")  String value , @PathVariable("parentDictCode") String parentDictCode){
        return dictService.getNameByValueAndParentCode(value ,  parentDictCode);
    }

    @GetMapping("/getNameByValue/{value}")
    public String getNameByValue(@PathVariable("value")  String value){
        return dictService.getNameByValue(value);
    }


}
