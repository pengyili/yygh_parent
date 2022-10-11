package com.atguigu.yygh.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class UserFilter implements GlobalFilter , Ordered {
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        List<String> strings = headers.get("X-Token");

        if(strings == null){
            strings = headers.get("token") ;
        }
        boolean match = antPathMatcher.match("/admin/user/**", request.getURI().getPath());

        antPathMatcher.match("/admin/cmn/**" , request.getURI().getPath());
        if(!match )
            match = antPathMatcher.match("/user/hosp/**" , request.getURI().getPath());
        if(!match)
            match = antPathMatcher.match("/admin/cmn/**" , request.getURI().getPath());
        if(!match)
            match = antPathMatcher.match("/user/sms/**" ,request.getURI().getPath());
        if(!match)
            match = antPathMatcher.match("/user/info/**" ,request.getURI().getPath());
        if(!match)
            match = antPathMatcher.match("/user/wx/**" ,request.getURI().getPath());
        if(!match)
            match = antPathMatcher.match("/*/oss/**" ,request.getURI().getPath());
        if(!match) {
            if (strings == null) {
                HttpHeaders headers1 = response.getHeaders();
                response.setStatusCode(HttpStatus.SEE_OTHER);
                headers1.set(HttpHeaders.CONTENT_LOCATION, "http://localhost:9528");
                return response.setComplete();
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
