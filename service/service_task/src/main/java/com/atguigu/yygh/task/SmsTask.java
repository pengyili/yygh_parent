package com.atguigu.yygh.task;

import com.atguigu.yygh.client.OrderClient;
import com.atguigu.yygh.constans.MqConst;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class SmsTask {

    @Autowired
    private OrderClient orderClient;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Scheduled(cron = "0 0 8 * * ?")
    public void smsTask(){
        OrderMqVo orderMqVo = new OrderMqVo();
        List<OrderInfo> orderList = orderClient.getOrderList();

//        orderMqVo.setMsmVo();
        orderList.forEach(s -> {
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(s.getPatientPhone());
            msmVo.setTemplateCode("lypeaygewtgawegaweg");
            rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_MSM ,MqConst.QUEUE_MSM_ITEM, orderMqVo);
        });

    }
}
