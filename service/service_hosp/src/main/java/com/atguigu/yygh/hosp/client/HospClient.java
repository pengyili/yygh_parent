package com.atguigu.yygh.hosp.client;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RequestMapping("/hosp/client")
@RestController
public class HospClient {
    @Autowired
    private HospitalService hospitalService ;





    @GetMapping("/schedule/{id}")
    ScheduleOrderVo getScheduleOrderVoById(@PathVariable("id") String id ){
        Schedule schedule = hospitalService.getScheduleById(id);

        if(schedule == null )
            throw  new YYGHException();

        Hospital hospital = hospitalService.getHospitalByHoscode(schedule.getHoscode());

        Integer quitDay = hospital.getBookingRule().getQuitDay();


        String quitTimeString  = hospital.getBookingRule().getQuitTime();
        Date quitTime = hospitalService.parseDateAndString(new DateTime(schedule.getWorkDate()).plusDays(quitDay)
                .toDate(), quitTimeString).toDate();



        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();

        BeanUtils.copyProperties(schedule , scheduleOrderVo);

        scheduleOrderVo.setDepname(hospitalService.getDepByHoscodeAndDepcode(schedule.getHoscode(), schedule.getDepcode()).getDepname());
        scheduleOrderVo.setHosname(hospital.getHosname());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setQuitTime(quitTime);


        return scheduleOrderVo;
    }
}
