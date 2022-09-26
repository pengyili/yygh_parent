package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.hosp.service.HospitalSetService;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    @Override
    public void changeStatus(List<Long> ids ,Integer status ) {
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setStatus(status);
        baseMapper.update(hospitalSet  ,new LambdaUpdateWrapper<HospitalSet>().set(HospitalSet::getStatus , status).in(HospitalSet::getId , ids)) ;
    }
}
