package com.atguigu.yygh.api.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;

public interface  HospitalService {

    void saveHospital(Hospital hospital);

    Hospital getHospitalByHoscode(String hoscode);

    void saveDeprtment(Department department);

    Department getDepartmentByHoscodeAndDepartmentCode(String hoscode , String depcode);

    void saveSchedule(Schedule schedule);
}
