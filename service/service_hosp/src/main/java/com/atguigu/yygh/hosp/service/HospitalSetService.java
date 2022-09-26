package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface HospitalSetService extends IService<HospitalSet> {


    void changeStatus(List<Long> ids ,Integer status);
}
