package com.atguigu.yygh.service.impl;

import com.atguigu.yygh.dict.DictClient;
import com.atguigu.yygh.model.user.Patient;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.yygh.service.PatientService;
import com.atguigu.yygh.mapper.PatientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
* @author lyp
* @description 针对表【patient(就诊人表)】的数据库操作Service实现
* @createDate 2022-10-08 11:36:06
*/
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService{

    @Autowired
    private DictClient dictClient;

    public void setPatientParam(Patient patient){

        String certificatesType = dictClient.getNameByValueAndParentCode(patient.getCertificatesType(), "CertificatesType");
        String provinceString = dictClient.getNameByValue(patient.getProvinceCode());
        String cityString = dictClient.getNameByValue(patient.getCityCode());
        String districtString = dictClient.getNameByValue(patient.getDistrictCode());
        patient.setParam(
                Map.of("certificatesTypeString" ,certificatesType ,
                        "provinceString"  , provinceString ,
                        "cityString" ,cityString ,
                        "districtString"  ,  districtString ,
                         "fullAddress" , provinceString + cityString + districtString + patient.getAddress()
                ));
    }

}
