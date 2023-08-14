package com.ray.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ray.yygh.common.result.Result;
import com.ray.yygh.hosp.service.HospitalSetService;
import com.ray.yygh.model.hosp.HospitalSet;
import com.ray.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    //1.查询医院设置表所有信息
    @ApiOperation("查询所有hosipitalSet")
    @GetMapping("findAll")
    public Result findAll(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    @ApiOperation("逻辑删除hospitalSet")
    @DeleteMapping("{id}")
    public Result removeHospitalSet(@PathVariable Long id){
        boolean flag = hospitalSetService.removeById(id);
        if(flag){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("条件分页查询")
    @GetMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){


    //创建分页对象，传递当前页
        Page<HospitalSet> page = new Page<>(current,limit);
        System.out.println(limit);
        QueryWrapper<HospitalSet> hospitalSetQueryWrapper = new QueryWrapper<>();
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        if(!StringUtils.isEmpty(hosname)){
            hospitalSetQueryWrapper.like("hosname",hosname);
        }
        if(!StringUtils.isEmpty(hoscode)){
            hospitalSetQueryWrapper.eq("hoscode",hoscode);
        }
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page,hospitalSetQueryWrapper);
        return Result.ok(hospitalSetPage);
    }

}
