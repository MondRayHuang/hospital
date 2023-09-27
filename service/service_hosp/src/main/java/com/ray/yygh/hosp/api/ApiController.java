package com.ray.yygh.hosp.api;

import com.alibaba.fastjson.JSONObject;
import com.ray.yygh.common.exception.YyghException;
import com.ray.yygh.common.helper.HttpRequestHelper;
import com.ray.yygh.common.result.Result;
import com.ray.yygh.common.result.ResultCodeEnum;
import com.ray.yygh.common.util.MD5;
import com.ray.yygh.hosp.service.HospitalService;
import com.ray.yygh.hosp.service.HospitalSetService;
import com.ray.yygh.model.hosp.Hospital;
import com.ray.yygh.model.hosp.HospitalSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
