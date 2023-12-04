package com.ray.yygh.user.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ray.yygh.common.helper.JwtHelper;
import com.ray.yygh.common.result.Result;
import com.ray.yygh.model.user.UserInfo;
import com.ray.yygh.user.service.UserInfoService;
import com.ray.yygh.user.utils.ConstantWxPropertiesUtils;
import com.ray.yygh.user.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
    @Autowired
    private UserInfoService userInfoService;
    //1.生成微信扫码二维码
    //返回生成二维码的参数
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect(){
//        try{
            Map<String,Object> map = new HashMap<>();
            map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
            map.put("scope","snsapi_login");
            String wxOpenRedirectUrl = ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL;
//            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl,"utf-8");
            map.put("redirect_uri",wxOpenRedirectUrl);
            map.put("state",System.currentTimeMillis()+"");
            return Result.ok(map);
//        }catch(UnsupportedEncodingException e){
//            e.printStackTrace();
//            return null;
//        }

    }

    @GetMapping("callback")
    public String callback(String code,String state){
        //1.当扫描完二维码，微信端会重定向到我们规定的路径，并且以get的请求方式发来code和state
        //获取微信传来的code
        System.out.println("code" + code);

        //2.给微信端发送请求，在此之前先整理要发过去的东西
        StringBuilder baseAccessTokenUrl = new StringBuilder();
        baseAccessTokenUrl.append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET,
                code);

        //3.向微信端发出请求，换取登陆的令牌access_token
        try{
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);
            //从返回字符串中获取open_id和access_token
            JSONObject jsonObject = JSON.parseObject(accessTokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");

            //判断数据库是否有这么一号人
            UserInfo userInfo = userInfoService.selectWxInfoOpenId(openid);
            if(userInfo == null){
                //数据库没有这个人，我们要获取他的信息
                //4.拿着这个token去请求微信，获取用户信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo"
                        + "?access_token=%s" + "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl,access_token,openid);
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println(resultInfo);
                JSONObject resultUserInfoJson = JSON.parseObject(resultInfo);
                //解析用户信息
                String nickname = resultUserInfoJson.getString("nickname");
                String headimgurl = resultUserInfoJson.getString("headimgurl");

                //获取扫描人的信息添加到数据库上
                userInfo = new UserInfo();
                userInfo.setNickName(nickname);
                userInfo.setStatus(1);
                userInfo.setOpenid(openid);
                userInfoService.save(userInfo);
            }
            //返回name和token字符串
            Map<String,String> map = new HashMap<>();
            String name = userInfo.getName();
            if(StringUtils.isEmpty(name)){
                name = userInfo.getNickName();
            }
            if(StringUtils.isEmpty(name)){
                name = userInfo.getPhone();
            }
            map.put("name",name);
            //判断userInfo手机号是否为空，如果为空说明还没有登陆，返回openid的值
            //以后根据openid的值，如果有值则说明没有手机号，需要转到手机号登陆页面
            if(StringUtils.isEmpty(userInfo.getPhone())){
                map.put("openid",userInfo.getOpenid());
            }else{
                map.put("openid","");
            }
            //生成token
            String token = JwtHelper.createToken(userInfo.getId(),name);
            map.put("token",token);
            return "redirect:" +
                    ConstantWxPropertiesUtils.YYGH_BASE_URL +
                    "/weixin/callback?token=" +
                    map.get("token") + "&openid=" + map.get("openid") +
                    "&name=" + URLEncoder.encode(map.get("name"),"utf-8");

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
