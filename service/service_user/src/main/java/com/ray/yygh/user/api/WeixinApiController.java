package com.ray.yygh.user.api;

import com.ray.yygh.common.result.Result;
import com.ray.yygh.user.utils.ConstantWxPropertiesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {
    //1.生成微信扫码二维码
    //返回生成二维码的参数
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect(){
        try{
            Map<String,Object> map = new HashMap<>();
            map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
            map.put("scope","snsapi_login");
            String wxOpenRedirectUrl = ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL;
            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl,"utf-8");
            map.put("redirect_uri",wxOpenRedirectUrl);
            map.put("state",System.currentTimeMillis()+"");
            return Result.ok(map);
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }

    }

}
