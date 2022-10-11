package com.atguigu.yygh.api.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface  APIService {

    /**
     * 保存hospital的第3方接口
     * @param hospital 需要保存的hospital对象
     */
    void saveHospital(Hospital hospital);

    /**
     * 根据hoscode获取 hospital对象
     * @param hoscode 医院的唯一编码
     * @return 从mongodb查询到的 hospital
     */
    Hospital getHospitalByHoscode(String hoscode);

    /**
     * 保存department 的第3方接口
     * @param department 需要保存的department对象
     */
    void saveDeprtment(Department department);

//    Department getDepartmentByHoscodeAndDepartmentCode(String hoscode , String depcode);

    void saveSchedule(Schedule schedule);

   Boolean checkSign(Map<String , String > map , String signKey);

    Boolean checkSign(String sign  ,String hoscode);

    Page<Department> getDepartmentList(String hoscode, Integer pageNum, Integer pageSize);

    Page<Schedule> getScheduleList(String hoscode, Integer pageNum, Integer pageSize ) ;


    void removeDepartment(String hoscode, String depcode);

    void removeSchedule(String hoscode, String hosScheduleId);
}
