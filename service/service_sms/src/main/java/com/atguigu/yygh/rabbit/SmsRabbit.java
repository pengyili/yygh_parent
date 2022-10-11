package com.atguigu.yygh.rabbit;

import com.atguigu.yygh.constans.MqConst;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
        System.out.println(phone + " " + msmVo.getTemplateCode());
    }
}
