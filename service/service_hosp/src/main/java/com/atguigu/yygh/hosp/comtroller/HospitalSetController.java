package com.atguigu.yygh.hosp.comtroller;

import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import springfox.documentation.service.ParameterType;

import java.util.Random;
import java.util.random.RandomGenerator;


@Api(tags = "医院设置相关服务" , protocols = "http" )
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService ;


    @ApiOperation("分页查询医院设置")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "page", value = "当前页码" , dataTypeClass=Integer.class, required = true,paramType = "path" ,example = "1"),
                    @ApiImplicitParam(name = "limit", value = "每页最大多少条数据" , paramType = "path", dataTypeClass = Integer.class, required = true , example = "2") ,
                    @ApiImplicitParam(name = "hospitalSetQueryVo" ,value = "查询条件封装" , paramType = "form", required = true  ,dataTypeClass = HospitalSetQueryVo.class)
            }


    )
    @GetMapping("/{page}/{limit}")
    public Result getHospitalSetList(@PathVariable("page") Integer page, @PathVariable("limit") Integer limit,
                                     HospitalSetQueryVo hospitalSetQueryVo){

        Page<HospitalSet> pageList = hospitalSetService.page(
                new Page<>(page, limit) ,
                new LambdaQueryWrapper<HospitalSet>()
                        .like(StringUtils.isEmpty(hospitalSetQueryVo.getHosname()),HospitalSet::getHosname , hospitalSetQueryVo.getHosname())
                        .or(StringUtils.isEmpty(hospitalSetQueryVo.getHoscode()) ,query->query.eq(HospitalSet::getHoscode , hospitalSetQueryVo.getHoscode()))
                );
        return Result.ok().data("list" , pageList).data("total" , pageList.getTotal());
    }

    @ApiOperation("添加医院设置信息" )
    @ApiImplicitParam(name = "hospitalSet" , value = "需要添加的医院设置信息", required = true ,dataTypeClass = HospitalSet.class , paramType = "body" )
    @PostMapping("/")
    public Result addHospitalSet(@RequestBody HospitalSet hospitalSet){

        hospitalSet.setSignKey(String.valueOf(System.currentTimeMillis()) + RandomGenerator.getDefault().nextLong(10000L));
        hospitalSetService.save(hospitalSet);
        return Result.ok();
    }
    @ApiOperation(value = "根据医院设置id查询医院设置详情" )
    @ApiImplicitParam(name = "id", value="需要查询的医院设置id")
    @GetMapping("/{id}")
    public Result getHospitalSet(@PathVariable("id") Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok().data("item" , hospitalSet);
    }

    @ApiOperation(value = "修改医院设置项" )
    @ApiImplicitParam(name = "hospitalSet"  , value = "修改后的设置信息", paramType = "body" , dataTypeClass = HospitalSet.class)
    @PutMapping("/")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }


    @ApiOperation("根据id删除对应的医院设置")
    @ApiImplicitParam(name="id", value = "需要删除的医院id", paramType = "path" , dataTypeClass = Long.class)
    @DeleteMapping("/{id}")
    public Result deleteHospitalSet(@PathVariable Long id ){
        return Result.ok().data( "effect", hospitalSetService.removeById(id)) ;
    }


    @ApiOperation("获取所有的医院设置")
    @GetMapping("/findAll")
    public Result selectAllHospitalSet(){
        return Result.ok().data("list" , hospitalSetService.list());
    }

}
