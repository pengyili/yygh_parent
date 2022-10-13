package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.dict.DictClient;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@Slf4j
@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DictClient dictClient;

    @Autowired
    HospitalSetService hospitalSetService;

    @Override
    public Page<Hospital> getHospitalList(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo) {
        long count = mongoTemplate.count(Query.query(new Criteria()), Hospital.class);

        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        List<Hospital> hospitals = mongoTemplate.find(
                Query.query(
                        Criteria.byExample(
                                Example.of(
                                        hospital,
                                        ExampleMatcher
                                                .matching()
                                                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)))
                ).skip((pageNum.longValue() - 1) * pageSize.longValue()).limit(pageSize), Hospital.class
        );
//        mongoTemplate.find(
////                Query.query("")
//        )
        hospitals.forEach(s -> setHospParam(s));

        return new PageImpl<>(hospitals, PageRequest.of(pageNum, pageSize), count);
    }

    @Override
    public Hospital getHospitalById(String id) {

        Hospital hospital = mongoTemplate.findOne(
                Query.query(Criteria.where("id").is(id))
                , Hospital.class
        );
        if (hospital != null)
            setHospParam(hospital);
        return hospital;
    }

    //    @Override
//    public void changeHospitalStatus(String id, Integer status) {
//        Hospital hospital =  mongoTemplate.findById(id, Hospital.class);
//        if(hospital != null){
//            hospital.setStatus(status);
//            mongoTemplate.save(hospital);
//        }
//        hospitalSetService.changeStatus();
//    }
    @Override
    public void changeHospitalStatus(String id, Integer status) {
        Hospital hospital = mongoTemplate.findById(id, Hospital.class);
        Assert.notNull(hospital, "没有此医院");
        hospital.setStatus(status);
        mongoTemplate.save(hospital);

        BaseMapper<HospitalSet> baseMapper = hospitalSetService.getBaseMapper();

        HospitalSet hospitalSet = baseMapper.selectOne(new LambdaQueryWrapper<HospitalSet>().eq(HospitalSet::getHoscode, hospital.getHoscode()));

        hospitalSet.setStatus(status);

        baseMapper.updateById(hospitalSet);
//        hospitalSetService.changeStatus(hoscode , status);
    }


    @Override
    public List<DepartmentVo> setTreeStructure(List<Department> departments) {
        List<DepartmentVo> departmentVos = departments.stream()
                .collect(Collectors.groupingBy(Department::getBigcode))
                .entrySet()
                .stream()
                .map(departmentEntry -> {
                    DepartmentVo departmentVo = new DepartmentVo();
                    departmentVo.setDepname(departmentEntry.getValue().get(0).getBigname());
                    departmentVo.setDepcode(departmentEntry.getKey());
                    departmentVo.setChildren(
                            departmentEntry.getValue()
                                    .stream()
                                    .map(department -> {
                                        DepartmentVo departmentVo1 = new DepartmentVo();
                                        BeanUtils.copyProperties(department, departmentVo1);
                                        return departmentVo1;
                                    })
                                    .toList()
                    );
                    return departmentVo;
                })
                .toList();

        return departmentVos;
    }

    @Override
    public List<Department> getDepartmentList(String hoscode) {

        return mongoTemplate.find(Query.query(Criteria.where("hoscode").is(hoscode)), Department.class);
    }

    @Override
    public List<BookingScheduleRuleVo> getScheduleGroupByWoreData(Integer pageNum, Integer pageSize, String hoscode, String depcode) {

        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.match(Criteria.where("hoscode").is(hoscode)),
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.by("workDate")),
                Aggregation.skip((pageNum - 1) * pageSize),
                Aggregation.limit(pageSize)

        ), Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        mappedResults.forEach(s -> s.setDayOfWeek(dateToWeekDay(new DateTime(s.getWorkDate()))));
        return mappedResults;

    }

    @Override
    public Integer getScheduleGroupByWoreDataTotal(String hoscode, String depcode) {

        AggregationResults<Schedule> workDate = mongoTemplate.aggregate(Aggregation.newAggregation(
                Aggregation.group("workDate")
        ), Schedule.class, Schedule.class);

        return workDate.getMappedResults().size();
    }

    @Override
    public List<Schedule> getScheduleDetailList(ScheduleQueryVo scheduleQueryVo) {
        List<Schedule> scheduleList = mongoTemplate.find(
                Query.query(
                        Criteria.where("hoscode").is(scheduleQueryVo.getHoscode())
                                .and("depcode").is(scheduleQueryVo.getDepcode())
                                .and("workDate").is(scheduleQueryVo.getWorkDate())
                )
                , Schedule.class);

        return scheduleList;
    }

    @Override
    public Hospital getHospitalByHoscode(String hoscode) {
        return mongoTemplate.findOne(Query.query(Criteria.where("hoscode").is(hoscode)) , Hospital.class);
    }

    @Override
    public Department getDepByHoscodeAndDepcode(String hoscode, String depcode) {
        return mongoTemplate.findOne(Query.query(Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode)) , Department.class);
    }

    @Override
    public List<Hospital> getHospitalByHosname(String hosname) {
        Hospital hospital = new Hospital();
        hospital.setHosname(hosname);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Query query = Query.query(Criteria.byExample(Example.of(hospital, exampleMatcher)));
        List<Hospital> one = mongoTemplate.find(query, Hospital.class);
        one.forEach( s -> setHospParam(s));
        return one;
    }

    public Department getDepartment(String hoscode , String depcode){
        return mongoTemplate.findOne(
                Query.query(Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode)) ,
                Department.class);
    }

    private void setHospParam(Hospital hospital) {

        String hosTypeString = dictClient.getNameByValueAndParentCode(hospital.getHostype(), "Hostype");
        String fullAddress = new StringBuilder()
                .append(dictClient.getNameByValue(hospital.getProvinceCode()))
                .append(dictClient.getNameByValue(hospital.getCityCode()))
                .append(dictClient.getNameByValue(hospital.getDistrictCode()))
                .append(hospital.getAddress())
                .toString();

        hospital.setParam(Map.of("hostypeString", hosTypeString, "fullAddress", fullAddress));
    }

    private String dateToWeekDay(DateTime dateTime) {

        return switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY -> "周日";
            case DateTimeConstants.FRIDAY -> "周五";
            case DateTimeConstants.WEDNESDAY -> "周三";
            case DateTimeConstants.TUESDAY -> "周二";
            case DateTimeConstants.THURSDAY -> "周四";
            case DateTimeConstants.SATURDAY -> "周六";
            default -> "周一";
        };

    }

    public Map buildFrontBookingScheduleRuleVo( Integer page , Integer limit  , String hoscode ,String depcode ){
        Hospital hospital = getHospitalByHoscode(hoscode);
        Integer cycle = hospital.getBookingRule().getCycle();
        Integer showcycle = cycle +1  ;

        Integer beginCount = (page - 1 ) * limit ;
        DateTime pageStartDate = DateTime.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT+8"))).plusDays((page - 1) * limit ) ;

        if(parseDateAndString(new Date() ,hospital.getBookingRule().getStopTime()).isBeforeNow())
            showcycle +=  1;
        List<Date> dateTimes = new ArrayList<>() ;
        for(int i  = 0 ; i < limit ; i++ ) {
            if(beginCount + i >=  showcycle){
                break;
            }
            String s = pageStartDate.plusDays(i).toString("yyyy/MM/dd");
            dateTimes.add(DateTimeFormat.forPattern("yyyy/MM/dd").parseDateTime(s).toDate());
        }

        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("hoscode").is(hoscode).and("workDate").in(dateTimes).and("depcode").is(depcode)),
                        Aggregation.group("workDate").first("workDate").as("workDate")
                                .sum("reservedNumber").as("reservedNumber")
                                .sum("availableNumber").as("availableNumber")

                ), Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        Map<Date, BookingScheduleRuleVo> collect = mappedResults.stream().collect(Collectors.toMap(s -> s.getWorkDate(), s -> s));

        List<BookingScheduleRuleVo> resultBookingScheduleRuleVo = new ArrayList<>() ;

        for(Date date : dateTimes){
            BookingScheduleRuleVo bookingScheduleRuleVo = collect.get(date);
            if(bookingScheduleRuleVo == null ){
                 bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //就诊医生人数
                bookingScheduleRuleVo.setDocCount(0);
                //科室剩余预约数  -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }

            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setDayOfWeek(dateToWeekDay(new DateTime(date)));

            if(parseDateAndString(date ,hospital.getBookingRule().getStopTime()).isBeforeNow()) {
                bookingScheduleRuleVo.setStatus(-1);
            }
            else if (parseDateAndString(date , hospital.getBookingRule().getReleaseTime()).minusDays(cycle).isAfterNow())
                bookingScheduleRuleVo.setStatus(1);
            else
                bookingScheduleRuleVo.setStatus(0);

            resultBookingScheduleRuleVo.add(bookingScheduleRuleVo) ;
        }

        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospital.getHosname());
        //科室
        Department department =getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", hospital.getBookingRule().getReleaseTime());
        //停号时间
        baseMap.put("stopTime", hospital.getBookingRule().getStopTime());

        Map map = new HashMap();
        map.put("bookingScheduleList", resultBookingScheduleRuleVo);
        map.put("total", showcycle);
        map.put("baseMap", baseMap);




        return map ;
    }

    public  DateTime parseDateAndString(Date date , String  str ){
        DateTime dateTime = new DateTime(date);
        String s = dateTime.toString("yyyy-MM-dd ");
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(s + str);
    }

    @Override
    public List<Schedule> getWorkDateScheduleList(String hoscode, String depcode, Date workDate) {
        return mongoTemplate.find(Query.query(
                Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").is(workDate)
        ) ,                 Schedule.class);
    }

    @Override
    public Schedule getScheduleById(String id) {
        return mongoTemplate.findById(id ,  Schedule.class);
    }

    @Override
    public void setScheduleParam(Schedule schedule) {
        schedule.setParam(Map.of("dayOfWeek" , dateToWeekDay(new DateTime(schedule.getWorkDate())),
                "hosname" , getHospitalByHoscode(schedule.getHoscode()).getHosname(),
                "depname" , getDepartment(schedule.getHoscode() , schedule.getDepcode()).getDepname()
                ));
    }
}
