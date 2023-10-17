package com.ray.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ray.yygh.cmn.client.DictFeignClient;
import com.ray.yygh.hosp.repository.HospitalRepository;
import com.ray.yygh.hosp.service.HospitalService;
import com.ray.yygh.model.hosp.Hospital;
import com.ray.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    //医院端上传医院信息接口,信息最终存储在 mangodb
    @Override
    public void save(Map<String, Object> paramMap) {
        String mapString = JSONObject.toJSONString(paramMap);
        Hospital hospital = JSONObject.parseObject(mapString,Hospital.class);

        //判断mangodb 是否已经存在当前医院的信息
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist = hospitalRepository.getHospitalByHoscode(hoscode);

        //如果已经存在，则进行更新
        if(hospitalExist != null){
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else{
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    //医院端从 mangodb 数据库获取医院信息
    @Override
    public Hospital getByHoscode(String hoscode) {
        return hospitalRepository.getHospitalByHoscode(hoscode);
    }

    //后端分页查询医院信息
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo)
    {
        //创建分页对象
        Pageable pageable = PageRequest.of(page - 1,limit);
        //创建匹配规则
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //开始弄查询条件
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建匹配对象
        Example<Hospital> hospitalExample = Example.of(hospital,exampleMatcher);

        //调用方法查询
        Page<Hospital> hospitalPage = hospitalRepository.findAll(hospitalExample,pageable);
        hospitalPage.getContent().forEach(this::setHospitalMessage);
        return hospitalPage;
    }

    //后端更新医院上线状态
    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    //后端查询医院详情
    @Override
    public Map<String, Object> getHospById(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();
        //对 mongodb 拿到的数据进行加工（医院等级，地址等)）
        this.setHospitalMessage(hospital);
        //由于需求规定一些关键信息需要单独拿出来，所以用 map 返回
        Map<String,Object> map = new HashMap<>();
        //将预约规则单独拎出来
        map.put("bookingRule",hospital.getBookingRule());
        //不需要重复返回
        hospital.setBookingRule(null);
        //医院信息
        map.put("hospital",hospital);
        return map;
    }

    //根据医院编号获取医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(hospital != null){
            return hospital.getHosname();
        }
        return null;

    }

    //用户端根据医院名称查询医院列表
    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.findHospitalByHosnameLike(hosname);

    }

    //用户端根据医院编号查询医院预约挂号详情
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String,Object> map = new HashMap<>();
        Hospital hospital = this.getByHoscode(hoscode);
        setHospitalMessage(hospital);
        map.put("hospital",hospital);
        map.put("bookingRule",hospital.getBookingRule());
        hospital.setBookingRule(null);
        return map;
    }

    //对分页查询后的医院信息进行加工，加入医院等级和医院地址
    private void setHospitalMessage(Hospital hospitalItem) {
        //医院等级
        String hostype = dictFeignClient.getName("Hostype", hospitalItem.getHostype());
        hospitalItem.getParam().put("hostypeString",hostype);
        //省份
        String provinceString = dictFeignClient.getName(hospitalItem.getProvinceCode());
        //市区
        String cityString = dictFeignClient.getName(hospitalItem.getCityCode());
        //区县
        String districtString = dictFeignClient.getName(hospitalItem.getDistrictCode());
        hospitalItem.getParam().put("hostypeString",dictFeignClient.getName("Hostype", hospitalItem.getHostype()));
        hospitalItem.getParam().put("fullAddress",provinceString + cityString + districtString);
    }
}
