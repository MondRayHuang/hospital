package com.ray.yygh.hosp.service;

import com.ray.yygh.model.hosp.Department;
import com.ray.yygh.vo.hosp.DepartmentQueryVo;
import com.ray.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    //添加科室信息到 mongodb
    void save(Map<String, Object> paramMap);

    //分页查询特定医院的科室信息
    Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo);

    //根据医院端传过来的hoscpde 和 depcode 删除科室接口
    void remove(String hoscode, String depcode);

    //后端查询医院所有科室列表
    List<DepartmentVo> findDeptTree(String hoscode);

    //根据医院编号和科室编号获取科室名称
    String getDepName(String hoscode, String depcode);
}
