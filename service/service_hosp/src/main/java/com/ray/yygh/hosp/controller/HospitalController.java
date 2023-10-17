package com.ray.yygh.hosp.controller;

import com.ray.yygh.common.result.Result;
import com.ray.yygh.hosp.service.HospitalService;
import com.ray.yygh.hosp.service.HospitalSetService;
import com.ray.yygh.model.hosp.Hospital;
import com.ray.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin
@Api(tags = "后端操作医院列表")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    //后端更新医院上线状态
    @ApiOperation("后端更新医院上线状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id,
                               @PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return Result.ok();
    }

    //后端分页查询医院列表
    @ApiOperation("后端医院列表分页查询")
    @GetMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           HospitalQueryVo hospitalQueryVo){
        Page pageModel = hospitalService.selectHospPage(page,limit,hospitalQueryVo);
        return Result.ok(pageModel);
    }

    //后端查询医院详情
    @ApiOperation("查询后端医院详情")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){
        Map<String,Object> map = hospitalService.getHospById(id);
        return Result.ok(map);
    }

}
