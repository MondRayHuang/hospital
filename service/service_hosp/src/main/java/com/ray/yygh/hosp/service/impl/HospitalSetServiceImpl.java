package com.ray.yygh.hosp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ray.yygh.hosp.mapper.HospitalSetMapper;
import com.ray.yygh.hosp.service.HospitalSetService;
import com.ray.yygh.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    @Autowired
    private HospitalSetMapper hospitalSetMapper;
}
