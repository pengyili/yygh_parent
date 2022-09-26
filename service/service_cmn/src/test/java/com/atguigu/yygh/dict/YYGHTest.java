package com.atguigu.yygh.dict;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.atguigu.yygh.dict.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
public class YYGHTest {
    @Autowired
    DictService dictService;


    @Test
    public void test1(){
//        File file = new File("D://abc.xlsx");
//        EasyExcel.write(file , Dict.class).sheet("数据字典1").doWrite(() -> dictService.getDictByParentId(1l));
        System.out.println(UUID.randomUUID().toString());
    }
    @Test
    public void test2(){
        File file = new File("D://abc.xlsx");
//        List<Object> dict = EasyExcel.read(file).sheet("数据字典1").doReadSync();
//        System.out.println(dict);

        ExcelWriter excelWriter = EasyExcel.write(file , Dict.class).build();
        excelWriter.write(dictService.getDictByParentId(1L),EasyExcel.writerSheet("字典数据").build());
        excelWriter.finish();


//
        EasyExcel.read(file , new PageReadListener( System.out::println)).sheet("数据字典1").doRead();
//        EasyExcel.read();
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void tst1(){
        BaseMapper<Dict> baseMapper = dictService.getBaseMapper();
        Dict dict = baseMapper.selectOne(new LambdaQueryWrapper<Dict>().or(s ->s.eq(Dict::getId, 1)));
        baseMapper.update(dict,  new LambdaUpdateWrapper<Dict>().set(Dict::getIsDeleted , 0) ) ;
        System.out.println(dict);
    }

    @Test
    public void te1(){
        Map<Integer  , String > map = new HashMap<>() ;
        map.put(10 , "12");
        map.put(11 , "12");
        map.put(12 , "12");
        map.put(13 , "12");
        map.put(14 , "12");
        map.put(15 , "12");

        Collection<String> values = map.values();
        map.put(16 , "12");

        Collection<String> values1 = map.values();



    }
    @Test // 4501
    public void te2(){
        Dict dict = new Dict() ;
        dict.setId(1L);
        dict.setParentId(0L);
        Long startTime = System.currentTimeMillis();
        for (int i = 0 ; i < 4000  ; i++){
            dictService.updateById(dict);
        }
        Long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

    }
    @Test
    public void t3(){
        Dict dict = new Dict() ;
        dict.setId(1L);
        dict.setParentId(0L);
        dict.setName("obj");
        dict.setDictCode("qwqw");
        Dict dict1 =new Dict() ;
        BeanUtils.copyProperties(dict , dict1);
        dict1.setDictCode("qs1a5w");
        Long startTime = System.currentTimeMillis();
        for (int i = 0 ; i < 4000  ; i++){
            dict1.equals(dict);
        }
        Long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);


    }

}
