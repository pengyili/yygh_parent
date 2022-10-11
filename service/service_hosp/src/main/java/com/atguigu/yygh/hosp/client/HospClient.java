package com.atguigu.yygh.hosp.client;

import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/hosp/client")
@RestController
public class HospClient {
    @Autowired
    private HospitalService hospitalService ;





    @GetMapping("/schedule/{id}")
    ScheduleOrderVo getScheduleOrderVoById(@PathVariable("id") String id ){
        Schedule schedule = hospitalService.getScheduleById(id);

        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();

        BeanUtils.copyProperties(schedule , scheduleOrderVo);

        scheduleOrderVo.setDepname(hospitalService.getHospitalByHoscodeAndDepcode(schedule.getHoscode(), schedule.getDepcode()).getDepname());
        scheduleOrderVo.setHosname(hospitalService.getHospitalByHoscode(schedule.getHoscode()).getHosname());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());


        return scheduleOrderVo;
    }
}
