package com.ray.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ray.yygh.hosp.repository.HospitalRepository;
import com.ray.yygh.hosp.repository.ScheduleRepository;
import com.ray.yygh.hosp.service.DepartmentService;
import com.ray.yygh.hosp.service.HospitalService;
import com.ray.yygh.hosp.service.ScheduleService;
import com.ray.yygh.model.hosp.Schedule;
import com.ray.yygh.vo.hosp.BookingScheduleRuleVo;
import com.ray.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    //医院端上传排班接口
    @Override
    public void save(Map<String, Object> paramMap) {
        String paramMapString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramMapString, Schedule.class);

        //根据医院编号和排班编号查询
        Schedule scheduleExist = scheduleRepository.
                getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());
        //判断
        if (scheduleExist != null) {
            scheduleExist.setUpdateTime(new Date());
            scheduleExist.setIsDeleted(0);
            scheduleExist.setStatus(1);
            scheduleRepository.save(scheduleExist);
        } else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setStatus(1);
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }
    }

    //医院端分页查询排班信息
    @Override
    public Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        //创建 Example 对象
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);
        schedule.setStatus(1);
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule, exampleMatcher);
        Page<Schedule> schedulePage = scheduleRepository.findAll(example, pageable);
        return schedulePage;
    }

    //医院端删除排班信息
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        //根据医院编号和排班编号查询排班信息
        Schedule schedule = scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (schedule != null) {
            scheduleRepository.deleteById(schedule.getId());
        }
    }

    //根据医院编号和科室编号查询排班规则数据
    @Override
    public Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode) {
        //先按照医院编号和科室编号查询排班信息，然后按照日期分组展示
        //构建查询条件
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        //构建查询对象
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组对象
                        .first("workDate").as("workDate")
                        //统计上班的医生数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC, "workDate"),//排序
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );
        //调用 mongoTemplate 方法进行查询
        AggregationResults<BookingScheduleRuleVo> bookingScheduleRuleVoAggregationResults = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        //bookingScheduleRuleVoList记录的是每一天医生数量，号源数
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = bookingScheduleRuleVoAggregationResults.getMappedResults();

        //计算有多少天排班信息已经公布，用于实现前端的选择日期分页功能
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggResults = mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggResults.getMappedResults().size();

        //将 bookingScheduleRuleVoList中每一个对象的日期转换为星期
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hosName);
        //组装最后结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        resultMap.put("total", total);
        resultMap.put("baseMap", baseMap);

        return resultMap;
    }

    //根据医院编号，科室编号以及日期查询详细排班信息
    @Override
    public List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate) {
        List<Schedule> scheduleList = scheduleRepository.getScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, new DateTime(workDate).toDate());
        scheduleList.stream().forEach(item ->{
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    //封装排班详情信息
    private void packageSchedule(Schedule item) {
        //医院名称
        item.getParam().put("hosname",hospitalService.getHospName(item.getHoscode()));
        //科室名称
        item.getParam().put("depname",departmentService.getDepName(item.getHoscode(),item.getDepcode()));
        //设置日期对应的星期
        item.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(item.getWorkDate())));

    }

    /**
     * 根据日期获取周几数据
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;

    }
}
