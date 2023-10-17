package com.ray.yygh.hosp.controller;

import com.ray.yygh.common.result.Result;
import com.ray.yygh.hosp.service.DepartmentService;
import com.ray.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
//@CrossOrigin
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @ApiOperation("后端查询医院所有科室列表")
    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable String hoscode){
        List<DepartmentVo> departmentVoList = departmentService.findDeptTree(hoscode);
        return Result.ok(departmentVoList);
    }
}
