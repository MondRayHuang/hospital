package com.ray.yygh.msm.service;

public interface MsmService {
    //发送短信
    boolean send(String phone, String code);
}
