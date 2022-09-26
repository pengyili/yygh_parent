package com.atguigu.yygh.api.service.impl;

import com.atguigu.yygh.api.mapper.HospMapper;
import com.atguigu.yygh.api.service.HospitalService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.hosp.Schedule;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private HospMapper hospMapper;


    @Override
    public void saveHospital(Hospital hospital) {


        Hospital sourceHospital = getHospitalByHoscode(hospital.getHoscode());
        if (sourceHospital != null) {
            hospital.setId(sourceHospital.getId());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(sourceHospital.getIsDeleted());
            hospital.setStatus(sourceHospital.getStatus());
            hospital.setCreateTime(sourceHospital.getCreateTime());
        } else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
        }
        mongoTemplate.save(hospital);
    }

    public Hospital getHospitalByHoscode(String hoscode) {

        return mongoTemplate.findOne(Query.query(Criteria.where("hoscode").is(hoscode)), Hospital.class);
    }

    @Override
    public void saveDeprtment(Department department) {


        Department sourceDepartment = getDepartmentByHoscodeAndDepartmentCode(department.getHoscode(), department.getDepcode());
        if (sourceDepartment != null) {
            department.setId(sourceDepartment.getId());
            department.setCreateTime(sourceDepartment.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(sourceDepartment.getIsDeleted());
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
        }
        mongoTemplate.save(department);
    }

    public Department getDepartmentByHoscodeAndDepartmentCode(String hoscode, String depcode) {
        return mongoTemplate.findOne(Query
                .query(Criteria.where("depcode").is(depcode))
                .addCriteria(Criteria.where("hoscode").is(hoscode)), Department.class);
    }

    @Override
    public void saveSchedule(Schedule schedule) {

        Schedule scheduleByHosScheduleCode = getScheduleByHosScheduleCode(schedule.getHoscode(), schedule.getDepcode(), schedule.getHosScheduleId());
        if (scheduleByHosScheduleCode != null) {
            schedule.setId(scheduleByHosScheduleCode.getId());
            schedule.setCreateTime(scheduleByHosScheduleCode.getCreateTime());
            schedule.setIsDeleted(scheduleByHosScheduleCode.getIsDeleted());
        } else {
            schedule.setCreateTime(new Date());
            schedule.setIsDeleted(0);
        }

        schedule.setUpdateTime(new Date());
        mongoTemplate.save(schedule);
    }

    public Schedule getScheduleByHosScheduleCode(String hoscode, String depcode, String hosScheduleCode) {
        return mongoTemplate.findOne(Query.query(
                Criteria.where("hoscode").is(hoscode))
                        .addCriteria(Criteria.where("depcode").is(depcode))
                        .addCriteria((Criteria.where("hosScheduleCode").is(hosScheduleCode)))
                ,Schedule.class);
    }


    public String getSignKey(String hoscode) {
        return hospMapper.selectOne(
                new LambdaQueryWrapper<HospitalSet>()
                .eq(HospitalSet::getHoscode, hoscode)
        ).getSignKey();
    }
}
