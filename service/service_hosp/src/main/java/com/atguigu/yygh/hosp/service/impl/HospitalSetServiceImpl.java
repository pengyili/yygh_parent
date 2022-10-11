package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.hosp.service.HospitalSetService;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void changeStatus(List<Long> ids ,Integer status ) {

        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setStatus(status);

            baseMapper.update( hospitalSet , new LambdaUpdateWrapper<HospitalSet>().set(HospitalSet::getStatus , status).in(HospitalSet::getId , ids));

        List<HospitalSet> hospitalSets = baseMapper.selectBatchIds(ids);

        hospitalSets.forEach(s -> {
            String hoscode = s.getHoscode();
            Hospital hoscode1 = mongoTemplate.findOne(Query.query(Criteria.where("hoscode").is(hoscode)), Hospital.class);
            Assert.notNull(hoscode1 , "根据医院编码获取需要修改的医院为空");
            hoscode1.setStatus(status);
            mongoTemplate.save(hoscode1);
        });
    }

    @Override
    public void changeStatus(Integer id, Integer status) {
//        HospitalSet hospitalSet = new HospitalSet();
//        hospitalSet.setStatus(status);
        HospitalSet hospitalSet = baseMapper.selectById(id);
        hospitalSet.setStatus(status);
        baseMapper.updateById(hospitalSet);
        Hospital hospital = mongoTemplate.findOne(Query.query(Criteria.where("hoscode").is(hospitalSet.getHoscode())), Hospital.class);
        Assert.notNull(hospital , "医院设置的编码和医院实际编码不符合");
        hospital.setStatus(status);
        mongoTemplate.save(hospital);
    }
}
