package com.atguigu.yygh.hosp;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "hosp-service" , path = "/hosp/client")
public interface HospClient {
    @GetMapping("/schedule/{id}")
    ScheduleOrderVo getScheduleOrderVoById(@PathVariable("id") String id );


}
