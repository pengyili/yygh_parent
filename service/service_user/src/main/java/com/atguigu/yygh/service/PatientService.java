package com.atguigu.yygh.service;


import com.atguigu.yygh.model.user.Patient;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author lyp
* @description 针对表【patient(就诊人表)】的数据库操作Service
* @createDate 2022-10-08 11:36:06
*/
public interface PatientService extends IService<Patient> {

    void setPatientParam(Patient patient);

}
