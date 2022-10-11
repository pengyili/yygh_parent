package com.atguigu.yygh.user;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/hosp/front")
public class FrontController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/hospitalByHosname/{hosname}")
    public Result getHospital(@PathVariable("hosname") String hosname){
        List<Hospital> hospitalByHosname = hospitalService.getHospitalByHosname(hosname);

        return Result.ok().data("items" , hospitalByHosname) ;
    }
    @GetMapping("/show/{hoscode}")
    public Result show(@PathVariable("hoscode") String hoscode){
        Hospital hospital =  hospitalService.getHospitalByHoscode(hoscode);
        return Result.ok().data("item" , hospital );
    }

    @GetMapping("/{pageNum}/{pageSize}")
    public Result getHospitalList(@PathVariable("pageNum") Integer pageNum,
                                  @PathVariable("pageSize") Integer pageSize,
                                  HospitalQueryVo hospitalQueryVo) {


        Page<Hospital> hospitalList = hospitalService.getHospitalList(pageNum, pageSize, hospitalQueryVo);

        return Result.ok().data("items", hospitalList.getContent()).data("total" , hospitalList.getTotalElements());
    }
    @GetMapping("/department/{hoscode}")
    public Result showDepartmentList(@PathVariable String hoscode){
        List<Department> departmentList = hospitalService.getDepartmentList(hoscode);
        List<DepartmentVo> departmentVos = hospitalService.setTreeStructure(departmentList);
        return Result.ok().data("items" , departmentVos);
    }

    @GetMapping("/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingScheduleRule(@PathVariable Integer page , @PathVariable Integer limit ,
                                         @PathVariable String hoscode , @PathVariable String depcode){
        Map map = hospitalService.buildFrontBookingScheduleRuleVo(page, limit, hoscode, depcode);

        return Result.ok().data(map) ;
    }

    @GetMapping("/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(@PathVariable String  hoscode,@PathVariable String depcode, @PathVariable Date workDate){

        List<Schedule> scheduleList =  hospitalService.getWorkDateScheduleList(hoscode ,depcode , workDate);

        return Result.ok().data("items" , scheduleList);

    }
    @GetMapping("/getSchedule/{id}")
    public Result getSchedule(@PathVariable String id){
        Schedule schedule = hospitalService.getScheduleById(id);
        hospitalService.setScheduleParam(schedule) ;
        return Result.ok().data("item" , schedule);
    }
}
