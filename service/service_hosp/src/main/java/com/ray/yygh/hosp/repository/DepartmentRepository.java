package com.ray.yygh.hosp.repository;

import com.ray.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends MongoRepository<Department,String> {

    //通过医院编号和科室号查询科室信息
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
