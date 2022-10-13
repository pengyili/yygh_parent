package com.atguigu.yygh.hosp.amqp;

import com.atguigu.yygh.api.service.APIService;
import com.atguigu.yygh.constans.MqConst;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.order.OrderMqVo;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HospMq {
    @Autowired
    private  HospitalService hospitalService;
    @Autowired
    private APIService apiService;

    @RabbitListener( bindings = {
            @QueueBinding(value = @Queue(value = MqConst.QUEUE_ORDER , declare = "true"),
                    exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ORDER ,  declare = "true"),
                    key = {MqConst.ROUTING_ORDER}
            )
    } ,admin = "amqpAdmin")
    public void updateSchedule(OrderMqVo order , Message message , Channel channel) throws IOException {

        assert order != null;
        if( order .getAvailableNumber() != null && order
                .getReservedNumber()!= null ) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            Schedule scheduleById = hospitalService.getScheduleById(order.getScheduleId());
            scheduleById.setReservedNumber(order.getReservedNumber());
            scheduleById.setAvailableNumber(order.getAvailableNumber());
            apiService.saveSchedule(scheduleById);
        }
        else{
            Schedule scheduleById = hospitalService.getScheduleById(order.getScheduleId());
            scheduleById.setReservedNumber(scheduleById.getReservedNumber() + 1 );
            scheduleById.setAvailableNumber(scheduleById.getAvailableNumber() -1 );
            apiService.saveSchedule(scheduleById);
        }
    }
}
