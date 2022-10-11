package com.atguigu.yygh.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("oss")
public class OSSProperties {
    private String   endpoint;
    private  String accessKeyId;
    private String accessKeySecret;
    private  String bucketName;
}
