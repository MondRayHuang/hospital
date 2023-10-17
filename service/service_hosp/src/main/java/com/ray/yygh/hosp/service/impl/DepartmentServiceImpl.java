package com.ray.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ray.yygh.hosp.repository.DepartmentRepository;
import com.ray.yygh.hosp.service.DepartmentService;
import com.ray.yygh.model.hosp.Department;
import com.ray.yygh.vo.hosp.DepartmentQueryVo;
import com.ray.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    //添加科室信息到 mongodb
    @Override
    public void save(Map<String, Object> paramMap) {
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString, Department.class);

        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());

        //判断是否已经存在当前科室
        if(departmentExist != null){
            departmentExist.setUpdateTime(new Date());
            departmentExist.setIsDeleted(0);
            departmentRepository.save(departmentExist);
        }else{
            department.setUpdateTime(new Date());
            department.setCreateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    //分页查询某医院的科室信息，返回到医院端
    @Override
    public Page<Department> findPageDepartment(int page, int limit, DepartmentQueryVo departmentQueryVo) {
        //创建Pageable对象,设置当前页和每页显示数
        Pageable pageable = PageRequest.of(page-1,limit);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Department department = new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        Example<Department> example = Example.of(department,matcher);
        Page<Department> all = departmentRepository.findAll(example,pageable);
        return all;
    }

    //根据医院端传过来的医院编号和科室编号进行科室删除
    @Override
    public void remove(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null){
            departmentRepository.deleteById(department.getId());
        }
    }

    //后端根据 hoscode查询医院所有科室列表
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建结果集合，这个 result里面的包含大科室，大科室下的子科室放在 children 里面
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号查询所有科室
        Department departmentQueryVo = new Department();
        departmentQueryVo.setHoscode(hoscode);
        Example<Department> departmentExample = Example.of(departmentQueryVo);
        //查询到了医院全部科室信息
        List<Department> departmentAll = departmentRepository.findAll(departmentExample);

        //根据 bigCode 分组,这个 collect 里面 key 是 bigcode，里面同一 bigcode 组成 list
        Map<String, List<Department>> collect = departmentAll.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //拿到每一个 bigcode 以及其对应的科室集合
        for(Map.Entry<String,List<Department>> entry : collect.entrySet()){
            //bigcode
            String bigcode = entry.getKey();
            //对应的科室集合
            List<Department> departmentListGroupByBigCode = entry.getValue();

            //封装大科室
            DepartmentVo departmentVo = new DepartmentVo();
            departmentVo.setDepcode(bigcode);
            departmentVo.setDepname(departmentListGroupByBigCode.get(0).getBigname());
            //封装在大科室里面的子科室
            List<DepartmentVo> departmentVoChildrenList = new ArrayList<>();
            for (Department department : departmentListGroupByBigCode){
                DepartmentVo departmentChildVo = new DepartmentVo();
                departmentChildVo.setDepname(department.getDepname());
                departmentChildVo.setDepcode(department.getDepcode());
                departmentVoChildrenList.add(departmentChildVo);
            }
            departmentVo.setChildren(departmentVoChildrenList);
            result.add(departmentVo);
        }
        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if(department != null){
            return department.getDepname();
        }
        return null;



    }
}
