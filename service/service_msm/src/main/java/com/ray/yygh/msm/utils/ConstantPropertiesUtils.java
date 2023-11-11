package com.ray.yygh.msm.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertiesUtils implements InitializingBean {
    @Value("${ronglianyun.sms.serverIp}")
    private String serverIp;
    @Value("${ronglianyun.sms.serverPort}")
    private String serverPort;
    @Value("${ronglianyun.sms.accountSId}")
    private String accountSId;
    @Value("${ronglianyun.sms.accountToken}")
    private String accountToken;
    @Value("${ronglianyun.sms.appId}")
    private String appId;

    public static String SERVER_IP;
    public static String SERVER_PORT;
    public static String ACCOUNT_SID;
    public static String ACCOUNT_TOKEN;
    public static String APPID;

    @Override
    public void afterPropertiesSet() throws Exception {
        SERVER_IP = serverIp;
        SERVER_PORT = serverPort;
        ACCOUNT_SID = accountSId;
        ACCOUNT_TOKEN = accountToken;
        APPID = appId;
    }
}
