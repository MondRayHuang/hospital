package com.ray.yygh.hosp.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ray.yygh.common.exception.YyghException;
import com.ray.yygh.common.result.Result;
import com.ray.yygh.common.utils.MD5;
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
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin
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
    @PostMapping("findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){


    //创建分页对象，传递当前页
        Page<HospitalSet> page = new Page<>(current,limit);
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

    @ApiOperation("添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置医院状态:1 表示可用；2 表示不可用
        hospitalSet.setStatus(1);

        //设置签名密钥
        Random random = new Random();

        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000)));
        boolean flag = hospitalSetService.save(hospitalSet);
        if(flag){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation("根据医院id查询医院设置")
    @GetMapping("getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok(hospitalSet);
    }

    @ApiOperation("修改医院设置")
    @PostMapping("updateHospitalSet")
    public Result updateHospitalSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalSetService.updateById(hospitalSet);
        if(flag){
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation("批量删除医院设置")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        hospitalSetService.removeByIds(idList);
        return Result.ok();
    }

    @ApiOperation("医院设置锁定和解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status){
        //根据 id 查询出医院设置
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //修改医院设置的状态
        hospitalSet.setStatus(status);
        //更新医院设置
        hospitalSetService.updateById(hospitalSet);
        return Result.ok();
    }

    @ApiOperation("发送签名密钥")
    @PutMapping("sendKey/{id}")
    public Result sendKey(@PathVariable Long id){
        //通过 id 查询医院设置
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //获取医院的医院密钥
        String signKey = hospitalSet.getSignKey();
        //获取医院的医院编号
        String hoscode = hospitalSet.getHoscode();
        System.out.println();
        // TODO: 2023/8/15 发送短信
        return Result.ok();
    }


}
