package com.ray.yygh.hosp.service;

import com.ray.yygh.model.hosp.Hospital;
import com.ray.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    //医院端上传医院信息接口
    void save(Map<String, Object> paramMap);

    //医院端获取医院信息接口
    Hospital getByHoscode(String hoscode);

    //后端分页查询医院信息接口
    Page selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo);

    //后端更新医院上线状态
    void updateStatus(String id, Integer status);

    //后端查询医院详情
    Map<String, Object> getHospById(String id);


    //根据医院编号获取医院名称
    String getHospName(String hoscode);

    //用户端根据医院名称查询医院列表
    List<Hospital> findByHosname(String hosname);

    //用户端根据医院编号查询医院预约挂号详情
    Map<String, Object> item(String hoscode);
}
