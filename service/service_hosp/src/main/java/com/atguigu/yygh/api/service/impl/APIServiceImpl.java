package com.atguigu.yygh.api.service.impl;

import com.atguigu.yygh.api.service.APIService;

import com.atguigu.yygh.common.utlis.MD5;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.hosp.Schedule;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("ALL")
@Service
public class APIServiceImpl implements APIService {

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private HospitalSetMapper hospitalSetMapper;


    @Override
    public void saveHospital(Hospital hospital) {


        Hospital sourceHospital = getHospitalByHoscode(hospital.getHoscode());
        if (sourceHospital != null) {
            hospital.setId(sourceHospital.getId());
            hospital.setIsDeleted(0);
            hospital.setStatus(sourceHospital.getStatus());
            hospital.setCreateTime(sourceHospital.getCreateTime());
        } else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setIsDeleted(0);
        }
        hospital.setUpdateTime(new Date());
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
            department.setIsDeleted(0);
        } else {
            department.setCreateTime(new Date());

            department.setIsDeleted(0);
        }
        department.setUpdateTime(new Date());
        mongoTemplate.save(department);
    }

    private Department getDepartmentByHoscodeAndDepartmentCode(String hoscode, String depcode) {
        return mongoTemplate.findOne(Query
                .query(Criteria.where("depcode").is(depcode))
                .addCriteria(Criteria.where("hoscode").is(hoscode)), Department.class);
    }

    @Override
    public void saveSchedule(Schedule schedule) {

        Schedule scheduleByHosScheduleCode = getScheduleByHosScheduleCode(schedule.getHoscode(), schedule.getHosScheduleId());
        if (scheduleByHosScheduleCode != null) {
            schedule.setId(scheduleByHosScheduleCode.getId());
            schedule.setCreateTime(scheduleByHosScheduleCode.getCreateTime());
            schedule.setIsDeleted(0);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setIsDeleted(0);
        }

        schedule.setUpdateTime(new Date());
        mongoTemplate.save(schedule);
    }

    private Schedule getScheduleByHosScheduleCode(String hoscode, String hosScheduleCode) {
        return mongoTemplate.findOne(
                Query.query(
                                Criteria.where("hoscode")
                                        .is(hoscode)
                                        .and("hosScheduleId")
                                        .is(hosScheduleCode)
                )
                , Schedule.class);
    }


    private String getSignKey(String hoscode) {
        return hospitalSetMapper.selectOne(
                new LambdaQueryWrapper<HospitalSet>()
                        .eq(HospitalSet::getHoscode, hoscode)
        ).getSignKey();
    }

    public Boolean checkSign(String sign, String hoscode) {
        return sign.equals(MD5.encrypt(getSignKey(hoscode)));
    }

    @Override
    public Page<Department> getDepartmentList(String hoscode, Integer pageNum, Integer pageSize) {

        long count = mongoTemplate.count(Query.query(Criteria.where("hoscode").is(hoscode)), Department.class);
        List<Department> departmentList = mongoTemplate.find(
                Query.query(
                                Criteria.where("hoscode")
                                        .is(hoscode)
                                        .and("isDeleted")
                                        .is(0)
                        )
                        .skip((pageNum - 1) * pageSize)
                        .limit(pageSize),
                Department.class
        );
        return new PageImpl<>(departmentList, PageRequest.of(pageNum - 1, pageSize), count);
    }

    @Override
    public Page<Schedule> getScheduleList(String hoscode, Integer pageNum, Integer pageSize) {
        long count = mongoTemplate.count(Query.query(Criteria.where("hoscode").is(hoscode)), Schedule.class);
        List<Schedule> scheduleList = mongoTemplate.find(
                Query.query(
                                Criteria.where("hoscode")
                                        .is(hoscode)
                                        .and("isDeleted")
                                        .is(0)
                        )
                        .skip((pageNum - 1) * pageSize)
                        .limit(pageSize)
                , Schedule.class
        );

        return new PageImpl<>(scheduleList, PageRequest.of(pageNum, pageSize), count);
    }


    @Override
    public void removeDepartment(String hoscode, String depcode) {
        Department department = getDepartmentByHoscodeAndDepartmentCode(hoscode, depcode);
        if(department != null ) {
            department.setIsDeleted(1);
            mongoTemplate.save(department);
        }

    }

    @Override
    public void removeSchedule(String hoscode, String hosScheduleId) {
        Schedule schedule = getScheduleByHosScheduleCode(hoscode, hosScheduleId);
        if(schedule != null ) {
            schedule.setIsDeleted(1);
            mongoTemplate.save(schedule);
        }


    }


    public Boolean checkSign(Map<String, String> map, String signKey) {
        String sign = map.remove("sign");
        TreeMap<String, String> treeMap = new TreeMap<>(map);
        StringBuilder sb = new StringBuilder();
        treeMap.forEach((key, value) -> sb.append(value).append("|"));
        sb.append(getSignKey(map.get("hoscode")));
        return signKey.equals(MD5.encrypt(sb.toString()));
    }
}
