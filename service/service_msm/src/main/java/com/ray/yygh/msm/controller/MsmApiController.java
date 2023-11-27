package com.ray.yygh.msm.controller;

import com.ray.yygh.common.result.Result;
import com.ray.yygh.msm.service.MsmService;
import com.ray.yygh.msm.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")
public class MsmApiController {
    @Autowired
    private MsmService msmService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //发送手机验证码
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone){
        //从redis获取验证码，如果能获取则直接返回验证码
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)){
            return Result.ok();
        }
        //如何获取不到验证码则生成验证码
        code = RandomUtil.getFourBitRandom();

        //调用发短信功能
        boolean isSend = msmService.send(phone,code);
        //验证码存进redis中，并且设置有效时间
        if(isSend){
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            System.out.println(code);
            return Result.ok();
        }else{
            return Result.fail().message("发送短信失败");
        }
    }
}
