package com.ray.yygh.hosp.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ray.yygh.hosp.mapper.HospitalSetMapper;
import com.ray.yygh.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {

    String getSignKey(String hoscode);
}
