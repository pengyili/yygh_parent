package com.atguigu.yygh.prop;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("wx")
@Data
public class WxProperties {
    private String appid ;
    private String redirectUri ;
    private String appSecret;
}
