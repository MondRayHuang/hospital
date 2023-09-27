package com.ray.yygh.hosp.service;

import com.ray.yygh.model.hosp.Hospital;

import java.util.Map;

public interface HospitalService {
    //医院端上传医院信息接口
    void save(Map<String, Object> paramMap);

    //医院端获取医院信息接口
    Hospital getByHoscode(String hoscode);
}
