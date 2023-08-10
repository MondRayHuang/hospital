package com.ray.yygh.hosp.controller;

import com.ray.yygh.hosp.service.HospitalSetService;
import com.ray.yygh.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;

    //1.查询医院设置表所有信息
    @GetMapping("findAll")
    public List<HospitalSet> findAll(){
        List<HospitalSet> list = hospitalSetService.list();
        return list;
    }

}
