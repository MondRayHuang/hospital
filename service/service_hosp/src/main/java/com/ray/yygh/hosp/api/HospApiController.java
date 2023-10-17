package com.ray.yygh.hosp.api;


import com.ray.yygh.common.result.Result;
import com.ray.yygh.hosp.service.DepartmentService;
import com.ray.yygh.hosp.service.HospitalService;
import com.ray.yygh.model.hosp.Hospital;
import com.ray.yygh.vo.hosp.DepartmentVo;
import com.ray.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static jdk.nashorn.internal.runtime.Context.DEBUG;

@Api(tags = "用户端医院接口")
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospApiController {
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    //用户端查询医院列表
    @ApiOperation(value = "查询医院列表")
    @GetMapping("/findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){
        System.out.println("+++++++++");
        Page<Hospital> hospitalList = hospitalService.selectHospPage(page,limit,hospitalQueryVo);
        return Result.ok(hospitalList);
    }

    //根据医院名称模糊查询
    @ApiOperation(value = "根据医院名称模糊查询")
    @GetMapping("findByHosName/{hosname}")
    public Result findByHosName(@PathVariable String hosname){
        List<Hospital> hospitalList = hospitalService.findByHosname(hosname);
        return Result.ok(hospitalList);
    }

    //根据医院编号查询医院科室详情
    @ApiOperation(value = "根据医院编号查询医院科室详情")
    @GetMapping("department/{hoscode}")
    public Result index(@PathVariable String hoscode){
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }

    //根据医院编号查询医院预约挂号详情
    @ApiOperation(value = "根据医院编号查询医院预约挂号详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result item(@PathVariable String hoscode){
        System.out.println(hoscode);
        Map<String,Object> map = hospitalService.item(hoscode);
        return Result.ok(map);
    }
}
