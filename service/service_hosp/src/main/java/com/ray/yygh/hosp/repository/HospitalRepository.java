package com.ray.yygh.hosp.repository;

import com.ray.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {
    //根据医院编号获取医院信息
    Hospital getHospitalByHoscode(String hoscode);

    //用户端通过医院名称模糊查询医院列表
    List<Hospital> findHospitalByHosnameLike(String hosname);
}
