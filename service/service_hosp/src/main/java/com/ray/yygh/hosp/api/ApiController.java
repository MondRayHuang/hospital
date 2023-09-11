package com.ray.yygh.hosp.api;

import com.ray.yygh.common.exception.YyghException;
import com.ray.yygh.common.helper.HttpRequestHelper;
import com.ray.yygh.common.result.Result;
import com.ray.yygh.common.result.ResultCodeEnum;
import com.ray.yygh.common.util.MD5;
import com.ray.yygh.hosp.service.HospitalService;
import io.swagger.annotations.Api;
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

    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String,String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //1.获取医院系统传来进行过MD5加密的
        String hospSign = (String)paramMap.get("sign");

        //2.获取医院的hoscdoe，查询数据库用于校验签名
        String hoscode = (String)paramMap.get("hoscode");
        String signKey = hospitalService.getSignKey(hoscode);
        //3.对获取的signKey进行加密
        String signKeyMd5 = MD5.encrypt(signKey);

        //判断签名是否一致
        if(!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }





        return Result.ok();
    }


}
