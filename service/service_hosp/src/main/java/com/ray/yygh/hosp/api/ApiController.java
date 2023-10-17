package com.ray.yygh.hosp.api;

import com.alibaba.fastjson.JSONObject;
import com.ray.yygh.common.exception.YyghException;
import com.ray.yygh.common.helper.HttpRequestHelper;
import com.ray.yygh.common.result.Result;
import com.ray.yygh.common.result.ResultCodeEnum;
import com.ray.yygh.common.util.MD5;
import com.ray.yygh.hosp.service.DepartmentService;
import com.ray.yygh.hosp.service.HospitalService;
import com.ray.yygh.hosp.service.HospitalSetService;
import com.ray.yygh.hosp.service.ScheduleService;
import com.ray.yygh.model.hosp.Department;
import com.ray.yygh.model.hosp.Hospital;
import com.ray.yygh.model.hosp.HospitalSet;
import com.ray.yygh.model.hosp.Schedule;
import com.ray.yygh.vo.hosp.DepartmentQueryVo;
import com.ray.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {
    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    //医院端删除排班信息
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest request){
        Map<String,String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //验签
        //获取医院端传过来的 hoscode
        String hoscode = (String) paramMap.get("hoscode");
        //获取医院端传过来的加密后的签名
        String hospSign = (String) paramMap.get("sign");
        //获取医院端传过来的排班的编号
        String hosScheduleId = (String) paramMap.get("hosScheduleId");
        //获取后端存放着的签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //对后端签名进行加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //进行验签
        if(!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }



    //医院端查询排班信息
    @PostMapping("schedule/list")
    public Result findSchedule(HttpServletRequest request){
        Map<String,String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //验签
        //获取医院端传过来的 hoscode
        String hoscode = (String) paramMap.get("hoscode");
        //获取医院端传过来的加密后的签名
        String hospSign = (String) paramMap.get("sign");
        //获取医院端传过来的科室的编号
        String depcode = (String) paramMap.get("depcode");
        //获取后端存放着的签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //对后端签名进行加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //进行验签
        if(!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //分页信息
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String)paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String)paramMap.get("limit"));
        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        //调用service方法
        Page<Schedule> schedulePage = scheduleService.findPageSchedule(page,limit,scheduleQueryVo);
        return Result.ok(schedulePage);
    }

    //医院端上传排班信息
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String,String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院端传过来的 hoscode
        String hoscode = (String) paramMap.get("hoscode");
        //获取医院端传过来的加密后的签名
        String hospSign = (String) paramMap.get("sign");
        //获取医院端传过来的科室的编号
        String depcode = (String) paramMap.get("depcode");
        //获取后端存放着的签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //对后端签名进行加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //进行验签
        if(!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.save(paramMap);

        return Result.ok();
    }


    //医院端删除科室信息
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request){
        Map<String,String[]> requestMap = request.getParameterMap();
        Map<String,Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院端传过来的 hoscode
        String hoscode = (String) paramMap.get("hoscode");
        //获取医院端传过来的加密后的签名
        String hospSign = (String) paramMap.get("sign");
        //获取医院端传过来的科室的编号
        String depcode = (String) paramMap.get("depcode");
        //获取后端存放着的签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //对后端签名进行加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //进行验签
        if(!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        departmentService.remove(hoscode,depcode);
        return Result.ok();

    }

    //医院端从 Mongodb 查询科室信息
    @PostMapping("department/list")
    public Result findDepartment(HttpServletRequest request){
        Map<String,String[]> requestMap = request.getParameterMap();
        Map<String,Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        //获取医院端传过来的加密后的签名
        String hospSign = (String) paramMap.get("sign");
        //获取后端医院的签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //对后端医院签名加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //验签
        if(!signKeyMd5.equals(hospSign)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //分页设置
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String) paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 1 : Integer.parseInt((String) paramMap.get("limit"));

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        Page<Department> departmentPage = departmentService.findPageDepartment(page,limit,departmentQueryVo);
        return Result.ok(departmentPage);
    }


    //医院端上传科室信息到 mongodb 上
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String,String[]> requestMap = request.getParameterMap();
        Map<String,Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        //获取医院端传过来的已经加密过的签名
        String hospSign = (String) paramMap.get("sign");
        //查询后端当前医院的签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //对后端签名进行加密
        String signKeyMd5 = MD5.encrypt(signKey);

        //验签
        if(!signKeyMd5.equals(hospSign)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        departmentService.save(paramMap);
        return Result.ok();
    }



    //医院端使用的医院管理系统查询医院接口
    @RequestMapping("/hospital/show")
    public Result getHospital(HttpServletRequest request){
        //获取医院端传过来的信息
        Map<String,String[]> requestMap = request.getParameterMap();
        Map<String,Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院编号，准备进行验签
        String hoscode = (String) paramMap.get("hoscode");
        //获取医院端传来的已经加密的签名
        String hospSign = (String) paramMap.get("sign");
        //获取存在后端人员系统中的医院签名
        String signKey = hospitalSetService.getSignKey(hoscode);
        //对后端的签名进行加密
        String signKeyMd5 = MD5.encrypt(signKey);

        //进行验签
        if(!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }



    //各地医院上传自家信息到系统mangodb的接口
    @ApiOperation("上传医院接口")
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String,String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //1.获取医院系统传来的签名，这个签名已经进行过MD5 加密
        String hospSign = (String)paramMap.get("sign");
        System.out.println(hospSign);

        //2.获取医院的hoscdoe，查询数据库用于校验签名
        String hoscode = (String)paramMap.get("hoscode");
        System.out.println(hoscode);
        String signKey = hospitalSetService.getSignKey(hoscode);
        System.out.println(signKey);
        //3.对获取的医院系统的signKey进行加密
        String signKeyMd5 = MD5.encrypt(signKey);
        System.out.println(signKeyMd5);
        //判断从医院系统加密的签名与医院端传过来的签名是否一致
        if(!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        String logoData = (String) paramMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logoData",logoData);
        hospitalService.save(paramMap);
        return Result.ok();
    }


}
