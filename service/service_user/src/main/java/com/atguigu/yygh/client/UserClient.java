package com.atguigu.yygh.client;

import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;

import com.atguigu.yygh.service.PatientService;
import com.atguigu.yygh.service.UserService;
import com.atguigu.yygh.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user/client")
@RestController
public class UserClient {

    @Autowired
    private UserService userService;

    @Autowired
    private PatientService patientService;

    @PostMapping("/getUserInfo")
    public UserInfo getUserInfo(@RequestHeader("token") String token){
        Long userId = JwtHelper.getUserId(token);
        return userService.getById(userId);

    }

    @GetMapping("/patient/{id}")
    public Patient getPatient(@PathVariable("id") Long id ){

        return patientService.getById(id);
    }
}
