package com.atguigu.yygh.api.controller;


import com.atguigu.yygh.api.result.Result;
import com.atguigu.yygh.api.result.ResultCodeEnum;
import com.atguigu.yygh.api.service.APIService;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;

import com.atguigu.yygh.model.hosp.Schedule;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Api(tags = "第三方服务的访问接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private APIService apiService;

    @ApiOperation("保存医院信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="hospital" , value="上传的医院信息" ,paramType = "form",dataType = "Hospital"),
            @ApiImplicitParam(name="sign" ,value="唯一校验参数0" , paramType="form" , dataType="String")
    })
    @PostMapping("/saveHospital")
    public Result saveHospital(Hospital hospital, String sign) {
        hospital.setLogoData(hospital.getLogoData().replaceAll(" ", "+"));
        if (StringUtils.isBlank(hospital.getHoscode()) && StringUtils.isBlank(sign)) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }

        if (apiService.checkSign(sign, hospital.getHoscode())) {
            apiService.saveHospital(hospital);

            return Result.ok();

        }

        return Result.build(null, ResultCodeEnum.SIGN_ERROR);

    }

    @ApiOperation("查看医院信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="map" ,value="保存了医院的唯一编码和校验参数信息" , paramType="form" , dataType="Map")
    })
    @PostMapping("/hospital/show")
    public Result<Hospital> getHospital(@RequestParam Map<String, String> map) {


        if (apiService.checkSign(map, map.get("sign")))
            return Result.ok(apiService.getHospitalByHoscode(map.get("hoscode")));
        return Result.build(null, ResultCodeEnum.SIGN_ERROR);
    }
    @ApiOperation("保存科室信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="department" ,value="上传的科室信息" ,paramType = "form",dataType = "Department"),
            @ApiImplicitParam(name="sign" ,value="唯一校验参数" , paramType="form" , dataType="String")
    })
    @PostMapping("/saveDepartment")
    public Result saveDeprtment(Department department, String sign) {

        if (StringUtils.isBlank(department.getHoscode()) || StringUtils.isBlank(department.getDepcode())) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }

        if (apiService.checkSign(sign, department.getHoscode())) {

            apiService.saveDeprtment(department);

            return Result.ok();

        }
        return Result.build(null, ResultCodeEnum.SIGN_ERROR);

    }
    @ApiOperation("查看科室信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="hoscode"  ,value = "查询的医院的唯一编码",dataType="String" , paramType="form"),
            @ApiImplicitParam(name="page"  ,value = "分页显示的当前页数" ,defaultValue="1",dataType="String" , paramType="form"),
            @ApiImplicitParam(name="limit"  ,value = "每页多少记录数" ,defaultValue="10",dataType="String" , paramType="form"),
    })
    @PostMapping("/department/list")
    public Result getDeprtment(String hoscode, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit, Long timestamp, String sign) {
        if (apiService.checkSign(sign, hoscode))
            return Result.ok(apiService.getDepartmentList(hoscode, page, limit));
        return Result.build(null, ResultCodeEnum.SIGN_ERROR);
    }

    @ApiOperation("保存排班信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="schedule" ,value="上传的排班信息" ,paramType = "form",dataType = "Department"),
            @ApiImplicitParam(name="sign" ,value="唯一校验参数" , paramType="form" , dataType="String")
    })
    @PostMapping("/saveSchedule")
    public Result saveSchedule(Schedule schedule, String sign) {
        if (StringUtils.isBlank(schedule.getHoscode()) || StringUtils.isBlank(schedule.getDepcode())) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if (apiService.checkSign(sign, schedule.getHoscode())) {
            apiService.saveSchedule(schedule);
            return Result.ok();
        }

        return Result.build(null, ResultCodeEnum.SIGN_ERROR);
    }
    @ApiOperation("查看排班信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="hoscode"  ,value = "查询的医院的唯一编码",dataType="String" , paramType="form"),
            @ApiImplicitParam(name="page"  ,value = "分页显示的当前页数" ,defaultValue="1",dataType="String" , paramType="form"),
            @ApiImplicitParam(name="limit"  ,value = "每页多少记录数" ,defaultValue="10",dataType="String" , paramType="form"),
    })
    @PostMapping("/schedule/list")
    public Result getSchedule(String hoscode, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer limit, Long timestamp, String sign) {
        if (apiService.checkSign(sign, hoscode))
            return Result.ok(apiService.getScheduleList(hoscode, page, limit));
        return Result.build(null, ResultCodeEnum.SIGN_ERROR);
    }
    @ApiOperation("删除科室信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value="hoscode" , name="操作的医院的唯一编码", paramType="form" , dataType="String" , required = true) ,
            @ApiImplicitParam(value="depcode" , name="删除的科室编码", paramType="form" , dataType="String" , required = true) ,
            @ApiImplicitParam(value="timestamp" , name="发起请求得到时间戳", paramType="form" , dataType="Long" , required = true) ,
            @ApiImplicitParam(value="sign" , name="校验参数", paramType="form" , dataType="String" , required = true) ,
    })
    @PostMapping("department/remove")
    public Result removeDepartment(String hoscode, String depcode, Long timestamp, String sign) {
        if (StringUtils.isBlank(hoscode) || StringUtils.isBlank(depcode)) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if (apiService.checkSign(sign, hoscode)) {
            apiService.removeDepartment(hoscode, depcode);
            return Result.ok();
        }
        return Result.build(null, ResultCodeEnum.SIGN_ERROR);
    }
    @ApiOperation("删除排班信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value="hoscode" , name="操作的医院的唯一编码", paramType="form" , dataType="String" , required = true) ,
            @ApiImplicitParam(value="hosScheduleId" , name="删除的排班的唯一编码", paramType="form" , dataType="String" , required = true) ,
            @ApiImplicitParam(value="timestamp" , name="发起请求得到时间戳", paramType="form" , dataType="Long" , required = true) ,
            @ApiImplicitParam(value="sign" , name="校验参数", paramType="form" , dataType="String" , required = true) ,
    })

    @PostMapping("schedule/remove")
    public Result removeSchedule(String hoscode, String hosScheduleId, Long timestamp, String sign) {
        if (StringUtils.isBlank(hoscode) || StringUtils.isBlank(hosScheduleId)) {
            return Result.build(null, ResultCodeEnum.PARAM_ERROR);
        }
        if (apiService.checkSign(sign, hoscode)) {
            apiService.removeSchedule(hoscode, hosScheduleId);
            ;
            return Result.ok();
        }
        return Result.build(null, ResultCodeEnum.SIGN_ERROR);
    }

}
