package com.ray.yygh.msm.service.impl;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.ray.yygh.msm.service.MsmService;
import com.ray.yygh.msm.utils.ConstantPropertiesUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Set;

@Service
public class MsmServiceImpl implements MsmService {

    @Override
    public boolean send(String phone, String code) {
        //判断手机号是否为空
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(ConstantPropertiesUtils.SERVER_IP, ConstantPropertiesUtils.SERVER_PORT);
        sdk.setAccount(ConstantPropertiesUtils.ACCOUNT_SID, ConstantPropertiesUtils.ACCOUNT_TOKEN);
        sdk.setAppId(ConstantPropertiesUtils.APPID);
        sdk.setBodyType(BodyType.Type_JSON);
        String to = "15626167981";
        String templateId = "1";
        String[] datas = {code, "2"};
        HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas);
        if ("000000".equals(result.get("statusCode"))) {
            //正常返回输出data包体信息（map）
            HashMap<String, Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object object = data.get(key);
                System.out.println(key + " = " + object);
            }
            return true;
        } else {
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") + " 错误信息= " + result.get("statusMsg"));
            return false;
        }
    }
}
