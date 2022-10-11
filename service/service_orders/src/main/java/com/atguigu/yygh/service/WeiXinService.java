package com.atguigu.yygh.service;

import java.io.IOException;
import java.util.Map;

public interface WeiXinService {
    String getCodeUrl(Long orderId) throws IOException, Exception;

    void queryPayStatus(Long orderId) throws Exception;
}
