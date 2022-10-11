package com.atguigu.yygh;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "wx")
public class WeiXinProperties {
    private String appid;
    private String mchId;
    private String unifiedOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    private String orderQueryUrl = "https://api.mch.weixin.qq.com/pay/orderquery";
    private String partnerKey;


}
