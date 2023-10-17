package com.ray.yygh.hosp.controller;

import com.ray.yygh.common.result.Result;
import com.ray.yygh.hosp.service.ScheduleService;
import com.ray.yygh.model.hosp.Schedule;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
//@CrossOrigin
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    //根据医院编号和科室编号查询排班规则数据
    @ApiOperation(value = "查询排班规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable long page,
                                  @PathVariable long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode){
        Map<String,Object>  map = scheduleService.getScheduleRule(page,limit,hoscode,depcode);
        return Result.ok(map);
    }

    //点击排班预约日期显示排班详情
    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result getScheduleDetail(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate){
        List<Schedule> scheduleList = scheduleService.getScheduleDetail(hoscode,depcode,workDate);
        return Result.ok(scheduleList);
    }
}
