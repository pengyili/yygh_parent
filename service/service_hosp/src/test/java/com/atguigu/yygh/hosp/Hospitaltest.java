package com.atguigu.yygh.hosp;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@SpringBootTest
public class Hospitaltest {

    @Autowired
    MongoTemplate mongoTemplate;
    @Test
    public void test() {

        HospitalQueryVo hospitalQueryVo = new HospitalQueryVo();
        Hospital hospital = new Hospital();

        long count = mongoTemplate.count(Query.query(new Criteria()), Hospital.class);
        System.out.println(count);


        hospital.setHosname("北京协和医院");

//        BeanUtils.copyProperties(hospitalQueryVo , hospital);
//        List<Schedule> scheduleList = mongoTemplate.find(null, Schedule.class);
//        List<Hospital> hospitals = mongoTemplate.find(
//                Query.query(
//                        Criteria.byExample(
//                                Example.of(
//                                        hospital, ExampleMatcher.matching()
//                                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
//                                                .withIgnoreCase(false)
//                                )
//                        )
//                ),
//                Hospital.class
//        );
//
//        System.out.println(hospitals.size());
//        hospitals.forEach(System.out::println);


    }
}