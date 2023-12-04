package com.ray.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ray.yygh.model.user.UserInfo;
import com.ray.yygh.vo.user.LoginVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    //用户手机号登录
    Map<String, Object> loginUser(LoginVo loginVo);

    //通过openid获取用户信息
    UserInfo selectWxInfoOpenId(String openid);
}
