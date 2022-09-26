package com.atguigu.yygh.dict.controller;

import com.alibaba.excel.EasyExcel;

import com.atguigu.yygh.common.constant.YYGHConstant;
import com.atguigu.yygh.common.result.Result;
import com.atguigu.yygh.dict.listener.DictUpdateListener;
import com.atguigu.yygh.dict.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/admin/cmn/dict")
@Api(tags = "字典查询" , protocols="http")
@CrossOrigin
public class DictController {

    @Autowired
    private DictService dictService;

    private static final Map<Long, DictEeVo> DICT_EE_VO_MAP_CACHE = new ConcurrentHashMap<>();


    @ApiImplicitParam(name = "parentId", value = "根据id获取此节点下一级的所有节点" ,paramType = "path" , required = true , dataTypeClass = Long.class)
    @GetMapping("/getDictByParentId/{parentId}")
    public Result getParent(@PathVariable(value = "parentId" ) Long parentId) {
        List<Dict> dictList=dictService.getDictByParentId(parentId);
        return Result.ok().data(YYGHConstant.ITEM_LIST , dictList);
    }
    @ApiImplicitParam(name = "parentCode", value = "根据编码获取此节点下一级的所有节点" ,paramType = "path" , required = true , dataTypeClass = String.class)
    @GetMapping("/getDictByParentCode/{parentCode}")
    public Result getDict(@PathVariable String parentCode){
        return Result.ok().data(YYGHConstant.ITEM_LIST , dictService.getDictByParentCode(parentCode));
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        Collection<DictEeVo> dictEe = null ;
        if (DICT_EE_VO_MAP_CACHE.isEmpty()) {
             dictEe = dictService.list().stream().map(s -> {
                DictEeVo dictEeVo = new DictEeVo();
                BeanUtils.copyProperties(s, dictEeVo);
                 DICT_EE_VO_MAP_CACHE.put(dictEeVo.getId() , dictEeVo);
                return dictEeVo;
            }).toList();
        }else dictEe = DICT_EE_VO_MAP_CACHE.values();

        String fileName = URLEncoder.encode("字典数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), DictEeVo.class)
                .sheet("模板")
                .doWrite(dictEe);
    }


    @CacheEvict("dict")
    @PostMapping("/upload")
    public Result upload( MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictUpdateListener(dictService.getBaseMapper() ,DICT_EE_VO_MAP_CACHE)).doReadAll();
        return Result.ok();
    }
}
