package com.ray.yygh.hosp.service;

import com.ray.yygh.model.hosp.Schedule;
import com.ray.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    //上传排班接口
    void save(Map<String, Object> paramMap);

    //医院端分页查询排班接口
    Page<Schedule> findPageSchedule(int page, int limit, ScheduleQueryVo scheduleQueryVo);

    //医院端删除排班信息接口
    void remove(String hoscode, String hosScheduleId);

    //根据医院编号和科室编号查询排班规则数据
    Map<String, Object> getScheduleRule(long page, long limit, String hoscode, String depcode);

    //根据医院编号，科室编号以及日期查询详细排班信息
    List<Schedule> getScheduleDetail(String hoscode, String depcode, String workDate);
}
