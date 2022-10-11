package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface HospitalService {
    Page<Hospital> getHospitalList( Integer pageNum , Integer pageSize, HospitalQueryVo hospitalQueryVo);

    Hospital getHospitalById(String  id);

    void changeHospitalStatus(String id, Integer status);
    

    List<DepartmentVo> setTreeStructure(List<Department> departments);

    List<Department> getDepartmentList(String hoscode);

    List<BookingScheduleRuleVo> getScheduleGroupByWoreData(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    Integer getScheduleGroupByWoreDataTotal(String hoscode, String depcode);

    List<Schedule> getScheduleDetailList(ScheduleQueryVo scheduleQueryVo);

    List<Hospital> getHospitalByHosname(String hosname);

    Hospital getHospitalByHoscode(String hoscode);

    Department getHospitalByHoscodeAndDepcode(String hoscode , String depcode);


    Map buildFrontBookingScheduleRuleVo(Integer page , Integer limit  , String hoscode , String depcode );

    List<Schedule> getWorkDateScheduleList(String hoscode, String depcode, Date workDate);

    Schedule getScheduleById(String id);

    void setScheduleParam(Schedule schedule);
}
