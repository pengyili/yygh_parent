package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.api.service.APIService;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@Api("后台管理员操作医院相关项")
@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {


    @Autowired
    DiscoveryClient discoveryClient;
    @Autowired
    private HospitalService hospitalService;


    @ApiOperation("根据查询条件分页展示医院列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum" , value = "当前页码"),
            @ApiImplicitParam(name = "pageSize" , value = "每页显示多少行数据"),
            @ApiImplicitParam(name = "hospitalQueryVo" , value = "查询条件"),

    })
    @GetMapping("/{pageNum}/{pageSize}")
    public Result getHospitalList(@PathVariable("pageNum") Integer pageNum,
                                  @PathVariable("pageSize") Integer pageSize,
                                  HospitalQueryVo hospitalQueryVo) {


        Page<Hospital> hospitalList = hospitalService.getHospitalList(pageNum, pageSize, hospitalQueryVo);

        return Result.ok().data("pages", hospitalList);
    }

    @ApiOperation("根据医院在mongodb中的id 获取唯一的医院")
    @ApiImplicitParam("mongodb中的医院id")
    @GetMapping("/{id}")
    public Result getHospitalById(@PathVariable("id") String id) {
        Hospital hospital = hospitalService.getHospitalById(id);
        return Result.ok().data("item", hospital);
    }


    @ApiOperation("修改医院的状态,并同步到医院设置项的状态")
    @ApiImplicitParams({
            @ApiImplicitParam("医院的唯一编码"),
            @ApiImplicitParam("医院需要修改到的状态")
    })
    @PutMapping("/changeStatus/{hoscode}/{status}")
    public Result changeHospitalStatus(@PathVariable("hoscode") String hoscode, @PathVariable("status") Integer status) {
        hospitalService.changeHospitalStatus(hoscode, status);
        return Result.ok();
    }

    @ApiOperation("根据医院编码获取,医院的科室列表")
    @ApiImplicitParam("医院编码")
    @GetMapping("/department/{hoscode}")
    public Result getDepartment(@PathVariable("hoscode") String hoscode) {

        List<Department> departments = hospitalService.getDepartmentList(hoscode);

        List<DepartmentVo> departmentVos = hospitalService.setTreeStructure(departments);

        return Result.ok().data("items", departmentVos);
    }
    @ApiOperation("根据医院编码和部门编码,分页获取排班列表")
    @ApiImplicitParams({
            @ApiImplicitParam("当前页码"),
            @ApiImplicitParam("每页显示数据数"),
            @ApiImplicitParam("医院的唯一编码"),
            @ApiImplicitParam("部门的唯一编码"),
    })
    @GetMapping("/schedule/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public Result getSchedule(@PathVariable("pageNum") Integer pageNum,
                              @PathVariable("pageSize") Integer pageSize,
                              @PathVariable("hoscode") String hoscode,
                              @PathVariable("depcode") String depcode){

        List<BookingScheduleRuleVo> scheduleGroupByWoreData = hospitalService.getScheduleGroupByWoreData(pageNum, pageSize, hoscode, depcode);

        Integer scheduleGroupByWoreDataTotal = hospitalService.getScheduleGroupByWoreDataTotal(hoscode, depcode);

        return Result.ok().data("items" , scheduleGroupByWoreData ) .data("total" ,scheduleGroupByWoreDataTotal ) ;

    }

    @ApiOperation("获取排班详情")
    @ApiImplicitParam("获取排班详情的查询条件  ,  根据 workDate , hoscode , depcode 查询")
    @GetMapping("/scheduleDetail")
    public Result getScheduleDetailList(ScheduleQueryVo scheduleQueryVo){

       List<Schedule> scheduleList = hospitalService.getScheduleDetailList(scheduleQueryVo);

       return Result.ok().data("items" , scheduleList);
    }

}
