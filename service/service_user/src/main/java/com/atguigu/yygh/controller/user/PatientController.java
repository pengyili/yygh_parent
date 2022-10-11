package com.atguigu.yygh.controller.user;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.service.PatientService;
import com.atguigu.yygh.utils.JwtHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/info/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;


    @PostMapping()
    public Result save (@RequestBody  Patient patient , @RequestHeader String token){
        patient.setUserId(JwtHelper.getUserId(token));
        boolean save = patientService.save(patient);

       return save ? Result.ok() : Result.fail() ;
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id ){
        boolean b = patientService.removeById(id);
        return b ?  Result.ok() : Result.fail();
    }
    @GetMapping("/{id}")
    public Result getPatient(@PathVariable Long id ){

        Patient byId = patientService.getById(id);
        patientService.setPatientParam(byId);
        return Result.ok().data("item" , byId );
    }
    @PutMapping()
    public Result update( @RequestBody Patient patient ){
        boolean b = patientService.updateById(patient);
        return b ? Result.ok() : Result.fail();
    }
    @GetMapping()
    public Result list(@RequestHeader String token){
        Long userId = JwtHelper.getUserId(token);
        List<Patient> list = patientService.list(new LambdaQueryWrapper<Patient>().eq(Patient::getUserId, userId));
        list.forEach(s -> patientService.setPatientParam(s));
        return Result.ok().data("items" , list);
    }
}
