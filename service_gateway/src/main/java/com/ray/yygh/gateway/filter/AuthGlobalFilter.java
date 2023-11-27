package com.ray.yygh.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.ray.yygh.common.helper.JwtHelper;
import com.ray.yygh.common.result.Result;
import com.ray.yygh.common.result.ResultCodeEnum;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private AntPathMatcher antPathMatcher = new AntPathMatcher();


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest httpServerRequest = exchange.getRequest();
        String path = httpServerRequest.getURI().getPath();
        System.out.println("=======" + path);

        //内部服务接口，不允许外部访问
        if(antPathMatcher.match("/**/inner/**",path)){
            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            return out(serverHttpResponse, ResultCodeEnum.PERMISSION);
        }

        //api接口，异步请求，校验用户是否登陆
        if(antPathMatcher.match("/api/**/auth/**",path)){
            Long userId = this.getUserId(httpServerRequest);
            if(StringUtils.isEmpty(userId)){
                ServerHttpResponse response = exchange.getResponse();
                return out(response,ResultCodeEnum.LOGIN_AUTH);
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Long getUserId(ServerHttpRequest serverHttpRequest){
        String token = "";
        List<String> tokenList = serverHttpRequest.getHeaders().get("token");
        if(null != tokenList){
            token = tokenList.get(0);
        }
        if(StringUtils.isEmpty(token)){
            return JwtHelper.getUserId(token);
        }
        return null;
    }

    private Mono<Void> out(ServerHttpResponse serverHttpResponse,ResultCodeEnum resultCodeEnum){
        Result result = Result.build(null,resultCodeEnum);
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = serverHttpResponse.bufferFactory().wrap(bits);
        //制定编码
        serverHttpResponse.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        return serverHttpResponse.writeWith(Mono.just(buffer));
    }
}
