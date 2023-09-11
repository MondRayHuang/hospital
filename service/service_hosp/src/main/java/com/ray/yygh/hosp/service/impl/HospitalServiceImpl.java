package com.ray.yygh.hosp.service.impl;

import com.ray.yygh.hosp.repository.HospitalRepository;
import com.ray.yygh.hosp.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;


}
