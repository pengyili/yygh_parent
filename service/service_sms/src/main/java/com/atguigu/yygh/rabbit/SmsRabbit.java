package com.atguigu.yygh.rabbit;

import com.atguigu.yygh.constans.MqConst;
import com.atguigu.yygh.utils.HttpUtils;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.rabbitmq.client.Channel;
import org.apache.http.HttpResponse;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SmsRabbit {

    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue( value = MqConst.QUEUE_MSM_ITEM ,  declare = "true"  ),
                    exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM , declare = "true"),
                    key = {MqConst.ROUTING_MSM_ITEM})
    } ,admin = "amqpAdmin")
    public void send(OrderMqVo order , Message message , Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag() , false );
        MsmVo msmVo = order.getMsmVo();
        String phone = msmVo.getPhone();

        String host = "https://cxwg.market.alicloudapi.com";
        String path = "/sendSms";
        String method = "POST";
        String appcode = "你自己的AppCode";//开通服务后 买家中心-查看AppCode
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("content", msmVo.getTemplateCode());
        querys.put("mobile", msmVo.getPhone());
        Map<String, String> bodys = new HashMap<String, String>();

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */

            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
