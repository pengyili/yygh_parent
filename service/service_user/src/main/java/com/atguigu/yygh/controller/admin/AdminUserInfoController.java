package com.atguigu.yygh.controller.admin;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.service.PatientService;
import com.atguigu.yygh.service.UserService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/userinfo")
public class AdminUserInfoController {

    @Autowired
    private UserService userService;

    @Autowired
    private PatientService patientService ;
    @GetMapping("/{pageNum}/{pageSize}")
    public Result list(@PathVariable Integer  pageNum , @PathVariable Integer pageSize , UserInfoQueryVo userInfoQueryVo){
       Page<UserInfo> page = userService.page(Page.of(pageNum ,pageSize),
                new LambdaQueryWrapper<UserInfo>()
                        .and( StringUtils.isNotBlank(userInfoQueryVo.getKeyword()) ,
                                s -> s.like(UserInfo::getName, userInfoQueryVo.getKeyword())
                                .or( a -> a.like(UserInfo::getPhone, userInfoQueryVo.getKeyword()))
                        )
                        .eq(userInfoQueryVo.getStatus() != null, UserInfo::getStatus, userInfoQueryVo.getStatus())
                        .eq(userInfoQueryVo.getAuthStatus() != null , UserInfo::getAuthStatus , userInfoQueryVo.getAuthStatus())
                        .ge(StringUtils.isNotBlank(userInfoQueryVo.getCreateTimeBegin()) , UserInfo::getCreateTime , userInfoQueryVo.getCreateTimeBegin())
                        .le(StringUtils.isNotBlank(userInfoQueryVo.getCreateTimeEnd())  , UserInfo::getCreateTime , userInfoQueryVo.getCreateTimeEnd())

        );
       page.getRecords().forEach(s -> userService.setUserInfoParam(s));

       return Result.ok().data("page" , page) ;
    }
    @PutMapping("/lock/{id}/{status}")
    public Result changeStatus(@PathVariable Long id , @PathVariable Integer status){
        UserInfo userInfo = new UserInfo();
        userInfo.setStatus(status);
        userInfo.setId(id);
        boolean b = userService.updateById(userInfo);
        return b ? Result.ok() : Result.fail();
    }

    @GetMapping("/show/{id}")
    public Result show(@PathVariable Long id ){
        UserInfo userInfo = userService.getById(id);
        userService.setUserInfoParam(userInfo);

        List<Patient> list = patientService.list(new LambdaQueryWrapper<Patient>().eq(Patient::getUserId, id));
        list.forEach( s -> patientService.setPatientParam(s));
        return Result.ok().data("userInfo" , userInfo).data("patientList" , list) ;
    }

    @PutMapping("/approval/{id}/{authStatus}")
    public Result approval(@PathVariable Long id , @PathVariable  Integer authStatus) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setAuthStatus(authStatus);
        boolean b = userService.updateById(userInfo);
        return b ? Result.ok() : Result.fail();
    }
}
