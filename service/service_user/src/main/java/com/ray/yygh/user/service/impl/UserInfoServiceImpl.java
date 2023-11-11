package com.ray.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ray.yygh.common.exception.YyghException;
import com.ray.yygh.common.helper.JwtHelper;
import com.ray.yygh.common.result.ResultCodeEnum;
import com.ray.yygh.model.user.UserInfo;
import com.ray.yygh.user.mapper.UserInfoMapper;
import com.ray.yygh.user.service.UserInfoService;
import com.ray.yygh.vo.user.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    //用户手机号登录
    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        //1.获取用户的手机号
        String phone = loginVo.getPhone();
        //2.获取用户的验证码
        String code = loginVo.getCode();
        //3.判断手机号和验证码是否为空
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //4.判断手机验证码与输入的验证码是否一致
        String redisCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(redisCode)){
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        //5.判断是否为第一次用手机号登录
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",phone);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        //若是第一次登录则添加当前用户
        if(userInfo == null){
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        }

        //校验当前用户是否被禁用
        if(userInfo.getStatus() == 0){
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }

        //若不是第一次登录则直接登录

        //返回登录信息
        //返回登录用户名
        //返回 token 信息

        Map<String,Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)){
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)){
            name = userInfo.getPhone();
        }
//      //JWT生成 token
        String token = JwtHelper.createToken(userInfo.getId(),name);
        map.put("name",name);
        map.put("token",token);
        return map;

    }
}
