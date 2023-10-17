package com.ray.yygh.user.service;

import com.ray.yygh.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService {
    //用户手机号登录
    Map<String, Object> loginUser(LoginVo loginVo);
}
