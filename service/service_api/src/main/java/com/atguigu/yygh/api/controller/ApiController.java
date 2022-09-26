package com.atguigu.yygh.api.controller;


import com.alibaba.fastjson.JSON;
import com.atguigu.yygh.api.service.HospitalService;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;

import com.atguigu.yygh.model.hosp.Schedule;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @PostMapping("/saveHospital")
    public Result  saveHospital(Hospital hospital ,String sign){
//        Hospital hospital = new Hospital();
//
//        Object o = map.get("bookingRule");
//        BeanUtils.copyProperties(map ,hospital );
//        try {
//            BeanUtils.copyProperties(hospital , map);
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//
//        System.out.println(map);


//        Hospital hospital = new Hospital();

//        try {
//            BeanUtils.copyProperties(hospital , map);
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }

//        System.out.println(map.get("sign"));

        if (StringUtils.isBlank(hospital.getHoscode())) {
            throw  new YYGHException(30000 , "没有携带参数医院编码或科室编码");
        }


        hospitalService.saveHospital(hospital);

        return Result.ok();
    }

    @PostMapping("/getHospital")
    public Result getHospital(@RequestBody Hospital hospital){

        return Result.ok().data("item" , hospitalService.getHospitalByHoscode(hospital.getHoscode()));
    }

    @PostMapping("saveDeprtment")
    public Result saveDeprtment(Department department , String sign){

        if (StringUtils.isBlank(department.getHoscode()) || StringUtils.isBlank(department.getDepcode())) {
            throw  new YYGHException(30000 , "没有携带参数医院编码或科室编码");
        }

        hospitalService.saveDeprtment(department);

        return Result.ok();
    }

    @PostMapping("/getDeprtment")
    public Result getDeprtment(@RequestBody Department department ){
       return Result.ok().data("item" ,  hospitalService.getDepartmentByHoscodeAndDepartmentCode(department.getHoscode(), department.getDepcode()));
    }

    @PostMapping("/saveSchedule")
    public Result saveSchedule(Schedule schedule , String sign){
        if (StringUtils.isBlank(schedule.getHoscode()) || StringUtils.isBlank(schedule.getDepcode())) {
            throw  new YYGHException(30000 , "没有携带参数医院编码或科室编码");
        }

        hospitalService.saveSchedule(schedule);
        return Result.ok();
    }

}
